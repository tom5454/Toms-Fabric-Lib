package com.tom.fabriclibs.client;

import java.util.HashMap;
import java.util.Map;

public interface IModelData {
	<T> T getData(ModelProperty<T> key);
	<T> IModelData set(ModelProperty<T> key, T value);

	public static IModelData EMPTY = new IModelData() {

		@Override
		public <T> T getData(ModelProperty<T> key) {
			return null;
		}

		@Override
		public <T> IModelData set(ModelProperty<T> key, T value) {
			throw new UnsupportedOperationException();
		}
	};

	public static class ModelData implements IModelData {
		private Map<ModelProperty<?>, Object> map = new HashMap<>();

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getData(ModelProperty<T> key) {
			return (T) map.get(key);
		}

		@Override
		public <T> IModelData set(ModelProperty<T> key, T value) {
			map.put(key, value);
			return this;
		}
	}
}
