package com.cuijb.web.test;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AAa {
	private Map<String, Integer> unhandledMap = new ConcurrentHashMap<>();

	@Test
	public void sgChanged() {
		unhandledMap.put("1", 1);
		unhandledMap.put("2", 2);
		unhandledMap.put("3", 3);
		unhandledMap.put("4", 4);
		unhandledMap.put("5", 5);
		unhandledMap.put("6", 6);
		unhandledMap.put("7", 7);
		Iterator<Entry<String, Integer>> unhandledIt = unhandledMap.entrySet().iterator();
		while (unhandledIt.hasNext()) {
			int dbp = unhandledIt.next().getValue();
			if (startNewChannel(dbp)) {
				unhandledIt.remove();
			}
			log.info("map: {}", unhandledMap);
		}
	}

	private boolean startNewChannel(int dbp) {
		if (dbp % 2 == 0) {
			log.warn("sg service type invalid: {}", dbp);
			unhandledMap.put(dbp + "", dbp);
			return false;
		}
		log.warn("sg service type valid: {}", dbp);
		return true;
	}
}
