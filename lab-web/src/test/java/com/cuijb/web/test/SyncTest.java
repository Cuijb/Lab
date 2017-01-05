package com.cuijb.web.test;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class SyncTest {

	public void SyncTest() {
		final SortedSet<String> sortedSet = Collections.synchronizedSortedSet(new TreeSet<String>());
		// SortedSet<String> sortedSet = new TreeSet<String>();
		new Thread(new Runnable() {

			public void run() {
				// synchronized (sortedSet) {
				for (int i = 0; i < 10; i++) {
					sortedSet.add("A" + i);
					System.out.print("  A" + i);
				}
				// System.out.println(sortedSet);
				// }
			}
		}).start();
		new Thread(new Runnable() {

			public void run() {
				// synchronized (sortedSet) {
				for (int i = 0; i < 10; i++) {
					sortedSet.add("B" + i);
					System.out.print("  B" + i);
				}
				// System.out.println(sortedSet);
			}
			// }
		}).start();
	}
}
