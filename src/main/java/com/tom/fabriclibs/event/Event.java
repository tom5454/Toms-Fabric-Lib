package com.tom.fabriclibs.event;

public class Event {
	private boolean isCanceled;

	public void setCanceled(boolean b) {
		isCanceled = b;
	}

	public boolean isCanceled() {
		return isCanceled;
	}
}
