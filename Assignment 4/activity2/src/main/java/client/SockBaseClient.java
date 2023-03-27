package client;

import java.net.*;
import java.io.*;

import org.json.*;

import buffers.RequestProtos.Request;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

class SockBaseClient {

    public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int port = 9099; // default port

        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }

        // Ask user for username
        System.out.println("Please provide your name for the server.");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String strToSend = stdin.readLine();

        // Build the first request object just including the name
        Request op = Request.newBuilder()
                .setOperationType(Request.OperationType.NAME)
                .setName(strToSend).build();
        Response response;
        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // write to the server
            out = serverSock.getOutputStream();
            in = serverSock.getInputStream();

            op.writeDelimitedTo(out);

            // read from the server
            response = Response.parseDelimitedFrom(in);

            // print the server response.
            //GREETING response
            System.out.println(response.getMessage());

            while (true) {
                System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - quit the game");

                int choice = inputCheckChoice();

                Request rq = null;
                Response rp = null;

                switch (choice) {
                    case (1):
                        rq = Request.newBuilder()
                                .setOperationType(Request.OperationType.LEADER)
                                .setName(strToSend).build();
                        break;
                    case (2):
                        rq = Request.newBuilder()
                                .setOperationType(Request.OperationType.NEW)
                                .setName(strToSend).build();
                        System.out.println(rq.getOperationType());
                        rq.writeDelimitedTo(out);
                        while (true) {
                            Response rpPlay = null;
                            rpPlay = Response.parseDelimitedFrom(in);
                            if (rpPlay.getResponseType() == Response.ResponseType.PLAY && !rpPlay.getSecond()) {

                                if (rpPlay.getEval()){
                                    System.out.println("You found a match!");
                                }
                                System.out.println(rpPlay.getBoard());
                                System.out.println("Please enter the location of the FIRST tile:\nEXAMPLE response 'a2' (for row 1 and column 2)\nOR Type 'exit' to exit");
                                String tile = inputCheckTile();
                                if (tile.equals("exit")) {
                                    break;
                                }
                                rq = Request.newBuilder()
                                        .setOperationType(Request.OperationType.TILE1)
                                        .setTile(tile)
                                        .setName(strToSend).build();
                                System.out.println(rq.getOperationType());
                                //sends tile 1
                                System.out.println(rq.toBuilder());
                                rq.writeDelimitedTo(out);
                            } else if (rpPlay.getResponseType() == Response.ResponseType.PLAY && rpPlay.getSecond()) {
                                System.out.println(rpPlay.getBoard());
                                System.out.println("Please enter the location of the SECOND tile:\nEXAMPLE response 'a2' (for row 1 and column 2)\nOR Type 'exit' to exit");
                                String tile = inputCheckTile();
                                if (tile.equals("exit")) {
                                    break;
                                }
                                rq = Request.newBuilder()
                                        .setOperationType(Request.OperationType.TILE2)
                                        .setTile(tile)
                                        .setName(strToSend).build();
                                System.out.println(rq.getOperationType());
                                //sends tile 1
                                rq.writeDelimitedTo(out);
                            } else if (rpPlay.getResponseType() == Response.ResponseType.WON) {
                                System.out.println("Congratulations, you won!");
                                break;
                            }
                        }
                        break;
                    case (3):
                        rq = Request.newBuilder()
                            .setOperationType(Request.OperationType.QUIT)
                            .setName(strToSend).build();
                        break;
                    default:
                        System.out.println("Please select a valid option (1-3).");
                        break;
                }
                if (rq != null && choice != 2) {
                    System.out.println(rq.getOperationType());
                    rq.writeDelimitedTo(out);
                    rp = Response.parseDelimitedFrom(in);
                    //System.out.println(rp.getMessage());

                    if (rp.getResponseType() == Response.ResponseType.ERROR) {
                        System.out.println("OH NO, YOU SUCK AT READING THE INSTRUCTIONS");
                        System.out.println(rp.getMessage());
                    } else {
                        System.out.println("The response from the server: ");
                        if (rp.getResponseType() == Response.ResponseType.BYE){
                            System.out.println(rp.getMessage());
                            in.close();
                            out.close();
                            serverSock.close();
                            System.exit(0);
                        } else if (rp.getResponseType() == Response.ResponseType.LEADER) {

                        } else {
                            System.out.println("WRONG RESPONSE TYPE");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }

    public static int inputCheckChoice() {
        Scanner scan = new Scanner(System.in);
        int choice = 0;
        try {
            choice = scan.nextInt();
        }catch (InputMismatchException e){
            System.out.println("Need A Number!!!");
            inputCheckChoice();
        }
        return choice;
    }

    public static String inputCheckTile() {
        Scanner scan = new Scanner(System.in);
        String tile = null;
        try {
            tile = scan.nextLine();
            assert tile != null;
        }catch (Exception e){
            System.out.println("Try again in the format 'b2'!");
            inputCheckTile();
        }
        if (tile.equals("exit")) {
            return "exit";
        }
        if (tile.length() != 2) {
            System.out.println("Try again with 2 characters!");
            inputCheckTile();
        }
        return tile;
    }
}


