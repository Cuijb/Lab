package com.cuijb.web.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import lombok.Getter;
import lombok.Setter;

public class ListTest {
	ThreadLocal<MyObj> aaa = new ThreadLocal<>();

	@Test
	public void arrayListTest() {
		List<MyObj> myList = new ArrayList<>();
		aaa.set(new MyObj(Math.round(Math.random() * 100000)));

		MyThread addThread = new MyThread("AddThread", 4000);
		addThread.start();

		MyThread removeThread = new MyThread("RemoveThread", 2000);
		removeThread.start();

		MyThread removeThread2 = new MyThread("RemoveThread2", 1500);
		removeThread2.start();

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			System.out.println(new Date() + "myList sleep exception: " + e);
		}
	}

	@Getter
	@Setter
	private class MyObj {
		private long sequence;

		public MyObj(long sequence) {
			this.sequence = sequence;
		}
	}

	private class MyThread extends Thread {
		private volatile boolean stop = false;
		private long interrupt;

		public MyThread(String name, long interrupt) {
			this.interrupt = interrupt;
			setName(name);
		}

		public void close() {
			this.stop = true;
		}

		@Override
		public void run() {
			while (!stop) {
				// if (myList.size() > 0) {
				// // Iterator<MyObj> iterator = myList.iterator();
				// int index = new Double(Math.random() * (myList.size() -
				// 1)).intValue();
				// System.out.println(new Date() + "myList remove2 " +
				// myList.get(index).getSequence());
				// myList.remove(index);
				// }
				if (null != aaa.get()) {
					System.out.println(new Date() + " " + getName() + " : " + aaa.get().getSequence());
				}
				aaa.set(new MyObj(Math.round(Math.random() * 100000)));
				System.out.println(new Date() + " " + getName() + " : " + aaa.get().getSequence());
				try {
					Thread.sleep(interrupt);
				} catch (InterruptedException e) {
					System.out.println(new Date() + " " + getName() + " : sleep exception" + e);
				}
			}
		}
	}
}
