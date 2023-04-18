import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * ServerThread for Leader
 */

public class LeaderServer extends Thread{
    private ServerSocket serverSocket;
    public Set<Socket> listeningSockets = new HashSet<Socket>();
    public Set<Socket> nodes = new HashSet<Socket>();
    public Set<Socket> clients = new HashSet<Socket>();
    public Map<Integer, Socket> nodeSockets = new HashMap<>();
    public Map<Integer, Socket> clientSockets = new HashMap<>();
    public Set<Socket> confirmedNodes = new HashSet<Socket>();
    private BufferedReader bufferedReader;

    public LeaderServer(String portNum) throws IOException {
        serverSocket = new ServerSocket(Integer.valueOf(portNum));
    }

    /**
     * Starting the thread, we are waiting for clients wanting to talk to us, then save the socket in a list
     */
    public void run() {
        try {
            while (true) {
                Socket sock = serverSocket.accept();
                listeningSockets.add(sock);

                OutputStream out = sock.getOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(out);
                JSONObject send = new JSONObject();
                send.put("key", "HI from SERVER!");
                os.writeObject(send.toString());

                InputStream in = sock.getInputStream();
                ObjectInputStream is = new ObjectInputStream(in);
                String jsonString = (String) is.readObject();
                JSONObject receive = new JSONObject(jsonString);
                System.out.println(receive.get("key"));
                identifySocket(sock, receive.getString("key"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void identifySocket(Socket sock, String messageIn) throws IOException {
        try {
            String[] parts = messageIn.split(":");
            if (parts[1].equals("client")){
                clients.add(sock);
                System.out.println("This is a client");
                clientSockets.put(Integer.valueOf(parts[0]), sock);
            } else if (parts[1].equals("node")) {
                nodes.add(sock);
                System.out.println("This is a node");
                nodeSockets.put(Integer.valueOf(parts[0]), sock);
            } else {
                System.out.println("Something went wrong in LeaderServer getting identifySocket response!");
            }
        } catch (Exception c) {
            if (sock != null) {
                sock.close();
            } else {
                System.out.println("Something went wrong LeaderServer identifySocket");
                System.exit(0);
            }
        }
    }
}