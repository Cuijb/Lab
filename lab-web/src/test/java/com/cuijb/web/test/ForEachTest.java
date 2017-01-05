package com.cuijb.web.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForEachTest {

	// Foreach、Iterator循环过程删除报并发异常
	// .size() for循环 多线程 数组越界

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		list.add("f");
		list.add("g");
		list.add("h");
		list.add("i");
		list.add("j");
		list.add("k");
		// for (int i = 0; i < list.size(); i++) {
		// System.out.println("array " + i + " " + list.get(i));
		// if (i == 5) {
		// list.remove(5);
		// }
		// System.out.println("array " + i + " " + list.get(i));
		// }
		// for (String str : list) {
		// System.out.println("Foreach " + str);
		// if (str.equals("g")) {
		// list.remove(5);
		// }
		// }

		log.debug("iterator start.");
		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String str = iter.next();
			log.debug("iterator with " + str);
			if (str.equals("g")) {
				iter.remove();
			}
			// System.out.println("iterator b " + iter.next());
		}

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // synchronized (list) {
		// for (int i = 0; i < list.size(); i++) {
		// System.out.println("list " + i + " " + list.get(i));
		// }
		// // System.out.println(list);
		//// }
		// }
		// }).start();
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // synchronized (list) {
		// list.remove(5);
		// // System.out.println(list);
		// }
		//// }
		// }).start();
		log.info("iterator end.");
	}
}
