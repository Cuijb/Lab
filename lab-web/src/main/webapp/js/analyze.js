$(document).ready(function() {
	var REG_TIME = "([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}).*?\\[(DEBUG|INFO|WARN|ERROR)\\]";
	var REG_CHANNEL = "( .*\\] | \\d+ )"
	var PATTERN_TIME_P = new RegExp("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})\\.([0-9]{3})");
	var PATTERN_TIME = new RegExp(REG_TIME);
	var PATTERN_MBOX = new RegExp(REG_TIME + " MBox ROM Version is: (.*)");
	var PATTERN_CHANNEL = new RegExp(REG_TIME + REG_CHANNEL);
	var PATTERN_PUBLISH_COST = new RegExp(REG_TIME + REG_CHANNEL + "first publish m3u8 used time: (.*)ms");
	var PATTERN_BC_NUM = new RegExp(REG_TIME + REG_CHANNEL + "bcRecvNumber increase ([0-9]+)");
	var PATTERN_BC_BYTES = new RegExp(REG_TIME + REG_CHANNEL + "bcRecvBytes increase ([0-9]+)");
	var PATTERN_DLD_NUM = new RegExp(REG_TIME + REG_CHANNEL + "httpDownloadNumber increase ([0-9]+)");
	var PATTERN_DLD_BYTES = new RegExp(REG_TIME + REG_CHANNEL + "httpRecvBytes increase ([0-9]+)");
	var PATTERN_UNNEED_D = new RegExp(REG_TIME + REG_CHANNEL + "notNeedHttpDownload increase ([0-9]+)");
	var PATTERN_PLAY_LIST = new RegExp(REG_TIME + REG_CHANNEL + "play list is");
	var PATTERN_PLAY_MISS = new RegExp(REG_TIME + REG_CHANNEL + "missNumber increase ([0-9]+)");
	var PATTERN_RESTART = new RegExp(REG_TIME + REG_CHANNEL + "will restart");
	var DF_TIME = "yyyy-MM-dd HH24:mm:ss.SSS";
	var DF_SHOW = "MM-dd HH:mm";
	var NF_PERCENT = "#.##";
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
		this.publishCost = null;
		this.bcN = 0;
		this.bcB = 0;
		this.dldN = 0;
		this.dldB = 0;
		this.unNeedDld = 0;
		this.miss = 0;
		this.errMsgs = [];
		this.updatePT = function(timeStr) {
			var tempD = parseDate(timeStr);
			if (tempD) {
				this.endTime = tempD;
				if (!this.startTime) {
					this.startTime = this.endTime;
				}
			}
		};
		this.isEmpty = function(){
			return this.bcN <= 0 && this.dldN <= 0;
		};
		this.getST = function(){
			if (!this.startTime) {
				return "unknown";
			}
			return this.startTime.format();
		};
		this.getET = function(){
			if (!this.endTime) {
				return "unknown";
			}
			return this.endTime.format();
		};
		this.getPT = function(){
			if (!this.startTime) {
				return "unknown";
			}
			if (!this.endTime) {
				return "unknown";
			}
			return ((this.endTime.getTime() - this.startTime.getTime()) / 1000.0 / 60 / 60).toFixed(2);
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
			if (this.miss == 0) {
				return 0;
			}
			return (100.0 * this.miss / (this.bcN + this.dldN - this.unNeedDld + this.miss)).toFixed(2);
		};
		this.reset = function(){
			this.startTime = null;
			this.endTime = null;
			this.bcN = 0;
			this.bcB = 0;
			this.dldN = 0;
			this.dldB = 0;
			this.unNeedDld = 0;
			this.miss = 0;
			this.errMsgs = [];
		};
	};
	var $fileProgress = $("#fileProgress");
	var $liveTable = $("#liveTable tbody");
	var $logList = $("ul#logList");
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
	var appendResult = function(desc, count) {
    	if (count.isEmpty()) {
    		return;
    	}
        $liveTable.append($("<tr>").addClass("ALL" == desc ? "info" : "success")
			.append($("<td>").text("ALL" == desc ? "总计" : desc))
			.append($("<td>").text(count.getST()))
			.append($("<td>").text(count.getET()))
			.append($("<td>").text(count.getPT()))
			.append($("<td>").text(count.getPC()))
			.append($("<td>").text(count.bcN))
			.append($("<td>").text(count.bcB))
			.append($("<td>").text(count.dldN))
			.append($("<td>").text(count.dldB))
			.append($("<td>").text(count.unNeedDld))
			.append($("<td>").text(count.miss))
			.append($("<td>").text(count.getDNP()))
			.append($("<td>").text(count.getDBP()))
			.append($("<td>").text(count.getUNP()))
			.append($("<td>").text(count.getNP()))
			.append($("<td>").text(count.getMP())));
    };
    var showResult = function() {
		var errMsgStr = "";
		$.each(count.errMsgs, function(index, errMsg){
			errMsgStr += errMsg + "; ";
		});
		appendResult("ALL", count);
		$.each(channel, function(channel, count){
			appendResult(channel, count);
		});
	};
	var logAnalyze = function(lines) {
		$liveTable.empty();
        $logList.empty();
        for (var index in lines) {
        	var line = lines[index];
			var mcMBox = line.match(PATTERN_MBOX);
			if (mcMBox) {
				showResult();
		        channel = {};
	            count.reset();
				count.updatePT(mcMBox[1]);
			}

			var mcTime = line.match(PATTERN_TIME);
			if (mcTime) {
				count.updatePT(mcTime[1]);
			}

			var mcChannel = line.match(PATTERN_CHANNEL);
			if (mcChannel) {
				count.updatePT(mcChannel[1]);
				if (!channel[mcChannel[3]]){
					channel[mcChannel[3]] = new Counter();
				}
				channel[mcChannel[3]].updatePT(mcChannel[1]);
			}

			var mcPublish = line.match(PATTERN_PUBLISH_COST);
			if (mcPublish) {
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

			var mcMiss = line.match(PATTERN_PLAY_MISS);
			if (mcMiss) {
				count.updatePT(mcMiss[1]);
				count.miss += parseInt(mcMiss[4]);
				channel[mcMiss[3]].updatePT(mcMiss[1]);
				channel[mcMiss[3]].miss += parseInt(mcMiss[4]);
			}

			var mcRestart = line.match(PATTERN_RESTART);
			if (mcRestart) {
				channel[mcRestart[3]].errMsgs.push(parseDate(mcRestart[1]).format() + " 频道重启");
			}
		}
		showResult();
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
		$logList.empty();
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
});