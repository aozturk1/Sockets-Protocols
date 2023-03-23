package taskthree;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;
import taskone.Performer;
import taskone.StringList;

/**
 * Class: Server
 * Description: Server tasks.
 */
class ConnectionHandler implements Runnable {

    private Socket sock;
    private StringList strings;

    public ConnectionHandler(Socket sock, StringList strings) {
        this.sock = sock;
        this.strings = strings;
    }

    public void run() {
        Performer performer = new Performer(sock, strings);
        performer.doPerform();
        try {
            System.out.println("close socket of client ");
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class ThreadPoolServer {

    public static void main(String[] args) throws Exception {
        int port;
        int pool;
        StringList strings = new StringList();

        if (args.length != 2) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runServer -Pport=9099 -Ppool=10 -q --console=plain");
            System.exit(1);
        }
        port = -1;
        pool = -1;
        try {
            port = Integer.parseInt(args[0]);
            pool = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] or [Pool] must be an integer");
            System.exit(2);
        }
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server Started...");
//        for (int i = 0; i < pool; i++) {
//            System.out.println("Accepting a Request...");
//            Socket sock = server.accept();
//
//            Thread handlerThread = new Thread(new tasktwo.ConnectionHandler(sock, strings));
//            handlerThread.start();
//        }

        int clinetNum = 0;
        while (true) {
            if(pool != 0) {
                System.out.println("Accepting a Request...");
                Socket sock = server.accept();
                clinetNum++;
                System.out.println("Accepted Client Number " + clinetNum);
                Thread handlerThread = new Thread(new taskthree.ConnectionHandler(sock, strings));
                handlerThread.start();
                pool--;
            } else {
                System.out.println("Go away!");
                break;
            }
        }
    }
}


