$(document).ready(function() {
	initModules();
	loadModels();
});

/* ------------------------------------------------------------ SHOWING / HIDING MODULES ------------------------------------------------------------ */
function initModules() {
	showModule("TrackStatus");
	$(document.querySelector("html")).keypress(function(event) {
		if (event.keyCode == 47) {
			$("#track-search").focus();
			event.preventDefault();
		}
	});
	$("#execution-start-time").timepicker({step: 5}).val("00:00");
}

function showModule(moduleName) {
	hideModules();
	$("#" + moduleName).show();
}

function hideModules() {
	$(".view-module").hide();
}

function closeTrainSidebar() {
	$(".train-data-row").removeClass("selected");
	trainsDataModel.currentTrainObject(null);
}

/* ------------------------------------------------------------ UTILITY FUNCTIONS ------------------------------------------------------------ */
function truncateDecimalPlaces(number, numPlaces) {
	var mult = Math.pow(10, numPlaces);
	return (Math.round(number * mult) / mult);
}

function isInteger(input) {
	return (input === parseInt(input));
}

function copyObject(obj) {
	return jQuery.extend(true, {}, obj);
}

/* ------------------------------------------------------------ DATA MODELS ------------------------------------------------------------ */
var trackDataModel;
var switchesDataModel;
var crossingsDataModel;
var trainsDataModel;
var timeModelObject;

function loadModels() {
	ko.applyBindings(trackDataModel = new trackModelConstructor(), document.getElementById("TrackStatus"));
	ko.applyBindings(switchesDataModel = new switchesDataModelConstructor(), document.getElementById("switch-list-body"));
	ko.applyBindings(crossingsDataModel = new crossingsModelConstructor(), document.getElementById("crossing-list-body"));
	ko.applyBindings(trainsDataModel = new trainsModelConstructor(), document.getElementById("TrainInfo"));
	ko.applyBindings(timeModelObject = new timeModel(), document.getElementById("navbar"));
	ko.applyBindings(timeModelObject, document.getElementById("ExecuteModal"));
}

function trackModelConstructor() {
	var self = this;
	
	self.trackList = ko.observableArray();
	self.currentTrackRow = ko.observable(-1);
	self.currentRowObject = ko.observable();
	
	self.SearchTerm = ko.observable("");
	self.TrackListFiltered = ko.computed(function() {
		return ko.utils.arrayFilter(self.trackList(), function(track) {
			if (self.SearchTerm().length == 0) {
				return true;
			}
			var trackString = JSON.stringify(track).toLowerCase().replace(/"/g, "").replace("'", "").replace("block_number", "block");
			if (trackString.indexOf(self.SearchTerm().toLowerCase()) == -1) {
				return false;
			}
			return true;
		});
	});
	self.clearSearch = function(obj, event) {
		if (event.keyCode == 27) {
			self.SearchTerm("");
		}
	};
		
	self.selectTrack = function(data, element) {
		$(".track-data-row").removeClass("selected");
		self.currentTrackRow(parseInt($(element.currentTarget).addClass("selected").attr("dataindex")));
		self.currentRowObject(data);
	};
	
	$.getJSON("/static/json/TrackInfo.json", function(data) {
		for (var index = 0; index < data.length; index++) {
			data[index].Status = "Operational";
			data[index].Occupancy = "Empty";
			data[index].Maintenance = "Open";
			data[index].Weather = "Clear";
			self.trackList.push(data[index]);
		}
	});
}

function switchesDataModelConstructor() {
	var self = this;
	
	self.switchesList = ko.observableArray();
	self.getConnectionText = function(index, connection) {
		return self.switchesList()[index].Block_Numbers[connection] + " to " + self.switchesList()[index].Block_Numbers[connection + 1];
	};
	
	$.getJSON("/static/json/Switches.json", function(data) {
		for (var index = 0; index < data.length; index++) {
			data[index].Connection = 0;
			self.switchesList.push(data[index]);
		}
	});
}

function crossingsModelConstructor() {
	var self = this;
	
	self.crossingsList = ko.observableArray();
	
	$.getJSON("/static/json/Crossings.json", function(data) {
		for (var index = 0; index < data.length; index++) {
			data[index].Status = "Open";
			self.crossingsList.push(data[index]);
		}
	});
}

function trainsModelConstructor() {
	var self = this;
	
	self.trainsList = ko.observableArray();
	self.currentTrainObject = ko.observable(null);
	self.currentLine = ko.observable("Red");
	self.executing = ko.observable(false);
	
	self.selectTrain = function(data, element) {
		$(".train-data-row").removeClass("selected");
		$(element.currentTarget).addClass("selected");
		self.currentTrainObject(data);
		$("#arrival-time").timepicker({'step': 5});
	};
	
	self.stationsList = ko.observableArray([]);
	self.stationsListFiltered = ko.computed(function() {
		return ko.utils.arrayFilter(self.stationsList(), function(station) {
			return station.Line == self.currentLine();
		});		
	});
	
	$.getJSON("/static/json/Stations.json", function(data) {
		for (var index = 0; index < data.length; index++) {
			self.stationsList.push(data[index]);
		}
	});
	
	self.removeStop = function(data, obj) {
		self.currentTrainObject().Schedule.remove(data);
	};
}

function stopObjectModel(name, block, arrival, dwell) {
	var self = this;
	self.name = name;
	self.block = block;
	self.arrival = arrival;
	self.dwell = dwell;
	self.fullStopName = ko.computed(function() {
		return self.name + ": " + self.block;
	});
}

function trainObjectModel(name, line, block, speed, authority, schedule, ID) {
	var self = this;
	self.Name = ko.observable(name);
	self.Line = ko.observable(line);
	self.Block = ko.observable(block);
	self.Suggested_Speed = ko.observable(speed);
	self.Authority = ko.observable(authority);
	self.Schedule = ko.observableArray(schedule);
	self.ID = ko.observable(ID);
	
	self.NextDestination = ko.computed(function() {
		if (self.Schedule().length == 0) {
			return "None";
		}
		else {
			return self.Schedule()[0].fullStopName();
		}
	});
}

function timeModel() {
	var self = this;
	self.currentTime = ko.observable(0); // Current time in MS
	self.timeModifier = ko.observable(1); // Time rate multiplier
	self.play = ko.observable(false);
	
	self.updateCurrentTimeByMs = function(ms) {
		if (self.play()) {
			self.currentTime(self.currentTime() + (ms * self.timeModifier()));
		}
	}
	
	self.setCurrentTime = function(timeString) {
		var ms = 0;
		var minute = parseInt(timeString.split(":")[1]);
		var hour = parseInt(timeString.split(":")[0]);
		ms += minute * 60000;
		ms += hour * 3600000;
		self.currentTime(ms);
	};
	
	self.TimeString = ko.computed(function() {
		var seconds = parseInt((self.currentTime() % 60000) / 1000);
		var minutes = parseInt((self.currentTime() % 3600000) / 60000);
		var hours   = parseInt((self.currentTime() % 86400000) / 3600000);
		return padTime(hours) + ":" + padTime(minutes) + ":" + padTime(seconds);
	});
}


/* ------------------------------------------------------------ MODEL INTERACTIONS ------------------------------------------------------------ */

function importSchedule() {
	trainsDataModel.trainsList.removeAll();
	var inputSchedule = JSON.parse($("#import-text").val());
	for (var index = 0; index < inputSchedule.length; index++) {
		var train = inputSchedule[index];
		var obsSchedule = []
		for (var stopNum = 0; stopNum < train.Schedule.length; stopNum++) {
			var stop = train.Schedule[stopNum];
			obsSchedule.push(new stopObjectModel(stop.name, stop.block, stop.arrival, stop.dwell));
		}
		var obsTrain = new trainObjectModel(train.Name, train.Line, train.Block, train.Suggested_Speed, train.Authority, obsSchedule, train.ID);
		trainsDataModel.trainsList.push(obsTrain);
	}
}

var trainID = 0;
function addTrain() {
	var train = new trainObjectModel("New Train", "Red", 0, 0, 0, [], trainID);
	trainsDataModel.trainsList.push(train);
	trainID++;
}

function lineSelectChanged() {
	trainsDataModel.currentLine($("#line-select").val());
	trainsDataModel.currentTrainObject().Schedule.removeAll();
}

function addStop() {
	var element = document.getElementById("station-select");
	var stopObj = new stopObjectModel($(element.options[element.selectedIndex]).attr("data-name"), $(element.options[element.selectedIndex]).attr("data-block"), $("#arrival-time").val(), $("#dwell-time").val());
	trainsDataModel.currentTrainObject().Schedule.push(stopObj);
}

function deleteSelectedTrain() {
	var removedItems = trainsDataModel.trainsList.remove(function(item) {
		return item.ID() == trainsDataModel.currentTrainObject().ID();
	});
	if (removedItems.length == 0) {
		console.error("Item not removed successfully!");
	}
	trainsDataModel.currentTrainObject(null);
}

function exportSchedule() {
	$("#export-text").val(ko.toJSON(trainsDataModel.trainsList()));
}

/* ------------------------------------------------------------ TIMING CODE ------------------------------------------------------------ */

function padTime(number) {
	return ("0" + number).slice(-2);
}

function executeSchedule() {
	trainsDataModel.executing(true);
	$("#import-schedule").prop("disabled", true);
	$("#execute-schedule").prop("disabled", true);
	$("#execute-schedule-modal").prop("disabled", true);
	$("#execution-start-time").prop("disabled", true);
	$("#simulation-buttons").show();
	setInterval(function() {
		timeModelObject.updateCurrentTimeByMs(100);
	}, 100);
	timeModelObject.setCurrentTime($("#execution-start-time").val()); // Set current time
	continueTime();
}

function continueTime() {
	timeModelObject.play(true);
}

function pauseTime() {
	timeModelObject.play(false);
}

/* ------------------------------------------------------------ NOTIFICATION CODE ------------------------------------------------------------ */
function trackSavedNotification(obj) {
	$("#sidebar-saved-alert").show();
	setTimeout(function() {
		$("#sidebar-saved-alert").fadeOut();
	}, 1500);
	var data = trackDataModel.currentRowObject();
	var message = {
		Type: "Maintenance",
		Block: data.Block_Number,
		Maintenance: (obj.value == "Closed")
	};
	sendObjectToServer(message);
}

/* ------------------------------------------------------------ SERVER COMMUNICATION ------------------------------------------------------------ */

function objToGetString(object) {
	var returnString = "";
	for (var propertyName in object) {
		returnString += propertyName + "=" + object[propertyName] + "&";
	}
	return returnString.substring(0, returnString.length - 1);
}

function sendObjectToServer(obj) {
	$.ajax({
		url: "/handlers/relay_message?" + objToGetString(obj),
		type: "get"
	});
}

function sendStringToServer(str) {
	$.ajax({
		url: "/handlers/relay_message" + "?msg=" + str,
		type: "get"
	});
}

checkServerUpdates();
function checkServerUpdates() {
	$.ajax("/handlers/delete_messages").done(function(string) {
		// Do something here with the returned messages in JSON format.
		setTimeout(checkServerUpdates, 500); // Server update interval
	});
}