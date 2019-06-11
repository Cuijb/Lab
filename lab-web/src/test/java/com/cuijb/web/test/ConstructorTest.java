package com.cuijb.web.test;

import org.junit.Test;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConstructorTest {

	@Test
	public void aaa() {
		Student std = new Student(17, null, "A");
		log.info("17-null-A" + new Gson().toJson(std));
		Student std1 = new Student(19, null, "A");
		log.info("19-null-A" + new Gson().toJson(std1));
		Student std2 = new Student(20, "Liping", "A");
		log.info("20-null-A" + new Gson().toJson(std2));
		Student std3 = new Student(21, "Liping", "F");
		log.info("21-null-A" + new Gson().toJson(std3));
	}

}

@Slf4j
@Getter
@Setter
class Student {
	private int age;
	private String name;
	private String sex;

	public Student(int age, String name, String sex) {
		if (age < 18) {
			log.info("age need to be bigger than 18");
			return;
		}
		this.age = age;
		if (null == name || "".equals(name)) {
			log.info("name can not be null or empty");
			return;
		}
		this.name = name;
		if (!"F".equals(sex) && !"M".equals(sex)) {
			log.info("unknown sex:{}", sex);
			return;
		}
		this.sex = sex;
	}
}
