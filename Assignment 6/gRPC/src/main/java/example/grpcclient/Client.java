package example.grpcclient;

import com.google.protobuf.Service;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import service.*;
import test.TestProtobuf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.protobuf.Empty; // needed to use Empty

// just to show how to use the empty in the protobuf protocol
// Empty empt = Empty.newBuilder().build();

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class Client {
    private final EchoGrpc.EchoBlockingStub blockingStub;
    private final JokeGrpc.JokeBlockingStub blockingStub2;
    private final RegistryGrpc.RegistryBlockingStub blockingStub3;
    private final RegistryGrpc.RegistryBlockingStub blockingStub4;
    private final ZodiacGrpc.ZodiacBlockingStub blockingStub5;
    private final RockPaperScissorsGrpc.RockPaperScissorsBlockingStub blockingStub6;
    private final LibraryGrpc.LibraryBlockingStub blockingStub7;

    public static List<String> servicesList;

    /** Construct client for accessing server using the existing channel. */
    public Client(Channel channel, Channel regChannel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
        // responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to
        // reuse Channels.
        blockingStub = EchoGrpc.newBlockingStub(channel);
        blockingStub2 = JokeGrpc.newBlockingStub(channel);
        blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
        blockingStub4 = RegistryGrpc.newBlockingStub(channel);
        blockingStub5 = ZodiacGrpc.newBlockingStub(channel);
        blockingStub6 = RockPaperScissorsGrpc.newBlockingStub(channel);
        blockingStub7 = LibraryGrpc.newBlockingStub(channel);
    }

    public void askServerToParrot(String message) {
        ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
        ServerResponse response;
        try {
            response = blockingStub.parrot(request);
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
        System.out.println("Received from server: " + response.getMessage());
    }

    public void askForJokes(int num) {
        JokeReq request = JokeReq.newBuilder().setNumber(num).build();
        JokeRes response;
        try {
            response = blockingStub2.getJoke(request);
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
        System.out.println("Your jokes: ");
        for (String joke : response.getJokeList()) {
            System.out.println("--- " + joke);
        }
    }

    public void setJoke(String joke) {
        JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
        JokeSetRes response;
        try {
            response = blockingStub2.setJoke(request);
            System.out.println(response.getOk());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void getNodeServices() {
        GetServicesReq request = GetServicesReq.newBuilder().build();
        ServicesListRes response;
        try {
            response = blockingStub4.getServices(request);
            if (response.getIsSuccess()) {
                servicesList = response.getServicesList();
                System.out.println("Services: " + servicesList);
            }
            //System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void getServices() {
        GetServicesReq request = GetServicesReq.newBuilder().build();
        ServicesListRes response;
        try {
            response = blockingStub3.getServices(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void findServer(String name) {
        FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
        SingleServerRes response;
        try {
            response = blockingStub3.findServer(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void findServers(String name) {
        FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
        ServerListRes response;
        try {
            response = blockingStub3.findServers(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void play(String name, PlayReq.Played played) {
        PlayReq request = PlayReq.newBuilder().setName(name).setPlay(played).build();
        PlayRes response;
        try {
            //plays rock/paper/scis
            response = blockingStub6.play(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void leaderboard() {
        Empty request = Empty.newBuilder().build();
        LeaderboardRes response;
        try {
            //returns the list players
            response = blockingStub6.leaderboard(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void sign(String name, String month, int day) {
        SignRequest request = SignRequest.newBuilder().setName(name).setMonth(month).setDay(day).build();
        SignResponse response;
        try {
            //returns your zodiac traits
            response = blockingStub5.sign(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void find(String sign) {
        FindRequest request = FindRequest.newBuilder().setSign(sign).build();
        FindResponse response;
        try {
            //returns list of people with your zodiac match
            response = blockingStub5.find(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void borrow(String title, String has) {
        BorrowRequest request = BorrowRequest.newBuilder().setTitle(title).setHas(has).build();
        BorrowResponse response;
        try {
            response = blockingStub7.borrow(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: borrow " + e);
            return;
        }
    }

    public void donate(String title, String author, String genre) {
        DonateRequest request = DonateRequest.newBuilder().setTitle(title).setAuthor(author).setHas("nobody").setGenre(genre).build();
        DonateResponse response;
        try {
            //returns your zodiac traits
            response = blockingStub7.donate(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: donate " + e);
            return;
        }
    }

    public void books() {
        Empty request = Empty.newBuilder().build();
        BooksResponse response;
        try {
            //returns the list of books
            response = blockingStub7.books(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: books " + e);
            return;
        }
    }

    public int serviceDisplay(){
        Scanner scan = new Scanner(System.in);

//    System.out.println("THE LIST OF SERVICES:");
//    List<String> serviceNames = new ArrayList<>();
//    for (String service : servicesList) {
//      int startIndex = service.indexOf(".") + 1;
//      int endIndex = service.indexOf("/");
//      String serviceName = service.substring(startIndex, endIndex);
//      if (!serviceNames.contains(serviceName)) {
//        serviceNames.add(serviceName);
//      }
//    }
//
//    int i = 0;
//    for (String name: serviceNames) {
//      System.out.println(i + ": " + name);
//      i++;
//    }

        System.out.println("1: Library\n2: ...\n3: ...\n0: QUIT");

        System.out.println("Pick you service number: \nFor example: type '4' to use Library");
        int service = scan.nextInt();
        return service;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            System.out
                    .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
            System.exit(1);
        }
        int port = 9099;
        int regPort = 9003;
        String host = args[0];
        String regHost = args[2];
        String message = args[4];
        try {
            port = Integer.parseInt(args[1]);
            regPort = Integer.parseInt(args[3]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }

        // Create a communication channel to the server, known as a Channel. Channels
        // are thread-safe
        // and reusable. It is common to create channels at the beginning of your
        // application and reuse
        // them until the application shuts down.
        String target = host + ":" + port;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext().build();

        String regTarget = regHost + ":" + regPort;
        ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
        try {

            // ##############################################################################
            // ## Assume we know the port here from the service node it is basically set through Gradle
            // here.
            // In your version you should first contact the registry to check which services
            // are available and what the port
            // etc is.

            /**
             * Your client should start off with
             * 1. contacting the Registry to check for the available services
             * 2. List the services in the terminal and the client can
             *    choose one (preferably through numbering)
             * 3. Based on what the client chooses
             *    the terminal should ask for input, eg. a new sentence, a sorting array or
             *    whatever the request needs
             * 4. The request should be sent to one of the
             *    available services (client should call the registry again and ask for a
             *    Server providing the chosen service) should send the request to this service and
             *    return the response in a good way to the client
             *
             * You should make sure your client does not crash in case the service node
             * crashes or went offline.
             */

            // Just doing some hard coded calls to the service node without using the
            // registry
            // create client
            Client client = new Client(channel, regChannel);

            System.out.println("Services on the connected node. (without registry)");
            client.getNodeServices(); // get all registered services

            // ask the user for input how many jokes the user wants
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Scanner scan = new Scanner(System.in);

            int serviceTwo;

            boolean quit = false;
            while (!quit) {
                switch (client.serviceDisplay()) {
                    //library
                    case 1:
                        System.out.println("1: Borrow a book\n2: Donate a book\n3: See all books\nPick a request number:");
                        serviceTwo = scan.nextInt();
                        switch (serviceTwo) {
                            case 1:
                                System.out.println("Enter your name:");
                                String name = scan.nextLine();
                                System.out.println("Enter the title of your book:");
                                String title = scan.nextLine();
                                client.borrow(title, name);
                                break;
                            case 2:
                                System.out.println("Enter the title of your book:");
                                String title2 = scan.nextLine();
                                System.out.println("Enter the author of your book:");
                                String author2 = scan.nextLine();
                                System.out.println("Enter the genre of your book:");
                                String genre2 = scan.nextLine();
                                client.donate(title2, author2, genre2);
                                break;
                            case 3:
                                System.out.println("Here are all the library books:");
                                client.books();
                                break;
                            default:
                                System.out.println("Unknown service! Try '2' to donate a book!");
                                break;
                        }
                        break;
                    //service 2
                    case 2:
                        System.out.println("1: ...\n2: ...\n3: ...\nPick a request number:");
                        serviceTwo = scan.nextInt();
                        switch (serviceTwo) {
                            case 1:
                                System.out.println("Enter your name:");

                                break;
                            case 2:
                                System.out.println("Enter the title of your book:");

                                break;
                            case 3:
                                System.out.println("Here are all the library books:");

                                break;
                            default:
                                System.out.println("Unknown service! Try '...' to ...!");
                                break;
                        }
                        break;
                    //service 3
                    case 3:
                        System.out.println("1: ...\n2: ...\n3: ...\nPick a request number:");
                        serviceTwo = scan.nextInt();
                        switch (serviceTwo) {
                            case 1:
                                System.out.println("Enter your name: ");

                                break;
                            case 2:
                                System.out.println("Enter the title of your book:");

                                break;
                            case 3:
                                System.out.println("Here are all the library books:");

                                break;
                            default:
                                System.out.println("Unknown service! Try '...' to ...!");
                                break;
                        }
                        break;
                    case 0:
                        quit = true;
                        break;
                    default:
                        System.out.println("Unknown service! Try Library, ..., or ...");
                        break;
                }
            }

//      ///TESTING_LIBRARY///
//      client.donate("title1", "author1", "genre1");
//      client.donate("title2", "author2", "genre2");
//      client.books();
//      client.borrow("title1", "Alper");
//      client.books();

//      ///TESTING_ZODIAC///
//      System.out.print("Enter name: ");
//      String name = scan.nextLine();
//      System.out.print("Enter birth month: ");
//      String month = scan.nextLine();
//      System.out.print("Enter birth day: ");
//      int day = scan.nextInt();
//      client.sign(name, month, day);
//
//      client.sign("person1", "Jan", 11);
//
//      client.sign("person2", "Jan", 12);
//
//      client.find("Capricorn");

//      ///TESTING_RPS///
//      System.out.println("Enter your name:");
//      String name = reader.readLine();
//      System.out.println("Enter your move (0 for ROCK, 1 for PAPER, 2 for SCISSORS):");
//      int move = scan.nextInt();
//
//      // Convert the move integer to the corresponding enum value
//      PlayReq.Played played;
//      switch (move) {
//        case 0:
//          played = PlayReq.Played.ROCK;
//          break;
//        case 1:
//          played = PlayReq.Played.PAPER;
//          break;
//        case 2:
//          played = PlayReq.Played.SCISSORS;
//          break;
//        default:
//          System.err.println("Invalid move");
//          return; // exit the program or handle the error as appropriate
//      }
//
//      client.play(name, played);
//      client.leaderboard();

//      ////TESTING////
//      // call the parrot service on the server
//      client.askServerToParrot(message);
//
//      // Reading data using readLine
//      System.out.println("How many jokes would you like?"); // NO ERROR handling of wrong input here.
//      String num = reader.readLine();
//
//      // calling the joked service from the server with num from user input
//      client.askForJokes(Integer.valueOf(num));
//
//      // adding a joke to the server
//      client.setJoke("I made a pencil with two erasers. It was pointless.");
//
//      // showing 6 joked
//      client.askForJokes(Integer.valueOf(6));
//
//      // list all the services that are implemented on the node that this client is connected to
//
//      System.out.println("Services on the connected node. (without registry)");
//      client.getNodeServices(); // get all registered services
//      ////TESTING////
//
//      // ############### Contacting the registry just so you see how it can be done
//
//      if (args[5].equals("true")) {
//        // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
//        // get thread's services
//        client.getServices(); // get all registered services
//
//        // get parrot
//        client.findServer("services.Echo/parrot"); // get ONE server that provides the parrot service
//
//        // get all setJoke
//        client.findServers("services.Joke/setJoke"); // get ALL servers that provide the setJoke service
//
//        // get getJoke
//        client.findServer("services.Joke/getJoke"); // get ALL servers that provide the getJoke service
//
//        // does not exist
//        client.findServer("random"); // shows the output if the server does not find a given service
//      }

        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent
            // leaking these
            // resources the channel should be shut down when it will no longer be used. If
            // it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
