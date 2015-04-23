Notes:

Coding Standard: the default Java coding standard is followed, rather than the one set aside in previous documentation.  Java should look like Java.  http://www.oracle.com/technetwork/java/codeconvtoc-136057.html

Authority: the total path distance, from the current location to the end stop.
		   the train knows to when to stop at a station if there is a dwell time for that block.
		   
Reverse Trains: the CTC leaves the option for dispatchers to route trains backwards.
				this is because the CTC does not verify that trains do not become deadlocked facing the same direction.
				in this case, the dispatcher must route the trains backwards manually to resolve the deadlock.