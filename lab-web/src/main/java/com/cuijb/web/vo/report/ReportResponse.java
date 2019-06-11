package com.cuijb.web.vo.report;

import java.util.ArrayList;
import java.util.List;

public class ReportResponse extends BaseResponse {
	private List<BoxReport> boxs = new ArrayList<>();

	public List<BoxReport> getBoxs() {
		return boxs;
	}

	public void setBoxs(List<BoxReport> boxs) {
		this.boxs = boxs;
	}
}
