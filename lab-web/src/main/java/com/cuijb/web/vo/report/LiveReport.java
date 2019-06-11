package com.cuijb.web.vo.report;

import java.util.HashSet;
import java.util.Set;

public class LiveReport {
	private String name;
	private long start;
	private long stop;
	private long publish;
	private long bcN;
	private long bcB;
	private long bcUn;
	private long dldN;
	private long dldB;
	private long dldUn;
	private long playN;
	private long adN;
	private long missN;

	private Set<Long> seqSet = new HashSet<>();

	public LiveReport() {
	}

	public void fixed() {
		seqSet.clear();
	}

	public LiveReport(String name, long time) {
		this.name = name;
		refresh(time);
	}

	public void refresh(long time) {
		if (this.start <= 0) {
			this.start = time;
		}
		if (stop < start) {
			stop = start;
		}
	}

	public void addSeq(Long sequence) {
		if (!seqSet.contains(sequence)) {
			playN++;
			seqSet.add(sequence);
		}
	}

	public boolean empty() {
		return bcN <= 0 && dldN <= 0 && adN <= 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public long getPublish() {
		return publish;
	}

	public void setPublish(long publish) {
		this.publish = publish;
	}

	public long getBcN() {
		return bcN;
	}

	public void setBcN(long bcN) {
		this.bcN = bcN;
	}

	public long getBcB() {
		return bcB;
	}

	public void setBcB(long bcB) {
		this.bcB = bcB;
	}

	public long getBcUn() {
		return bcUn;
	}

	public void setBcUn(long bcUn) {
		this.bcUn = bcUn;
	}

	public long getDldN() {
		return dldN;
	}

	public void setDldN(long dldN) {
		this.dldN = dldN;
	}

	public long getDldB() {
		return dldB;
	}

	public void setDldB(long dldB) {
		this.dldB = dldB;
	}

	public long getDldUn() {
		return dldUn;
	}

	public void setDldUn(long dldUn) {
		this.dldUn = dldUn;
	}

	public long getPlayN() {
		return playN;
	}

	public void setPlayN(long playN) {
		this.playN = playN;
	}

	public long getAdN() {
		return adN;
	}

	public void setAdN(long adN) {
		this.adN = adN;
	}

	public long getMissN() {
		return missN;
	}

	public void setMissN(long missN) {
		this.missN = missN;
	}
}
