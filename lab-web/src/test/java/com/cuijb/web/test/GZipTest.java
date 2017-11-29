package com.cuijb.web.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GZipTest extends BaseTest {

	@Test
	public void toGzip() {
		File logDir = new File("E:\\apk\\develop\\gzip");
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

	@Test
	public void fromGzip() {
		File logDir = new File("E:\\apk\\develop\\gzip\\1K");
		if (!logDir.exists()) {
			log.error("from gzip failed, log dir is not exists");
			return;
		}
		if (logDir.isFile()) {
			log.error("from gzip failed, log dir is file?!");
			return;
		}
		if (logDir.isDirectory()) {
			File[] logFiles = logDir.listFiles();
			if (null == logFiles || logFiles.length < 1) {
				log.info("from gzip failed, log dir is empty");
				return;
			}
			// 1、文件压缩
			for (File file : logFiles) {
				if (file.isFile()) {
					if (file.getName().endsWith(".gz")) {
						unpackGzip(file.getPath(), file.getPath().replace(".gz", ""));
					}
				}
			}
		}
	}

	/**
	 * Gzip 解压
	 *
	 * @param gzipFilePath
	 * @param saveFilePath
	 * @return
	 */
	private void unpackGzip(String gzipFilePath, String saveFilePath) {
		InputStream is;
		GZIPInputStream gzipInputStream = null;
		OutputStream outputStream = null;
		File fileZip = null;
		try {
			fileZip = new File(gzipFilePath);
			is = new FileInputStream(fileZip);
			gzipInputStream = new GZIPInputStream(new BufferedInputStream(is), (int) fileZip.length());
			outputStream = new FileOutputStream(saveFilePath);
			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = gzipInputStream.read(buffer, 0, 1024)) != -1) {
				outputStream.write(buffer, 0, readLen);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("unpackGzip FileNotFoundException:" + e);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("unpackGzip IOException:" + e);
		} catch (Exception e) {
			log.error("unpackGzip Exception:" + e);
		} finally {
			try {
				if (gzipInputStream != null) {
					gzipInputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
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
