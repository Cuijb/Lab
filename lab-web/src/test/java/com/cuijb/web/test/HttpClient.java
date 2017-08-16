package com.cuijb.web.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClient {

	@Test
	public void uploadCSServerTest() throws Exception {
		String versionName = null;
		String filePath = null;

		// versionName = "1.1.0";
		// filePath = "E:\\apk\\update\\CSServer-1.1.0.apk";
		//
		versionName = "1.1.2";
		filePath = "E:\\apk\\update\\CSServer-1.1.2.apk";
		//
		// versionName = "1.1.3";
		// filePath = "E:\\apk\\update\\CSServer-1.1.3.apk";
		String digest = getDigest(filePath);

		String str = "http://192.168.43.1:8899/v1/upload/csserver?digest=" + digest + "&version=" + versionName;
		try {
			System.out.println(new Date().toLocaleString() + "post test");
			URL url = new URL(str);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("content-type", "text/html");
			BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());

			// 读取文件上传到服务器
			File file = new File(filePath);
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int numReadByte = 0;
			while ((numReadByte = fileInputStream.read(bytes, 0, 1024)) > 0) {
				out.write(bytes, 0, numReadByte);
			}

			out.flush();

			System.out.println(new Date().toLocaleString() + "post flush");
			fileInputStream.close();
			// 读取URLConnection的响应
			DataInputStream in = new DataInputStream(connection.getInputStream());
			System.out.println(new String(stream2Bytes(in), "UTF-8"));
			System.out.println(new Date().toLocaleString() + "post finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void uploadROMTest() throws Exception {
		String versionName = "1.0.71";
		String filePath = "E:\\rom-update\\update.zip";
		// filePath = "E:\\apk\\update\\CSServer-1.0.21.apk";
		String digest = getDigest(filePath);

		String str = "http://192.168.43.1:8899/v1/upload/mboxrom?digest=" + digest + "&version=" + versionName;
		try {
			System.out.println(new Date().toLocaleString() + "post test");
			URL url = new URL(str);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("content-type", "text/html");
			BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());

			// 读取文件上传到服务器
			File file = new File(filePath);
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int numReadByte = 0;
			while ((numReadByte = fileInputStream.read(bytes, 0, 1024)) > 0) {
				out.write(bytes, 0, numReadByte);
			}

			out.flush();

			System.out.println(new Date().toLocaleString() + "post flush");
			fileInputStream.close();
			// 读取URLConnection的响应
			DataInputStream in = new DataInputStream(connection.getInputStream());
			System.out.println(new String(stream2Bytes(in), "UTF-8"));
			System.out.println(new Date().toLocaleString() + "post finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把一个InputStream里面的内容转化成一个byte[]
	 *
	 * @param is
	 *            输入流
	 */
	private static byte[] stream2Bytes(InputStream is) {
		if (null == is) {
			return new byte[0];
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		try {
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
		} catch (IOException e) {
			System.out.println("HttpUtil stream2Bytes has Exception:" + e.toString());
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					System.out.println("HttpUtil stream2Bytes close InputStream has Exception:" + e.toString());
				}
			}
		}
		return bos.toByteArray();
	}

	private String getDigest(String filePath) {
		// int size = 1024 * 1024;
		// byte[] bytes = new byte[size];
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		if (null == md) {
			return null;
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
		System.out.println(String.format(filePath + ": %d ms", (endTime - startTime) / 1000000));
		return String.format("%14x", new java.math.BigInteger(1, md.digest()));
	}
}
