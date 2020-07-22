package com.tom.fabriclibs;

public class DeferredWorkQueue {

	public static void runLater(Runnable task) {
		task.run();
	}

}
