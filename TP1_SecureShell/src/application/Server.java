package application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	private ServerSocket serverSocket;
	private Socket client ;

	private int port;
	//port = 9990
	public Server(int Port) throws IOException {
		port = Port;
	}

	@Override
	public void run()
	{
		String msgFromClient = ""; 
		try{
			InitServer();
			msgFromClient = readResponse();
			System.out.println(msgFromClient);
			if(msgFromClient == "test");
			{
				System.out.println("Envoi du message");
				sendMessage("Recu");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
	}


	public void InitServer() {
		System.out.println("Starting the socket server at port:" + port);
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Waiting client...");
		try {
			client = serverSocket.accept();
			System.out.println("Connected to a client...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		writer.write(msg);
		writer.flush();
		writer.close();
	}

	public String readResponse() throws IOException
	{
		String msg= "";
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(client.getInputStream());
			try {
				
				msg =  (String) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		//ois.close();
		return msg;

	}

}
