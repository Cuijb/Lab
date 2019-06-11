package com.cuijb.web.vo.report;

import java.util.ArrayList;
import java.util.List;

public class BootReport {
	private String rom;
	private String version;
	private long start;
	private long stop;
	private List<LiveReport> lives = new ArrayList<>();

	public void refresh(long time) {
		if (this.start <= 0) {
			this.start = time;
		}
		if (stop < start) {
			stop = start;
		}
	}

	public void fixed() {
		List<LiveReport> filtedLives = new ArrayList<>();
		for (LiveReport live : lives) {
			live.fixed();
			if (!live.empty()) {
				filtedLives.add(live);
			}
		}
		lives = filtedLives;
	}

	public boolean empty() {
		return lives.isEmpty();
	}

	public void add(LiveReport liveReport) {
		if (null == liveReport) {
			return;
		}
		lives.add(liveReport);
	}

	/**
	 * 频道多次启动的情况下，需要找最后一个
	 * 
	 * @param name
	 *            频道名称
	 * @param startTime
	 *            开始时间
	 * @return
	 */
	public LiveReport live(String name, long startTime) {
		for (int i = (lives.size() - 1); i >= 0; i--) {
			LiveReport live = lives.get(i);
			if (live.getName().equals(name)) {
				return live;
			}
		}

		LiveReport live = new LiveReport(name, startTime);
		lives.add(live);
		return live;
	}

	public String getRom() {
		return rom;
	}

	public void setRom(String rom) {
		this.rom = rom;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getStop() {
		return stop;
	}

	public void setStop(long stop) {
		this.stop = stop;
	}

	public List<LiveReport> getLives() {
		return lives;
	}

	public void setLives(List<LiveReport> lives) {
		this.lives = lives;
	}
}
