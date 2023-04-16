import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to.
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in.
 *
 */

public class Peer {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	private String thisPort;
	private String thatPort;
	private boolean first;
	Socket firstSocket;

	public Peer(BufferedReader bufReader, String username, ServerThread serverThread, String thisPort, String thatPort, boolean first, Socket firstSocket){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
		this.thisPort = thisPort;
		this.thatPort = thatPort;
		this.first = first;
		this.firstSocket = firstSocket;
	}
	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		// starting the Server Thread, which waits for other peers to want to connect
		ServerThread serverThread = new ServerThread(args[1]);
		serverThread.start();
		Peer peer = new Peer(bufferedReader, username, serverThread, null, null, false, null);
		if (args.length == 2) {
			System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);
			System.out.println("Waiting for others to connect");
			peer.thisPort = args[1];
			peer.thatPort = "-1";
			peer.first = true;
		} else if (args.length == 3) {
			System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);
			System.out.println("If possible, you will be connected to "+ args[2] + " and it's connections");
			peer.thisPort = args[1];
			peer.thatPort = args[2];
			Socket sock = new Socket("localhost", Integer.parseInt(peer.thatPort));
			serverThread.listeningSockets.add(sock);
		} else {
			System.out.println("Invalid number of parameters!");
		}
		while(serverThread.listeningSockets.isEmpty()) {
			Thread.sleep(1000);

		}
		Iterator<Socket> iterator = serverThread.listeningSockets.iterator();
		if (iterator.hasNext()) {
			peer.firstSocket = iterator.next();
		}

		peer.updateListenToPeers();
	}

	/**
	 * User is asked to define who they want to subscribe/listen to
	 * Per default we listen to no one
	 *
	 */
	public void updateListenToPeers() throws Exception {
//		System.out.println("> Who do you want to listen to? Enter host:port");
//		String input = bufferedReader.readLine();
//		String[] setupValue = input.split(" ");
//		for (Socket s : serverThread.listeningSockets) {

		Socket socket = null;
		try {
//				socket = new Socket("localhost", s.getPort());
			socket = firstSocket;
			new ClientThread(socket).start();
		} catch (Exception c) {
			if (socket != null) {
				socket.close();
			} else {
				System.out.println("Cannot connect, wrong input");
				System.out.println("Exiting: I know really user friendly");
				System.exit(0);
			}
		}
//		}

		askForInput();
	}

	/**
	 * Client waits for user to input their message or quit
	 *
	 * @param bufReader bufferedReader to listen for user entries
	 * @param username name of this peer
	 * @param serverThread server thread that is waiting for peers to sign up
	 */
	public void askForInput() throws Exception {
		try {
			System.out.println("> You can now start chatting (exit to exit)");
			while(true) {
				String message = bufferedReader.readLine();
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					break;
				} else {
					// we are sending the message to our server thread. this one is then responsible for sending it to listening peers
					serverThread.sendMessage("{'username': '"+ username +"','message':'" + message + "'}");
				}
			}
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}