package com.cuijb.web.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;

public class UnzipTest {

	@Test
	public void unzipTest() {
		unZip("E:\\apk\\wifiportal\\nodogsplash\\nodogsplash.zip", "E:\\test\\nodogsplash");
	}

	/**
	 * Zip解压缩
	 *
	 * @param zipFile
	 * @param folderPath
	 * @return
	 */
	public static boolean unZip(String zipFile, String folderPath) {
		ZipFile zf = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			File desDir = new File(folderPath);
			if (!desDir.exists()) {
				desDir.mkdir();
			}

			zf = new ZipFile(zipFile);
			for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				System.out.println("unZip entry name: " + entry.getName());
				try {
					byte[] buffer = new byte[1024];
					if (entry.isDirectory()) {
						String dirStr = folderPath + entry.getName();
						File dir = new File(zhEncoding(dirStr));
						dir.mkdir();
						continue;
					}
					out = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, entry.getName())));
					in = new BufferedInputStream(zf.getInputStream(entry));
					int readLen = 0;
					while ((readLen = in.read(buffer, 0, 1024)) != -1) {
						out.write(buffer, 0, readLen);
					}
				} catch (Exception e) {
					System.out.println("unZip entry has exception: " + e.toString());
					return false;
				} finally {
					if (null != in) {
						in.close();
					}
					if (null != out) {
						out.close();
					}
				}
			}
			return true;
		} catch (Exception e) {
			System.out.println("unZip file has Exception:" + e);
		} finally {
			try {
				zf.close();
			} catch (IOException e) {
				System.out.println("unZip close zip file has Exception:" + e);
			}
		}
		return false;
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 *
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File file = new File(baseDir);
		String subStr = null;
		for (int i = 0; i < dirs.length - 1; i++) {
			subStr = zhEncoding(dirs[i]);
			file = new File(file, subStr);
		}
		if (!file.exists()) {
			file.mkdirs();
		}
		subStr = zhEncoding(dirs[dirs.length - 1]);
		file = new File(file, subStr);
		System.out.println("getRealFileName path:" + file);
		return file;
	}

	private static String zhEncoding(String subStr) {
		try {
			subStr = new String(subStr.getBytes("8859_1"), "GB2312");
		} catch (UnsupportedEncodingException e) {
			System.out.println("getRealFileName folder has UnsupportedEncodingException:" + e);
		}
		return subStr;
	}
}
