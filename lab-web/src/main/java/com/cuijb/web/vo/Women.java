package com.cuijb.web.vo;

public class Women extends Human {
	public Women() {
		new mouth().say();
		changeHair("long hairs");
	}

	@Override
	public String introduce() {
		return "I am Woman";
	}
}
