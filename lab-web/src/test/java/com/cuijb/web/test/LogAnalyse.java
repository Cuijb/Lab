package com.cuijb.web.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogAnalyse {
	private static final String REG_TIME = "([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}).*?";
	private static final Pattern PATTERN_TIME = Pattern.compile("^" + REG_TIME);
	private static final Pattern PATTERN_MBOX = Pattern.compile(REG_TIME + "MBox");
	private static final Pattern PATTERN_BC_RECEIVE = Pattern.compile(REG_TIME + "bcRecvNumber increase ([0-9]+)");
	private static final Pattern PATTERN_DOWNLOAD = Pattern.compile(REG_TIME + "httpDownloadNumber increase ([0-9]+)");
	private static final Pattern PATTERN_UNNEED_D = Pattern.compile(REG_TIME + "notNeedHttpDownload increase ([0-9]+)");
	private static final Pattern PATTERN_PLAY_LIST = Pattern.compile(REG_TIME + "play list is");
	private static final Pattern PATTERN_PLAY_MISS = Pattern.compile(REG_TIME + "missNumber increase ([0-9]+)");
	private static final Pattern PATTERN_RESTART = Pattern.compile(REG_TIME + "will restart");
	private static final DateFormat DF_LOG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final DateFormat DF_SHOW = new SimpleDateFormat("MM-dd HH:mm");
	private static final NumberFormat NF_PERCENT = new DecimalFormat("#.##");

	// 开机时间 、关机时间、播放终止时间、有效时长、BC接收、4G下载、无效下载、丢包、补包率、无效补包率、有效补包率、丢包率、异常
	private static final String RESULT_FMT = "{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}";

	private Date startTime = null;
	private Date playTime = null;
	private Date endTime = null;
	private long bcr = 0;
	private long dld = 0;
	private long unNeedDld = 0;
	private long miss = 0;
	private List<String> errMsgs = new ArrayList<>();

	private void initParams() {
		startTime = null;
		playTime = null;
		endTime = null;
		bcr = 0;
		dld = 0;
		unNeedDld = 0;
		miss = 0;
		errMsgs.clear();
	}

	private void logAnalyse(File logFile) {
		if (!logFile.exists()) {
			log.warn("{} is not exist!", logFile.getName());
			return;
		}
		if (!logFile.getName().endsWith(".log")) {
			log.warn("{} has no suffix .log", logFile.getName());
			return;
		}
		initParams();
		log.info("Analyse {} start", logFile.getName());
		log.info(RESULT_FMT, "开机时间", "关机时间", "播放终止时间", "有效时长(h)", "BC接收", "4G下载", "无效下载", "丢包", "补包率(%)", "无效补包率(%)",
				"有效补包率(%)", "丢包率(%)", "异常");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(logFile));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				Matcher matcherBCR = PATTERN_BC_RECEIVE.matcher(s);
				if (matcherBCR.find()) {
					updatePlayTime(matcherBCR.group(1));
					bcr += new Long(matcherBCR.group(2));
				}

				Matcher matcherDLD = PATTERN_DOWNLOAD.matcher(s);
				if (matcherDLD.find()) {
					updatePlayTime(matcherDLD.group(1));
					dld += new Long(matcherDLD.group(2));
				}

				Matcher matcherUnNeed = PATTERN_UNNEED_D.matcher(s);
				if (matcherUnNeed.find()) {
					updatePlayTime(matcherUnNeed.group(1));
					unNeedDld += new Long(matcherUnNeed.group(2));
				}

				Matcher matcherList = PATTERN_PLAY_LIST.matcher(s);
				if (matcherList.find()) {
					updatePlayTime(matcherList.group(1));
				}

				Matcher matcherMiss = PATTERN_PLAY_MISS.matcher(s);
				if (matcherMiss.find()) {
					updatePlayTime(matcherMiss.group(1));
					miss += new Long(matcherMiss.group(2));
				}

				Matcher matcherRestart = PATTERN_RESTART.matcher(s);
				if (matcherRestart.find()) {
					errMsgs.add(DF_SHOW.format(DF_LOG.parse(matcherRestart.group(1))) + " 频道重启");
				}

				Matcher matcherMBox = PATTERN_MBOX.matcher(s);
				if (matcherMBox.find()) {
					if (null != startTime && null != playTime) {
						showResult(startTime, endTime, playTime, bcr, dld, unNeedDld, miss, errMsgs);
					}

					initParams();
					updatePlayTime(matcherMBox.group(1));
				}

				Matcher matcherTime = PATTERN_TIME.matcher(s);
				if (matcherTime.find()) {
					String dateTimeStr = matcherTime.group(1);
					endTime = DF_LOG.parse(dateTimeStr);
				}
			}
			showResult(startTime, endTime, playTime, bcr, dld, unNeedDld, miss, errMsgs);
			log.info("Analyse {} end{}", logFile.getName(), System.lineSeparator());
		} catch (Exception e) {
			log.error("analyse {} has exception: {}", logFile.getName(), e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("analyse {} close br has exception: {}", logFile.getName(), e);
				}
			}
		}
	}

	private void updatePlayTime(String dateTimeStr) throws ParseException {
		if (null == startTime) {
			startTime = DF_LOG.parse(dateTimeStr);
		}
		playTime = DF_LOG.parse(dateTimeStr);
	}

	private void showResult(Date startTime, Date endTime, Date playTime, long bcr, long dld, long unNeedDld, long miss,
			List<String> errMsgs) {
		// 开机时间、关机时间、播放终止时间、有效时长、BC接收、4G下载、无效下载、丢包、补包率、无效补包率、有效补包率、丢包率、异常
		String errMsgStr = "";
		for (String errMsg : errMsgs) {
			errMsgStr += errMsg + "; ";
		}
		log.info(RESULT_FMT, DF_SHOW.format(startTime), DF_SHOW.format(endTime), DF_SHOW.format(playTime),
				NF_PERCENT.format((playTime.getTime() - startTime.getTime()) / 1000.0 / 60 / 60), bcr, dld, unNeedDld,
				miss, (dld == 0 ? 0 : NF_PERCENT.format(100.0 * dld / (bcr + dld))),
				(dld == 0 ? 0 : NF_PERCENT.format(100.0 * unNeedDld / (bcr + dld))),
				(dld == 0 ? 0 : NF_PERCENT.format(100.0 * (dld - unNeedDld) / (bcr + dld))),
				(miss == 0 ? 0 : NF_PERCENT.format(100.0 * miss / (bcr + dld - unNeedDld + miss))), errMsgStr);
	}

	@Test
	public void test() {
		File logDir = new File("D:\\Downloads\\定点-1201~04-868770001666378.log");
		if (!logDir.exists()) {
			log.error("log dir({}) is not exists", logDir.getPath());
			return;
		}
		if (logDir.isFile()) {
			logAnalyse(logDir);
			return;
		}
		if (logDir.isDirectory()) {
			for (File file : logDir.listFiles()) {
				logAnalyse(file);
			}
			log.info("Analyse log file(s) end");
		}
	}
}
