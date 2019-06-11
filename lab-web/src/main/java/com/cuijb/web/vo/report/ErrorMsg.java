package com.cuijb.web.vo.report;

public class ErrorMsg {
	private long timestramp;
	private String desc;
	private String value;

	public ErrorMsg() {
	}

	public ErrorMsg(long timestramp, String desc, String value) {
		this.timestramp = timestramp;
		this.desc = desc;
		this.value = value;
	}

	public long getTimestramp() {
		return timestramp;
	}

	public void setTimestramp(long timestramp) {
		this.timestramp = timestramp;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
