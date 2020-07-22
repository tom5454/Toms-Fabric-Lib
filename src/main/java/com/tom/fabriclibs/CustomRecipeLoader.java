package com.tom.fabriclibs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import com.tom.fabriclibs.events.ServerResourceManagerEvent;

public class CustomRecipeLoader {
	private static final List<Recipe<?>> recipes = new ArrayList<>();

	public static void addRecipe(Recipe<?> rec) {
		synchronized (recipes) {
			if (rec == null) {
				System.err.println("Attempted to add null recipe, this is invalid behavior.");
				Thread.dumpStack();
			}
			recipes.add(rec);
		}
	}

	public static void serverStart(ServerResourceManagerEvent e) {
		ReloadableResourceManagerImpl resMan = e.getResourceManager();
		Reloader rel = new Reloader(e.getManager());
		for (int i = 0; i < resMan.listeners.size(); i++) {
			if (resMan.listeners.get(i) instanceof RecipeManager) {
				resMan.listeners.add(i + 1, rel);
				break;
			}
		}
	}

	private static void addRecipes(RecipeManager mgr) {
		recipes.forEach(r -> {
			Map<Identifier, Recipe<?>> map = mgr.recipes.computeIfAbsent(r.getType(), t -> new HashMap<>());
			Recipe<?> old = map.get(r.getId());
			if (old == null) map.put(r.getId(), r);
		});
		FabricLibs.LOGGER.info("Registered " + recipes.size() + " additional recipes.");
	}

	private static class Reloader extends SinglePreparationResourceReloadListener<List<Recipe<?>>> {
		private ServerResourceManager manager;

		public Reloader(ServerResourceManager manager) {
			this.manager = manager;
		}

		@Override
		protected List<Recipe<?>> prepare(ResourceManager p_212854_1_, Profiler p_212854_2_) {
			return null;
		}

		@Override
		protected void apply(List<Recipe<?>> recipes, ResourceManager manager, Profiler profiler) {
			RecipeManager mgr = this.manager.getRecipeManager();
			mgr.recipes = new HashMap<>(mgr.recipes);
			for (RecipeType<?> type : mgr.recipes.keySet()) {
				mgr.recipes.put(type, new HashMap<>(mgr.recipes.get(type)));
			}
			addRecipes(mgr);
		}
	}
}
