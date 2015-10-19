package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.KeyGenerator;

import algorithme.Authentification;
import algorithme.ReseauFeistel;
import algorithme.RivestCipher4;

//Le nom de fonction illustre bien la fonction de chacun d'entre elles.

public class Client extends Thread {

	String newLine = System.getProperty("line.separator");
	String clePublic = null;
	Integer port;

	Socket socketClient;
	ObjectInputStream in ;
	ObjectOutputStream out;

	KeyGenerator generateurCle = null;
	Scanner scanneur = new Scanner(System.in); //Création d'un scanneur pour la lecture des entrées utilisateurs

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
		KeyGenerator generateurCle = null; 
		int choix = 0; 
		Boolean estFin = true;
		String messageRecu= null;

		try{
			connection();

			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());

			while(estFin){
				System.out.println("pClient : Veuillez choisir l'option que vous voullez : " + newLine + " 1. Envoyer un texte " + newLine + " 2. Envoyer un fichier.txt " + newLine + " 3. Terminer la conection");
				if(scanneur.hasNextInt()){
					choix = scanneur.nextInt();
					switch (choix) {
					case 1:
						//System.out.println("pClient :texte ");
						envoiMessage("1" + avoirMesageEncode(true));
						break;

					case 2:
						//System.out.println("pClient : fichier .txt ");
						envoiMessage("1" + avoirMesageEncode(false));
						break;
					case 3:
						envoiMessage("3");
						estFin = false;
						System.out.println("Systeme : Fermeture du systeme.");
						System.exit(1);;
						break;
					default:// aucune commande
						envoiMessage("4");
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
		System.out.println("pClient : Connection établie");
	}

	public String lireReponse() throws IOException{
		String entreClient = null;

		try {
			entreClient = in.readObject().toString();
		} catch (ClassNotFoundException e) {
			System.out.println("pClient : Erreur de la reception." + e.getMessage());	
		}

		return entreClient;
	}

	public void envoiMessage(String msg) throws IOException
	{		
		System.out.println("pClient : Envoi du message vers le serveur..." + msg);
		out.writeObject(msg);
		out.flush();
	}


	@SuppressWarnings("null")
	public String genereMsgEncrypteRF(String strMessageClair)
	{
		byte[] donnees;
		byte[][] sousCles = new byte[3][];
		String strAEnvoyer = "";
		strAEnvoyer= "1";
		try 
		{
			//Génération d'une clé aléatoire de 16 bits
			generateurCle = KeyGenerator.getInstance("HmacSHA1");
			generateurCle.init(16);
			sousCles[0] = generateurCle.generateKey().getEncoded();
			sousCles[1] = generateurCle.generateKey().getEncoded();
			sousCles[2] = generateurCle.generateKey().getEncoded();

			donnees = ReseauFeistel.tripleEncryption(strMessageClair.getBytes(), sousCles);
			
			strAEnvoyer += "/ ";
			strAEnvoyer += "RF";
			strAEnvoyer += "/ ";
			strAEnvoyer += new String(donnees, "UTF-8");
			strAEnvoyer += "/ ";
			strAEnvoyer += new String(sousCles[0],"UTF-8") + " - " + new String(sousCles[1],"UTF-8") + " - " + new String(sousCles[2],"UTF-8");
			strAEnvoyer += "/ ";
			strAEnvoyer += new String( Authentification.genereHMAC(sousCles[0], strMessageClair.getBytes()),"UTF-8");			

			return strAEnvoyer.toString();

		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public String genereMsgEncrypteRC4(String strMessageClair) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String cle = null;
		String strAEnvoyer = "";
		strAEnvoyer = "1";
		Integer nombreAll = new SecureRandom().nextInt();
		
		generateurCle = KeyGenerator.getInstance("HmacSHA1");
		generateurCle.init(16);

		cle = new String(generateurCle.generateKey().getEncoded(), "UTF-8") ;
		nombreAll = new SecureRandom().nextInt();

		if(nombreAll < 0)
			nombreAll -= nombreAll;
		
		initialiserCle(cle);
		
		RivestCipher4 rivestCipher4 = new RivestCipher4(cle,nombreAll); 

		try 
		{
			strAEnvoyer += "/";
			strAEnvoyer += "RC";
			strAEnvoyer += "/";
			strAEnvoyer += new String( rivestCipher4.encrypte(strMessageClair.toCharArray()));
			strAEnvoyer += "/";
			strAEnvoyer += cle + "-" + nombreAll;
			strAEnvoyer += "/";
			strAEnvoyer += new String( Authentification.genereHMAC(cle.getBytes("UTF-8"), strMessageClair.getBytes("UTF-8")), "UTF-8");		


			return strAEnvoyer;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public String avoirMesageEncode(Boolean estUlr ) throws InvalidKeyException, IOException, NoSuchAlgorithmException
	{
		String strMessageClair = null;
		String strMessageEncrypte = null;
		String choixAlgo = null;

		if(estUlr)
		{
			System.out.print("Entrer un message à encrypter: "); //On demande à l'usager de saisir un message à encrypter
			strMessageClair = scanneur.next(); //On récupère le message saisi par l'usager
			
		}else
		{
			strMessageClair = avoirTexteUlr();
		}
		System.out.print("Veuillez choisir le type d'encryption: " + newLine + " A - ReseauFeistel " + newLine + "B - RivestCipher4 ");
		choixAlgo = scanneur.next();

		if(choixAlgo.contains("A"))
		{
			strMessageEncrypte = genereMsgEncrypteRF(strMessageClair);
		}else if(choixAlgo.contains("B")) {
			strMessageEncrypte = genereMsgEncrypteRC4(strMessageClair);
		}

		return strMessageEncrypte;

	}

	public String avoirTexteUlr() throws IOException
	{
		String  path = "";

		System.out.print("Entrer l'url du fichier: "); 
		path = scanneur.next();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new FileReader (new File(path)));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		String         ligne = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");

		while( ( ligne = reader.readLine() ) != null ) {
			stringBuilder.append( ligne );
			stringBuilder.append( ls );
		}

		return stringBuilder.toString();
	}



}
