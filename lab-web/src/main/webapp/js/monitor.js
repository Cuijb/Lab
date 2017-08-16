$(document).ready(function() {
	var $swGet = function(url, successHandler, errorHandler, data) {
		data = !!!data ? {} : data;
		$.ajax({
			url : url,
			type : "GET",
			data : data,
			success : function(response, status, xhr) {
				if (response.status) {
					console.log("query(" + url + ") failed", response);
					errorHandler(response, status, xhr);
				} else {
					successHandler(response, status, xhr);
				}
			},
			error : function(response, status, xhr) {
				console.log("query(" + url + ") has error", response);
				errorHandler(response, status, xhr);
			},
		});
	};
	// 状态格式化
	var statusFmt = function(isRunning){
		return !!isRunning ? "运行中" : "未运行";
	};
	// 状态样式
	var statusClass = function(isRunning){
		return !!isRunning ? "label-success" : "label-danger";
	};
	// 查询服务器状态
	var queryStatus = function(){
		$swGet("/fes_monitor", function(response) {
			responseHandler(response);
		}, function(response) {
			console.log("error", response);
		});
	};
	// 查询结果处理
	var responseHandler = function(result) {
		// 区分各服务器的信息
		var segmenter = result.segmenter || {};
		var scheduler = result.scheduler || {};
		var swhls = result.swhls || {};
		var swServer = result.swhls || {};
		var programMap = {};
		var segmenterMap = {};
		var schedulerMap = {};
		var swhlsMap = {};
		$.each(segmenter.pid_seq_arr || [], function(index, program) {
			programMap[program.program_number] = program.stream_pid;
			segmenterMap[program.program_number] = program;
		});
		$.each(scheduler.pid_seq_arr || [], function(index, program) {
			programMap[program.program_number] = program.stream_pid;
			schedulerMap[program.program_number] = program;
		});
		$.each(swhls.pid_seq_arr || [], function(index, program) {
			programMap[program.program_number] = program.stream_pid;
			swhlsMap[program.program_number] = program;
		});
		
		// fes | 信息通览
		$("#fesSegmenterStatus,#segmenterStatus").text(statusFmt(segmenter.is_runnig)).addClass(statusClass(segmenter.is_runnig));
		$("#fesSegmenterVersion,#segmenterVersion").text(segmenter.version_number);
		$("#fesSchedulerStatus,#schedulerStatus").text(statusFmt(scheduler.is_runnig)).addClass(statusClass(scheduler.is_runnig));
		$("#fesSchedulerVersion,#schedulerVersion").text(scheduler.version_number);
		$("#fesSwhlsStatus,#swhlsStatus").text(statusFmt(swhls.is_runnig)).addClass(statusClass(swhls.is_runnig));
		$("#fesSwhlsVersion,#swhlsVersion").text(swhls.version_number);
		$("#fesSwserverStatus,#swserverStatus").text(statusFmt(swServer.is_runnig)).addClass(statusClass(swServer.is_runnig));
		$("#fesSwserverVersion,#swserverVersion").text(swServer.version_number);
		$("#fesTable tbody").empty().append(
			$.map(programMap, function(pid, number) {
				var programSgmt = segmenterMap[number];
				var programscdl = schedulerMap[number];
				var programhls = swhlsMap[number];
				return $("<tr>")
					.append($("<td>").text(number))
					.append($("<td>").text(pid))
					.append($("<td>").text(programSgmt.current_seq))
					.append($("<td>").text(programscdl.current_seq))
					.append($("<td>").text(programhls.current_seq));
			})
		);
		
		// 分片器 | SEGMENTER
		$("#segmenterTable tbody").empty().append(
			$.map(segmenter.pid_seq_arr, function(program, index) {
				return $("<tr>")
					.append($("<td>").text(program.program_number))
					.append($("<td>").text(program.stream_pid))
					.append($("<td>").text(program.current_seq))
					.append($("<td>").text(program.last_seq));
			})
		);

		// 调度器 | SCHEDULER
		$("#schedulerTable tbody").empty().append(
			$.map(scheduler.pid_seq_arr, function(program, index) {
				return $("<tr>")
					.append($("<td>").text(program.program_number))
					.append($("<td>").text(program.stream_pid))
					.append($("<td>").text(program.current_seq))
					.append($("<td>").text(program.last_seq));
			})
		);

		// NGINX | SW-HLS
		$("#swhlsTable tbody").empty().append(
			$.map(scheduler.pid_seq_arr, function(program, index) {
				return $("<tr>")
					.append($("<td>").text(program.program_number))
					.append($("<td>").text(program.stream_pid))
					.append($("<td>").text(program.current_seq))
					.append($("<td>").text(program.last_seq));
			})
		);

		// 服务器 | SW-SERVER
		$("#swserverTable tbody").empty().append(
			$("<tr>")
				.append($("<td>").text(swServer.version_number))
				.append($("<td>").text(swServer.is_runnig))
		);
	}

	var resultObj = {
		"protocol_version" : 1,
		"timestamp" : 1489655608,
		"segmenter" : {
			"version_number" : 1,
			"is_runnig" : true,
			"pid_seq_arr" : [ {
				"program_number" : 1,
				"stream_pid" : 2004,
				"current_seq" : "13498",
				"last_seq" : "13495"
			}, {
				"program_number" : 2,
				"stream_pid" : 2005,
				"current_seq" : "13497",
				"last_seq" : "13493"
			}, {
				"program_number" : 3,
				"stream_pid" : 2006,
				"current_seq" : "13498",
				"last_seq" : "13495"
			}, {
				"program_number" : 4,
				"stream_pid" : 2007,
				"current_seq" : "13497",
				"last_seq" : "13495"
			}, {
				"program_number" : 5,
				"stream_pid" : 2008,
				"current_seq" : "13497",
				"last_seq" : "13495"
			}, {
				"program_number" : 6,
				"stream_pid" : 2009,
				"current_seq" : "13497",
				"last_seq" : "13494"
			}, {
				"program_number" : 7,
				"stream_pid" : 2010,
				"current_seq" : "13494",
				"last_seq" : "13494"
			}, {
				"program_number" : 8,
				"stream_pid" : 2011,
				"current_seq" : "13498",
				"last_seq" : "13494"
			}, {
				"program_number" : 9,
				"stream_pid" : 2012,
				"current_seq" : "13496",
				"last_seq" : "13494"
			}, {
				"program_number" : 10,
				"stream_pid" : 2013,
				"current_seq" : "13498",
				"last_seq" : "13494"
			}, {
				"program_number" : 11,
				"stream_pid" : 2014,
				"current_seq" : "13498",
				"last_seq" : "13492"
			}, {
				"program_number" : 12,
				"stream_pid" : 2015,
				"current_seq" : "13497",
				"last_seq" : "13494"
			} ]
		},
		"scheduler" : {
			"version_number" : 1,
			"is_runnig" : true,
			"pid_seq_arr" : [ {
				"program_number" : 1,
				"stream_pid" : 2004,
				"current_seq" : 13493,
				"last_seq" : 13491
			}, {
				"program_number" : 2,
				"stream_pid" : 2005,
				"current_seq" : 13493,
				"last_seq" : 13489
			}, {
				"program_number" : 3,
				"stream_pid" : 2006,
				"current_seq" : 13493,
				"last_seq" : 13490
			}, {
				"program_number" : 4,
				"stream_pid" : 2007,
				"current_seq" : 13493,
				"last_seq" : 13490
			}, {
				"program_number" : 5,
				"stream_pid" : 2008,
				"current_seq" : 13492,
				"last_seq" : 13490
			}, {
				"program_number" : 6,
				"stream_pid" : 2009,
				"current_seq" : 13493,
				"last_seq" : 13491
			}, {
				"program_number" : 7,
				"stream_pid" : 2010,
				"current_seq" : 13493,
				"last_seq" : 13489
			}, {
				"program_number" : 8,
				"stream_pid" : 2011,
				"current_seq" : 13493,
				"last_seq" : 13490
			}, {
				"program_number" : 9,
				"stream_pid" : 2012,
				"current_seq" : 13493,
				"last_seq" : 13491
			}, {
				"program_number" : 10,
				"stream_pid" : 2013,
				"current_seq" : 13492,
				"last_seq" : 13490
			}, {
				"program_number" : 11,
				"stream_pid" : 2014,
				"current_seq" : 13493,
				"last_seq" : 13491
			}, {
				"program_number" : 12,
				"stream_pid" : 2015,
				"current_seq" : 13492,
				"last_seq" : 13489
			} ]
		},
		"swhls" : {
			"version_number" : 1,
			"is_runnig" : true,
			"pid_seq_arr" : [ {
				"program_number" : 1,
				"stream_pid" : 2004,
				"current_seq" : 13496,
				"last_seq" : 13493
			}, {
				"program_number" : 2,
				"stream_pid" : 2005,
				"current_seq" : 13496,
				"last_seq" : 13492
			}, {
				"program_number" : 3,
				"stream_pid" : 2006,
				"current_seq" : 13495,
				"last_seq" : 13493
			}, {
				"program_number" : 4,
				"stream_pid" : 2007,
				"current_seq" : 13496,
				"last_seq" : 13492
			}, {
				"program_number" : 5,
				"stream_pid" : 2008,
				"current_seq" : 13495,
				"last_seq" : 13492
			}, {
				"program_number" : 6,
				"stream_pid" : 2009,
				"current_seq" : 13496,
				"last_seq" : 13493
			}, {
				"program_number" : 7,
				"stream_pid" : 2010,
				"current_seq" : 13496,
				"last_seq" : 13492
			}, {
				"program_number" : 8,
				"stream_pid" : 2011,
				"current_seq" : 13496,
				"last_seq" : 13492
			}, {
				"program_number" : 9,
				"stream_pid" : 2012,
				"current_seq" : 13496,
				"last_seq" : 13492
			}, {
				"program_number" : 10,
				"stream_pid" : 2013,
				"current_seq" : 13495,
				"last_seq" : 13492
			}, {
				"program_number" : 11,
				"stream_pid" : 2014,
				"current_seq" : 13496,
				"last_seq" : 13493
			}, {
				"program_number" : 12,
				"stream_pid" : 2015,
				"current_seq" : 13496,
				"last_seq" : 13492
			} ]
		},
		"swserver" : {
			"version_number" : 1,
			"is_runnig" : true
		}
	};
//	responseHandler(resultObj);;
	
	// 每1秒定时刷新
	queryStatus();
	var queryInterval = window.setInterval(function(){
		queryStatus();
	}, 5000);
});