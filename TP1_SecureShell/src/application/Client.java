package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.crypto.KeyGenerator;

//Le nom de fonction illustre bien la fonction de chacun d'entre elles.

public class Client extends Thread {

	private Integer port;
	Socket socketClient;
	String clePublic = null;
	ObjectInputStream in ;
	ObjectOutputStream out;

	public Client(Integer Port){
		port = Port;
	}

	public void initialiserCle(String cle)
	{
		clePublic = cle;
	}

	//Fonction dans laquelle on fait les actions qui vont etre dans le multitache
	@Override
	public void run()
	{
		Scanner scanneur = new Scanner(System.in); //Cr�ation d'un scanneur pour la lecture des entr�es utilisateurs
		KeyGenerator generateurCle = null; 
		int choix = 0; 
		Boolean estFin = true;
		
		

		try{
			connection();
			
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			

			envoiMessage(clePublic);
			System.out.println(lireReponse());

			while(estFin){
				System.out.println("pClient : Veuillez choisir l'option que vous voullez : /n 1. Envoyer un texte /n 2. Envoyer un fichier.txt 3. Terminer la conection .");
				if(scanneur.hasNextInt()){
					choix = scanneur.nextInt();
					switch (choix) {
					case 1:
						System.out.println("pClient :texte ");
						envoiMessage("1");
						break;

					case 2:
						System.out.println("pClient : fichier .txt ");
						envoiMessage("2");
						break;
					case 3:
						envoiMessage("3");
						//oos.close();
						estFin = false;
						break;
					default:
						envoiMessage("pClient : Commande inconnu");
						break;
					}
					System.out.println(lireReponse());
				}
				
			}
			scanneur.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
	}

	public void connection() throws UnknownHostException, IOException{
		System.out.println("pClient : Tentative de connection sur le port :" + port);
		socketClient = new Socket(InetAddress.getLocalHost(),port);
		System.out.println("pClient : Connection �tablie");
	}

	public String lireReponse() throws IOException{
		String entreClient = null;
		
		try {
			entreClient = in.readObject().toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return entreClient;
	}

	public void envoiMessage(String msg) throws IOException
	{		
		System.out.println("pClient : Envoi du message vers le serveur..." + msg);
		out.writeObject(msg);
		out.flush();
	}


}
