package com.cuijb.web.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SGTest {
	private static final String DIR = "/storage/emulated/0/lband_server/resource/asset/";

	private static final SimpleDateFormat DATE_FARMAT = new SimpleDateFormat("yyyyMM");

	public void assetInsert() {
		List<String> assets = new ArrayList<>();
		assets.add("tag:gvmedia.com.cn,2017:silkwave/icons/dsj/bfp_xqyi_20190121.png");
		assets.add("tag:gvmedia.com.cn,2017:silkwave/icons/dsj/bfp_xqer_20190122.png");
		assets.add("tag:gvmedia.com.cn,2017:silkwave/icons/dsj/bfp_xqsan_20190123.png");
		assets.add("tag:gvmedia.com.cn,2017:silkwave/icons/dsj/bfp_xqsi_20190124.png");
		assets.add("tag:gvmedia.com.cn,2017:silkwave/icons/dsj/bfp_xqwu_20190125.png");
		for (String uri : assets) {
			String fileName = new String(Base64.getEncoder().encode(uri.getBytes())).trim() + ".png";
			Date date = new Date();
			String savePath = DIR + DATE_FARMAT.format(date) + "/" + fileName;
			log.info("insert into uri_resource('uri','mime','save_path','insert_time') values ('{}','{}','{}',{});",
					uri, "image/png", savePath, date.getTime() / 1000);
		}
	}

	@Test
	public void set() {
		long endTime = System.currentTimeMillis();
		long beginTime = endTime - 5L * 24 * 60 * 60 * 1000;
		String pattern = "yyyyMMdd";
		log.info("result {} ~ {} : {}", format(pattern, beginTime), format(pattern, endTime),
				duration(pattern, beginTime, endTime));
	}

	public String format(String pattern, long time) {
		DATE_FMT.applyPattern(pattern);
		return DATE_FMT.format(new Date(time));
	}

	private String duration(String pattern, long beginTime, long endTime) {
		String beginStr = format(pattern, beginTime);
		String endStr = format(pattern, endTime);
		int preIndex = 0;
		if (endStr.startsWith(beginStr.substring(0, 6))) {
			preIndex = 6;
		} else if (endStr.startsWith(beginStr.substring(0, 4))) {
			preIndex = 4;
		}
		if (preIndex > 0) {
			return beginStr.substring(0, preIndex) + " " + beginStr.substring(preIndex) + "~"
					+ endStr.substring(preIndex);
		}
		return beginStr + "~" + endStr;
	}

	private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Test
	public void unpackZip() {
		String zipFileName = "E:\\voicecast.zip";
		String dstDir = "E:\\aaabbb";
		InputStream is;
		ZipInputStream zis = null;
		if (!dstDir.endsWith(File.separator)) {
			dstDir = dstDir + File.separator;
		}
		try {
			String filename;
			is = new FileInputStream(zipFileName);
			zis = new ZipInputStream(new BufferedInputStream(is));
			ZipEntry ze;
			byte[] buffer = new byte[1024];
			int count;
			List<String> nameList = new ArrayList<>();
			while ((ze = zis.getNextEntry()) != null) {
				filename = ze.getName();
				// Need to create directories if not exists, or
				// it will generate an Exception...
				if (ze.isDirectory()) {
					File fmd = new File(dstDir + filename);
					fmd.mkdirs();
					continue;
				}
				File file = new File(dstDir + filename);
				if (!new File(file.getParent()).exists()) {
					log.info("create dir:" + file.getParent());
					createDirs(file.getParent());
				}
				FileOutputStream fout = new FileOutputStream(dstDir + filename);
				while ((count = zis.read(buffer)) != -1) {
					fout.write(buffer, 0, count);
				}
				nameList.add(dstDir + filename);
				fout.close();
				zis.closeEntry();
			}
			log.info("name list: {}", nameList);
		} catch (Exception e) {
			log.info("unzip file error:" + zipFileName);
			e.printStackTrace();
		} finally {
			if (null != zis) {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean createDirs(String dir) {
		File destDir = new File(dir);
		if (destDir.exists()) {
			return true;
		}
		return destDir.mkdirs();
	}
}
