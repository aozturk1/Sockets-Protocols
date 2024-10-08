package server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Logs;
import buffers.RequestProtos.Message;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

class SockBaseServer {
    static String logFilename = "logs.txt";
    static String leaderboardFilename = "leaderboard.txt";

    ServerSocket serv = null;
    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket = null;
    int port = 9099; // default port
    Game game;


    public SockBaseServer(Socket sock, Game game){
        this.clientSocket = sock;
        this.game = game;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (Exception e){
            System.out.println("Error in constructor: " + e);
        }
    }

    // Handles the communication right now it just accepts one input and then is done you should make sure the server stays open
    // can handle multiple requests and does not crash when the server crashes
    // you can use this server as based or start a new one if you prefer. 
    public void start() throws IOException {
        String name = "";

        System.out.println("Ready...");
        try {
            // read the proto object and put into new objct
            boolean quit = false;
            Response leaderboard;

            while (!quit) {
                Request op = Request.parseDelimitedFrom(in);
                String result = null;
                Response rp;
                game.newGame();
                switch (op.getOperationType()) {
                    case NAME:
                        // get name from proto object
                        name = op.getName();

                        // writing a connect message to the log with name and CONNENCT
                        writeToLog(name, Message.CONNECT);
                        System.out.println("Got a connection and a name: " + name);
                        rp = Response.newBuilder()
                                .setResponseType(Response.ResponseType.GREETING)
                                .setMessage("Hello " + name + " and welcome. Welcome to a simple game of tile matching. ")
                                .build();
                        rp.writeDelimitedTo(out);
                        break;
                    case LEADER:
                        name = op.getName();
                        System.out.println("\n\nExample Leaderboard:");
//                        for (Entry lead : response3.getLeaderList()) {
//                            System.out.println(lead.getName() + ": " + lead.getWins());
//                        }
//                        rp = Response.newBuilder()
//                                .setResponseType(Response.ResponseType.LEADER)
//                                .setLeader()
//                                .build();
//                        rp.writeDelimitedTo(out);
                        break;
                    case NEW:
                        rp = Response.newBuilder()
                            .setResponseType(Response.ResponseType.PLAY)
                            .setBoard(game.getBoard()) // gets the hidden board
                            .setEval(false)
                            .setSecond(false)
                            .build();
                        rp.writeDelimitedTo(out);
                        break;
                    case TILE1:
                        String tile = op.getTile();
                        //ascii conversion -50 to get from a to 0
                        int tile1 = (tile.charAt(0)-'a');
                        System.out.println(tile1);
                        int tile2 = (tile.charAt(1)-'1');
                        System.out.println(tile2);
                        rp = Response.newBuilder()
                                .setResponseType(Response.ResponseType.PLAY)
                                .setBoard(game.tempFlipWrongTiles(tile1,tile2)) // gets the hidden board
                                .setEval(false)
                                .setSecond(true)
                                .build();
                        rp.writeDelimitedTo(out);
                        break;
                    case TILE2:
                        leaderboard = Response.newBuilder()
                                .build();
                        break;
                    case QUIT:
                        name = op.getName();
                        System.out.println("Quitting session for " + name + " ...");
                        rp = Response.newBuilder()
                                .setResponseType(Response.ResponseType.BYE)
                                .setMessage("Bye " + name + ". Don't come back")
                                .build();
                        rp.writeDelimitedTo(out);
                        //quit = true;
                        break;
                    default:
                        System.out.println("IDK WHAT WENT WRONG");
                        break;
                }
//                // if the operation is NAME (so the beginning then say there is a commention and greet the client)
//                if (op.getOperationType() == Request.OperationType.NAME) {
//                    // get name from proto object
//                    name = op.getName();
//
//                    // writing a connect message to the log with name and CONNENCT
//                    writeToLog(name, Message.CONNECT);
//                    System.out.println("Got a connection and a name: " + name);
//                    Response response = Response.newBuilder()
//                            .setResponseType(Response.ResponseType.GREETING)
//                            .setMessage("Hello " + name + " and welcome. Welcome to a simple game of battleship. ")
//                            .build();
//                    response.writeDelimitedTo(out);
//                }
//
//                // Example how to start a new game and how to build a response with the board which you could then send to the server
//                // LINES between ====== are just an example for Protobuf and how to work with the differnt types. They DO NOT
//                // belong into this code as is!
//
//                // ========= Example start
//                game.newGame(); // starting a new game
//
//                // Example on how you could build a simple response for PLAY as answer to NEW
//                Response response2 = Response.newBuilder()
//                        .setResponseType(Response.ResponseType.PLAY)
//                        .setBoard(game.getBoard()) // gets the hidden board
//                        .setEval(false)
//                        .setSecond(false)
//                        .build();
//
//                // this just temporarily unhides, the "hidden" image in game is still the same
//                System.out.println("One flipped tile");
//                System.out.println(game.tempFlipWrongTiles(1, 2));
//
//                System.out.println("Two flipped tiles");
//                System.out.println(game.tempFlipWrongTiles(1, 2, 2, 4));
//
//                System.out.println("Flip for found match, hidden in game will now be changed");
//                // I flip two tiles here but it will NOT necessarily be a match, since I hard code the rows/cols here
//                // and the board is randomized
//                game.replaceOneCharacter(1, 2);
//                game.replaceOneCharacter(2, 4);
//                System.out.println(game.getBoard()); // shows the now not hidden tiles
//
//
//                // On the client side you would receive a Response object which is the same as the one in line 73, so now you could read the fields
//                System.out.println("\n\nExample response:");
//                System.out.println("Type: " + response2.getResponseType());
//                System.out.println("Board: \n" + response2.getBoard());
//                System.out.println("Eval: \n" + response2.getEval());
//                System.out.println("Second: \n" + response2.getSecond());
//
//                // Creating Entry and Leader response
//                Response.Builder res = Response.newBuilder()
//                        .setResponseType(Response.ResponseType.LEADER);
//
//                // building an Entry for the leaderboard
//                Entry leader = Entry.newBuilder()
//                        .setName("name")
//                        .setWins(0)
//                        .setLogins(0)
//                        .build();
//
//                // building another Entry for the leaderboard
//                Entry leader2 = Entry.newBuilder()
//                        .setName("name2")
//                        .setWins(1)
//                        .setLogins(1)
//                        .build();
//
//                // adding entries to the leaderboard
//                res.addLeader(leader);
//                res.addLeader(leader2);
//
//                // building the response
//                Response response3 = res.build();
//
//                // iterating through the current leaderboard and showing the entries
//
//                System.out.println("\n\nExample Leaderboard:");
//                for (Entry lead : response3.getLeaderList()) {
//                    System.out.println(lead.getName() + ": " + lead.getWins());
//                }
//
//                // ========= Example end

            }

//            if (quit){
//                out.close();
//                in.close();
//                clientSocket.close();
//                System.exit(0);
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (out != null)  out.close();
            if (in != null)   in.close();
            if (clientSocket != null) clientSocket.close();
        }
    }


    /**
     * Writing a new entry to our log
     * @param name - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect) 
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message){
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();
            System.out.println(date);

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " +  name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log: logsObj.getLogList()){

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        }catch(Exception e){
            System.out.println("Issue while trying to save");
        }
    }

    /**
     * Reading the current log file
     * @return Logs.Builder a builder of a logs entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception{
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
        }
    }

//    public static void writeToLeaderboard(String name, Message message){
//        try {
//            // read old log file
//            Logs.Builder logs = readLeaderboardFile();
//
//            // get current time and data
//            Date date = java.util.Calendar.getInstance().getTime();
//            System.out.println(date);
//
//            // we are writing a new log entry to our log
//            // add a new log entry to the log list of the Protobuf object
//            logs.addLog(date.toString() + ": " +  name + " - " + message);
//
//            // open log file
//            FileOutputStream output = new FileOutputStream(leaderboardFilename);
//            Logs logsObj = logs.build();
//
//            // This is only to show how you can iterate through a Logs object which is a protobuf object
//            // which has a repeated field "log"
//            for (String log: logsObj.getLogList()){
//                System.out.println(log);
//            }
//
//            // write to log file
//            logsObj.writeTo(output);
//        }catch(Exception e){
//            System.out.println("Issue while trying to save");
//        }
//    }
//
//    public static Logs.Builder readLeaderboardFile() throws Exception{
//        Logs.Builder logs = Logs.newBuilder();
//
//        try {
//            // just read the file and put what is in it into the logs object
//            return logs.mergeFrom(new FileInputStream(leaderboardFilename));
//        } catch (FileNotFoundException e) {
//            System.out.println(leaderboardFilename + ": File not found.  Creating a new file.");
//            return logs;
//        }
//    }

    public static void main (String args[]) throws Exception {
        Game game = new Game();

        if (args.length != 2) {
            System.out.println("Expected arguments: <port(int)> <delay(int)>");
            System.exit(1);
        }
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        Socket clientSocket = null;
        ServerSocket serv = null;

        try {
            port = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|sleepDelay] must be an integer");
            System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
        while (true) {
            clientSocket = serv.accept();
            //SockBaseServer server = new SockBaseServer(clientSocket, game);
            Thread handlerThread = new Thread(new ConnectionHandler(clientSocket, game));
            handlerThread.start();
            //server.start();
        }
    }
}

class ConnectionHandler implements Runnable {

    private Socket sock;
    private Game game;

    public ConnectionHandler(Socket sock, Game game) {
        this.sock = sock;
        this.game = game;
    }

    public void run() {
        SockBaseServer server = new SockBaseServer(sock, game);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("close socket of client ");
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

