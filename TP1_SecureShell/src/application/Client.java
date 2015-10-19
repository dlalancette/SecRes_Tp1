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

import Model.MessageInfo;
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

	MessageInfo msgInfo = new MessageInfo();
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
		int choix = 0; 
		Boolean estFin = true;

		try{
			connection();

			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());

			while(estFin){
				System.out.println("pClient : Veuillez choisir l'option que vous voullez : " + newLine + " 1. Envoyer un texte " + newLine + " 2. Envoyer un fichier.txt " + newLine + " 3. Terminer la conection");
				if(scanneur.hasNextInt()){
					choix = scanneur.nextInt();
					switch (choix) {
					case 1://Envoyer un message à partir d'un texte saisi par l'utilisateur
						avoirMesageEncode(true);
						envoiMessage(msgInfo);
						break;
					case 2: //Envoyer un message à partir d'un fichier texte
						avoirMesageEncode(false);
						envoiMessage(msgInfo);
						break;
					case 3: //Fermeture du systeme
						msgInfo.Clear();
						msgInfo.typeChoix = 3;
						envoiMessage(msgInfo);
						estFin = false;
						System.out.println("Systeme : Fermeture du systeme.");
						System.exit(1);
						break;
					default:// aucune commande
						msgInfo.Clear();
						msgInfo.typeChoix = 4;
						envoiMessage(msgInfo);
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

	public void envoiMessage(MessageInfo msgInfo) throws IOException
	{		
		System.out.println("pClient : Envoi du message vers le serveur...");
		out.writeObject(msgInfo);
		out.flush();
	}

	// Génération du message avec l'encryption Reseau Feistel
	public void genereMsgEncrypteRF(String strMessageClair)
	{
		byte[] donnees;
		byte[][] sousCles = new byte[3][];
		msgInfo.typeChoix = 1;
		try 
		{
			//Génération d'une clé aléatoire de 16 bits
			generateurCle = KeyGenerator.getInstance("HmacSHA1");
			generateurCle.init(16);
			sousCles[0] = generateurCle.generateKey().getEncoded();
			sousCles[1] = generateurCle.generateKey().getEncoded();
			sousCles[2] = generateurCle.generateKey().getEncoded();

			donnees = ReseauFeistel.tripleEncryption(strMessageClair.getBytes(), sousCles);

			msgInfo.infoAlgo = "RF";
			msgInfo.bytemessageEncrypte = donnees;
			msgInfo.sousCle1 = sousCles[0] ;
			msgInfo.sousCle2 = sousCles[1];
			msgInfo.sousCle3 = sousCles[2];
			msgInfo.hMac =  Authentification.genereHMAC(sousCles[0], strMessageClair.getBytes("UTF-8"));			

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	// Génération du message avec l'encryption RivestCipher4
	public void genereMsgEncrypteRC4(String strMessageClair) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String cle = null;
		Integer nombreAll = new SecureRandom().nextInt();

		msgInfo.typeChoix = 1;

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

			msgInfo.infoAlgo = "RC";

			msgInfo.strmessageEncrypte = new String( RivestCipher4.encrypte(strMessageClair.toCharArray()));

			msgInfo.cle = cle;
			msgInfo.graine= nombreAll;

			msgInfo.hMac = Authentification.genereHMAC(cle.getBytes("UTF-8"), strMessageClair.getBytes("UTF-8"));		

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	//Permet de diriger l'utilisateur a l'aide de question aux bonnes place selons les choix faites.
	public void avoirMesageEncode(Boolean estUlr ) throws InvalidKeyException, IOException, NoSuchAlgorithmException
	{
		String strMessageClair = null;
		String choixAlgo = null;

		if(estUlr)
		{
			System.out.print("Entrer un message à encrypter: "); //On demande à l'usager de saisir un message à encrypter
			strMessageClair = scanneur.next(); //On récupère le message saisi par l'usager

		}else
		{
			strMessageClair = avoirTexteUlr();
		}
		System.out.print("Veuillez choisir le type d'encryption: " + newLine + " A - ReseauFeistel " + newLine + " B - RivestCipher4 ");
		choixAlgo = scanneur.next();

		if(choixAlgo.contains("A"))
		{
			genereMsgEncrypteRF(strMessageClair);
		}else if(choixAlgo.contains("B")) {
			genereMsgEncrypteRC4(strMessageClair);
		}
	}

	//Permet d'avoid un fichier .txt
	public String avoirTexteUlr() throws IOException
	{
		String  path = "";

		while(!path.contains(".txt")){
			System.out.print("Entrer l'url du fichier (.txt): "); 
			path = scanneur.next();
		}

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
