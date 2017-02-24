package com.cuijb.web.vo;

public class Man extends Human {
	public Man() {
		new mouth().say();
		changeHair("short hairs");
	}

	@Override
	public void changeHair(String myHair) {
		System.out.println("Man changeHair");
		super.changeHair(myHair);
	}

	@Override
	public String introduce() {
		return "I am Man";
	}
}
