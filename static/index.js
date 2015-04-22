$(document).ready(function() {
	initModules();
	loadModels();
	checkServerUpdates();
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

String.prototype.capitalizeFirstLetter = function() {
	return this.charAt(0).toUpperCase() + this.slice(1);
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
	ko.applyBindings(trainsDataModel, document.getElementById("MetricsModal"));
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
			var trackString = JSON.stringify(track()).toLowerCase().replace(/"/g, "").replace("'", "").replace("block_number", "block");
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
		
	self.selectTrack = function(data, element, test) {
		$(".track-data-row").removeClass("selected");
		self.currentTrackRow(parseInt($(element.currentTarget).addClass("selected").attr("dataindex")));
		self.currentRowObject(data);
	};
	
	/* ------------------------------------------------------------------ Pathfinding ------------------------------------------------------------------ */
	self.getBlockObject = function(line, number) {
		for (var index = 0; index < self.trackList().length; index++) {
			if ((self.trackList()[index]().Line.toLowerCase() == line.toLowerCase()) && (self.trackList()[index]().Block_Number == number)) {
				return self.trackList()[index]();
			}
		}
		return null;
	};
	
	self.getBlockModel = function(line, number) {
		for (var index = 0; index < self.trackList().length; index++) {
			if ((self.trackList()[index]().Line.toLowerCase() == line.toLowerCase()) && (self.trackList()[index]().Block_Number == number)) {
				return self.trackList()[index];
			}
		}
		return null;
	};
	
	self.discoveredNodes = [];  // This is for search.
	self.foundPath = []; // This is the final path.
	self.pathLength = 0; // This is the length of the final path.
	self.blockDistance = 0; // This is how many blocks are on the path.
	
	// Given a line, a start block number, and a finish block number, calculates the path between them.
	self.getPathBetween = function(line, start, finish) {
		self.discoveredNodes.length = 0; // Clears the discovered nodes array
		self.foundPath.length = 0;
		self.pathLengthMeters = 0;
		self.blockDistance = 0;
		self.getPathBetweenRecursive(line, start, finish, [], 0, 0);
		return {path: self.foundPath, length: self.pathLength * 3.28084, blockDistance: self.blockDistance};
	};
	
	self.getPathBetweenRecursive = function(line, currentNode, finish, currentPath, currentLength, currentBlockDistance) {
		// If we've already found the block, don't continue executing.
		if (self.foundPath.length > 0) {
			return;
		}
		var currentBlockObject = self.getBlockObject(line, currentNode);
		// If the block is inaccessible, don't evaluate it.
		if (currentBlockObject.Status.toLowerCase() == "broken") {
			return;
		}
		self.discoveredNodes.push(currentNode);
		var newPath = currentPath.slice();
		newPath.push(currentNode);
		if (currentNode == finish) {
			self.foundPath = newPath;
			self.pathLength = currentLength;
			self.blockDistance = currentBlockDistance;
		}
		currentBlockDistance++;
		var accessibleBlocks;
		for (var index = 0; index < (accessibleBlocks = currentBlockObject.Accessible_Blocks).length; index++) {
			if (self.discoveredNodes.indexOf(accessibleBlocks[index]) == -1) {
				// If we've already found the block, don't continue executing.
				if (self.foundPath.length > 0) {
					return;
				}
				self.getPathBetweenRecursive(line, accessibleBlocks[index], finish, newPath, currentLength + currentBlockObject.Block_Length, currentBlockDistance);
			}
		}
	};
	
	/* ------------------------------------------------------------------ End Pathfinding ------------------------------------------------------------------ */
	
	$.getJSON("/static/json/TrackInfo.json", function(data) {
		for (var index = 0; index < data.length; index++) {
			data[index].Status = "Operational";
			data[index].Occupancy = "--";
			data[index].Maintenance = "Open";
			data[index].Weather = "Clear";
			data[index].Heater = "Off";
			data[index].Lights = "Red";
			self.trackList.push(ko.observable(data[index]));
		}
	});
}

function switchesDataModelConstructor() {
	var self = this;
	
	self.switchesList = ko.observableArray();

	$.getJSON("/static/json/Switches.json", function(data) {
		for (var index = 0; index < data.length; index++) {
			data[index].Connection = [data[index].Block_Numbers[0], data[index].Block_Numbers[1]];
			self.switchesList.push(ko.observable(data[index]));
		}
	});
}

function crossingsModelConstructor() {
	var self = this;
	
	self.crossingsList = ko.observableArray();
	
	$.getJSON("/static/json/Crossings.json", function(data) {
		for (var index = 0; index < data.length; index++) {
			data[index].Status = "Open";
			self.crossingsList.push(ko.observable(data[index]));
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
		$("#schedule-start-time").timepicker({'step': 5});
	};
	
	self.stationsList = ko.observableArray([]);
	self.stationsListFiltered = ko.computed(function() {
		return ko.utils.arrayFilter(self.stationsList(), function(station) {
			return station.Line == self.currentLine();
		});		
	});
	
	$.getJSON("/static/json/Stations.json", function(data) {
		var redMax = data.redMax;
		var greenMax = data.greenMax;
		var redStationBlocks = [];
		var greenStationBlocks = [];
		for (var index = 0; index < data.stations.length; index++) {
			data.stations[index].Throughput = ko.observable(0);
			self.stationsList.push(data.stations[index]);
			if (data.stations[index].Line.toLowerCase() == "red") {
				redStationBlocks.push(data.stations[index].Block);
			}
			if (data.stations[index].Line.toLowerCase() == "green") {
				greenStationBlocks.push(data.stations[index].Block);
			}
		}
		for (var redIndex = 0; redIndex < redMax; redIndex++) {
			if (redStationBlocks.indexOf(redIndex) == -1)  {
				var newObject = {
					Name: "Block",
					Line: "Red",
					Block: redIndex
				};
				self.stationsList.push(newObject);
			}
		}
		for (var greenIndex = 0; greenIndex < greenMax; greenIndex++) {
			if (greenStationBlocks.indexOf(greenIndex) == -1) {
				var newObject = {
					Name: "Block",
					Line: "Green",
					Block: greenIndex
				};
				self.stationsList.push(newObject);
			}
		}
	});
	
	self.removeStop = function(data, obj) {
		self.currentTrainObject().Schedule.remove(data);
	};
	
	self.getTrainWithID = function(ID) {
		for (var index = 0; index < self.trainsList().length; index++) {
			if (self.trainsList()[index].ID() == ID) {
				return self.trainsList()[index];
			}
		}
	}
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
	self.Suggested_Speed.subscribe(function(newValue) {
		// Send string to track controller indicating that the train's suggested speed has been updated.
		if (self.Active()) {
			updateSuggestedSpeed(self.ID(), newValue);
		}
	});
	
	self.Authority = ko.observable(authority);
	self.Authority.subscribe(function(newValue) {
		if (self.Active()) {
			updateAuthority(self.ID(), newValue);
		}
	});
	
	self.Schedule = ko.observableArray(schedule);
	self.ID = ko.observable(ID);
	self.Active = ko.observable(false);
	self.startTime = ko.observable("00:00"); // This is when the schedule will start executing.
	self.startTimeMS = ko.computed(function() {
		return timeStringToMS(self.startTime());
	});
	
	self.NextDestinationIndex = ko.observable(0);
	self.NextDestination = ko.computed(function() {
		if (self.NextDestinationIndex() >= self.Schedule().length) {
			return "None";
		}
		else {
			return self.Schedule()[self.NextDestinationIndex()].fullStopName();
		}
	});
	
	self.activate = function() {
		var scheduleObject = self.generateScheduleObject();
		// If we can get to our destination:
		if (scheduleObject.distance > 0) {
			// Mark train as active.
			self.Active(true);
			// Send path to server.
			sendStringToServer("CTC : " + self.Line() + " " + self.ID() + " : set, Path = " + scheduleObject.schedule);
			// Send authority:
			self.Authority(scheduleObject.blockDistance);
			// Send suggested speed:
			var startTime = timeModelObject.currentTime();
			var endTime = timeStringToMS(self.Schedule()[self.Schedule().length - 1].arrival);
			var MPH = self.calculateSuggestedSpeed(startTime, endTime, scheduleObject.distance);
			self.Suggested_Speed(MPH);
			
			// Reset the destinations because it's a new schedule.
			self.NextDestinationIndex(0);
		}
	};
	
	// EndTime is the time the train is scheduled to arrive at the last stop.
	// DistanceToTravel is the total trip distance in feet.
	// Times are in ms.
	// Generates miles per hour suggested speed that the train should follow.
	self.calculateSuggestedSpeed = function(startTime, endTime, distanceToTravel) {
		var maxSpeed = 40;
		
		var milesDistance = distanceToTravel / 5280;
		var difference = Math.abs(endTime - startTime) / 3600000; // How much time we have to get there in HOURS.
		var speedUnbounded = milesDistance / difference;
		if (speedUnbounded > maxSpeed) {
			return maxSpeed;
		}
		if ((endTime - startTime) < 0) {
			return maxSpeed;
		}
		else {
			return speedUnbounded.toFixed(2);
		}
	};
	
	self.generateScheduleObject = function() {
		var scheduleString = "[";
		var currentPosition = self.Block();
		var currentLength = 0;
		var totalBlockLength = 0;
		for (var index = 0; index < self.Schedule().length; index++) {
			var pathObject = trackDataModel.getPathBetween(self.Line(), currentPosition, self.Schedule()[index].block);
			var pathArray = pathObject.path;
			if (pathArray.length == 0) {
				var errorMsg = "Error: Destination block " + self.Schedule()[index].block + " unreachable from block " + currentPosition + ".  Please repair tracks before continuing."
				console.error(errorMsg);
				return {schedule: "[]", distance: -1};
			}
			currentLength += pathObject.length;
			totalBlockLength += pathObject.blockDistance;
			currentPosition = pathArray.pop();
			pathArray.shift(); // Remove the first element from the array, since that's where we just started.
			scheduleString += pathArray.toString();
			scheduleString += ",{" + currentPosition + "/" + self.Schedule()[index].dwell + "},";
			
		}
		scheduleString = scheduleString.substring(0, scheduleString.length - 1);
		scheduleString += "]";
		return {schedule: scheduleString, distance: currentLength, blockDistance: totalBlockLength};
	};
}

function timeModel() {
	var self = this;
	self.currentTime = ko.observable(0); // Current time in MS
	self.timeModifier = ko.observable(1); // Time rate multiplier
	self.timeModifier.subscribe(function(newValue) {
		sendTimeModifierToAllModules(newValue);
	});
		
	self.play = ko.observable(false);
	self.play.subscribe(function(newValue) {
		if (newValue) {
			sendTimeModifierToAllModules(self.timeModifier());
		}
		else {
			sendTimeModifierToAllModules(0);
		}
		
	});
	
	self.updateCurrentTimeByMs = function(ms) {
		if (self.play()) {
			self.currentTime(self.currentTime() + (ms * self.timeModifier()));
			// At every update, check if a train should be sent out.
			for (var index = 0; index < trainsDataModel.trainsList().length; index++) {
				var train = trainsDataModel.trainsList()[index];
				var a = train.startTimeMS();
				var b = self.currentTime();
				var c = !train.Active();
				if ((train.startTimeMS() <= self.currentTime()) && (!train.Active())) {
					train.activate();
				}
			}
		}
	}
	
	self.setCurrentTime = function(timeString) {
		self.currentTime(timeStringToMS(timeString));
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
		if (index == (inputSchedule.length - 1)) {
			trainID = train.ID + 1;
		}
		var obsTrain = new trainObjectModel(train.Name, train.Line, train.Block, train.Suggested_Speed, train.Authority, obsSchedule, train.ID);
		obsTrain.startTime(train.startTime);
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

function timeStringToMS(timeString) {
	var pieces = timeString.split(":").reverse();
	var seconds, minutes, hours;
	if (pieces.length == 2) {
		seconds = 0;
		minutes = parseInt(pieces[0]);
		hours = parseInt(pieces[1]);
	}
	else if (pieces.length == 3) {
		seconds = parseInt(pieces[0]);
		minutes = parseInt(pieces[1]);
		hours = parseInt(pieces[2]);
	}
	else {
		alert("Invalid time string.");
	}
	return (1000 * seconds) + (1000 * 60 * minutes) + (1000 * 60 * 60 * hours);
}

/* ------------------------------------------------------------ NOTIFICATION CODE ------------------------------------------------------------ */
function trackSavedNotification(obj) {
	$("#sidebar-saved-alert").show();
	setTimeout(function() {
		$("#sidebar-saved-alert").fadeOut();
	}, 1500);
	var data = trackDataModel.currentRowObject();
	var maintenanceMessage = "CTC : " + data.Line + " " + data.Block_Number + " : set, Maintenance = " + (obj.value == "Closed");
	sendStringToServer(maintenanceMessage);
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
		url: "/handlers/relay_message" + "?msg=" + str.replaceAll("=", "/equals/").replaceAll("{", "/lsbracket/").replaceAll("}", "/rsbracket/").replaceAll("[", "/lbracket/").replaceAll("]", "/rbracket/"),
		type: "get"
	});
}

String.prototype.replaceAll = function(search, replacement) {
	return this.split(search).join(replacement);
};


function replaceAll(str, search, replacement) {
	return str.split(search).join(replacement);
}

function sendTimeModifierToAllModules(modifier) {
	if (modifier == 0) {
		modifier = 0.0001;
	}
	var str = "CTC : 0 : set, TimeModifier = " + modifier
	$.ajax({
		url: "/handlers/send_all" + "?msg=" + str.replace("=", "/equals/"),
		type: "get"
	});
		
}

function checkServerUpdates() {
	$.ajax("/handlers/messages?type=delete").done(function(string) {
		var messages = JSON.parse(string);
		for (var index = 0; index < messages.length; index++) {
			var message = messages[index];
			switch(message.commandType) {
				case "location":
					alert("location obtained.");
				
					for (var index = 0; index < trainsDataModel.trainsList().length; index++) {
						var train = trainsDataModel.trainsList()[index];
						if (train.ID() == message.subjectID) {
							// Update the old block and set it to empty.
							var oldBlock = trackDataModel.getBlockObject(train.Line(), train.Block());
							oldBlock.Occupancy = "--";
							refreshCurrentRowObject(oldBlock);
							trackDataModel.getBlockModel(train.Line(), train.Block())(oldBlock);
							// Set the train's new block value.
							train.Block(message.commandValue);
							// Update the new block in the trackDataModel
							var newBlock = trackDataModel.getBlockObject(train.Line(), train.Block());
							newBlock.Occupancy = train.Name();
							refreshCurrentRowObject(newBlock);
							trackDataModel.getBlockModel(train.Line(), train.Block())(newBlock);
							// If the new block is a destination, update the next destination.
							if (train.Schedule()[train.NextDestinationIndex()] !== undefined) {
								if (train.Schedule()[train.NextDestinationIndex()].block == message.commandValue) {
									train.NextDestinationIndex(train.NextDestinationIndex() + 1);
								}
							}
							// Decrease authority.
							train.Authority(train.Authority() - 1);
							if (train.Authority() == 0) {
								train.Suggested_Speed(0);
							}
<<<<<<< HEAD
							
							// Update stations throughput
							for (var station_index = 0; station_index < trainsDataModel.stationsList().length; station_index++) {
								var station = trainsDataModel.stationsList()[station_index];
								if ((station.Line == train.Line()) && (station.Block == message.commandValue)) {
									station.Throughput(station.Throughput() + 1);
								}
							}
=======
>>>>>>> origin/CTCServer
						}
					}
					break;
				case "switch":
					console.log(message.commandValue);
					for (var index = 0; index < switchesDataModel.switchesList().length; index++) {
						var switchModel = switchesDataModel.switchesList()[index];
						var switchObject = switchModel();
						if ((switchObject.Line.toLowerCase() == message.subjectType) && (switchObject.Block_Numbers.indexOf(message.subjectID) >= 0)) {
							switchObject.Connection = JSON.parse(message.commandValue);
							switchModel(switchObject);
						}
					}
					break;
				case "crossing":
					for (var index = 0; index < crossingsDataModel.crossingsList().length; index++) {
						var crossingModel = crossingsDataModel.crossingsList()[index];
						var crossingObject = crossingModel();
						if ((crossingObject.Block_Number == message.subjectID) && (crossingObject.Line.toLowerCase() == message.subjectType)) {
							crossingObject.Status = message.commandValue.capitalizeFirstLetter();
							crossingModel(crossingObject);
						}
					}
					break;
				case "status":
					var block = trackDataModel.getBlockObject(message.subjectType, message.subjectID);
					block.Status = message.commandValue.capitalizeFirstLetter();
					refreshCurrentRowObject(block);
					trackDataModel.getBlockModel(message.subjectType, message.subjectID)(block);
					break;
				case "heater":
					var block = trackDataModel.getBlockObject(message.subjectType, message.subjectID);
					block.Heater = message.commandValue.capitalizeFirstLetter();
					refreshCurrentRowObject(block);
					break;
				case "lights":
					var block = trackDataModel.getBlockObject(message.subjectType, message.subjectID);
					block.Lights = message.commandValue.capitalizeFirstLetter();
					refreshCurrentRowObject(block);
					break;
				default:
					alert("Invalid message received!");
			}
		}
		setTimeout(checkServerUpdates, 500); // Server update interval
	});
}

function refreshCurrentRowObject(block) {
	// If the block that was updated matches the current row object, the current row object is refreshed.
	if ((trackDataModel.currentRowObject() !== undefined) && (block.Block_Number == trackDataModel.currentRowObject().Block_Number) && (block.Line == trackDataModel.currentRowObject().Line)) {
		trackDataModel.currentRowObject(block);
	}
}

function updateSuggestedSpeed(ID, value) {
	var updateString = "CTC : " + ID + " : set, Speed = " + value;
	sendStringToServer(updateString);
}

function updateAuthority(ID, value) {
	var updateString = "CTC : " + ID + " : set, Authority = " + value;
	sendStringToServer(updateString);
}