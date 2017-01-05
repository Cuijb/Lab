package com.cuijb.web.test;

import com.cuijb.web.vo.Student;

public class LombokTest {

	public static void main(String[] args) {
		Student st = new Student();
		st.setName("Cuijb");
		st.setSex("F");
		st.setAge(18);
		System.out.println(st);
	}
}
