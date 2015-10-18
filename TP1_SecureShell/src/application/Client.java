package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

//Le nom de fonction illustre bien la fonction de chacun d'entre elles.

public class Client extends Thread {

	private Integer port;
	Socket socketClient;
	ObjectOutputStream  oos;
	String clePublic

	public Client(Integer Port){
		port = Port;
	}

	public setCle(String cle)
	{
		clePublic = cle;
	}
	
	//Fonction dans laquelle on fait les actions qui vont etre dans le multitache
	@Override
	public void run()
	{
		try{
			connection();
			String choix = ""; 
			
			envoiMessage("En attente de la clé.");
			
			
			while(true){
				choix = lireResponse();
				switch (choix) {
				case "texte":

					break;

				case "fichier":

					break;
				case "fin":
					oos.close();
					break;
				default:
					envoiMessage("Commande inconnu")
					break;
				}
			}

			//envoiMessage("test");
			//System.out.println(lireResponse());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
	}

	public void connection() throws UnknownHostException, IOException{
		System.out.println("Tentative de connection sur le port :" + port);
		socketClient = new Socket(InetAddress.getLocalHost(),port);
		System.out.println("Connection établie");
	}

	public String lireResponse() throws IOException{
		String entreClient;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

		System.out.println("Response du serveur:");
		while ((entreClient = stdIn.readLine()) != null) {
			return entreClient;
		}

		return null;
	}

	public void envoiMessage(String msg) throws IOException
	{
		oos = new ObjectOutputStream(socketClient.getOutputStream());
		System.out.println("Envoi du message vers le serveur");
		oos.writeObject(msg);
	}


}
