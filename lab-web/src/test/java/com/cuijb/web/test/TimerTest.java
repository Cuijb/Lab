package com.cuijb.web.test;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

public class TimerTest {
	Timer timer = new Timer("Test", false);

	@Test
	public void testMethod() {
		System.out.println("timer task start: " + System.currentTimeMillis());
		MyTask myTask = new MyTask();
		timer.schedule(myTask, 5000);
		System.out.println("timer task end: " + System.currentTimeMillis());
		try {
			Thread.sleep(1 * 1000);
			timer.schedule(myTask, 2000, 1000);
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}

	private class MyTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("my task start: " + System.currentTimeMillis());
			System.out.println("My task running");
			System.out.println("my task end: " + System.currentTimeMillis());
		}
	}
}
