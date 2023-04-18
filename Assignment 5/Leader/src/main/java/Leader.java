import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Leader
 */

public class Leader extends Thread{
	private BufferedReader bufferedReader;
	private LeaderServer leaderServer;


	public Leader(BufferedReader bufReader, LeaderServer leaderServer){
		this.bufferedReader = bufReader;
		this.leaderServer = leaderServer;
	}

	/**
	 * Main method starting the Server thread
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Starting Leader...");

		LeaderServer leaderServer = new LeaderServer("7000");
		leaderServer.start();
		Leader leader = new Leader(bufferedReader, leaderServer);

		System.out.println("Waiting for a Client and a Node...");
		leader.output();
	}

	/**
	 * Managing Output
	 */
	public void output() throws Exception {
		while(true) {
			try {
				while(leaderServer.clients.isEmpty()) {
					Thread.sleep(1000);
				}
				System.out.println(leaderServer.clients);
				System.out.println(leaderServer.nodes);
				ObjectInputStream in = new ObjectInputStream(leaderServer.clientSockets.get(1).getInputStream());
				String s = (String) in.readObject();
				JSONObject receiveClient = new JSONObject(s);
				System.out.println("Here5");
				String choice = receiveClient.getString("type");
//				String clientId = receiveClient.getString("id");
				JSONObject sendNode = new JSONObject();
				switch (choice) {
					case "request":
						System.out.println("Here6");
						sendNode.put("id", receiveClient.get("id"));
						sendNode.put("type", "eligibility");
						sendNode.put("value", receiveClient.get("value"));
						break;
					case "pay":
						sendNode.put("id", receiveClient.get("id"));
						sendNode.put("type", "pay");
						sendNode.put("value", receiveClient.get("value"));
						break;
					default:
						System.out.println("Default switch case, something went wrong!");
						break;
				}
				JSONObject receiveNode = new JSONObject(askNode(sendNode)); //not storing the returned node

//				OutputStream out = serverSock.getOutputStream();
//				ObjectOutputStream os = new ObjectOutputStream(out);
//				JSONObject send = new JSONObject();
//				switch (choice) {
//					case "loan":
//						String response = "Your loan is declined";
//						rep.put("type", "loan");
//						rep.put("value", response);
//						break;
//					case "pay":
//
//						break;
//					default:
//						System.out.println("Default switch case, something went wrong!");
//						break;
//				}
				OutputStream out = leaderServer.clientSockets.get(1).getOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);
				os.writeObject(receiveNode);
				leaderServer.confirmedNodes.clear();
			} catch (Exception e) {
				System.out.println("Something went wrong between Leader and Node on Leader side 96");
				System.exit(0);
			}
		}

	}

	public JSONObject askNode(JSONObject sendNode){
		System.out.println("Here7");
		JSONObject receiveAgainNode = new JSONObject();
		try {
			for (Socket s : leaderServer.nodes) {
				System.out.println("Here8");
				OutputStream out = s.getOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);
				os.writeObject(sendNode);

				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				String r = (String) in.readObject();
				JSONObject receiveNode = new JSONObject(r);
				System.out.println("Here9");
				String choice = receiveNode.getString("type");
				JSONObject sendClient = new JSONObject();
				switch (choice) {
					case "eligibility":
						System.out.println("Here10");
						System.out.println("Talking to Nodes about loan request...");
						sendClient.put("id", receiveNode.get("id"));
						sendClient.put("type", "eligibility");
						sendClient.put("value", receiveNode.get("value"));
						if (receiveNode.get("decision").equals("true")){
							leaderServer.confirmedNodes.add(s);
						}
						break;
					case "pay":
						System.out.println("Talking to Nodes about loan payment...");
						break;
					case "confirm":
						System.out.println("Talking to Nodes about approval process...");
						break;
					default:
						System.out.println("Default switch case, something went wrong!");
						break;
				}
			}
			double share = 0;
			if ((leaderServer.nodeSockets.size() == 2 && leaderServer.confirmedNodes.size() == 2) || ((leaderServer.nodeSockets.size())/2 < leaderServer.confirmedNodes.size()) ) {
				//APPROVED
				System.out.println("Here11");
				System.out.println("Approval documentation started!");
				double loan = (double) sendNode.get("value");
				share = loan/leaderServer.confirmedNodes.size();
				receiveAgainNode = confirmNode(sendNode, share); //not storing the returned node
			} else {
				System.out.println("Declined!");
				//RETURN
				receiveAgainNode.put("id", sendNode.get("id"));
				receiveAgainNode.put("type", "confirm");
				receiveAgainNode.put("confirm", "NO");
				receiveAgainNode.put("value", share);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		////DO I NEED TO RETURN ANYTHING OR NOT???????
		return receiveAgainNode;
	}

	public JSONObject confirmNode(JSONObject fromAskNode, double share){
		JSONObject sendNode = new JSONObject();
		JSONObject receiveNode = null;
		JSONObject fromConfirmNode = null;
		try {
			for (Socket s : leaderServer.confirmedNodes) {
				OutputStream out = s.getOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);

				sendNode.put("id", fromAskNode.get("id"));
				sendNode.put("type", "confirm");
				sendNode.put("value", share);
				os.writeObject(sendNode);

				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				String r = (String) in.readObject();
				receiveNode = new JSONObject(r);
			}
			assert receiveNode != null;
			if (receiveNode.get("confirm").equals("YES")) {
				//APPROVED
				System.out.println("Bank confirmed and applied amount!");
				fromConfirmNode.put("id", fromAskNode.get("id"));
				fromConfirmNode.put("type", "confirm");
				fromConfirmNode.put("confirm", "YES");
				fromConfirmNode.put("value", share);
			} else {
				//DECLINED
				System.out.println("Declined!");
				fromConfirmNode.put("id", fromAskNode.get("id"));
				fromConfirmNode.put("type", "confirm");
				fromConfirmNode.put("confirm", "NO");
				fromConfirmNode.put("value", share);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return fromConfirmNode;
	}
}