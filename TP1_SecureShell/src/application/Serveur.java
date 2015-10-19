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
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import algorithme.Authentification;
import algorithme.ReseauFeistel;
import algorithme.RivestCipher4;

//Le nom de fonction illustre bien la fonction de chacun d'entre elles.

public class Serveur extends Thread {

	private ServerSocket serveurSocket;
	private Socket client ;
	private int port;
	String cle = "";
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
		String choix = null; 
		boolean estFin = true;
		String strMessageClair = null;

		try{
			IniServeur();

			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());

			while(estFin){

				choix = lireReponse();

				switch (Integer.valueOf(choix.substring(0, 1)))
				{
				case 1:// Envoie d'un texte du client;
					if(verificationMessage(choix))
						envoiMessage("bien recu");
					else
						envoiMessage("mal recu");
					break;
				case 3:// Fin de la connection.
					estFin = false;
					break;

				default:
					envoiMessage("pServeur : Commande inconnu");
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
			System.out.println("pServeur : Connection établie avec un client");
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

	private boolean verificationMessage(String msgRecu) throws UnsupportedEncodingException, Exception// [0]choix menu -[1]-Type d'algo - [2] - message [3]- Cle   [4] - code hmac;
	{
		String [] splitMsg = msgRecu.split("/");
		String strMessageDecrypte = null ;
		byte[] codeMac,codeMac2 = null ;

		if(splitMsg[1].contains("RF"))
		{
			byte[][] sousCles = new byte[3][];

			String[] splitSousCles = splitMsg[3].split("-");

			sousCles[0] = splitSousCles[0].getBytes();
			sousCles[1] = splitSousCles[0].getBytes();
			sousCles[2] = splitSousCles[0].getBytes();

			strMessageDecrypte = new String(ReseauFeistel.tripleDecryption(splitMsg[2].getBytes(), sousCles), "UTF-8");

			codeMac = Authentification.genereHMAC(sousCles[0], strMessageDecrypte.getBytes());
		}
		else
		{
			String[] splitInfoAlgo = splitMsg[3].split("-");

			strMessageDecrypte = new String( new RivestCipher4(splitInfoAlgo[0].toString(),Integer.valueOf( splitInfoAlgo[1]) ).decrypte(splitMsg[2].toCharArray()));

			codeMac = Authentification.genereHMAC(splitInfoAlgo[0].getBytes("UTF-8"), strMessageDecrypte.getBytes("UTF-8"));
		}

		codeMac2 = splitMsg[4].getBytes("UTF-8");

		if(Arrays.equals(codeMac, codeMac2))
		{
			System.out.println("\n\nLe message n'a pas ete corrompu!");
			return true;
		}
		else{
			System.out.println("\n\nLe message a ete corrompu!");
			return false;
		}

	}
}
