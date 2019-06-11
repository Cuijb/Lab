package com.cuijb.web.vo.report;

public class Continuity {
	private long count = 0;
	private long sum = 0;
	private long begin = 0;
	private long end = 0;
	private long duration = 0;

	public void plus(long begin, long end) {
		long duration = end - begin;
		count++;
		sum += duration;
		if (duration > this.duration) {
			this.begin = begin;
			this.end = end;
			this.duration = duration;
		}
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getSum() {
		return sum;
	}

	public void setSum(long sum) {
		this.sum = sum;
	}

	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
