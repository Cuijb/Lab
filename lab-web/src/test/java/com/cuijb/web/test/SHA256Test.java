package com.cuijb.web.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class SHA256Test {

	@Test
	public void sha256Test() {
		String filePath = null;
		filePath = "E:\\Workspace\\Docs\\DaTang\\rom-update-0510\\update.zip";
		whileTest(filePath);
		// filePath = "E:\\apk\\update\\CSServer-1.0.31.apk";
		// whileTest(filePath);
		// filePath = "E:\\apk\\update\\CSServer-1.0.51.apk";
		// whileTest(filePath);
		// filePath = "E:\\apk\\update\\CSServer-1.0.61.apk";
		// whileTest(filePath);
		// filePath = "E:\\apk\\update\\CSServer-1.0.71.apk";
		// whileTest(filePath);
		//
		// filePath = "E:\\rom-update-0425\\msm8909-ota-eng-20170425.zip";
		// whileTest(filePath);
	}

	private void oneTest(String filePath) {
		File file = new File(filePath);
		byte[] data = new byte[(int) file.length()];
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			inputStream.read(data);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// int size = 1024 * 1024;
		// byte[] bytes = new byte[size];
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null == md) {
			return;
		}
		long startTime = System.nanoTime();
		// for (int i = 0; i < 1024; i++)
		// md.update(bytes, 0, size);
		md.update(data);
		long endTime = System.nanoTime();
		System.out.println(String.format("%x", new java.math.BigInteger(1, md.digest())));
		System.out.println(String.format("%d ms", (endTime - startTime) / 1000000));
	}

	private void whileTest(String filePath) {
		// int size = 1024 * 1024;
		// byte[] bytes = new byte[size];
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		if (null == md) {
			return;
		}
		long startTime = System.nanoTime();
		File file = new File(filePath);
		byte[] data = new byte[1024];
		InputStream inputStream = null;
		int len = 0;
		try {
			inputStream = new FileInputStream(file);
			while ((len = inputStream.read(data)) != -1) {
				md.update(data, 0, len);
			}
			inputStream.read(data);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		long endTime = System.nanoTime();
		System.out.println(String.format("%14x", new java.math.BigInteger(1, md.digest())));
		System.out.println(String.format("%d ms", (endTime - startTime) / 1000000));
		// 5c5e83aa88182821a44b580de52996e15b508ada922d7b24ee31bcdb0e47658f
		// 38 ms
		// 5c5e83aa88182821a44b580de52996e15b508ada922d7b24ee31bcdb0e47658f
		// 51 ms
	}
}
