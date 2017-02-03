package com.cuijb.web.test;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.Test;

import com.cuijb.web.vo.Student;

public class SimpleTest extends BaseTest {

	@Test
	public void TestDequePoll() {
		// create an empty array deque with an initial capacity
		Deque<Integer> deque = new ArrayDeque<Integer>(8);

		// use add() method to add elements in the deque
		deque.add(25);
		deque.add(30);
		deque.add(20);
		deque.add(18);

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

		Student last = sd.getLast();
		last.setAge(last.getAge() + 1);
		sd.addLast(st);
		System.out.println(toStr(sd));
	}
}
