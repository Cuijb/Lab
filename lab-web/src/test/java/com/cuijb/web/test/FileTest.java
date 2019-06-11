package com.cuijb.web.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileTest {

	@Test
	public void aaa() {
		long globalFileId = 268703891489L;
		int tsId = (int) ((globalFileId >> 28) & 0x0FFF);
		int issId = (int) ((globalFileId >> 20) & 0xff);
		int fileId = (int) (globalFileId & 0xFFFFF);
		log.info("{} -> {}-{}-{}", globalFileId, tsId, issId, fileId);
	}

	public List<File> childFiles(File dir) {
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (null != files) {
				return Arrays.asList(files);
			}
		}
		return new ArrayList<>();
	}

	public List<File> descendantFiles(File dir) {
		List<File> list = new ArrayList<>();

		if (dir.exists()) {
			if (dir.isFile()) {
				list.add(dir);
			} else {
				for (File child : childFiles(dir)) {
					list.addAll(descendantFiles(child));
				}
			}
		}

		return list;
	}

	@Test
	public void bbb() {
		for (File file : descendantFiles(new File("E:\\apk\\develop"))) {
			log.info("{}", file.getPath());
		}
	}

	// private static String bfp_raptor =
	// "E:\\Workspace\\Docs\\BFP\\X002_8909_v1.5_20170509_18_user.tar.gz";
	private static String bfp_raptor = "E:\\Workspace\\Docs\\BFP\\bfp.raptor";

	@Test
	public void ccc() {
		String raptorFilePath = bfp_raptor;
		log.info("begin to check digest");
		if (!checkDigest(raptorFilePath)) {
			log.error("digest is error");
			return;
		}
		log.info("check digest OK");
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(new File(raptorFilePath), "r");
			while (true) {
				byte tag = randomAccessFile.readByte();
				byte[] byLen = new byte[4];
				randomAccessFile.readFully(byLen);
				int len = (int) longWith4Byte(byLen);
				switch (tag) {
				case TAG_PLAYLIST:
					if (!copyPart(randomAccessFile, 0, len, playlistFileName())) {
						log.error("copy playlist file error");
						return;
					}
					log.info("copy playlist file ok");
					break;
				case TAG_DATA:
					String outputName = dataFileName();
					outputName = getOutFileNameFromPlaylist(playlistFileName());
					if (!outputName.equals(dataFileName())) {
						log.debug("data file contentName is not :" + dataFileName());
					}
					String outputFile = getBaseDir() + outputName;
					if (!copyPart(randomAccessFile, randomAccessFile.getFilePointer(), len, outputFile)) {
						log.error("copy data file error");
						return;
					}
					log.info("copy data file ok");
					return;
				default:
					log.warn("impossible the tag is:" + tag);
					break;
				}
			}
		} catch (Exception e) {
			log.error("handle raptor file error:" + e);
		} finally {
			if (null != randomAccessFile) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final byte TAG_PLAYLIST = (byte) 0xA0;
	private static final byte TAG_DATA = (byte) 0xA1;
	private static final byte TAG_ALG = (byte) 0xA2;
	private static final byte DIGEST_NO = 0;
	private static final byte DIGEST_MD5 = 1;
	private static final byte DIGEST_SHA1 = 2;
	private static final byte DIGEST_SHA256 = 3;

	private boolean checkDigest(String outputFile) {
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(new File(outputFile), "r");
			while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
				byte tag = randomAccessFile.readByte();
				String tagHex = "0x" + Integer.toHexString(tag & 0xFF).toUpperCase();
				if (tag == TAG_PLAYLIST || tag == TAG_DATA) {
					byte[] byLen = new byte[4];
					randomAccessFile.readFully(byLen);
					long len = longWith4Byte(byLen);
					randomAccessFile.skipBytes((int) len);
					log.info("tag:" + tagHex + " len:" + len);
				} else if (tag == TAG_ALG) {
					long digestDataLen = randomAccessFile.getFilePointer() - 1;
					byte algId = randomAccessFile.readByte();
					if (algId == DIGEST_NO) {
						log.info("no digest");
						return true;
					}
					String alg = algType(algId);
					if (null == alg) {
						return true;
					}
					int digestLen = randomAccessFile.readByte();
					if (digestLen == 0) {
						log.error("digest len is 0 when alg is:" + alg);
						return false;
					}
					log.info("tag:" + tagHex + " type:" + alg + " len:" + digestLen);
					byte[] digest = readPart(randomAccessFile, randomAccessFile.getFilePointer(), digestLen);
					byte[] calDigest = getDigest(randomAccessFile, 0, digestDataLen, alg);
					if (Arrays.equals(digest, calDigest)) {
						return true;
					}
					return false;
				} else {
					log.warn("tag:0x" + Integer.toHexString(tag & 0xFF).toUpperCase() + " invalid");
					return false;
				}
			}
			log.warn("length:" + randomAccessFile.length() + " invalid");
		} catch (Exception e) {
			log.error("check digest error:" + e);
		} finally {
			if (null != randomAccessFile) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private String dataFileName() {
		return getBaseDir() + "bfp.ts";
	}

	private String playlistFileName() {
		return getBaseDir() + "bfp.m3u8";
	}

	private String getBaseDir() {
		// TODO Auto-generated method stub
		return "E:\\Workspace\\Docs\\BFP\\";
	}

	private String algType(byte algId) {
		switch (algId) {
		case DIGEST_MD5:
			return "MD5";
		case DIGEST_SHA1:
			return "SHA-1";
		case DIGEST_SHA256:
			return "SHA-256";
		default:
			log.error("digest alg not support:" + algId);
			return null;
		}
	}

	public long longWith4Byte(byte[] data) {
		return ((((data[0] & 0xFF) << 24) & 0xFF000000) | (((data[1] & 0xFF) << 16) & 0x00FF0000)
				| (((data[2] & 0xFF) << 8) & 0x0000FF00) | ((data[3] & 0x000000FF))) & 0xFFFFFFFFL;
	}

	public byte[] getDigest(RandomAccessFile randomAccessFile, long offset, long length, String algorithm) {
		try {
			int onceReadMax = 1 * 1024 * 1024;
			randomAccessFile.seek(offset);
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] buffer = new byte[onceReadMax];
			while (randomAccessFile.getFilePointer() - offset < length) {
				int readLen = Math.min(onceReadMax, (int) (length - (randomAccessFile.getFilePointer() - offset)));
				int tempLen = randomAccessFile.read(buffer, 0, readLen);
				if (tempLen <= 0) {
					break;
				}
				md.update(buffer, 0, tempLen);
			}
			return md.digest();
		} catch (Exception e) {
			log.error("getDigest failed:" + e);
		}
		return null;
	}

	public String getOutFileNameFromPlaylist(String playlistFile) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(playlistFile);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				return line;
			}
		} catch (FileNotFoundException e) {
			log.error("read playlist File error:" + e);
		} catch (IOException e) {
			log.error("read playlist File error:" + e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Test
	public void ddd() {
		RandomAccessFile randomAccessFile = null;
		File raptorFile = new File(bfp_raptor);
		try {
			randomAccessFile = new RandomAccessFile(raptorFile, "rw");
			BfpMediaStruct mediaStruct = BfpMediaStruct.analyse("adsfasdfa", randomAccessFile);
			if (null == mediaStruct) {
				return;
			}
			byte[] digest = readPart(randomAccessFile, mediaStruct.getDigestOff(), mediaStruct.getDigestLen());
			byte[] calDigest = getDigest(randomAccessFile, 0, mediaStruct.getContentLen(), mediaStruct.getDigestType());
			if (!Arrays.equals(digest, calDigest)) {
				return;
			}
			copyPart(randomAccessFile, mediaStruct.getPlaylistOff(), mediaStruct.getPlaylistLen(), playlistFileName());
			retainPart(randomAccessFile, mediaStruct.getDataOff(), mediaStruct.getDataLen());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("readAndRemoveFirstLines error", e);
		} finally {
			try {
				if (randomAccessFile != null) {
					randomAccessFile.close();
				}
			} catch (IOException e) {
				log.error("close RandomAccessFile error", e);
			}
		}
		raptorFile.renameTo(new File(getBaseDir() + getOutFileNameFromPlaylist(playlistFileName())));
	}

	private byte[] readPart(RandomAccessFile randomAccessFile, long offset, int length) throws Exception {
		randomAccessFile.seek(offset);
		byte[] digest = new byte[length];
		randomAccessFile.read(digest);
		return digest;
	}

	private boolean copyPart(RandomAccessFile file, long offset, long length, String targetFile) throws Exception {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(targetFile);
			file.seek(offset);
			int onceReadMax = 1 * 1024 * 1024;
			byte[] buffer = new byte[onceReadMax];
			while (file.getFilePointer() - offset < length) {
				int readLen = Math.min(onceReadMax, (int) (length - (file.getFilePointer() - offset)));
				int tempLen = file.read(buffer, 0, readLen);
				if (tempLen <= 0) {
					break;
				}
				outputStream.write(buffer, 0, tempLen);
				if (file.getFilePointer() - offset < length) {
					try {
						Thread.sleep(50);// 每拷贝1M数据给IO让路50ms
					} catch (InterruptedException e) {
					}
				}
			}
			return file.getFilePointer() - offset >= length;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.getFD().sync();
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public boolean retainPart(RandomAccessFile file, long offset, long length) throws Exception {
		if (offset <= 0) {
			file.setLength(Math.max(0, length));
			return true;
		}

		long writePosition = 0; // Initial write position
		long readPosition = offset; // Shift the next read position
		int onceReadMax = 1 * 1024 * 1024;
		byte[] buffer = new byte[onceReadMax];
		do {
			file.seek(readPosition);
			int buffSize = Math.min(onceReadMax, (int) Math.min(offset, length - writePosition));
			int n = file.read(buffer, 0, buffSize);
			if (n <= 0) {
				break;
			}
			file.seek(writePosition);
			file.write(buffer, 0, n);
			writePosition += n;
			readPosition += n;
		} while (writePosition < length);

		file.setLength(writePosition);
		return file.length() >= length;
	}
}
