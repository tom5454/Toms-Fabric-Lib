package com.tom.fabriclibs.ext;

public interface IRegistry<T> {
	T register(T object);
	void registerAll(T... objects);
}
