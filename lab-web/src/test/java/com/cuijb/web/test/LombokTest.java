package com.cuijb.web.test;

import org.junit.Test;

import com.cuijb.web.vo.Student;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LombokTest extends BaseTest {

	@Test
	public void junitTest() {
		log.debug("Student junitTest start.");
		Student st = new Student();
		st.setName("Cuijb");
		st.setSex("F");
		st.setAge(18);
		System.out.println(new Gson().toJson(st));
		log.debug("Student junitTest end.");
	}
}
