Alper Ozturk

#SCREAN CAPTURE LINK
https://youtu.be/wQyIWr2uD1M

## Description
A program that is a simple TCP connection client server based quote guessing game with minimal simple GUI and a JSON custom protocol design. Server will send a picture of a quote which will be guessed by the client which has to add up to 3 correct guesses in 1 minute of gameplay for a win. Programs also features a scoreboard which can be called to see previous players/highscores.

## Check-List
### 2 Preliminary Things
- [X] 1 Structure (two programs one TCP one UDP(insteadData putJSONtoString jsonStringtoBytes), separate folder called tcp and udp)
- [X] 2 Gradle file (for each project)
- [X] 3 Run (gradle runServer - Pport = port and gradle runClient - Pport = port -Phost = hostIP)
- [X] 4 Also run (gradle runServer and gradle runClient shown in a 5 min screen capture showing both and their features)
- [X] 5 A README.md (for each project)
    - [X] a) Description
    - [X] b) Checklist
    - [X] c) Explanation of how to run
    - [X] d) UML Diagram
    - [X] e) Protocol
    - [ ] f) Explain how your code is robust

### 3.1 TCP The Game
- [X] 1 Client connects to server and server ask for player name
- [X] 2 Client send name, server greet the player
- [X] 3 Choice between a leader board or playing the game for client
- [X] 4 Leaderboard with all previous players names and points (in server)
  - [ ] 4 EXTRA CREDIT (leaderboard doesn't reset with connection)
- [X] 5 After start game, first quote sent, printed intended answer in the server terminal 
- [X] 6 Client can guess "Deadpool", type "more", or "next"
- [X] 7 Check the guess and respond with new quote, win, or try again
- [ ] 8 Typing "more" will get the client a new quote from same character until they are informed no more
- [ ] 9 Typing "next" will get a new character quote or an old one when out of new ones without crashing
- [ ] 10 If 3 guesses within 1 minute then "winner" image and message will be sent
- [ ] 11 If no 3 guesses within 1 minute then "loser" image and message will be sent
- [ ] 12 Needed point system for more points with faster guesses (maintained on server)
- [ ] 13 At the end display points. If they win, edit leaderboard
- [X] 14 Input evaluation happens on server side (pictures, answers, points, leaderboard)
- [ ] 15 Robust protocol (detailed protocol)
- [X] 16 Error handling on both sides
- [ ] 17 After game ends, client can replay by typing name or choosing quit
- [ ] 18 Second client attempt (make them wait or don't let them do anything)

### 3.2 UDP The Game
- [ ] 1 Asking and sending of the user's name
- [ ] 2 Send image from server to client
- [ ] 3 Explain how protocol changed for UDP

## How to run
Server should run through gradle runServer − P port = port and the
client through gradle runClient − P port = port − P host = hostIP

or

It will run default gradle runServer and gradle runClient

## UML Sequence Diagram
Picture Provided with the files as SequenceDiagram.jpg

## Protocol
type: game|scoreboard|guess|next|more

name
  Request
    {'type': 'name',
    'type': <String>}

  Response
    {'type': 'name',
    'data': <String>}

    happy case:
    'data' is a greating

    error:
    - empty request 

game
  Request
        {'type': 'game'}

  Response
        {'type': 'game',
        'data': <String>} -- image path

    happy case:
    'data' is a image path

    error:
    - empty request 

scoreboard
  Request
    {'type': 'scoreboard'}

  Response
    {'type': 'scoreboard',
    'data': <String>} -- string leaderboard separated by commas (parse using commas)

    happy case:
    'data' is a leaderboard

    error:
    - empty request
    'data' is a message

guess
  Request
    {'type': 'guess',
    'data': <String>}

  Response
    {'type': 'guess',
    'data': <String>, -- used if guess is incorrect
    'img': <String>} -- used if guess is correct

    happy case:
    'data' is a success message
    'img' is next image path

    error:
    - empty request 
    'data' is a helpful tip

next
  Request
    {'type': 'next'}

  Response
    TO BE IMPLEMENTED

    happy case:
    TO BE IMPLEMENTED

    error:
    TO BE IMPLEMENTED

more
  Request
    {'type': 'more'}

  Response
    TO BE IMPLEMENTED

    happy case:
    TO BE IMPLEMENTED

    error:
    TO BE IMPLEMENTED

## How is it robust
YET TO BE IMPLEMENTED
