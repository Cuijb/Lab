package com.cuijb.web.vo;

public class Man extends Human {
	public Man() {
		changeHair("short hairs");
	}

	@Override
	public void changeHair(String myHair) {
		System.out.println("Man changeHair");
		super.changeHair(myHair);
	}
}
