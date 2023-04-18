Video Link: https://youtu.be/ndoB3BxtELk

GitHub: https://github.com/aozturk1/ser321-spring2023-C-aozturk1/tree/master/Assignment%205

# Assignment 5 Activity 2 Leader
## Description
The task was to implement a simplified consensus algorithm between a number of nodes with one leader 
node which all the other nodes can talk to. Basic structure: Client sends a request to the leader node, 
the leader then asks the other nodes for consent, receives the answers and handles things accordingly. 
Client (command line) should communicates only with the leader node. The client can ask for a 
credit (loan) of a specific amount or pay back some or all of their existing credit.

## How to run it
Only the node has an argument for the amount of money that you want to start the bank with.
Run in the order below, one leader, multiple nodes, one client.

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

Client.java
Request Credit
{'id': int,
'type': "request",
'value': int}

Response
{'type': "request",
'value': <String>}

Pay Credit
{'id': int,
'type': "request",
'value': int}

Response
{'type': "request",
'value': <String>}


Leader.java
Request Credit
{'id': int,
'type': "eligibility",
'value': int}

Response
{'type': "request",
'value': int money}

Pay Credit
{'id': int,
'type': "pay",
'value': int}

Response
{'type': "pay",
'value': int}


Nnode.java
Request Credit
{'id': int,
'type': "eligibility",
'value': int}

Response
{'type': "eligibility",
'value': int money}

Pay Credit
{'id': int,
'type': "pay",
'value': int money}

Response
{'id': int id,
'type': "pay",
'value': int money}

Confirm
{'id': int,
'type': "confirm",
'value': int}

Response
{'id': int id,
'type': "confirm",
'confirm': "true/false",
'value': int money}