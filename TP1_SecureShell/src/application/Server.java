package application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

//Le nom de fonction illustre bien la fonction de chacun d'entre elles.

public class Serveur extends Thread {

	private ServerSocket serveurSocket;
	private Socket client ;
	private int port;
	ObjectInputStream ois;

	//Initialisation du serveur avec un port défini aléatoirement par nous.
	public Server(int Port) throws IOException {
		port = Port;
	}

	//Fonction dans laquelle on fait les actions qui vont etre dans le multitache
	@Override
	public void run()
	{
		String cle = "";
		String choix = ""; 

		try{
			connection();

			envoiMessage("En attente de la clé.");

			cle = lireResponse();

			while(true){
				choix = lireResponse();
				switch (choix) {
				case 1:// Envoie d'un texte du client;
						
					break;

				case 2:// Envoie d'un fichier du client.

					break;
				case 3:// Fin de la connection.
					ois.close();
					break;
				default:
					envoiMessage("Commande inconnu")
					break;
				}

			}
			catch(Exception ex)
			{
				ex.printStackTrace();	
			}
		}


		public void IniServeur() {
			System.out.println("Lancement du serveur sur le port : " + port);
			try {
				serveurSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Attente client...");
			try {
				client = serverSocket.accept();
				System.out.println("Connecter au client...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void envoiMessage(String msg) throws IOException {
			BufferedWriter ecrivain  = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			ecrivain.write(msg);
			ecrivain.flush();
			ecrivain.close();
		}

		public String lireResponse() throws IOException
		{
			String msg= "";
			ois = null;
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
			return msg;

		}

	}
