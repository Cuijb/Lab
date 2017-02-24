package com.cuijb.web.vo;

public class Human {
	private String myHair;
	private Head head;

	public Human() {
		head = new Head();
	}

	public void start() {
		head.start();
	}

	public void changeHair(String myHair) {
		System.out.println(this.getClass().getSimpleName() + ": I change my hairs!");
		this.myHair = myHair;
	}

	private class Head extends Thread {
		@Override
		public void run() {
			while (true) {
				System.out.println(Human.this.getClass().getSimpleName() + ": I have " + myHair + ".");
				try {
					Thread.sleep(2 * 1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String introduce() {
		return "I am Human!";
	}

	protected class mouth {
		public void say() {
			System.out.println(introduce());
		}
	}
}
