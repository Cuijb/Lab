package com.cuijb.web.test;

import java.io.RandomAccessFile;

import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 * Created by Cuijb on 2019/3/25<br>
 */
@Slf4j
public class BfpMediaStruct {
	private static final byte TAG_PLAYLIST = (byte) 0xA0;
	private static final byte TAG_DATA = (byte) 0xA1;
	private static final byte TAG_ALG = (byte) 0xA2;
	private static final byte DIGEST_NO = (byte) 0;
	private static final byte DIGEST_MD5 = (byte) 1;
	private static final byte DIGEST_SHA1 = (byte) 2;
	private static final byte DIGEST_SHA256 = (byte) 3;

	private long playlistOff;
	private long playlistLen;

	private long contentLen;

	private long dataOff;
	private long dataLen;

	private byte digestType;
	private long digestOff;
	private int digestLen;

	public static BfpMediaStruct analyse(String desc, RandomAccessFile file) {
		BfpMediaStruct struct = new BfpMediaStruct();
		try {
			byte playlistTag = file.readByte();
			if (TAG_PLAYLIST != playlistTag) {
				return null;
			}
			byte[] playlistLA = new byte[4];
			file.readFully(playlistLA);
			long playlistLen = longWith4Byte(playlistLA);
			struct.setPlaylistOff(file.getFilePointer());
			struct.setPlaylistLen(playlistLen);
			file.skipBytes((int) playlistLen);
			log.info(desc + "tag:0xA0 len:" + playlistLen);

			byte dataTag = file.readByte();
			if (TAG_DATA != dataTag) {
				return null;
			}
			byte[] dataLA = new byte[4];
			file.readFully(dataLA);
			long dataLen = longWith4Byte(dataLA);
			struct.setDataOff(file.getFilePointer());
			struct.setDataLen(dataLen);
			file.skipBytes((int) dataLen);
			log.info(desc + "tag:0xA1 len:" + dataLen);

			struct.setContentLen(file.getFilePointer());

			byte digestTag = file.readByte();
			if (TAG_ALG != digestTag) {
				return null;
			}
			byte digestType = file.readByte();
			struct.setDigestType(digestType);
			if (digestType == DIGEST_NO) {
				log.info(desc + "no digest");
				return struct;
			}
			byte digestLen = file.readByte();
			struct.setDigestOff(file.getFilePointer());
			struct.setDigestLen(digestLen);
			log.info(desc + "tag:0xA2 type:" + struct.getDigestType() + " len:" + digestLen);

			return struct;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(desc + " exception: " + e);
		}
		return null;
	}

	public static long longWith4Byte(byte[] data) {
		return ((((data[0] & 0xFF) << 24) & 0xFF000000) | (((data[1] & 0xFF) << 16) & 0x00FF0000)
				| (((data[2] & 0xFF) << 8) & 0x0000FF00) | ((data[3] & 0x000000FF))) & 0xFFFFFFFFL;
	}

	public static String algType(byte algId) {
		switch (algId) {
		case DIGEST_MD5:
			return "MD5";
		case DIGEST_SHA1:
			return "SHA-1";
		case DIGEST_SHA256:
			return "SHA-256";
		default:
			return null;
		}
	}

	public long getPlaylistOff() {
		return playlistOff;
	}

	public void setPlaylistOff(long playlistOff) {
		this.playlistOff = playlistOff;
	}

	public long getPlaylistLen() {
		return playlistLen;
	}

	public void setPlaylistLen(long playlistLen) {
		this.playlistLen = playlistLen;
	}

	public long getDataOff() {
		return dataOff;
	}

	public void setDataOff(long dataOff) {
		this.dataOff = dataOff;
	}

	public long getDataLen() {
		return dataLen;
	}

	public void setDataLen(long dataLen) {
		this.dataLen = dataLen;
	}

	public long getContentLen() {
		return contentLen;
	}

	public void setContentLen(long contentLen) {
		this.contentLen = contentLen;
	}

	public String getDigestType() {
		switch (digestType) {
		case DIGEST_MD5:
			return "MD5";
		case DIGEST_SHA1:
			return "SHA-1";
		case DIGEST_SHA256:
			return "SHA-256";
		default:
			return null;
		}
	}

	public void setDigestType(byte digestType) {
		this.digestType = digestType;
	}

	public long getDigestOff() {
		return digestOff;
	}

	public void setDigestOff(long digestOff) {
		this.digestOff = digestOff;
	}

	public int getDigestLen() {
		return digestLen;
	}

	public void setDigestLen(int digestLen) {
		this.digestLen = digestLen;
	}
}
