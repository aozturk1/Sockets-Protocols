Video Link: https://youtu.be/ndoB3BxtELk

# Assignment 5 Activity 2 Leader
## Description
The task was to implement a simplified consensus algorithm between a number of nodes with one leader 
node which all the other nodes can talk to. Basic structure: Client sends a request to the leader node, 
the leader then asks the other nodes for consent, receives the answers and handles things accordingly. 
Client (command line) should communicates only with the leader node. The client can ask for a 
credit (loan) of a specific amount or pay back some or all of their existing credit.

## How to run it
Arguments are name and port. Start 2 to many peers each having a unique port number.
First port is your port and second port is one of the peers from the peer group you want to join.

1. gradle leader --console=plain -q

2. gradle node -Pmoney=1000 --console=plain -q  (NOTE: as many as you want)

3. gradle client --console=plain -q (NOTE: only one)

## How different systems communicate with each other
I designed the systems to communicate with each other without threading the communication.
I have a ServerLeader(similar to activity 1) that handles incoming sockets and a main Leader that communicates to client and nodes(using loops).
Client and Node connect to the Leader using the given server socket which the ServerLeader deals with and adds them to the respective lists.

## Requirements that I think I fulfilled
-Using Git and GitHub

-Creating a separate program for each activity

-Doing a README.md for each activity

-Well structured project

-Screencast of Activity although

-Can run leader, nodes(can give money amount if you want), and client through gradle

-Asking Ids for Client and Nodes

-Displaying options menu

-Leader receives requests(both ServerLeader and Leader class)

-All backend of Credit

-Work in progress Pay Back

## Protocol
...
