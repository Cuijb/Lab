$(document).ready(function() {
	var REG_TIME = "([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}).*\\[(DEBUG|INFO|WARN|ERROR)\\] ";
	var REG_CHANNEL = "(.*\\]|\\d+)"
	var PATTERN_TIME_P = new RegExp("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})\\.([0-9]{3})");
	var PATTERN_TIME = new RegExp(REG_TIME);
	var PATTERN_MBOX = new RegExp(REG_TIME + "MBox ROM Version is: (.*)");
	var PATTERN_CSSERVER = new RegExp(REG_TIME + "CSServer version is: (.*)");
	var PATTERN_NETWORK = new RegExp(REG_TIME + "Network type: (\\d+)");
	var PATTERN_BROADCAST = new RegExp(REG_TIME + "Satellite status: (\\d+)");
	var PATTERN_LDPC = new RegExp(REG_TIME + "LDPC status: (\\d+) / (\\d+)");
	var PATTERN_CHANNEL = new RegExp(REG_TIME + REG_CHANNEL);
	var PATTERN_CHANNEL_START = new RegExp(REG_TIME + REG_CHANNEL + " will start");
	var PATTERN_PUBLISH_COST = new RegExp(REG_TIME + REG_CHANNEL + " first publish m3u8 used time: (.*)ms");
	var PATTERN_BC_NUM = new RegExp(REG_TIME + REG_CHANNEL + " bcRecvNumber increase ([0-9]+)");
	var PATTERN_BC_BYTES = new RegExp(REG_TIME + REG_CHANNEL + " bcRecvBytes increase ([0-9]+)");
	var PATTERN_DLD_NUM = new RegExp(REG_TIME + REG_CHANNEL + " httpDownloadNumber increase ([0-9]+)");
	var PATTERN_DLD_BYTES = new RegExp(REG_TIME + REG_CHANNEL + " httpRecvBytes increase ([0-9]+)");
	var PATTERN_UNNEED_BC = new RegExp(REG_TIME + REG_CHANNEL + " notNeedBcReceive increase ([0-9]+)");
	var PATTERN_UNNEED_D = new RegExp(REG_TIME + REG_CHANNEL + " notNeedHttpDownload increase ([0-9]+)");
	var PATTERN_PLAY_LIST = new RegExp(REG_TIME + REG_CHANNEL + " play list is");
	var PATTERN_PLAY_PLAY = new RegExp(REG_TIME + REG_CHANNEL + " playNumber increase ([0-9]+)");
	var PATTERN_PLAY_AD = new RegExp(REG_TIME + REG_CHANNEL + " adNumber increase ([0-9]+)");
	var PATTERN_PLAY_MISS = new RegExp(REG_TIME + REG_CHANNEL + " missNumber increase ([0-9]+)");
	var PATTERN_NOT_NEED = new RegExp(REG_TIME + REG_CHANNEL + " from (BC|4G) (not needed|duplicate) seq:(\\d+)");
	var PATTERN_CHANNEL_RESTART = new RegExp(REG_TIME + REG_CHANNEL + " will restart");
	var PATTERN_CHANNEL_STOP = new RegExp(REG_TIME + REG_CHANNEL + " will stop");
	var DF_TIME = "yyyy-MM-dd HH24:mm:ss.SSS";
	var DF_SHOW = "MM-dd HH:mm:ss";
	var NF_PERCENT = "#.##";
	var $fileProgress = $("#fileProgress");
	var $liveTable = $("#liveTable tbody");
	var $sysTable = $("#systemTable tbody");
	var $bcTable = $("#bcTable tbody");
	var $netTable = $("#netTable tbody");
	var $ldpcTable = $("#ldpcTable tbody");
	var $logList = $("ul#logList");
	var NetStatus = {
		"0" : "无网",
		"1" : "网络断开",
		"2" : "以太网",
		"3" : "WiFi",
		"4" : "2G",
		"5" : "3G",
		"6" : "4G",
	};
	Date.prototype.format = function (fmt) {
		if (!fmt) {
			fmt = DF_SHOW;
		}
	    var o = {
		    "y+": this.getYear(), // 月份
	        "M+": this.getMonth() + 1, // 月份
	        "d+": this.getDate(), // 日
	        "H+": this.getHours(), // 小时
	        "m+": this.getMinutes(), // 分
	        "s+": this.getSeconds(), // 秒
	        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
	        "S": this.getMilliseconds() // 毫秒
	    };
	    if (/(y+)/.test(fmt)){
	    	fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    }
	    for (var k in o){
		    if (new RegExp("(" + k + ")").test(fmt)) {
		    	fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		    }
	    }
	    return fmt;
	}

    var showTime = function(duration) {
        duration = parseInt(duration / 1000);
        if (duration <= 0) {
            return "0\""
        }
        var seconds = 0, minutes = 0, hours = 0;
        seconds = duration % 60;
        if (duration >= 60) {
            duration = parseInt(duration / 60);

            var minutes = duration % 60;
            if (duration >= 60) {
                hours = parseInt(duration / 60);
            }
        }
        return (!!hours ? (hours + ":") : "") + (!!minutes ? (minutes + "'") : "") + (seconds + "\"");
    };

    var parseDate = function(timeStr) {
		var mct = timeStr.match(PATTERN_TIME_P);
		if (mct) {
			return new Date(mct[1], parseInt(mct[2]) - 1, mct[3], mct[4], mct[5], mct[6]);
		}
		return null;
	};

	function Counter() {
		this.startTime = null;
		this.endTime = null;
		this.netST = null;
		this.netVal = null;
		this.bcST = null;
		this.bcVal = null;
		this.ldpcST = null;
		this.ldpcVal = null;
		this.publishCost = null;
		this.bcN = 0;
		this.bcB = 0;
		this.dldN = 0;
		this.dldB = 0;
		this.unNeedBc = 0;
		this.unNeedDld = 0;
		this.playN = 0;
		this.adN = 0;
		this.missN = 0;
		this.errMsgs = [];
		this.reset = function(){
			if (!!this.endTime) {
				if (!!this.netST) {
					appendNetInfo(this.netST, this.endTime, this.netVal);
				}
				if (!!this.bcST) {
					appendBcInfo(this.bcST, this.endTime, 1 == parseInt(this.bcVal) ? "锁定" : "失锁");
				}
				if (!!this.ldpcST) {
					appendLdpcInfo(this.ldpcST, this.endTime, this.ldpcVal);
				}
			}
			this.startTime = null;
			this.endTime = null;
			this.netST = null;
			this.netVal = null;
			this.bcST = null;
			this.bcVal = null;
			this.ldpcST = null;
			this.ldpcVal = null;
			this.publishCost = null;
			this.bcN = 0;
			this.bcB = 0;
			this.dldN = 0;
			this.dldB = 0;
			this.unNeedBc = 0;
			this.unNeedDld = 0;
			this.playN = 0;
			this.adN = 0;
			this.missN = 0;
			this.errMsgs = [];
		};
		this.updatePT = function(timeStr) {
			this.endTime = timeStr;
			if (!this.startTime) {
				this.startTime = this.endTime;
			}
		};
		this.updateNT = function(timeStr, value) {
			if (!this.netST) {
				this.netST = timeStr;
				this.netVal = value;
			} else {
				if (parseInt(this.netVal) != parseInt(value)) {
					appendNetInfo(this.netST, timeStr, this.netVal);
					this.netST = timeStr;
					this.netVal = value;
				}
			}
		};
		this.updateBT = function(timeStr, value) {
			if (!this.bcST) {
				this.bcST = timeStr;
				this.bcVal = value;
			} else {
				if (parseInt(this.bcVal) != parseInt(value)) {
					appendBcInfo(this.bcST, timeStr, 1 == parseInt(this.bcVal) ? "锁定" : "失锁");
					this.bcST = timeStr;
					this.bcVal = value;
				}
			}
		};
		this.updateLDPCT = function(timeStr, value) {
			if (!this.ldpcST) {
				this.ldpcST = timeStr;
				this.ldpcVal = value;
			} else {
				if (parseInt(this.ldpcVal) != parseInt(value)) {
					appendLdpcInfo(this.ldpcST, timeStr, this.ldpcVal);
					this.ldpcST = timeStr;
					this.ldpcVal = value;
				}
			}
		};
		this.isEmpty = function(){
			return this.bcN <= 0 && this.dldN <= 0;
		};
		this.getSET = function(){
			if (!this.startTime) {
				return "unknown";
			}
			if (!this.endTime) {
				return "unknown";
			}

			var startStr = parseDate(this.startTime).format();
			var endStr = parseDate(this.endTime).format().replace(startStr.substring(0, 6), "");
			return startStr + " ~ " + endStr
		};
		this.getPT = function(){
			if (!this.startTime) {
				return "unknown";
			}
			if (!this.endTime) {
				return "unknown";
			}
			return showTime(parseDate(this.endTime).getTime() - parseDate(this.startTime).getTime());
		};
		this.getPC = function(){
			if (!this.publishCost) {
				return "";
			}
			return !this.publishCost ? "unknown" : this.publishCost;
		};
		this.getDNP = function(){
			if (this.dldN == 0) {
				return 0;
			}
			return (100.0 * this.dldN / (this.bcN + this.dldN)).toFixed(2);
		};
		this.getDBP = function(){
			if (this.dldB == 0) {
				return 0;
			}
			return (100.0 * this.dldB / (this.bcB + this.dldB)).toFixed(2);
		};
		this.getUNP = function(){
			if (this.unNeedDld == 0) {
				return 0;
			}
			return (100.0 * this.unNeedDld / (this.bcN + this.dldN)).toFixed(2);
		};
		this.getNP = function(){
			if ((this.dldN - this.unNeedDld) == 0) {
				return 0;
			}
			return (100.0 * (this.dldN - this.unNeedDld) / (this.bcN + this.dldN)).toFixed(2);
		};
		this.getMP = function(){
			if (this.missN == 0) {
				return 0;
			}
			return (100.0 * this.missN / (this.bcN + this.dldN - this.unNeedDld + this.missN)).toFixed(2);
		};
	};
	var step = 1024 * 1024; // 每次读取1M
	var result = "";
	var loaded = 0;
	var times = 0;
	var channel = {};
	var count = new Counter();
	var reader;
	if (window.FileReader) {
		console.log("FileReader supported by your browser!");
		reader = new FileReader();

		// 读取开始时触发
		reader.onloadstart = function(e) {
			//console.log("onloadstart!", e);
		};

		// 读取中
		reader.onprogress = function(e) {
			//console.log("onprogress!", e);
	        $fileProgress.val((e.loaded / e.total) * 100);
		};

		// 文件读取成功完成时触发
		reader.onload = function(e) {
			//console.log("onload!", e);
			logAnalyze(this.result.split("\n"));
		};

		// 读取完成触发，无论成功或失败
		reader.onloadend = function(e) {
			//console.log("onloadend!", e);
		};

		// 中断时触发
		reader.onabort = function(e) {
			//console.log("onabort!", e);
		};

		// 出错时触发
		reader.onerror = function(e) {
			//console.log("onerror!", e);
		};
	} else {
		console.log("FileReader not supported by your browser!");
	}
	var appendSystemInfo = function(time, desc, value) {
		$sysTable.append($("<tr>")
				.append($("<td>").text(parseDate(time).format()))
				.append($("<td>").text(desc))
				.append($("<td>").text(value)));
	};
	var appendNetInfo = function(start, end, value) {
		var sd = parseDate(start);
		var ed = parseDate(end);
		var startStr = sd.format();
		var endStr = ed.format().replace(startStr.substring(0, 6), "");
		$netTable.append($("<tr>")
				.append($("<td>").text(startStr + " ~ " + endStr))
				.append($("<td>").text(showTime(ed.getTime() - sd.getTime())))
				.append($("<td>").text(value + " -- " + NetStatus[value])));
	};
	var appendBcInfo = function(start, end, value) {
		var sd = parseDate(start);
		var ed = parseDate(end);
		var startStr = sd.format();
		var endStr = ed.format().replace(startStr.substring(0, 6), "");
		$bcTable.append($("<tr>")
				.append($("<td>").text(startStr + " ~ " + endStr))
				.append($("<td>").text(showTime(ed.getTime() - sd.getTime())))
				.append($("<td>").text(value)));
	};
	var appendLdpcInfo = function(start, end, value) {
		var sd = parseDate(start);
		var ed = parseDate(end);
		var startStr = sd.format();
		var endStr = ed.format().replace(startStr.substring(0, 6), "");
		$ldpcTable.append($("<tr>")
				.append($("<td>").text(startStr + " ~ " + endStr))
				.append($("<td>").text(showTime(ed.getTime() - sd.getTime())))
				.append($("<td>").text(value)));
	};
	var appendLogList = function(index, line) {
		$logList.append($("<li>").addClass("list-group-item").text(index + " - " + line));
	};
	var appendResult = function(desc, count) {
    	if (count.isEmpty()) {
    		return;
    	}
        $liveTable.append($("<tr>").addClass("ALL" == desc ? "info" : "success")
			.append($("<td>").text("ALL" == desc ? "总计" : desc))
			.append($("<td>").text(count.getSET()))
			.append($("<td>").text(count.getPT()))
			.append($("<td>").text(count.getPC()))
			.append($("<td>").text(count.bcN))
			.append($("<td>").text(count.bcB))
			.append($("<td>").text(count.dldN))
			.append($("<td>").text(count.dldB))
			.append($("<td>").text(count.unNeedBc))
			.append($("<td>").text(count.unNeedDld))
			.append($("<td>").text(count.playN))
			.append($("<td>").text(count.adN))
			.append($("<td>").text(count.missN))
			.append($("<td>").text(count.getDBP()))
			.append($("<td>").text(count.getDNP()))
			.append($("<td>").text(count.getUNP()))
			.append($("<td>").text(count.getNP()))
			.append($("<td>").text(count.getMP())));
    };
    var showResult = function() {
		$.each(channel, function(channel, count){
			appendResult(channel, count);
		});
		appendResult("ALL", count);
		if ($liveTable.children().length) {
			$liveTable.append($("<tr>"));
		}
	};
	var logAnalyze = function(lines) {
		$liveTable.empty();
		$sysTable.empty();
		$netTable.empty();
		$bcTable.empty();
		$ldpcTable.empty();
        $logList.empty();
        for (var index in lines) {
        	var line = lines[index];
			var mcMBox = line.match(PATTERN_MBOX);
			if (mcMBox) {
				showResult();
	            count.reset();
		        channel = {};
				count.updatePT(mcMBox[1]);
				appendSystemInfo(mcMBox[1], "MBox ROM", mcMBox[3]);
			}

			var mcTime = line.match(PATTERN_TIME);
			if (mcTime) {
				count.updatePT(mcTime[1]);
			}

			var mcCSS = line.match(PATTERN_CSSERVER);
			if (mcCSS) {
				appendSystemInfo(mcCSS[1], "CSServer", mcCSS[3]);
			}

			var mcNW = line.match(PATTERN_NETWORK);
			if (mcNW) {
				count.updateNT(mcNW[1], mcNW[3]);
			}

			var mcBC = line.match(PATTERN_BROADCAST);
			if (mcBC) {
				count.updateBT(mcBC[1], mcBC[3]);
			}

			var mcLDPC = line.match(PATTERN_LDPC);
			if (mcLDPC) {
				count.updateLDPCT(mcLDPC[1], mcLDPC[4]);
			}

			var mcChannel = line.match(PATTERN_CHANNEL);
			if (mcChannel) {
				count.updatePT(mcChannel[1]);
				if (!channel[mcChannel[3]]){
					channel[mcChannel[3]] = new Counter();
				}
				channel[mcChannel[3]].updatePT(mcChannel[1]);
			}

			var mcCS = line.match(PATTERN_CHANNEL_START);
			if (mcCS) {
				appendResult(mcCS[3], channel[mcCS[3]]);
				channel[mcCS[3]].reset();
				channel[mcCS[3]].updatePT(mcCS[1]);
			}

			var mcPublish = line.match(PATTERN_PUBLISH_COST);
			if (mcPublish) {
				channel[mcPublish[3]].updatePT(mcPublish[1]);
				channel[mcPublish[3]].publishCost = parseInt(mcPublish[4]);
			}

			var mcBCN = line.match(PATTERN_BC_NUM);
			if (mcBCN) {
				count.updatePT(mcBCN[1]);
				count.bcN += parseInt(mcBCN[4]);
				channel[mcBCN[3]].updatePT(mcBCN[1]);
				channel[mcBCN[3]].bcN += parseInt(mcBCN[4]);
			}

			var mcBCB = line.match(PATTERN_BC_BYTES);
			if (mcBCB) {
				count.updatePT(mcBCB[1]);
				count.bcB += parseInt(mcBCB[4]);
				channel[mcBCB[3]].updatePT(mcBCB[1]);
				channel[mcBCB[3]].bcB += parseInt(mcBCB[4]);
			}

			var mcDLDN = line.match(PATTERN_DLD_NUM);
			if (mcDLDN) {
				count.updatePT(mcDLDN[1]);
				count.dldN += parseInt(mcDLDN[4]);
				channel[mcDLDN[3]].updatePT(mcDLDN[1]);
				channel[mcDLDN[3]].dldN += parseInt(mcDLDN[4]);
			}

			var mcDLDB = line.match(PATTERN_DLD_BYTES);
			if (mcDLDB) {
				count.updatePT(mcDLDB[1]);
				count.dldB += parseInt(mcDLDB[4]);
				channel[mcDLDB[3]].updatePT(mcDLDB[1]);
				channel[mcDLDB[3]].dldB += parseInt(mcDLDB[4]);
			}

			var mcNN = line.match(PATTERN_NOT_NEED);
			if (mcNN) {
				appendLogList(index, line);
			}

			var mcUnNeedBC = line.match(PATTERN_UNNEED_BC);
			if (mcUnNeedBC) {
				count.updatePT(mcUnNeedBC[1]);
				count.unNeedBc += parseInt(mcUnNeedBC[4]);
				channel[mcUnNeedBC[3]].updatePT(mcUnNeedBC[1]);
				channel[mcUnNeedBC[3]].unNeedBc += parseInt(mcUnNeedBC[4]);
			}

			var mcUnNeed = line.match(PATTERN_UNNEED_D);
			if (mcUnNeed) {
				count.updatePT(mcUnNeed[1]);
				count.unNeedDld += parseInt(mcUnNeed[4]);
				channel[mcUnNeed[3]].updatePT(mcUnNeed[1]);
				channel[mcUnNeed[3]].unNeedDld += parseInt(mcUnNeed[4]);
			}

			var mcList = line.match(PATTERN_PLAY_LIST);
			if (mcList) {
				count.updatePT(mcList[1]);
				channel[mcList[3]].updatePT(mcList[1]);
			}

			var mcPlay = line.match(PATTERN_PLAY_PLAY);
			if (mcPlay) {
				count.updatePT(mcPlay[1]);
				count.playN += parseInt(mcPlay[4]);
				channel[mcPlay[3]].updatePT(mcPlay[1]);
				channel[mcPlay[3]].playN += parseInt(mcPlay[4]);
			}

			var mcAd = line.match(PATTERN_PLAY_AD);
			if (mcAd) {
				count.updatePT(mcAd[1]);
				count.adN += parseInt(mcAd[4]);
				channel[mcAd[3]].updatePT(mcAd[1]);
				channel[mcAd[3]].adN += parseInt(mcAd[4]);
			}

			var mcMiss = line.match(PATTERN_PLAY_MISS);
			if (mcMiss) {
				count.updatePT(mcMiss[1]);
				count.missN += parseInt(mcMiss[4]);
				channel[mcMiss[3]].updatePT(mcMiss[1]);
				channel[mcMiss[3]].missN += parseInt(mcMiss[4]);
			}

			var mcCR = line.match(PATTERN_CHANNEL_RESTART);
			if (mcCR) {
				channel[mcCR[3]].updatePT(mcCR[1]);
				channel[mcCR[3]].errMsgs.push(parseDate(mcCR[1]).format() + " 频道重启");
				appendSystemInfo(mcCR[1], "重启", mcCR[3]);
			}

			var mcCT = line.match(PATTERN_CHANNEL_STOP);
			if (mcCT) {
				channel[mcCT[3]].updatePT(mcCT[1]);
				appendResult(mcCT[3], channel[mcCT[3]]);
				channel[mcCT[3]].reset();
			}
		}
		showResult();
		count.reset();
        channel = {};
        $fileProgress.val(100);
	};
    var readBlob = function(start) {
        var blob;
        times += 1;
        if(file.slice) {
            blob = file.slice(start, start + step + 1);
        } else if(file.webkitSlice) {
            blob = file.webkitSlice(start, start + step + 1);
        } else if(file.mozSlice) {
            blob = file.mozSlice(start, start + step + 1);
        }
        if (reader) {
        	reader.readAsText(blob);
        }
    }
	$("#logFile").change(function() {
		if (reader) {
			$.each(this.files, function(index, file){
				result = "";
		        loaded = 0;
		        times = 0;
		        channel = {};
	            count.reset();
	            $fileProgress.val(0);
				reader.readAsText(file);
			});
		}
	});
	$("#startTime").datetimepicker({
		format: 'yyyy-mm-dd hh:ii'
	}).val(new Date(new Date().getTime() - 24 * 60 * 60 * 1000).format("yyyy-MM-dd 08:00"));
	$("#endTime").datetimepicker({
		format: 'yyyy-mm-dd hh:ii'
	}).val(new Date().format("yyyy-MM-dd HH:mm"));
	if (!!localStorage && !!localStorage.downloadIds) {
		$("#boxIds").val(localStorage.downloadIds);
	}
	$("#logDownloadBtn").click(function(){
		var ids = $("#boxIds").val();
		if (!!!ids) {
		    toastr.warning("请输入要查询的盒子IDs");
			return;
		}
		if (window.localStorage) {
			localStorage.downloadIds = ids;
		}
		var startTime = $("#startTime").val();
		if (!!!startTime) {
		    toastr.warning("请输入查询开始时间");
			return;
		}
		startTime = parseDate(startTime + ":00.000").getTime() / 1000;
		var endTime = $("#endTime").val();
		if (!!!endTime) {
			toastr.warning("请输入查询结束时间");
			return;
		}
		endTime = parseDate(endTime + ":59.999").getTime() / 1000;
		$.each(ids.split(","), function(index, id){
			id = id.trim();
			if(!!id) {
				var downloadUrl = "http://192.168.10.33:9968/csServerLog/showLog?startTime=" + startTime + "&endTime=" + endTime + "&deviceId=" + id;
				window.open(downloadUrl, id);
			}
		});
	});
});