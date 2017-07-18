import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Client implements Runnable {

	Socket socket;
	private String userName = null;
	private ObjectInputStream clientInStream;
	private ObjectOutputStream clientOutStream;
	private Message newMessage;
	private Scanner scan;
	private SimpleDateFormat time;
	private int id;

	public Client() {
		try {
			time = new SimpleDateFormat("HH:mm:ss");
			scan = new Scanner(System.in);
			socket = new Socket("localhost", 8888);
			clientInStream = new ObjectInputStream(socket.getInputStream());
			clientOutStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			System.err.println("failed to initialize client");
			e.printStackTrace();
		}
		
		id = getMessage().getReceiver();

		System.out.print("Enter username: ");
		while ((userName = scan.nextLine()) == null)
			;
		if (userName == "")
			userName = "anon";
		sendMessage(userName);
	}
	
	public void run() {
		sendMessage("welcome " + userName + "! Your ID is " + this.id, id);
		Thread gM = new Thread(new Runnable() {
			public void run() {
				while (true) {
					Message msg = getMessage();
					System.out.println(
							"\n[" + time.format(msg.getTime()) + "]" + " <" + msg.getName() + "> " + msg.getContent());
					System.out.print("$ ");
				}
			}
		});
		gM.start();

		while (true) {
			//String text = null;
			//while((scan.nextLine()) == null);
			//sendMessage(text);
			sendMessage(scan.nextLine());
			System.out.print("$ ");
		}
	}

	private void sendMessage(String content) {
		Message msg = new Message(0, this.userName, content, -1);
		try {
			clientOutStream.writeObject(msg);
			clientOutStream.flush();
		} catch (IOException e) {
			System.err.println("could not send message");
			e.printStackTrace();
		}

	}
	private void sendMessage(String content, int cID) {
		Message msg = new Message(0, this.userName, content, cID);
		try {
			clientOutStream.writeObject(msg);
			clientOutStream.flush();
		} catch (IOException e) {
			System.err.println("could not send message");
			e.printStackTrace();
		}

	}

	private Message getMessage() {
		try {
			newMessage = (Message) clientInStream.readObject();
			return newMessage;
		} catch (Exception e) {
			System.err.println("could not read message, connection dead");
			e.printStackTrace();
			System.exit(5);
		}
		return null;
	}

}
