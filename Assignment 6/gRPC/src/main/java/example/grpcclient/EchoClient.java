package example.grpcclient;

import com.google.protobuf.Service;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
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
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2;
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;
  private final RegistryGrpc.RegistryBlockingStub blockingStub4;
  private final ZodiacGrpc.ZodiacBlockingStub blockingStub5;
  private final RockPaperScissorsGrpc.RockPaperScissorsBlockingStub blockingStub6;
  private final LibraryGrpc.LibraryBlockingStub blockingStub7;
  private final HometownsGrpc.HometownsBlockingStub blockingStub8;
  private final RecipeGrpc.RecipeBlockingStub blockingStub9;

  public static List<String> servicesList;
  public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel, Channel regChannel) {
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
    blockingStub8 = HometownsGrpc.newBlockingStub(channel);
    blockingStub9 = RecipeGrpc.newBlockingStub(channel);
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
    System.out.println("Received from server: askServerToParrot" + response.getMessage());
  }

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;
    try {
      response = blockingStub2.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: askForJokes" + e);
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
      System.err.println("RPC failed: setJoke" + e);
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
      System.err.println("RPC failed: getNodeServices" + e);
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
      System.err.println("RPC failed: getServices" + e);
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
      System.err.println("RPC failed: findServer" + e);
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
      System.err.println("RPC failed: findServers" + e);
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
      System.err.println("RPC failed: play" + e);
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
      System.err.println("RPC failed: leaderboard" + e);
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
      System.err.println("RPC failed: sign" + e);
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
      System.err.println("RPC failed: find" + e);
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

  ///////////////HOMETOWN//////////////////

  public void read() {
    Empty request = Empty.newBuilder().build();
    HometownsReadResponse response;
    try {
      response = blockingStub8.read(request);
      if (response.getIsSuccess()){
        for (Hometown hometown : response.getHometownsList()) {
          System.out.println(hometown);
        }
      } else {
        System.out.println(response.getError());
      }
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: read " + e);
      return;
    }
  }

  public void search(String city) {
    HometownsSearchRequest request = HometownsSearchRequest.newBuilder().setCity(city).build();
    HometownsReadResponse response;
    try {
      response = blockingStub8.search(request);
      if (response.getIsSuccess()){
        for (Hometown hometown : response.getHometownsList()) {
          System.out.println(hometown.getName());
        }
      } else {
        System.out.println(response.getError());
      }
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: search " + e);
      return;
    }
  }

  public void write(String name, String city, String region) {
    Hometown hometown = Hometown.newBuilder().setName(name).setCity(city).setRegion(region).build();
    HometownsWriteRequest request = HometownsWriteRequest.newBuilder().setHometown(hometown).build();
    HometownsWriteResponse response;
    try {
      response = blockingStub8.write(request);
      if (response.getIsSuccess()){
        System.out.println("You were added to the database!");
      } else {
        System.out.println(response.getError());
      }
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: write " + e);
      return;
    }
  }

  ///////////////RECIPE//////////////////

  public void addRecipe(String name, String author, List<Ingredient> ingredients) {
    RecipeReq request = RecipeReq.newBuilder().setName(name).setAuthor(author).addAllIngredient(ingredients).build();
    RecipeResp response;
    try {
      response = blockingStub9.addRecipe(request);
      if (response.getIsSuccess()){
        System.out.println(response.getMessage());
      } else {
        System.out.println(response.getError());
      }
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: read " + e);
      return;
    }
  }

  public void viewRecipes() {
    Empty request = Empty.newBuilder().build();
    RecipeViewResp response;
    try {
      response = blockingStub9.viewRecipes(request);
      if (response.getIsSuccess()){
        for (RecipeEntry recipeEntry : response.getRecipesList()) {
          System.out.println(recipeEntry);
        }
      } else {
        System.out.println(response.getError());
      }
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: search " + e);
      return;
    }
  }

  public void rateRecipe(int id, float rating) {
    RecipeRateReq request = RecipeRateReq.newBuilder().setId(id).setRating(rating).build();
    RecipeResp response;
    try {
      response = blockingStub9.rateRecipe(request);
      if (response.getIsSuccess()){
        System.out.println(response.getMessage());
      } else {
        System.out.println(response.getError());
      }
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: write " + e);
      return;
    }
  }

  public int serviceDisplay(){
    System.out.println("-----------------------");
    System.out.println("1: Library\n2: Hometowns\n3: Recipe\n0: QUIT");

    System.out.println("Pick you service number: \nFor example: type '1' to use Library");
      int service = 0;
      try {
          service = Integer.parseInt(reader.readLine());
      } catch (IOException e) {
          System.out.println("Need to enter a number!");
      }
      return service;
  }

  public static List<Ingredient> addIngredientsFromUser() throws IOException {
    List<Ingredient> ingredients = new ArrayList<>();
    while (true) {
      System.out.println("Enter ingredient name (or enter 'done' to finish):");
      String ingredientName = reader.readLine();
      if (ingredientName.equals("done")) {
        break;
      }
      System.out.println("Enter quantity (in grams):");
      int quantity = Integer.parseInt(reader.readLine());
      System.out.println("Enter details:");
      String details = reader.readLine();

      Ingredient ingredient = Ingredient.newBuilder()
              .setName(ingredientName)
              .setQuantity(quantity)
              .setDetails(details)
              .build();
      ingredients.add(ingredient);
    }

    return ingredients;
  }


  public static void main(String[] args) throws Exception {
    if (args.length != 7) {
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

      // create client
      EchoClient client = new EchoClient(channel, regChannel);

      boolean quit = false;

      //AUTO RUN TESTING
      System.out.println(args[6]);
      if (args[6].equals("1")) {
        //Just doing some hard coded calls to the service node without using the registry
        System.out.println("Services on the connected node. (without registry)");
        client.getNodeServices(); // get all registered services

        ///TESTING_LIBRARY///
        System.out.println("\n>>>>>>>>>>>>>>>\nTESTING LIBRARY SERVICE in the order of DONATE -> DONATE -> BOOKS -> BORROW -> BOOKS");
        client.donate("title1", "author1", "genre1");
        client.donate("title2", "author2", "genre2");
        client.books();
        client.borrow("title1", "Alper");
        client.books();

        ///TESTING_HOMETOWNS///
        System.out.println("\n>>>>>>>>>>>>>>>\nTESTING HOMETOWNS SERVICE in the order of READ -> SEARCH -> WRITE -> WRITE -> READ -> SEARCH");
        System.out.println("Fetching all hometowns:");
        client.read();
        client.search("Gilbert");
        client.write("Alper", "Gilbert","UnitedStates");
        client.write("NotAlper","NotGilbert","NotUnitedStates");
        client.read();
        client.search("Gilbert");

        ///TESTING_RECIPE///
        System.out.println("\n>>>>>>>>>>>>>>>\nTESTING HOMETOWNS SERVICE in the order of VIEW -> RATE -> ADD -> ADD -> VIEW -> RATE -> VIEW");

        System.out.println("Fetching all recipes:");
        client.viewRecipes();
        client.rateRecipe(3, 5);
        String recipeName = "Spaghetti Bolognese";
        String authorName = "John Smith";
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.newBuilder()
                .setName("Spaghetti")
                .setQuantity(200)
                .setDetails("dried")
                .build());
        ingredients.add(Ingredient.newBuilder()
                .setName("Ground beef")
                .setQuantity(500)
                .setDetails("")
                .build());
        ingredients.add(Ingredient.newBuilder()
                .setName("Onion")
                .setQuantity(1)
                .setDetails("finely chopped")
                .build());
        client.addRecipe(recipeName, authorName, ingredients);
        String recipeName2 = "Apple Spaghetti Pie";
        String authorName2 = "Alper Smith";
        List<Ingredient> ingredients2 = new ArrayList<>();
        ingredients.add(Ingredient.newBuilder()
                .setName("Spaghetti")
                .setQuantity(200)
                .setDetails("dried")
                .build());
        ingredients.add(Ingredient.newBuilder()
                .setName("Ground beef")
                .setQuantity(500)
                .setDetails("")
                .build());
        ingredients.add(Ingredient.newBuilder()
                .setName("Onion")
                .setQuantity(1)
                .setDetails("finely chopped")
                .build());
        client.addRecipe(recipeName2, authorName2, ingredients2);
        System.out.println("Fetching all recipes:");
        client.viewRecipes();
        client.rateRecipe(1, 3);
        System.out.println("Fetching all recipes:");
        client.viewRecipes();

        quit = true;
      }

      // ask the user for input how many jokes the user wants
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      int serviceTwo;

      while (!quit) {
        switch (client.serviceDisplay()) {
          //library
          case 1:
            System.out.println("1: Borrow a book\n2: Donate a book\n3: See all books\nPick a request number:");
            serviceTwo = Integer.parseInt(reader.readLine());
            switch (serviceTwo) {
              case 1:
                System.out.println("Enter your name:");
                String name = reader.readLine();
                System.out.println("Enter the title of your book:");
                String title = reader.readLine();
                client.borrow(title, name);
                break;
              case 2:
                System.out.println("Enter the title of your book:");
                String title2 = reader.readLine();
                System.out.println("Enter the author of your book:");
                String author2 = reader.readLine();
                System.out.println("Enter the genre of your book:");
                String genre2 = reader.readLine();
                client.donate(title2, author2, genre2);
                break;
              case 3:
                System.out.println("Fetching all books:");
                client.books();
                break;
              default:
                System.out.println("Unknown request! Try '2' to Donate a book!");
                break;
            }
            break;
          //hometowns
          case 2:
            System.out.println("1: Read hometowns\n2: Search hometown\n3: Write hometown\nPick a request number:");
            serviceTwo = Integer.parseInt(reader.readLine());
            switch (serviceTwo) {
              case 1:
                System.out.println("Fetching all hometowns:");
                client.read();
                break;
              case 2:
                System.out.println("Enter a city:");
                String city = reader.readLine();
                client.search(city);
                break;
              case 3:
                System.out.println("Enter name:");
                String name2 = reader.readLine();
                System.out.println("Enter city:");
                String city2 = reader.readLine();
                System.out.println("Enter region:");
                String region2 = reader.readLine();
                client.write(name2, city2,region2);
                break;
              default:
                System.out.println("Unknown request! Try '3' to Write hometown!");
                break;
            }
            break;
          //recipe
          case 3:
            System.out.println("1: Add a recipe\n2: View recipes\n3: Rate a recipe\nPick a request number:");
            serviceTwo = Integer.parseInt(reader.readLine());
            switch (serviceTwo) {
              case 1:
                System.out.println("Enter recipe name:");
                String name = reader.readLine();
                System.out.println("Enter author name:");
                String author = reader.readLine();
                List<Ingredient> ingredients = addIngredientsFromUser();
                client.addRecipe(name, author, ingredients);
                break;
              case 2:
                System.out.println("Fetching all recipes:");
                client.viewRecipes();
                break;
              case 3:
                System.out.println("Enter recipe id:");
                int id2 = Integer.parseInt(reader.readLine());
                System.out.println("Enter recipe rating between 1-5 (5 - meaning you LOVE it):");
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>check between 1-5
                int rating2 = Integer.parseInt(reader.readLine());
                float rating3 = (float)rating2;
                client.rateRecipe(id2, rating3);
                break;
              default:
                System.out.println("Unknown request! Try '1' to Add a recipe!");
                break;
            }
            break;
          case 0:
            quit = true;
            break;
          default:
            System.out.println("Unknown service! Try Library, Hometown, or Recipe");
            break;
        }
      }

//      ///TESTING_ZODIAC///
//      System.out.print("Enter name: ");
//      String name = reader.readLine();
//      System.out.print("Enter birth month: ");
//      String month = reader.readLine();
//      System.out.print("Enter birth day: ");
//      int day = reader.read();
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
//      int move = reader.read();
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
