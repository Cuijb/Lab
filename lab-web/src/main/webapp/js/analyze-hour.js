$(document).ready(function() {
	var REG_TIME = "([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}).*\\[(DEBUG|INFO|WARN|ERROR)\\] ";
	var REG_CHANNEL = "(.*\\]|\\d+)"
	var PATTERN_TIME_P = new RegExp("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})\\.([0-9]{3})");
	var PATTERN_TIME = new RegExp(REG_TIME);
	var PATTERN_MBOX = new RegExp(REG_TIME + "MBox ROM Version is: (.*)");
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
	var PATTERN_CHANNEL_RESTART = new RegExp(REG_TIME + REG_CHANNEL + " will restart");
	var PATTERN_CHANNEL_STOP = new RegExp(REG_TIME + REG_CHANNEL + " will stop");
	var DF_TIME = "yyyy-MM-dd HH24:mm:ss.SSS";
	var DF_SHOW = "MM-dd HH:mm:ss";
	var NF_PERCENT = "#.##";
	var $fileProgress = $("#fileProgress");
	var $liveTable = $("#liveTable tbody");
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

    var showTimeDuration = function(startTimeStr, endTimeStr) {
    	var startStr = parseDate(startTimeStr).format();
    	var endStr = parseDate(endTimeStr).format().replace(startStr.substring(0, 6), "");
    	return startStr + " ~ " + endStr
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

			return showTimeDuration(this.startTime, this.endTime);
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
	var appendResult = function(count) {
    	if (count.isEmpty()) {
    		return;
    	}
        $liveTable.append($("<tr>").addClass("info")
			.append($("<td>").text(count.getSET()))
			.append($("<td>").text(count.getPT()))
			.append($("<td>").text(count.getPC()))
			.append($("<td>").text(count.bcN))
			.append($("<td>").text(count.bcB))
			.append($("<td>").text(count.dldN))
			.append($("<td>").text(count.dldB))
			.append($("<td>").addClass(count.unNeedBc > 0 ? "danger" : "").text(count.unNeedBc))
			.append($("<td>").addClass(count.unNeedDld > 0 ? "danger" : "").text(count.unNeedDld))
			.append($("<td>").text(count.playN))
			.append($("<td>").addClass(count.adN > 0 ? "danger" : "").text(count.adN))
			.append($("<td>").addClass(count.missN > 0 ? "danger" : "").text(count.missN))
			.append($("<td>").text(count.getDBP()))
			.append($("<td>").text(count.getDNP()))
			.append($("<td>").text(count.getUNP()))
			.append($("<td>").text(count.getNP()))
			.append($("<td>").text(count.getMP())));
    };
    var showResult = function() {
    	appendResult(count);
	};
	var logAnalyze = function(lines) {
		$liveTable.empty();
        var sliptByHour = true;
        for (var index in lines) {
        	var line = lines[index];
			var mcMBox = line.match(PATTERN_MBOX);
			if (mcMBox) {
				count.updatePT(mcMBox[1]);
			}

			var mcTime = line.match(PATTERN_TIME);
			if (mcTime) {
				if (sliptByHour) {
					if (count.endTime && parseInt(parseDate(count.endTime).getHours() / 2) != parseInt(parseDate(mcTime[1]).getHours() / 2)) {
						showResult();
			            count.reset();
					}
				}
				count.updatePT(mcTime[1]);
			}

			var mcBCN = line.match(PATTERN_BC_NUM);
			if (mcBCN) {
				count.updatePT(mcBCN[1]);
				count.bcN += parseInt(mcBCN[4]);
			}

			var mcBCB = line.match(PATTERN_BC_BYTES);
			if (mcBCB) {
				count.updatePT(mcBCB[1]);
				count.bcB += parseInt(mcBCB[4]);
			}

			var mcDLDN = line.match(PATTERN_DLD_NUM);
			if (mcDLDN) {
				count.updatePT(mcDLDN[1]);
				count.dldN += parseInt(mcDLDN[4]);
			}

			var mcDLDB = line.match(PATTERN_DLD_BYTES);
			if (mcDLDB) {
				count.updatePT(mcDLDB[1]);
				count.dldB += parseInt(mcDLDB[4]);
			}

			var mcUnNeedBC = line.match(PATTERN_UNNEED_BC);
			if (mcUnNeedBC) {
				count.updatePT(mcUnNeedBC[1]);
				count.unNeedBc += parseInt(mcUnNeedBC[4]);
			}

			var mcUnNeed = line.match(PATTERN_UNNEED_D);
			if (mcUnNeed) {
				count.updatePT(mcUnNeed[1]);
				count.unNeedDld += parseInt(mcUnNeed[4]);
			}

			var mcList = line.match(PATTERN_PLAY_LIST);
			if (mcList) {
				count.updatePT(mcList[1]);
			}

			var mcPlay = line.match(PATTERN_PLAY_PLAY);
			if (mcPlay) {
				count.updatePT(mcPlay[1]);
				count.playN += parseInt(mcPlay[4]);
			}

			var mcAd = line.match(PATTERN_PLAY_AD);
			if (mcAd) {
				count.updatePT(mcAd[1]);
				count.adN += parseInt(mcAd[4]);
			}

			var mcMiss = line.match(PATTERN_PLAY_MISS);
			if (mcMiss) {
				count.updatePT(mcMiss[1]);
				count.missN += parseInt(mcMiss[4]);
			}
		}
		showResult();
		count.reset();
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