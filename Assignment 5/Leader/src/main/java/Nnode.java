import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Node
 */

public class Nnode{
	private int id;
	private int money;
	private BufferedReader bufferedReader;
	private Map<Integer, Integer> clients = new HashMap<>();

	public Nnode(int id, int money, Socket socket) throws IOException {
		this.id = id;
		this.money = money;
		this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		this.clientSockets = new HashMap<>();
	}

	/**
	 * Node main
	 * @param args[0] money
	 */
	public static void main (String[] args) throws Exception {
		Socket serverSock = new Socket("localhost", 7000);
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter a numerical id to represent this Node:");
		int id = scan.nextInt();
		Nnode node = new Nnode(id, Integer.parseInt(args[0]), serverSock);
		System.out.println("Establishing...");

		InputStream in = serverSock.getInputStream();
		ObjectInputStream is = new ObjectInputStream(in);
		String jsonString = (String) is.readObject();
		JSONObject receive = new JSONObject(jsonString);
		System.out.println(receive.get("key"));

		OutputStream out = serverSock.getOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		JSONObject send = new JSONObject();
		send.put("key", id+":node");
		os.writeObject(send.toString());

		node.run(node.id, node.money, serverSock);
	}

	public void run(int id, int money, Socket serverSock) {
		System.out.println("Bank" + id + " is ready for banking when a client is ready!");
		while (true) {
			try {
				ObjectInputStream in = new ObjectInputStream(serverSock.getInputStream());
				String s = (String) in.readObject();
				System.out.println("Discussing terms...");
				JSONObject receiveLeader = new JSONObject(s);
				String choice = receiveLeader.getString("type");
				JSONObject sendLeader = new JSONObject();
				switch (choice) {
					case "eligibility":
						System.out.println("Processing loan request...");
						System.out.println(Integer.parseInt((String) receiveLeader.get("value")));
						sendLeader.put("id", receiveLeader.get("id"));
						sendLeader.put("nodeId", id);
						sendLeader.put("type", "eligibility");
						sendLeader.put("value", receiveLeader.get("value"));
						if (clients.containsKey(receiveLeader.get("id")) || (Integer.parseInt((String) receiveLeader.get("value")) * 1.5) > (money * 1.0)) {
							sendLeader.put("decision", "false");
							break;
						}
						sendLeader.put("decision", "true");
						System.out.println("Client" + receiveLeader.get("id") + " is eligible for a loan!");
						break;
					case "pay":
						System.out.println("Processing loan payment...");

						break;
					case "confirm":
						System.out.println("Filing acceptance documents...");
						sendLeader.put("confirm", "YES");
						if (!clients.containsKey(receiveLeader.get("id"))) {
							clients.put(Integer.parseInt((String) receiveLeader.get("id")), Integer.parseInt((String) receiveLeader.get("value")));
							money -= Integer.parseInt((String) receiveLeader.get("value"));
						} else {
							clients.replace((Integer) receiveLeader.get("id"), clients.get(receiveLeader.get("id")), clients.get(receiveLeader.get("id")) + Integer.parseInt((String) receiveLeader.get("value")));
						}
						break;
					default:
						System.out.println("Default switch case, something went wrong!");
						break;
				}
				OutputStream out = serverSock.getOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);
				os.writeObject(sendLeader);

			} catch (Exception e) {
				System.out.println("Something went wrong between Leader and Node on the Node side 91");
				System.exit(0);
			}
		}
	}
}