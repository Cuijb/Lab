package com.cuijb.web.test;

import org.junit.Test;

public class ContinueTest extends BaseTest {

	@Test
	public void emptyTest() {

		long cacheLessContinueCount = 0; // 缓存连续抖动计数，1-0-1
		long cacheEmptyContinueCount = 0;// 缓存连续为空计数

		for (int i = 0; i < 10; i++) {
			long r = Math.round(Math.random() * 2);
			System.out.println(r + " ");
			if (r < 1) {
				cacheEmptyContinueCount++;
				if (cacheLessContinueCount > 0) {
					cacheLessContinueCount++;
				}
			} else if (r < 2) {
				cacheLessContinueCount++;
				cacheEmptyContinueCount = 0;
			} else {
				cacheLessContinueCount = 0;
				cacheEmptyContinueCount = 0;
			}
			System.out.println("lessCount : " + cacheLessContinueCount);
			System.out.println("emptyCount : " + cacheEmptyContinueCount);
			System.out.println();
		}

		cacheLessContinueCount = 0; // 缓存连续抖动计数，1-0-1
		cacheEmptyContinueCount = 0;// 缓存连续为空计数

		for (int i = 0; i < 10; i++) {
			long cacheSize = Math.round(Math.random() * 2);
			System.out.println(cacheSize + " ");
			if (cacheSize < 2) {
				if (cacheLessContinueCount > 0 || cacheEmptyContinueCount > 0) {
					cacheLessContinueCount++;
				}
				if (cacheSize < 1) {
					cacheEmptyContinueCount++;
				} else {
					cacheEmptyContinueCount = 0;
				}
			} else {
				cacheLessContinueCount = 0;
				cacheEmptyContinueCount = 0;
			}
			System.out.println("lessCount : " + cacheLessContinueCount);
			System.out.println("emptyCount : " + cacheEmptyContinueCount);
			System.out.println();
		}

	}
}
