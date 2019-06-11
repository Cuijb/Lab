package com.cuijb.web.test.fdh;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by wzhang on 2017/9/7.
 */
@Slf4j
public class FDHDescription {
	/**
	 * [fstp版本(8bit)-len(16)-serviceId(24)-seq(16)-fileLen(32)-playTime(16)-extraTag(1)-zip(2)-reserved(5)]
	 */
	private static final int FDH_HEAD_LEN = 15;
	private static final int EX_SEND_INTERVAL_TYPE = 0x01;
	private static final int EX_SEND_INTERVAL_LEN = 2;
	private static final int EX_CONTENT_SEQ_TYPE = 0x02;
	private static final int EX_CONTENT_SEQ_LEN = 5;
	private static final int EX_CONTENT_REF_TYPE = 0x03;

	private int fstpVersion;
	private int headLen;
	private int serviceId;
	private int seq;
	private int fileLen;
	private int playTime;
	private boolean zip;

	/**
	 * 切片所属Content
	 */
	private String contentRef;

	/**
	 * 切片所属Content的开始序号
	 */
	private int contentStart;

	/**
	 * 前一个Content的结束序号。
	 */
	private int preContentEnd;

	public FDHDescription(int fstpVersion, int headLen, int serviceId, int seq, int fileLen, int playTime,
			boolean zip) {
		this.fstpVersion = fstpVersion;
		this.headLen = headLen;
		this.serviceId = serviceId;
		this.seq = seq;
		this.fileLen = fileLen;
		this.playTime = playTime;
		this.zip = zip;
	}

	public static FDHDescription parse(byte[] data, boolean withErr) {
		if (null == data) {
			log.info("data is null");
			return null;
		}
		if (data.length < FDH_HEAD_LEN) {
			log.info("data len is error to be {}", data.length);
			return null;
		}
		int fstp = (data[0] & 0xF0) >> 4;
		int headLen = ((data[0] & 0x0f) << 12) | ((data[1] & 0xFF) << 4) | ((data[2] & 0xF0) >> 4);
		if (data.length <= headLen) {
			log.info("data len is error to be :{} while headLen is:{}", data.length, headLen);
			return null;
		}
		int serviceId = ((data[2] & 0x0f) << 16) + HexUtil.byte2short(data, 3);
		int sequence = ((data[5] & 0x0f) << 16) + HexUtil.byte2short(data, 6);
		int fileLen = (int) HexUtil.byte2long(data, 8);
		if (data.length - headLen != fileLen) {
			log.info("fileLen is invalid:{} data len is:{} head len:", fileLen, data.length, headLen);
			if (!withErr) {
				return null;
			}
			fileLen = data.length - headLen;
		}
		int playTime = HexUtil.byte2short(data, 12);
		boolean hasEx = (((data[14] & 0x80) >>> 7) == 1);
		boolean isZip = (((data[14] & 0x60) >>> 5) == 1);
		FDHDescription fdh = new FDHDescription(fstp, headLen, serviceId, sequence, fileLen, playTime, isZip);

		// 扩展信息处理
		if (hasEx) {
			int offset = FDH_HEAD_LEN;
			boolean nextEx;
			do {
				nextEx = (((data[offset] & 0x80) >>> 7) == 1);
				int exType = (data[offset] & 0x7F);
				offset += 1;
				int exLen = HexUtil.byte2short(data, offset);
				offset += 2;
				byte[] exData = new byte[exLen];
				System.arraycopy(data, offset, exData, 0, exLen);
				offset += exLen;
				if (EX_SEND_INTERVAL_TYPE == exType) {
					log.info("fdh of seq:{} has extra type: 0x01", sequence);
					// 文件流信息
					if (EX_SEND_INTERVAL_LEN == exLen) {
						int distance = HexUtil.byte2short(exData);
						log.info("fdh of seq:{} send interval: {}", sequence, distance);
					} else {
						log.info("fdh of seq:{} has extra type: 0x01, length:{} != 2", sequence, exLen);
					}
				} else if (EX_CONTENT_SEQ_TYPE == exType) {
					log.info("fdh of seq:" + sequence + " has extra type: 0x02");
					// 碎片化起止分片号
					if (EX_CONTENT_SEQ_LEN == exLen) {
						fdh.setPreContentEnd(
								((exData[0] & 0xFF) << 12) | ((exData[1] & 0xFF) << 4) | ((exData[2] & 0xF0) >> 4));
						fdh.setContentStart(
								((exData[2] & 0x0F) << 16) | ((exData[3] & 0xFF) << 8) | (exData[4] & 0xFF));
						// fdh.setPreContentEnd(((exData[0] << 12) & 0x000FF000)
						// | ((exData[1] << 4) & 0x00000FF0)
						// | (((exData[2] & 0xF0) >> 4) & 0x0000000F));
						// fdh.setContentStart((((exData[2] & 0x0F) << 16) &
						// 0x000F0000) | ((exData[3] << 8) & 0x0000FF00)
						// | (exData[4] & 0x000000FF));
						log.info("fdh of seq:{} preEnd:{}, ctStart:{}", sequence, fdh.getPreContentEnd(),
								fdh.getContentStart());
					} else {
						log.info("fdh of seq:{} has extra type: 0x02, length:{} != 5", sequence, exLen);
					}
				} else if (EX_CONTENT_REF_TYPE == exType) {
					log.info("fdh of seq:{} has extra type: 0x03", sequence);
					// 碎片化内容标识
					fdh.setContentRef(new String(exData));
					log.info("fdh of seq:{} contentRef:{}", sequence, fdh.getContentRef());
				} else {
					log.info("fdh of seq:{} has unknown extra type: 0x{}", sequence, String.format("%02x", exType));
				}
			} while (nextEx);
		}
		return fdh;
	}

	public int getFstpVersion() {
		return fstpVersion;
	}

	public void setFstpVersion(int fstpVersion) {
		this.fstpVersion = fstpVersion;
	}

	public int getHeadLen() {
		return headLen;
	}

	public void setHeadLen(int headLen) {
		this.headLen = headLen;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getFileLen() {
		return fileLen;
	}

	public void setFileLen(int fileLen) {
		this.fileLen = fileLen;
	}

	public int getPlayTime() {
		return playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public boolean isZip() {
		return zip;
	}

	public void setZip(boolean zip) {
		this.zip = zip;
	}

	public String getContentRef() {
		return contentRef;
	}

	public void setContentRef(String contentRef) {
		this.contentRef = contentRef;
	}

	public int getContentStart() {
		return contentStart;
	}

	public void setContentStart(int contentStart) {
		this.contentStart = contentStart;
	}

	public int getPreContentEnd() {
		return preContentEnd;
	}

	public void setPreContentEnd(int preContentEnd) {
		this.preContentEnd = preContentEnd;
	}
}
