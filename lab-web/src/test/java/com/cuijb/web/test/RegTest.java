package com.cuijb.web.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class RegTest extends BaseTest {

	@Test
	public void urlTest() {
		Pattern p = Pattern.compile("([a-z]*)://(.*):(\\d*)");
		Matcher m = p.matcher("udp://192.168.43.15:47000");
		if (m.find()) {
			System.out.println(m.groupCount());
			for (int i = 0; i <= m.groupCount(); i++) {
				// System.out.println("matcher " + i + " : " + m.group(i));
			}

			System.out.println("matcher " + 1 + " : " + m.group(1));
			System.out.println("matcher " + 2 + " : " + m.group(2));
			System.out.println("matcher " + 3 + " : " + m.group(3));
		}
		Pattern pattern = Pattern.compile("^(csServer|swServer).log.\\d{10}.\\d{4}$", Pattern.CASE_INSENSITIVE);
		System.out.println(Pattern.matches("^(csServer|swServer).log.\\d{10}.\\d{4}$", "swServer.log.2017072715.012"));
	}
}
