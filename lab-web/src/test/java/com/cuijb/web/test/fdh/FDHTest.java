package com.cuijb.web.test.fdh;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FDHTest {
	@Test
	public void fdhAnalyse() {
		File dir = new File("E:\\Workspace\\Docs\\CSServer\\Fragment\\201806151834");
		for (File file : dir.listFiles()) {
			log.info("Analyse file({}) start", file.getName());
			try {
				FDHDescription.parse(FileUtils.toByteArray(file.getPath()), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.info("Analyse file({}) end", file.getName());
			log.info("------------------------");
		}
	}

	@Test
	public void aaa() {
		log.info("256 byte value: {}", (new Integer(255).byteValue() & 0xFF));
		log.info("256 byte value: {}", new Short("128").byteValue());
	}
}
