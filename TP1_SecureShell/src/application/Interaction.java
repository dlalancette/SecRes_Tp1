package application;

import java.io.IOException;
import java.net.UnknownHostException;

public class Interaction {

	private static Server server;
	private static Client client;
	
	public void StartServer (Integer port) 
	{
		System.out.println("*Server initialisation*");
		try {
			server = new Server(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		server.start();
		
		while(true)
		{}
		
	}
	
	public void StartClient (Integer port) 
	{
		System.out.println("*Client initialisation*");
		client = new Client(port);
		
		try {
			client.connect();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true)
		{}
		
		
		
	}
	
	
}
