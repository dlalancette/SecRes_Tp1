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

import Model.MessageInfo;
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
	MessageInfo msgRecu = new MessageInfo();

	//Initialisation du serveur avec un port défini aléatoirement par nous.
	public Serveur(int Port) throws IOException {
		port = Port;
	}

	//Fonction dans laquelle on fait les actions qui vont etre dans le multitache
	@Override
	public void run()
	{
		boolean estFin = true;

		try{
			IniServeur();

			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());

			while(estFin){

				lireReponse();

				switch (msgRecu.typeChoix)
				{
				case 1:// Envoie d'un texte du client;
					if(verificationMessage())
						envoiMessage("Validé et bien recu");
					else
						envoiMessage("Erreur validation");
					break;
				case 3:// Fin de la connection.
					estFin = false;
					out.close();
					in.close();
					break;
				default:
					envoiMessage("pServeur : Commande inconnu");
					break;

				}
				if(estFin)
				{
					System.out.println("pServeur : Validation du message");
				}else{
					System.out.println("pServeur : Fermeture"); 
				}

			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
	}
	//Initialisation serveur
	public void IniServeur() {
		System.out.println("pServeur : Lancement du serveur sur le port : " + port);
		try {
			serveurSocket = new ServerSocket(port);

			System.out.println("pServeur : Attente client...");

			client = serveurSocket.accept();
			System.out.println("pServeur : Connection établie avec un client");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void envoiMessage(String msg) throws IOException {
		System.out.println("pServeur : Envoi message au client...");

		out.writeObject(msg);
		out.flush();
	}

	public void lireReponse() throws IOException
	{
		try {
			System.out.println("pServeur : Reception du message client...");

			try {
				msgRecu = (MessageInfo)in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	//Validation du message soit : décryption et validation avec hmac
	private boolean verificationMessage() throws UnsupportedEncodingException, Exception// [0]choix menu -[1]-Type d'algo - [2] - message [3]- Cle   [4] - code hmac;
	{
		String strMessageDecrypte = null ;
		byte[] codeMac,codeMac2 = null ;
		byte[][] sousCles;

		if(msgRecu.infoAlgo.contains("RF")){
			//Dcryptage avec Reseau Fesitel
			sousCles = new byte[3][];

			sousCles[0] = msgRecu.sousCle1;
			sousCles[1] = msgRecu.sousCle2;
			sousCles[2] = msgRecu.sousCle3;

			strMessageDecrypte = new String(ReseauFeistel.tripleDecryption(msgRecu.bytemessageEncrypte, sousCles), "UTF-8");

			codeMac = Authentification.genereHMAC(sousCles[0], strMessageDecrypte.getBytes("UTF-8"));
		}
		else
		{
			//Decryptage avec Rc4
			strMessageDecrypte = new String( new RivestCipher4( msgRecu.cle,msgRecu.graine).decrypte( msgRecu.strmessageEncrypte.toCharArray()));

			codeMac = Authentification.genereHMAC(msgRecu.cle.getBytes("UTF-8"), strMessageDecrypte.getBytes("UTF-8"));
		}

		codeMac2 = msgRecu.hMac;
		//vérification des code Hmac
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
