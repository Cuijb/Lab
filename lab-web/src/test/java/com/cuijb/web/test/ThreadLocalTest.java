package com.cuijb.web.test;

import org.junit.Test;

public class ThreadLocalTest {
	@Test
	public void Test() {
		ThreadLocalObj sn = new ThreadLocalObj();
		TestClient t1 = new TestClient(sn);
		TestClient t2 = new TestClient(sn);
		TestClient t3 = new TestClient(sn);
		t1.start();
		t2.start();
		t3.start();
	}
}

class ThreadLocalObj {
	private int seq = 0;
	private static ThreadLocal<Integer> seqNum = new ThreadLocal<Integer>() {
		@Override
		public Integer initialValue() {
			return 0;
		}
	};

	public void getNextNum() {
		seqNum.set(seqNum.get() + 1);
		System.out.println("thread[" + Thread.currentThread().getName() + "] sn: " + seqNum.get());
		System.out.println("thread[" + Thread.currentThread().getName() + "] seq: " + ++seq);
	}
}

class TestClient extends Thread {
	private ThreadLocalObj sn;

	public TestClient(ThreadLocalObj sn) {
		this.sn = sn;
	}

	@Override
	public void run() {
		for (int i = 0; i < 3; i++) {
			sn.getNextNum();
		}
	}
}
