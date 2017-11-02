package com.cuijb.web.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GZipTest extends BaseTest {

	@Test
	public void toGzip() {
		File logDir = new File("D:\\Downloads\\7046");
		if (!logDir.exists()) {
			log.error("to gzip failed, log dir is not exists");
			return;
		}
		if (logDir.isFile()) {
			log.error("to gzip failed, log dir is file?!");
			return;
		}
		if (logDir.isDirectory()) {
			File[] logFiles = logDir.listFiles();
			if (null == logFiles || logFiles.length < 1) {
				log.info("to gzip failed, log dir is empty");
				return;
			}

			// 1、文件压缩
			for (File file : logFiles) {
				if (file.isFile()) {
					if (file.getName().endsWith(".gz")) {
						if (file.length() <= 0) {
							// 压缩文件异常
							delFile(file);
							log.warn(file.getName() + " deleted, length<=0");
							continue;
						}
						continue;
					}

					File gzFile = new File(file.getPath() + ".gz");
					if (gzFile.exists()) {
						// 已经压缩过的文件不需要再做压缩处理
						log.debug(file.getName() + " has been gzipped");
					} else {
						if (packGZip(file.getPath(), gzFile.getPath())) {
							log.info("to gzip success " + file.getName() + " -> " + gzFile.getName());
						} else {
							log.error("to gzip failed " + file.getName());
						}
					}
				}
			}
		}
	}

	/**
	 * Gzip压缩
	 *
	 * @param fileName
	 * @param gzipFileName
	 * @return
	 */
	public boolean packGZip(String fileName, String gzipFileName) {
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			File file = new File(fileName);
			outputStream = new FileOutputStream(gzipFileName);
			inputStream = new FileInputStream(file);
			GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
			byte[] buffer = new byte[(int) file.length()];
			inputStream.read(buffer);
			gzip.write(buffer);
			gzip.close();
			gzip.flush();
			return true;
		} catch (IOException e) {
			log.error("gzip error:" + e.toString());
			return false;
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException e) {
				log.error("gzip close stream error:" + e.toString());
			}
		}
	}

	public void delFile(File file) {
		if (null == file) {
			return;
		}
		if (file.exists()) {
			file.delete();
		}
	}

}
