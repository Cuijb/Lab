package com.cuijb.web.test;

import org.junit.Test;

import com.cuijb.web.vo.Human;
import com.cuijb.web.vo.Man;
import com.cuijb.web.vo.Women;

public class HumanTest {

	@Test
	public void HeadTest() {
		Human w = new Women();
		w.start();

		Human m = new Man();
		m.start();

		try {
			Thread.sleep(4000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m.changeHair("short yellow hairs");
		w.changeHair("yellow red long hairs");

		try {
			Thread.sleep(4000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		w.changeHair("black red long hairs");

		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
