Video Link:

# Assignment 4 Activity 2
### Description:
Using a simple Proto protocol, a multi-threaded multiplayer Client/Server tile matching game. A Leaderboard functionality that
displays a list of winners and players, a quit function that lets you disconnect from the host, and
a Play functionality where you flip one tile at a time to see if you can find all the tile matches.

### The procotol
See the PROTOCOL.md for details on the REQUESTS and RESPONSES

### How to run it (optional)
The proto file can be compiled using
``gradle generateProto``
This will also be done when building the project.
You should see the compiled proto file in Java under build/generated/source/proto/main/java/buffers
Now you can run the client and server 

#### Default 
Server is Java
Per default on 9099
runServer

Clients runs per default on 
host localhost, port 9099
Run Java:
	runClient

#### With parameters:
Java
gradle runClient -Pport=9099 -Phost='localhost'
gradle runServer -Pport=9099

### Requirements that I think I fulfilled
-Using Git and GitHub
-Creating a separate program for each activity
-Doing a README.md for each activity
-Given protocols are implemented for Protobuff
	-Leaderboard
	-Menu
	-Displaying Tiles
	-Winning
	-Error Handling
	-AWS server
	-exit functionality
	-Quit
-Making a multi-threaded
-Gradle can run different tasks for each Task, with default if needed
-Screencast of Activity