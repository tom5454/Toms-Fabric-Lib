package com.tom.fabriclibs.events;

import java.lang.reflect.Type;

public interface ITypedEvent<T> {
	Type getType();

}
