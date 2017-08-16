package com.cuijb.web.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClient2 {

	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 10000000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	private static final String CONTENT_TYPE = "multipart/form-data";
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";

	@Test
	public void uploadCSServerTest() throws Exception {

		String logName = "E:\\apk\\rebuild\\lband_log\\csServer.log.2017060509";
		String requestURL = "http://192.168.43.15:9000/csserver/log/868770001666577";
		File file = new File(logName);
		RequestParams paramList = new RequestParams();
		paramList.add("ownerId", "1410065922");
		paramList.add("docType", "jpg");
		paramList.add("sessionKey", "dfbe0e1686656d5a0c8de11347f93bb6");
		paramList.add("sig", "e70cff74f433ded54b014e7402cf094a");
		paramList.add("file", file);
		postFile(requestURL, paramList);
	}

	private String postFile(String postUrl, RequestParams params) {
		String boundary = UUID.randomUUID().toString();
		HttpURLConnection conn = null;
		BufferedReader br = null;
		try {
			URL url = new URL(postUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET);// 设置编码
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + "; boundary=" + boundary);
			OutputStream outputSteam = conn.getOutputStream();
			DataOutputStream dos = new DataOutputStream(outputSteam);
			params.write(dos, boundary);
			dos.flush();
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
			System.out.println("response code:" + res);
			if (res == 200) {
				String oneLine;
				StringBuffer response = new StringBuffer();
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((oneLine = br.readLine()) != null) {
					response.append(oneLine);
				}
				return response.toString();
			}

			return new ResponseMsg(res, "").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseMsg(-1, e.getClass().getSimpleName()).toString();
		} finally {
			// 统一释放资源
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private class RequestParams {
		private List<RequestParam> params = new ArrayList<>();
		private String fileKey;
		private File file;

		public void add(String name, String value) {
			params.add(new RequestParam(name, value));
		}

		public void add(String name, File file) {
			this.fileKey = name;
			this.file = file;
		}

		public void write(DataOutputStream dos, String boundary) {
			InputStream input = null;
			try {
				dos.write(toString(boundary).getBytes());
				if (null != file) {
					input = new FileInputStream(file);
					byte[] bytes = new byte[1024];
					int len = 0;
					while ((len = input.read(bytes)) != -1) {
						dos.write(bytes, 0, len);
					}
				} else {
					System.err.println("file not found");
				}
				dos.write((LINE_END + PREFIX + boundary + PREFIX + LINE_END).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != input) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private String toString(String boundary) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < params.size(); i++) {
				// 添加分割边界
				sb.append(PREFIX);
				sb.append(boundary);
				sb.append(LINE_END);

				sb.append("Content-Disposition: form-data; name=" + params.get(i).getName() + LINE_END);
				sb.append(LINE_END);
				sb.append(params.get(i).getValue());
				sb.append(LINE_END);
			}

			// file内容
			if (null != file) {
				fileKey = null == fileKey ? "file" : fileKey;
				sb.append(PREFIX);
				sb.append(boundary);
				sb.append(LINE_END);

				sb.append("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=" + "\"" + file.getName()
						+ "\"" + LINE_END);
				sb.append("Content-Type: " + CONTENT_TYPE + LINE_END);
				sb.append(LINE_END);
			}
			return sb.toString();
		}

		private class RequestParam {
			private String name;
			private String value;

			public RequestParam(String name, String value) {
				this.name = name;
				this.value = value;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}
		}
	}

	private class ResponseMsg {
		/**
		 * 状态码
		 */
		private int status;

		/**
		 * 状态说明
		 */
		private String reason;

		/**
		 * 时间戳
		 */
		private long timestamp;

		public ResponseMsg(int status, String reason) {
			this.status = status;
			this.reason = reason;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
	}
}
