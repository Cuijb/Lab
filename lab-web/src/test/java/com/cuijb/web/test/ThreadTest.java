package com.cuijb.web.test;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadTest {

	@Test
	public void aaa() {
		Timer timer = new Timer();
		timer.schedule(new MyTask(1), 200);
		timer.schedule(new MyTask(2), 200);
		timer.schedule(new MyTask(3), 200);
		timer.schedule(new MyTask(4), 200);
		timer.schedule(new MyTask(5), 200);
		timer.schedule(new MyTask(6), 200);
		timer.schedule(new MyTask(7), 200);
		timer.schedule(new MyTask(8), 800);
		timer.schedule(new MyTask(8), 800);
		timer.schedule(new MyTask(8), 800);
		timer.schedule(new MyTask(8), 800);

		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class MyTask extends TimerTask {
		private int id = 0;

		public MyTask(int i) {
			this.id = i;
		}

		@Override
		public void run() {
			MyThread mt = MyThread.getInstance();
			if (mt.isAlive()) {
				log.info("Task({}) can not start", id);
			} else {
				log.info("Task({}) start", id);
				try {
					mt.start();
				} catch (Exception e) {
					log.info("Task({}) start Thread({}) has exception: {}", id, mt.getName(), e);
				}
			}
		}
	}
}

@Slf4j
class MyThread extends Thread {
	private MyThread() {
	}

	public static MyThread getInstance() {
		return Inner.instance;
	}

	@Override
	public void run() {
		for (int i = 1; i <= 10; i++) {
			log.info("{} running: {}", Thread.currentThread().getName(), i);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static class Inner {
		private static final MyThread instance = new MyThread();
	}
}
