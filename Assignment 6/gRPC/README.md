# GRPC Services and Registry Project Description

A simple gRPC service with Node, Client, and Registry. Client can connect to the nodes
to get a list of services that are available. Client can also connect to the Registry first
to pick a service that is offered by different Nodes and then connect to that Node that offers
the service that is picked. This specific Node includes a Hometown, Recipes, Library, and couple
other smaller services.

## Requirements Fulfilled
- The README.md
- Task1
  - Run service through runNode and runClient
  - 2 services (recipe and hometowns)
  - Client lets user decide service and request
  - Option for auto run with -Pauto=1
  - Server and Client robust
- Task2 (Custom Library Service)
  - Protocol design (shown in README.md and library.proto)
  - Client side methods
  - Server side methods
- Task3.1
  - Updating the gradle
  - Needed files
  - Services are registered correctly
- Task3.2
  - Online Node
  - Slack client run line
  - Slack feedback to others' nodes
- Screencast

## How to run
### gradle runRegistryServer
gradle runRegistryServer -q --console=plain

### gradle runNode
gradle runNode

### gradle runClient
gradle runClient -q --console=plain

gradle runClient -Pauto=1 -q --console=plain

## How to work with the program
The program will work with default arguments unless you want a specific port or want
to run the program in auto which will display a test set of hardcoded GRPC method calls
that show the different services and requests. 

If running the Node and Client with default arguments, you will be asked to choose from
different services as well as specific requests by putting in "1" or "2" (integer input values)
to chose specific services from the list given. Certain services and requests will require 
input such as name or id which will scan for input. There are prints on console for how to work with
the program when running.

## Protocol Design For Task2
Library Service
```
service Library {
rpc borrow (BorrowRequest) returns (BorrowResponse) {}
rpc donate (DonateRequest) returns (DonateResponse) {}
rpc books (google.protobuf.Empty) returns (BooksResponse) {}
}
```
message Book {
string title = 1;
string author = 2;
string has = 3;
string genre = 4;
}
```
message BorrowRequest {
string title = 1;
string has = 2;
}
```
message BorrowResponse {
bool isSuccess = 1;
string error = 2;
string message = 3;
}
```
message DonateRequest {
string title = 1;
string author = 2;
string has = 3;
string genre = 4;
}
```
message DonateResponse {
bool isSuccess = 1;
string error = 2;
string message = 3;
}
```
message BooksResponse {
bool isSuccess = 1;
string error = 2;
repeated Book books = 3;
}
```