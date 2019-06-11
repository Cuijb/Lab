package com.cuijb.web.vo.report;

/**
 * Created by wzhang on 2017/6/2.
 */
public class BaseResponse {
	private int status;
	private String reason;

	public BaseResponse() {

	}

	public BaseResponse(int status) {
		this.status = status;
	}

	public BaseResponse(int status, String reason) {
		this.status = status;
		this.reason = reason;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
