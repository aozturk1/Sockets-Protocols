import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import org.json.*;

/**
 * Client
 */

public class Client {
	private int id;
	private BufferedReader bufferedReader;

	public Client(int id, Socket socket) throws IOException {
		this.id = id;
		this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/**
	 * Client main
	 */
	public static void main (String[] args) throws Exception {
		Socket serverSock = new Socket("localhost", 7000);
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter a numerical id to represent this Node:");
		int id = scan.nextInt();
		Client client = new Client(id, serverSock);
		System.out.println("Connecting...");

		InputStream in = serverSock.getInputStream();
		ObjectInputStream is = new ObjectInputStream(in);
		String jsonString = (String) is.readObject();
		JSONObject receive = new JSONObject(jsonString);
		System.out.println(receive.get("key"));

		OutputStream out = serverSock.getOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		JSONObject send = new JSONObject();
		send.put("key", id+":client");
		os.writeObject(send.toString());

		client.run(client.id, serverSock);
	}

	public void run(int id, Socket serverSock) {
		System.out.println("Client" + id +" is ready to send request when a node is ready!");
		while (true) {
			try {
				Scanner scan = new Scanner(System.in);
				System.out.println("Choices:\n1:REQUEST CREDIT\n2:PAY CREDIT");
				int scanChoice = scan.nextInt();
				System.out.println("Enter the amount:");
				int amount = scan.nextInt();
				OutputStream out = serverSock.getOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);
				JSONObject send = new JSONObject(os);
				switch(scanChoice){
					case 1:
						send.put("id", id);
						send.put("type", "request");
						send.put("value", amount);
						break;
					case 2:
						send.put("id", id);
						send.put("type", "pay");
						send.put("value", amount);
						break;
					default:
						System.out.println("Default switch case, something went wrong!");
						break;
				}
				os.writeObject(send);

				ObjectInputStream in = new ObjectInputStream(serverSock.getInputStream());
				String s = (String) in.readObject();
				JSONObject receive = new JSONObject(s);
				String choice = receive.getString("type");

				switch(choice) {
					case "request":
						System.out.println(receive);
						break;
					case "pay":
						System.out.println(receive);
						break;
					default:
						System.out.println("Default switch case, something went wrong!");
						break;
				}
			} catch (Exception e) {
				System.out.println("Something went wrong between Leader and Client");
				System.exit(0);
			}
		}
	}

}