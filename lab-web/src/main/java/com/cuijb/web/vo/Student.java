package com.cuijb.web.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
	private String name;
	private String sex;
	private int age;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + "-" + age;
	}
}
