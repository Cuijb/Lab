package com.cuijb.web.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegTest extends BaseTest {
	private static final Pattern PATTERN_CONTENT_START = Pattern
			.compile("^#X-GVMEDIA-CONTENT-CELL:CID=(.*),TYPE=begin(,URL=(.*))?");
	private static final Pattern PATTERN_CONTENT_END = Pattern
			.compile("^#X-GVMEDIA-CONTENT-CELL:CID=(.*),TYPE=end(,URL=(.*))?");
	private static final Pattern PATTERN_EXTINF = Pattern.compile("^#EXTINF.*?([0-9]+\\.?[0-9]*)");
	private static final Pattern PATTERN_TS_SEQ = Pattern.compile(".*?([0-9]+)\\.ts");

	private void logAnalyse(File logFile) {
		if (!logFile.exists()) {
			log.warn("{} is not exist!", logFile.getName());
			return;
		}
		log.info("Analyse {} start", logFile.getName());
		BufferedReader br = null;
		try {
			float playtime = 2.0F;
			int sequence = 0;
			String startCell = null;
			br = new BufferedReader(new FileReader(logFile));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				Matcher matcherSt = PATTERN_CONTENT_START.matcher(s);
				if (matcherSt.find()) {
					startCell = matcherSt.group(1);
					continue;
				}

				Matcher matcherPt = PATTERN_EXTINF.matcher(s);
				if (matcherPt.find()) {
					playtime = Float.valueOf(matcherPt.group(1));
					continue;
				}

				Matcher matcherTs = PATTERN_TS_SEQ.matcher(s);
				if (matcherTs.find()) {
					sequence = Integer.valueOf(matcherTs.group(1));
					if (null != startCell) {
						log.info("seq:{}, start content:{}", sequence, startCell);
						startCell = null;
					}
					log.info("seq:{}, playtime:{}", sequence, playtime);
					continue;
				}

				Matcher matcherEd = PATTERN_CONTENT_END.matcher(s);
				if (matcherEd.find()) {
					log.info("seq:{}, end content:{}, url:{}", sequence, matcherEd.group(1), matcherEd.groupCount());
					continue;
				}
			}
			log.info("Analyse {} end{}", logFile.getName(), System.lineSeparator());
		} catch (Exception e) {
			log.error("analyse {} has exception: {}", logFile.getName(), e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("analyse {} close br has exception: {}", logFile.getName(), e);
				}
			}
		}
	}

	@Test
	public void cellTest() {
		File logDir = new File("D:\\log\\0615.m3u8");
		if (!logDir.exists()) {
			log.error("log dir({}) is not exists", logDir.getPath());
			return;
		}
		if (logDir.isFile()) {
			logAnalyse(logDir);
			return;
		}
		if (logDir.isDirectory()) {
			for (File file : logDir.listFiles()) {
				logAnalyse(file);
			}
			log.info("Analyse log file(s) end");
		}
	}

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

	@Test
	public void fragmentTest() {
		Pattern PATTERN_CONTENT = Pattern.compile("^#X-GVMEDIA-CONTENT-CELL:CID=(.*?),*TYPE=start(,URL=(.*))?");
		Matcher matcher = PATTERN_CONTENT.matcher("#X-GVMEDIA-CONTENT-CELL:CID=1,TYPE=start");
		log.info("fragment content cell match:{}, group1:{}, group2:{}, group3:{}", matcher.find(), matcher.group(1),
				matcher.group(2), matcher.group(3));

		matcher = PATTERN_CONTENT.matcher("#X-GVMEDIA-CONTENT-CELL:CID=1,TYPE=start,URa=asdfas");
		log.info("fragment content cell match:{}, group1:{}, group2:{}, group3:{}", matcher.find(), matcher.group(1),
				matcher.group(2), matcher.group(3));

		matcher = PATTERN_CONTENT.matcher("#X-GVMEDIA-CONTENT-CELL:CID=1,TYPE=start,URL=asdfas");
		log.info("fragment content cell match:{}, group1:{}, group2:{}, group3:{}", matcher.find(), matcher.group(1),
				matcher.group(2), matcher.group(3));

		Pattern PATTERN_CONTENT_END = Pattern.compile("^#X-GVMEDIA-CONTENT-CELL:CID=(.*?),TYPE=end(,URL=(.*))?");
		matcher = PATTERN_CONTENT_END.matcher("#X-GVMEDIA-CONTENT-CELL:CID=1,TYPE=end,URL=qerqwer");
		log.info("fragment content cell match:{}, group1:{}, group2:{}, group3:{}", matcher.find(), matcher.group(1),
				matcher.group(2), matcher.group(3));

		matcher = PATTERN_CONTENT_END.matcher("#X-GVMEDIA-CONTENT-CELL:CID=1,TYPE=end");
		log.info("fragment content cell match:{}, group1:{}, group2:{}, group3:{}", matcher.find(), matcher.group(1),
				matcher.group(2), matcher.group(3));

		matcher = PATTERN_CONTENT_END.matcher("#X-GVMEDIA-CONTENT-CELL:CID=1,TYPE=end,URa=asdfas");
		log.info("fragment content cell match:{}, group1:{}, group2:{}, group3:{}", matcher.find(), matcher.group(1),
				matcher.group(2), matcher.group(3));

		matcher = PATTERN_CONTENT_END.matcher("#X-GVMEDIA-CONTENT-CELL:CID=1,TYPE=end");
		log.info("fragment content cell match:{}, group1:{}, group2:{}, group3:{}", matcher.find(), matcher.group(1),
				matcher.group(2), matcher.group(3));
	}

	@Test
	public void lastIndex() {
		String aaa = "../../ad/audio/mute.ts";
		log.info("{} last name: {}", aaa, aaa.substring(aaa.lastIndexOf("/") + 1));
		aaa = "13246.ts";
		log.info("{} last name: {}", aaa, aaa.substring(aaa.lastIndexOf("/") + 1));
	}

	@Test
	public void moveTest() {
		int a1 = 0xF7;
		int b1 = (a1 & 0x0F) << 16;
		int b2 = a1 << 8;
		int b3 = b1 | b2 | a1;
		int b4 = b1 | b2 | a1;
		log.info("({} & 0x0F) << 16 : {}", a1, b1);
		log.info("{} << 8 : {}", a1, b2);
		log.info("{} | {} | {} : {}", b2, b1, a1, b3);
		log.info("{} + {} + {} : {}", b2, b1, a1, b4);
	}

	@Test
	public void unselectedTest() {
		Pattern PATTERN_CONTENT = Pattern.compile("!^(tmp)$", Pattern.CANON_EQ);

		Matcher matcher = PATTERN_CONTENT.matcher("tmpaaaaaaasd");
		log.info("fragment content cell match:{}", matcher.find());

		matcher = PATTERN_CONTENT.matcher("abbbbtmp");
		log.info("fragment content cell match:{}", matcher.find());

		matcher = PATTERN_CONTENT.matcher("abbbbtamp");
		log.info("fragment content cell match:{}", matcher.find());

		matcher = PATTERN_CONTENT.matcher("tmp");
		log.info("fragment content cell match:{}", matcher.find());
	}

	@Test
	public void aaa() {
		Pattern PT_NORMAL = Pattern.compile("^/(\\d+)/(\\d+)(?:/(\\d+)-(\\d+))?$");

		Matcher matcher = PT_NORMAL.matcher("/1/10");
		log.info("fragment content cell, group count:{} match:{}, {}", matcher.groupCount(), matcher.find(),
				matcher.group(4));

		matcher = PT_NORMAL.matcher("/1/10/2001-1");
		log.info("fragment content cell, group count:{} match:{}, {}", matcher.groupCount(), matcher.find(),
				matcher.group(4));

		matcher = PT_NORMAL.matcher("/1/10/2001");
		log.info("fragment content cell, group count:{} match:{}", matcher.groupCount(), matcher.find());
	}
}
