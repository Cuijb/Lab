package com.cuijb.web.test;

import org.junit.Test;

import com.cuijb.web.vo.Student;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LombokTest {

	public static void main(String[] args) {
		log.debug("Student test start.");
		Student st = new Student();
		st.setName("Cuijb");
		st.setSex("F");
		st.setAge(18);
		System.out.println(st);
		log.debug("Student test end.");
	}

	@Test
	public void junitTest() {
		log.debug("Student junitTest start.");
		Student st = new Student();
		st.setName("Cuijb");
		st.setSex("F");
		st.setAge(18);
		System.out.println(st);
		log.debug("Student junitTest end.");
	}
}
