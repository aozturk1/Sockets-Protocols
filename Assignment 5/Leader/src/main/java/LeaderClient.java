import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.*;

/**
 * ClientThread for Leader managing input
 */

public class LeaderClient extends Thread {
    private BufferedReader bufferedReader;

    public LeaderClient(Socket socket) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    public void run() {
        while (true) {
            try {
                JSONObject json = new JSONObject(bufferedReader.readLine());
                System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
            } catch (Exception e) {
                System.out.println("Something went wrong with sending message to Client/Node");
                interrupt();
                break;
            }
        }
    }

}