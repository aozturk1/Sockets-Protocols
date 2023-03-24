Video Link:
https://www.youtube.com/watch?v=5hMe42zlc1s

# Assignment 4 Activity 1
## Description
A simple single-threaded server as Task1 and a multi-threaded server for Task2 that allows threads to grow unbounded and lastly a Task3 version that sets
the number of allowed clients at a time to a fixed number. The program code has a function for adding strings to an array, clearing strings from a string list,
finding a specific string in from the string list, displaying all the strings in the string list, delete all the strings in the string list, a prepend functionality, and 
a quit functionality.

## Protocol
### Requests
request: { "selected": <int: 1=add, 2=clear, 3=find, 4=display, 5=delete, 6=prepend
0=quit>, "data": <thing to send>}

  add: data <string> -- a string to add to the list, this should already mostly work -- might neet changes though
  clear: data <> -- no data given, clears the whole list
  find: data <string> -- display index of string if found, else -1
  display: data <> -- no data given, displays the whole list
  delete: data <int> -- int of index which entry in list should be deleted
  prepend: data <int> <string> -- index and string, prepends given string to the string that is already at that index (so it changes that entry), e.g. "data":"1 hello"
  quit: data <> -- no data given, will quit the connection

### Responses
success response: {"ok" : true, type": <String>, "data": <thing to return> }

type <String>: echoes original selected from request
data <string>: 
    add: return current list
    clear: return empty list
    find: return integer value of index where that string was found or -1
    display: return current
    delete: return current list
    prepend: return current list


error response: {"ok" : false, "message": <error string> }
error string: Should give good error message of what went wrong which can be displayed as is to the user in the Client

The program you are given should run out of the box, but it is not threaded nor does it include all the functionality, nor are the things that are already given necessarily complete. You should make all necessary changes while still adhering to the given protocol. 

## Requirements that I think I fulfilled 
-Using Git and GitHub
-Creating a separate program for each activity
-Doing a README.md for each activity
-Given protocols are implemented
    -Add
    -Clear
    -Find
    -Display
    -Delete
    -Prepend
    -Quit
-Making a multi-threaded version of Task1
-Making a bounded version of Task2
-Gradle can run different tasks for each Task, with default if needed
-Screencast of Activity although

## How to run the program
### Terminal
Base Code, please use the following commands:
```
    Default port = 8000
            host = localhost
            pool = 2
```   
    For Client, run "gradle runClient -Phost=localhost -Pport=9099 -q --console=plain"
    or
    "gradle runClient" for default
```   
```
    For Task1, run "gradle runTask1" for deafult
    or
    "gradle runTask1 -Pport=9099 -q --console=plain"
```
```
    For Task2, run "gradle runTask2" for default
    or
    "gradle runTask2 -Pport=9099 -q --console=plain"
```
```
    For Task3, run "gradle runTask3" for default
    or
    "gradle runTask3 -Pport=9099 -Ppool=3 -q --console=plain"
```
