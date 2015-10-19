package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//Le nom de fonction illustre bien la fonction de chacun d'entre elles.

public class Serveur extends Thread {

	private ServerSocket serveurSocket;
	private Socket client ;
	private int port;
	ObjectInputStream in ;
	ObjectOutputStream out ;

	//Initialisation du serveur avec un port défini aléatoirement par nous.
	public Serveur(int Port) throws IOException {
		port = Port;
	}

	//Fonction dans laquelle on fait les actions qui vont etre dans le multitache
	@Override
	public void run()
	{
		String cle = "";
		Integer choix = null; 
		boolean estFin = true;

		try{
			IniServeur();
		    in = new ObjectInputStream(client.getInputStream());
		    out = new ObjectOutputStream(client.getOutputStream());
			
			cle = lireReponse();

			System.out.println("Serveur : clé recu : " + cle);
			
			envoiMessage("Serveur : Clé recu.");

			while(estFin){
				choix = Integer.valueOf(lireReponse());
				switch (choix) {
				case 1:// Envoie d'un texte du client;
					envoiMessage("Serveur : texte");
					break;

				case 2:// Envoie d'un fichier du client.
					envoiMessage("Serveur : fichier .txt");
					break;
				case 3:// Fin de la connection.
					estFin = false;
					//ois.close();
					break;
				default:
					envoiMessage("Serveur : Commande inconnu");
					break;
				}
				System.out.println(lireReponse());
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
	}


	public void IniServeur() {
		System.out.println("pServeur : Lancement du serveur sur le port : " + port);
		try {
			serveurSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("pServeur : Attente client...");
		try {
			client = serveurSocket.accept();
			System.out.println("pServeur : Connecter au client");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void envoiMessage(String msg) throws IOException {
		//IniServeur();
		System.out.println("pServeur : Envoi message au client...");
		
		out.writeObject(msg);
		out.flush();
		//ecrivain.close();
	}
	
	public String lireReponse() throws IOException
	{
		String msg= "";
		try {
			System.out.println("pServeur : Reception du message client...");
			
			try {
				msg = in.readObject().toString();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return msg;

	}


}
