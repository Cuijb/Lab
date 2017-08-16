package com.cuijb.web.test;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class PriorityQueueTest extends BaseTest {

	private class MyTask implements Runnable, Comparable<MyTask> {
		private int value;

		public MyTask(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		@Override
		public int compareTo(MyTask task) {
			return (getValue() - task.getValue()) * -1;
		}

		@Override
		public void run() {
			System.out.println(new Date().getTime() + " - " + value);
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	@Test
	public void test() {
		ExecutorService es = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());
		for (int i = 0; i < 4; i++) {
			es.execute(new MyTask(i));
			System.out.println(new Date().getTime() + " - exec " + i);
		}
		MyTask task = null;
		for (int i = 4; i < 10; i++) {
			if (i == 7) {
				task = new MyTask(2);
				es.execute(task);
				System.out.println(new Date().getTime() + " - exec " + i);
				continue;
			}
			es.execute(new MyTask(i));
			System.out.println(new Date().getTime() + " - exec " + i);
		}

		es.shutdown();
		try {
			es.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
