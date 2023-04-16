Video Link: https://youtu.be/8EEUfyCWr3g

# Assignment 5 Activity 1 Peer2Peer
## Description
Very basic peer-2-peer for a chat. All peers can communicate with each other.
Each peer is client and server at the same time.
When started the peer has a serverthread in which the peer listens for potential other peers to connect.
The peer can choose to listen to others by providing a port number of one of the peer from the peer group.
For every one of these peers that this peer wants to listen to a thread is created.
As soon as you have two peers connected, you can start chatting.
Client Thread constantly listens.
ServerThread writes every registered listener (the other peers).

## What I decided to do
First I made it so that the first peer never calls updateListenToPeers method but is instead
    put to sleep until a second peer joins them.
Next I made it so that if you are not the first peer and have a 3rd argument(the port you want
    to connect to) you automatically connect to that peer by calling updateListenToPeers using
    that socket.
After that, all peers would be at a state of waiting for a new peer or waiting for messages inputs.
Finally, if a new peer is detected through one of the peers' socket accepts, they will send this
    new list of sockets to every other peer and every other peer would start a new client thread
    for each peer that they are not already connect to.

## Requirements that I think I fulfilled
-Using Git and GitHub

-Creating a separate program for each activity

-Doing a README.md for each activity

-Somewhat automatic connection(fully for the first two, WIP for more than 2)

-Screencast of Activity although

### How to run it

Arguments are name and port. Start 2 to many peers each having a unique port number.
First port is your port and second port is one of the peers from the peer group you want to join.

gradle runPeer --args "Name1 7000" --console=plain -q

gradle runPeer --args "Name2 8000 7000" --console=plain -q

gradle runPeer --args "Name3 9000 7000" --console=plain -q
