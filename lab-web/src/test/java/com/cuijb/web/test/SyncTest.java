package com.cuijb.web.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class SyncTest extends BaseTest {

	@Test
	public void syncTest() {
		SortedSet<String> sortedSet = Collections.synchronizedSortedSet(new TreeSet<String>());
		// SortedSet<String> sortedSet = new TreeSet<String>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// synchronized (sortedSet) {
				for (int i = 0; i < 50; i++) {
					sortedSet.add("A" + i);
					System.out.print("  A" + i);
				}
				// System.out.println(sortedSet);
				// }
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// synchronized (sortedSet) {
				for (int i = 0; i < 50; i++) {
					sortedSet.add("B" + i);
					System.out.print("  B" + i);
				}
				// System.out.println(sortedSet);
			}
			// }
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// synchronized (sortedSet) {
				for (int i = 0; i < 10; i++) {
					sortedSet.add("C" + i);
					System.out.print("  C" + i);
				}
				// System.out.println(sortedSet);
			}
			// }
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// synchronized (sortedSet) {
				System.out.println();
				System.out.println(sortedSet);
				Iterator<String> iter = sortedSet.iterator();
				while (iter.hasNext()) {
					String item = iter.next();
					if (item.endsWith("2")) {
						iter.remove();
					}
				}
				System.out.println();
				System.out.println(sortedSet);
				// }
			}
		}).start();
	}

	@Test
	public void syncMapTest() {
		Map<Long, Boolean> map = Collections.synchronizedMap(new HashMap<>());
		new Thread(new Runnable() {

			@Override
			public void run() {
				// synchronized (sortedSet) {
				while (true) {
					long sleep = random();
					try {
						Thread.sleep(sleep * 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					System.out.println(sleep + " put ******");
				}
				// System.out.println(sortedSet);
				// }
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// synchronized (sortedSet) {
				while (true) {
					long sleep = random();
					try {
						Thread.sleep(sleep * 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.get(sleep);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					map.put(sleep, true);
					System.out.println(sleep + " put ------");
				}
				// System.out.println(sortedSet);
				// }
			}
		}).start();
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (true) {
		// // synchronized (sortedSet) {
		// long sleep = random();
		// try {
		// Thread.sleep(sleep * 1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// map.put(sleep, false);
		// // System.out.println(sortedSet);
		// }
		// }
		// // }
		// }).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					long sleep = random();
					try {
						Thread.sleep(sleep * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (map) {

						Iterator<Entry<Long, Boolean>> iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<Long, Boolean> entry = iter.next();
							if (entry.getValue()) {
								System.out.println(entry.getKey() + " set false ******");
								entry.setValue(false);
							} else {
								System.out.println(entry.getKey() + " remove ******");
								iter.remove();
							}
						}
					}
				}
			}
			// }
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					long sleep = random();
					try {
						Thread.sleep(sleep * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (map) {

						Iterator<Entry<Long, Boolean>> iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<Long, Boolean> entry = iter.next();
							if (entry.getValue()) {
								System.out.println(entry.getKey() + " set false ------");
								entry.setValue(false);
							} else {
								System.out.println(entry.getKey() + " remove ------");
								iter.remove();
							}
						}
					}
				}
			}
			// }
		}).start();

		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
	}

	private long random() {
		return Math.round(Math.random() * 4 + 1);
	}
}
