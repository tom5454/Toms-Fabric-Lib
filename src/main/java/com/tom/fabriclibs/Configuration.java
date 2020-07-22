package com.tom.fabriclibs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Configuration {
	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static final String CATEGORY_GENERAL = "general";
	private Map<String, Map<String, Object>> data = new HashMap<>();
	private Map<String, Map<String, Object>> comments = new HashMap<>();
	private boolean changed;
	private File file;

	public Configuration(File file) {
		this.file = file;
	}

	public boolean getBoolean(String id, String group, boolean def, String comment) {
		return get(id, group, def, comment);
	}

	public float getFloat(String id, String group, float def, float min, float max, String comment) {
		return get(id, group, def, comment);
	}

	public boolean hasChanged() {
		return changed;
	}

	public void save() {
		try (PrintWriter writer = new PrintWriter(file)){
			gson.toJson(data, writer);
		} catch (IOException e) {
		}
	}

	public int getInt(String id, String group, int def, int min, int max, String comment) {
		int i = get(id, group, def, comment);
		if(i < min || i > max) {
			changed = true;
			data.get(group).put(id, def);
			return def;
		}
		return i;
	}

	public String getString(String id, String group, String def, String comment) {
		return get(id, group, def, comment);
	}

	@SuppressWarnings("unchecked")
	public void load() {
		try (FileReader reader = new FileReader(file)){
			data = (Map<String, Map<String, Object>>) gson.fromJson(reader, Object.class);
		} catch (IOException e) {
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T get(String id, String group, T def, String comment) {
		comments.computeIfAbsent(group, k -> new HashMap<>()).put(id, comment);
		Map<String, Object> g = data.computeIfAbsent(group, k -> new HashMap<>());
		Object o = g.computeIfAbsent(id, k -> {
			changed = true;
			return def;
		});
		try {
			return (T) o;
		} catch (ClassCastException e) {
			g.put(id, def);
			changed = true;
			return def;
		}
	}

	public String[] getStringList(String id, String group, String[] def, String comment) {
		return get(id, group, Arrays.asList(def), comment).toArray(new String[0]);
	}

}
