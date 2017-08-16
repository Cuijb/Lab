package com.cuijb.web.test;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.cuijb.web.vo.Student;

public class SimpleTest extends BaseTest {

	private static List<Integer> IMMEDIATE_START = new ArrayList<>();
	{
		IMMEDIATE_START.add(5); // 北斗数据频道默认启动
		IMMEDIATE_START.add(8); // EMM频道默认启动
	}

	@Test
	public void TestDequePoll() {
		// create an empty array deque with an initial capacity
		Deque<Integer> deque = new ArrayDeque<Integer>(8);

		// use add() method to add elements in the deque
		deque.add(25);
		deque.add(30);
		deque.add(20);
		deque.add(18);
		System.out.println(deque.toArray(new Integer[0]));

		// printing all the elements available in deque
		for (Integer number : deque) {
			System.out.println("Number = " + number);
		}

		int retval = deque.pollFirst();
		System.out.println("Element removed is " + retval);

		// printing all the elements available in deque after using pollFirst()
		for (Integer number : deque) {
			System.out.println("Number = " + number);
		}

		retval = deque.pollLast();
		System.out.println("Element removed is " + retval);

		// printing all the elements available in deque after using pollFirst()
		for (Integer number : deque) {
			System.out.println("Number = " + number);
		}

		Deque<Student> sd = new ArrayDeque<Student>();
		Student st = new Student();
		st.setAge(19);
		sd.add(st);
		System.out.println(toStr(sd));
		System.out.println(toStr(sd.toArray(new Student[0])));
		System.out.println(Arrays.toString(sd.toArray(new Student[0])));

		Student last = sd.getLast();
		last.setAge(last.getAge() + 1);
		sd.addLast(st);
		System.out.println(toStr(sd));
	}

	@Test
	public void aaa() {

		Map<Integer, Integer> map = new HashMap<>();
		map.put(11, 11);
		map.put(12, 14);
		map.put(13, 15);
		map.put(16, 16);
		map.put(18, 18);
		map.put(19, 19);
		map.put(20, 20);
		int minContiune = 0;
		int min = 0;
		int count = 0;
		for (int i = 11; i <= 20; i++) {
			Integer cache = map.get(i);
			if (null != cache) {
				count++;
				if (1 == count) {
					min = cache;
				}
				if (count >= 3) {
					break;
				}
			} else {
				count = 0;
				min = 0;
			}
		}
		if (0 != min && count >= 3) {
			minContiune = min;
		}
		System.out.println("min conitune: " + minContiune);

		String msStr = (new SimpleDateFormat(".yyyyMMddHH.mmss")).format(new Date());
		System.out.println(msStr);
		System.out.println(IMMEDIATE_START.contains(5));
		System.out.println(IMMEDIATE_START.contains(1));
		System.out.println(IMMEDIATE_START.contains(8));
		System.out.println(IMMEDIATE_START.contains(3));

		int a = 1, b = 2, c = 3;

		a = (b += 1);
		System.out.println("a: " + a);

		c = b = ++a;
		System.out.println("b: " + b);
		System.out.println("c: " + c);
	}

	@Test
	public void excutor() throws InterruptedException, ExecutionException {
		List<Future<String>> results = new ArrayList<>();
		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 0; i < 100; i++) {
			results.add(es.submit(new Task()));
		}
		for (Future<String> result : results) {
			System.out.println(new Date().getTime() + " - " + result.get());
		}
	}

	public class Task implements Callable<String> {

		@Override
		public String call() throws Exception {
			System.out.println(new Date().getTime() + " - ah");
			return "something";
		}

	}
}
