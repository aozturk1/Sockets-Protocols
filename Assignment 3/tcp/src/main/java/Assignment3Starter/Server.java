package Assignment3Starter;

import netscape.javascript.JSObject;

import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;

import java.util.*;

import org.json.*;

import javax.imageio.ImageIO;

public class Server {
    public static void main (String args[]) throws Exception {
        Socket sock;
        int port = 8080;

        if (args.length == 1){ // port, if provided
            port= Integer.parseInt(args[0]);
        } else if (args.length > 1) {
            System.out.println("Wrong number of arguments: gradle runServer -Pport=8888");
            System.exit(0);
        }
//        try {
//            portNo = Integer.parseInt(args[0]);
//        } catch (NumberFormatException nfe) {
//            System.out.println("port must be integer");
//            System.exit(2);
//        }


        try {
            //open socket
            ServerSocket serv = new ServerSocket(port); // create server socket on port 8888
            System.out.println("Server ready for connections");

            while (true){
                System.out.println("Server waiting for a connection");
                sock = serv.accept(); // blocking wait

                // setup the object reading channel
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

                // get output channel
                OutputStream out = sock.getOutputStream();

                // create an object output writer (Java only)
                ObjectOutputStream os = new ObjectOutputStream(out);

                String s = (String) in.readObject();
                JSONObject req = new JSONObject(s);
                JSONObject rep = new JSONObject();

                //testing scoreboard extra credit
                String path = "src/main/java/Assignment3Starter/Json.json";
                //for testing scoreboard
                Random rand = new Random();
                JSONArray jsonArray = new JSONArray();

                boolean gameOn = false;
                String currentQuote = "";
                int quoteNum = 1;
                ArrayList<String> leaderName = new ArrayList<String>();
                ArrayList<String> leaderScore = new ArrayList<String>();
                //2D arraylist would have been a better idea
                ArrayList<String> charcters = new ArrayList<String>();
                charcters.add("Captain_America");
                charcters.add("Darth_Vader");
                charcters.add("Homer_Simpson");
                charcters.add("Jack_Sparrow");
                charcters.add("Joker");
                charcters.add("Tony_Stark");
                charcters.add("Wolverine");
                //to put in the win/loss request
                //leader.add("Sam");

                if (req.getString("type").equals("init")) {
                    s = "What is your player name?";
                    String s2 = "img/hi.png";
                    rep.put("type", "init");
                    rep.put("data", s);
                    rep.put("img", s2);
                } else if (req.getString("type").equals("name")) {
                    String str = "";
                    s = req.getString("data");
                    str = s;
                    s = "Hey there " + s + "!\nNow type 'GAME' to start a game or 'SCOREBOARD' to see the scoreboard";
                    rep.put("type", "name");
                    rep.put("data", s);

//                    JSONObject json = new JSONObject();
//                    try {
//                        json.put("name", str);
//                        json.put("score", rand.nextInt(100));
//                        jsonArray.put(json);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    try (PrintWriter send = new PrintWriter(new FileWriter(path))) {
//                        send.write(jsonArray.toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                } else if (req.getString("type").equals("next")) {
                    //s = req.getString("data");
                    rep.put("type", "img");

//                    File file = new File("img/To-Funny-For-Words1.png");
//                    BufferedImage img = ImageIO.read(file);
//                    byte[] bytes = null;
//                    try (ByteArrayOutputStream outbyte = new ByteArrayOutputStream()) {
//                        ImageIO.write(img, "png", outbyte);
//                        bytes = outbyte.toByteArray();
//                    }
//                    if (bytes != null) {
//                        Base64.Encoder encoder = Base64.getEncoder();
//                        req.put("data", encoder.encodeToString(bytes));
//                    }
//                    System.out.println("Unable to save image to byte array");

                } else if (req.getString("type").equals("game")) {
                    //start timer
                    //send first random quote
                    gameOn = true;
                    int characterNumber = rand.nextInt(8);
                    currentQuote = charcters.get(characterNumber);
                    s = "img/" + charcters.get(characterNumber) + "/quote" + quoteNum + ".png";
                    rep.put("type", "game");
                    rep.put("data", s);

                } else if (req.getString("type").equals("scoreboard")) {
                    if(leaderName.size() == 0){
                        s = "No winners just yet be the first one to earn a spot on the scoreboard!";
                        rep.put("type", "scoreboard");
                        rep.put("data", s);
                    } else {
                        for (int i = 0; i < leaderName.size(); i++) {
                            s += leaderName.get(i);
                            s += leaderScore.get(i) + ",";
                            System.out.println(leaderName.get(i));
                            System.out.println(leaderScore.get(i));
                        }
                        rep.put("type", "scoreboard");
                        rep.put("data", s);
                    }
                    //String name = (String)json.get("Name");
                    //s = path.toString();
                    //System.out.println(path);
//                    Iterator iterator = jsonArray.iterator();
//                    boolean bool = true;
//                    while (iterator.hasNext()) {
//                        if (bool == true) {
//                            //System.out.println(iterator.next());
//                            s = iterator.next().toString();
//                            bool = false;
//                        } else {
//                            //System.out.println(iterator.next());
//                            s = iterator.next().toString() + "\n";
//                            bool = true;
//                        }
//                    }
                } else if (req.getString("type").equals("error")) {
                    s = "not implemented";
                } else if (req.getString("type").equals("guess")) {
                    s = req.getString("data");
                    rep.put("type", "scoreboard");
//                    if (!gameOn || s.isEmpty()) {
//                        s = "Please start the game first using 'GAME'! or Enter your favorite superhero's name!";
//                        rep.put("data", s);
//                        rep.put("img", "");
//                    } else {
                        if (currentQuote.compareTo(s) == 0){
                            if (quoteNum <= 3) {
                                rep.put("data", "You got it!\nHurry, guess the next one!");
                                s = "img/" + currentQuote + "/quote" + quoteNum + 1 + ".png";
                                rep.put("img", s);
                            } else {
                                //move to next set of character quotes
                                s = "img/" + currentQuote + "/quote" + quoteNum + 1 + ".png";
                                rep.put("data", "You got it!\nNew Character!");
                                rep.put("img", s);
                            }
                        } else {
                            s = "Try again or try your luck with the 'NEXT' one";
                            rep.put("data", s);
                            rep.put("img", "");
                        }
                    //}
                }
                System.out.println("Received the String " + s);
                // write the whole message
                //os.writeObject(s);
                os.writeObject(rep.toString());

                // make sure it wrote and doesn't get cached in a buffer
                os.flush();
            }
        } catch(Exception e) {e.printStackTrace();}
    }
}
