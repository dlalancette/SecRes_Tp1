package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client extends Thread {

	private Integer port;
	Socket socketClient;

	public Client(Integer Port){
		port = Port;
	}

	@Override
	public void run()
	{
		try{
			
			connect();
			sendMessage("test");
			System.out.println(readResponse());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
	}

	public void connect() throws UnknownHostException, IOException{
		System.out.println("Attempting to connect to :" + port);
		socketClient = new Socket(InetAddress.getLocalHost(),port);
		System.out.println("Connection Established");
	}

	public String readResponse() throws IOException{
		String userInput;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

		System.out.println("Response from server:");
		while ((userInput = stdIn.readLine()) != null) {
			return userInput;
		}

		return null;
	}

	public void sendMessage(String msg) throws IOException
	{
		ObjectOutputStream  oos = new ObjectOutputStream(socketClient.getOutputStream());
		System.out.println("Sending request to Socket Server");
		oos.writeObject(msg);
		//oos.close();

	}


}
