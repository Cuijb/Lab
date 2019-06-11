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

import com.cuijb.web.vo.report.BootReport;
import com.cuijb.web.vo.report.BoxReport;
import com.cuijb.web.vo.report.ErrorMsg;
import com.cuijb.web.vo.report.LiveReport;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogAnalyse {
	private static final String REG_TIME = "([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}).*\\[(INFO|WARN|ERROR)\\] ";
	private static final Pattern PATTERN_TIME = Pattern.compile(REG_TIME);

	private static final Pattern PATTERN_MBOX_ROM = Pattern.compile(REG_TIME + "MBox ROM Version is: (.*)");
	private static final Pattern PATTERN_CSSERVER = Pattern.compile(REG_TIME + "CSServer version is: (.*)");

	private static final Pattern PATTERN_NETWORK = Pattern.compile(REG_TIME + "Network type: (\\d+)");
	private static final Pattern PATTERN_SATELLITE = Pattern.compile(REG_TIME + "Satellite status: (\\d+)");
	private static final Pattern PATTERN_LDPC = Pattern.compile(REG_TIME + "LDPC status: (\\d+) / (\\d+)");

	private static final String REG_CHANNEL = REG_TIME + "([1-9].* \\[.*?\\]|[1-9].*?) ";
	private static final Pattern PATTERN_CHANNEL = Pattern.compile(REG_CHANNEL);
	private static final Pattern PATTERN_CHANNEL_START = Pattern
			.compile(REG_CHANNEL + "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& CHANNEL START");
	private static final Pattern PATTERN_CHANNEL_RESTART = Pattern.compile(REG_CHANNEL + "will restart");
	private static final Pattern PATTERN_CHANNEL_STOP = Pattern
			.compile(REG_CHANNEL + "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& CHANNEL STOP");
	private static final Pattern PATTERN_CHANNEL_PUBLISH = Pattern
			.compile(REG_CHANNEL + "first publish m3u8 used time: (.*)ms");

	private static final Pattern PATTERN_BC_RECEIVE_V3 = Pattern
			.compile(REG_CHANNEL + "BC receive seq:(\\d+) OK.*dataLen is:(\\d+)");
	private static final Pattern PATTERN_BC_V2_N = Pattern.compile(REG_CHANNEL + "BC receive seq:(\\d+) OK$");
	private static final Pattern PATTERN_BC_V2_B = Pattern.compile(REG_CHANNEL + "BC data len is:(\\d+)$");
	private static final Pattern PATTERN_BC_UN = Pattern.compile(REG_CHANNEL + "from BC not needed seq:(\\d+)");
	private static final Pattern PATTERN_DLD_CT_V3 = Pattern
			.compile(REG_CHANNEL + "download seq:(\\d+) OK,using time:(\\d+).*bcSize:(\\d+),dldSize:(\\d+)");
	private static final Pattern PATTERN_DLD_UC_V3 = Pattern
			.compile(REG_CHANNEL + "download seq:(\\d+) OK,using time:(\\d+),dataLen:(\\d+)");
	private static final Pattern PATTERN_DLD_V2 = Pattern
			.compile(REG_CHANNEL + "download seq:(\\d+) size: (\\d+), using time:(\\d+)");
	private static final Pattern PATTERN_DLD_UN = Pattern.compile(REG_CHANNEL + "from 4G not needed seq:(\\d+)");
	private static final Pattern PATTERN_PLAY_LIST = Pattern.compile(REG_CHANNEL + "play list is: \\[(.*)\\]");
	private static final Pattern PATTERN_PLAY_SEQ = Pattern.compile("\\d+");
	private static final Pattern PATTERN_PLAY_AD = Pattern
			.compile(REG_CHANNEL + "(?:lost|drm error) seq:(\\d+), placeholder (.*)");
	private static final Pattern PATTERN_PLAY_MISS = Pattern
			.compile(REG_CHANNEL + "update LAST_PUBLISHED_SEQ\\(([1-9]\\d*)\\) with Seq:(\\d+)");

	private static final DateFormat DF_LOG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final DateFormat DF_SHOW = new SimpleDateFormat("MM-dd HH:mm");
	private static final NumberFormat NF_PERCENT = new DecimalFormat("#.##");

	private static final Pattern TEST_PATTERN = PATTERN_PLAY_MISS;

	private void logAnalyse(File logFile) {
		if (!logFile.exists()) {
			log.warn("{} is not exist!", logFile.getName());
			return;
		}
		if (!logFile.getName().endsWith(".log")) {
			log.warn("{} has no suffix .log", logFile.getName());
			return;
		}
		log.info("Analyse {} start", logFile.getName());
		List<String> logs = new ArrayList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(logFile));// 构造一个BufferedReader类来读取文件
			String line = null;
			while ((line = br.readLine()) != null) {// 使用readLine方法，一次读一行
				if (PATTERN_TIME.matcher(line).find()) {
					logs.add(line);
				}
			}
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
		logAnalyse(logFile.getName(), logs);
		log.info("Analyse {} end{}", logFile.getName(), System.lineSeparator());
	}

	private void logAnalyse(String id, List<String> logs) {
		BoxReport boxReport = new BoxReport(id);
		long preLogTime = 0;
		for (String line : logs) {
			try {
				// PATTERN_TIME
				Matcher timeM = PATTERN_TIME.matcher(line);
				if (timeM.find()) {
					long logTime = DF_LOG.parse(timeM.group(1)).getTime();
					preLogTime = logTime;
					// PATTERN_MBOX_ROM
					Matcher romM = PATTERN_MBOX_ROM.matcher(line);
					if (romM.find()) {
						boxReport.getErrors().add(new ErrorMsg(logTime, "ROM", romM.group(3)));
						BootReport liveReport = new BootReport();
						liveReport.refresh(logTime);
						boxReport.add(liveReport);
						continue;
					}
					BootReport bootReport = boxReport.boot(logTime);
					bootReport.refresh(logTime);
					// PATTERN_CSSERVER
					Matcher cssM = PATTERN_CSSERVER.matcher(line);
					if (cssM.find()) {
						boxReport.getErrors().add(new ErrorMsg(logTime, "CSServer Version", cssM.group(3)));
						bootReport.setVersion(cssM.group(3));
						continue;
					}
					// PATTERN_NETWORK
					Matcher netM = PATTERN_NETWORK.matcher(line);
					if (netM.find()) {
						boxReport.network(netM.group(3), logTime);
						continue;
					}
					// PATTERN_SATELLITE
					Matcher satM = PATTERN_SATELLITE.matcher(line);
					if (satM.find()) {
						boxReport.bc(satM.group(3), logTime);
						continue;
					}
					// PATTERN_LDPC
					Matcher ldpcM = PATTERN_LDPC.matcher(line);
					if (ldpcM.find()) {
						boxReport.ldpc(ldpcM.group(4), logTime);
						continue;
					}
					// PATTERN_CHANNEL
					Matcher channelM = PATTERN_CHANNEL.matcher(line);
					if (channelM.find()) {
						String name = channelM.group(3);

						// PATTERN_CHANNEL_START
						Matcher startM = PATTERN_CHANNEL_START.matcher(line);
						if (startM.find()) {
							LiveReport liveReport = new LiveReport(name, logTime);
							bootReport.add(liveReport);
							continue;
						}

						LiveReport liveReport = bootReport.live(name, logTime);
						liveReport.refresh(logTime);

						// PATTERN_CHANNEL_PUBLISH
						Matcher publishM = PATTERN_CHANNEL_PUBLISH.matcher(line);
						if (publishM.find()) {
							liveReport.setPublish(Long.valueOf(publishM.group(4)));
							continue;
						}
						// PATTERN_BC_RECEIVE_V3
						Matcher bcV3M = PATTERN_BC_RECEIVE_V3.matcher(line);
						if (bcV3M.find()) {
							liveReport.setBcN(liveReport.getBcN() + 1);
							liveReport.setBcB(liveReport.getBcB() + Long.valueOf(bcV3M.group(5)));
							continue;
						}
						// PATTERN_BC_V2_N
						Matcher bcV2NM = PATTERN_BC_V2_N.matcher(line);
						if (bcV2NM.find()) {
							liveReport.setBcN(liveReport.getBcN() + 1);
							continue;
						}
						// PATTERN_BC_V2_B
						Matcher bcV2BM = PATTERN_BC_V2_B.matcher(line);
						if (bcV2BM.find()) {
							liveReport.setBcB(liveReport.getBcB() + Long.valueOf(bcV2BM.group(4)));
							continue;
						}
						// PATTERN_BC_UN
						Matcher bcUnM = PATTERN_BC_UN.matcher(line);
						if (bcUnM.find()) {
							liveReport.setBcUn(liveReport.getBcUn() + 1);
							continue;
						}
						// PATTERN_DLD_CT_V3
						Matcher dldCtV3M = PATTERN_DLD_CT_V3.matcher(line);
						if (dldCtV3M.find()) {
							liveReport.setDldN(liveReport.getDldN() + 1);
							liveReport.setDldB(liveReport.getDldB() + Long.valueOf(dldCtV3M.group(7)));
							liveReport.setBcB(liveReport.getBcB() + Long.valueOf(dldCtV3M.group(6)));
							continue;
						}
						// PATTERN_DLD_UC_V3
						Matcher dldUcV3M = PATTERN_DLD_UC_V3.matcher(line);
						if (dldUcV3M.find()) {
							liveReport.setDldN(liveReport.getDldN() + 1);
							liveReport.setDldB(liveReport.getDldB() + Long.valueOf(dldUcV3M.group(6)));
							continue;
						}
						// PATTERN_DLD_V2
						Matcher dldV2M = PATTERN_DLD_V2.matcher(line);
						if (dldV2M.find()) {
							liveReport.setDldN(liveReport.getDldN() + 1);
							liveReport.setDldB(liveReport.getDldB() + Long.valueOf(dldV2M.group(5)));
							continue;
						}
						// PATTERN_DLD_UN
						Matcher dldUnM = PATTERN_DLD_UN.matcher(line);
						if (dldUnM.find()) {
							liveReport.setDldUn(liveReport.getDldUn() + 1);
							continue;
						}
						// PATTERN_PLAY_LIST
						Matcher playlistM = PATTERN_PLAY_LIST.matcher(line);
						if (playlistM.find()) {
							String[] seqAry = playlistM.group(4).split(", ");
							for (String seqStr : seqAry) {
								if (PATTERN_PLAY_SEQ.matcher(seqStr).find()) {
									liveReport.addSeq(Long.valueOf(seqStr));
								}
							}
							continue;
						}
						// PATTERN_PLAY_AD
						Matcher adM = PATTERN_PLAY_AD.matcher(line);
						if (adM.find()) {
							liveReport.setAdN(liveReport.getAdN() + 1);
							continue;
						}
						// PATTERN_PLAY_MISS
						Matcher missM = PATTERN_PLAY_MISS.matcher(line);
						if (missM.find()) {
							long miss = Long.valueOf(missM.group(5)) - Long.valueOf(missM.group(4)) - 1;
							if (miss > 0) {
								liveReport.setMissN(liveReport.getMissN() + miss);
							}
							continue;
						}
						// PATTERN_CHANNEL_RESTART
						Matcher restartM = PATTERN_CHANNEL_RESTART.matcher(line);
						if (restartM.find()) {
							boxReport.getErrors().add(new ErrorMsg(logTime, "Restart", restartM.group(3)));
							continue;
						}
					}
				}
			} catch (Exception e) {
				log.error("analyse has exception: {}", line);
				log.error("analyse has exception: {}", e);
			}
		}
		boxReport.fixed(preLogTime);
		log.info("box:{} report:{}", id, new GsonBuilder().setPrettyPrinting().create().toJson(boxReport));
		// log.info("box:{} report:{}", id, new Gson().toJson(boxReport));
	}

	@Test
	public void test() {
		File logDir = new File("D:\\Downloads\\report\\3-868770001666261-2018-07-11-3.log");
		// File logDir = new
		// File("D:\\Downloads\\report\\866828029988752-2018-07-08.log");
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

	@Test
	public void aaa() {
		Pattern PT = Pattern.compile("(/.*?)\\s+(.*?)\\s+(.*?)\\s+(.*?)\\s+(.*)");
		List<String> lines = new ArrayList<>();
		lines.add("/dev                   444.9M    60.0K   444.8M   4096");
		lines.add("/sys/fs/cgroup         444.9M    12.0K   444.8M   4096");
		lines.add("/mnt/asec              444.9M     0.0K   444.9M   4096");
		lines.add("/mnt/obb               444.9M     0.0K   444.9M   4096");
		lines.add("/system                991.9M   568.9M   423.0M   4096");
		lines.add("/data                    4.9G     3.5G     1.5G   4096");
		lines.add("/cache                 484.3M   404.0K   483.9M   4096");
		lines.add("/persist                27.5M   184.0K    27.3M   4096");
		lines.add("/firmware               64.0M    47.5M    16.5M   16384");
		lines.add("/storage/emulated      444.9M     0.0K   444.9M   4096");
		lines.add("/storage/sdcard1         1.9G    35.4M     1.8G   4096");
		lines.add("/storage/emulated/0      4.8G     3.5G     1.4G   4096");
		lines.add("/storage/emulated/legacy     4.8G     3.5G     1.4G   4096");
		for (String line : lines) {
			Matcher matcher = PT.matcher(line);
			if (matcher.find()) {
				log.info("Filesystem: {} Size:{}, Used:{}, Free:{}, Blksize:{}", matcher.group(1), matcher.group(2),
						matcher.group(3), matcher.group(4), matcher.group(5));
			} else {
				log.warn("line not matchs: {}", line);
			}
		}
	}

	@Test
	public void bbbb() {

		DateFormat dayHourFmt = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date date = dayHourFmt.parse("20170501000000");
			System.out.println(date.toString() + "  long time: " + date.getTime());
		} catch (ParseException e) {
		}
	}
}
