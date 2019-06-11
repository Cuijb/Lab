package com.cuijb.web.test.fdh;

import java.util.List;

/**
 * Created by wzhang on 2016/9/20.
 */
public class HexUtil {
	public static int byte2short(byte[] data) {
		return (((data[0] & 0xFF) << 8) & 0x0000FF00) | (data[1] & 0x000000FF);
	}

	public static int byte2short(byte[] data, int offset) {
		return (((data[offset] & 0xFF) << 8) & 0x0000FF00) | (data[offset + 1] & 0x000000FF);
	}

	public static long getLong(byte[] data, int offset, int len) {
		long ret = 0;
		for (int i = 0; i < len; i++) {
			ret = (ret << 8) | (data[offset + i] & 0xFF);
		}
		return ret;
	}

	public static long byte2long(byte[] data) {
		return ((((data[0] & 0xFF) << 24) & 0xFF000000) | (((data[1] & 0xFF) << 16) & 0x00FF0000)
				| (((data[2] & 0xFF) << 8) & 0x0000FF00) | ((data[3] & 0x000000FF))) & 0xFFFFFFFFL;
	}

	public static long byte2long(byte[] data, int offset) {
		return ((((data[offset] & 0xFF) << 24) & 0xFF000000) | (((data[offset + 1] & 0xFF) << 16) & 0x00FF0000)
				| (((data[offset + 2] & 0xFF) << 8) & 0x0000FF00) | ((data[offset + 3] & 0x000000FF))) & 0xFFFFFFFFL;
	}

	public static byte[] int2Byte(int value) {
		byte[] data = new byte[4];
		data[0] = (byte) ((value >>> 24) & 0xFF);
		data[1] = (byte) ((value >>> 16) & 0xFF);
		data[2] = (byte) ((value >>> 8) & 0xFF);
		data[3] = (byte) ((value) & 0xFF);
		return data;
	}

	public static byte[] listByteArray2ByteArray(List<byte[]> recvList) {
		int len = 0;
		for (byte[] ts : recvList) {
			len += ts.length;
		}
		byte[] blockData = new byte[len];
		int index = 0;
		for (byte[] ts : recvList) {
			System.arraycopy(ts, 0, blockData, index, ts.length);
			index += ts.length;
		}
		return blockData;
	}

	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}
}
