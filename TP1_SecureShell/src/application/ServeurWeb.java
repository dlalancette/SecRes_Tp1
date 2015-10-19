package application;

import algorithme.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ServeurWeb   
{
	public static void main(String[] args) throws IOException 
	{
        //Scanner scanneur = new Scanner(System.in); //Création d'un scanneur pour la lecture des entrées utilisateurs
        KeyGenerator generateurCle = null;

		String strMessageClair = null;
		String strMessageEncrypte = null;
		String strMessageDecrypte = null;
		String clePublic = null;
		
		byte[] donnees;
		byte[][] sousCles = new byte[3][];
		
		try 
		{
			//Génération d'une clé aléatoire de 16 bits
			generateurCle = KeyGenerator.getInstance("HmacSHA1");
	       // generateurCle.init(16);
	       // sousCles[0] = generateurCle.generateKey().getEncoded();
	       // sousCles[1] = generateurCle.generateKey().getEncoded();
	       // sousCles[2] = generateurCle.generateKey().getEncoded();
	        
	        generateurCle.init(1024);
	        
	        clePublic = generateurCle.generateKey().getEncoded().toString();
	        
	        Integer port = 4444;
			
			Serveur serveur = new Serveur(port);
			Client client = new Client(port);

			client.initialiserCle(clePublic);
			
			serveur.start();
			client.start();

	        
	        /*
	        System.out.print("Entrer un message à encrypter: "); //On demande à l'usager de saisir un message à encrypter
	        strMessageClair = scanneur.next(); //On récupère le message saisi par l'usager
	        
			donnees = ReseauFeistel.tripleEncryption(strMessageClair.getBytes(), sousCles);
			strMessageEncrypte = new String(donnees, "UTF-8");
			strMessageDecrypte = new String(ReseauFeistel.tripleDecryption(donnees, sousCles), "UTF-8");
			
			byte[] codeMac = Authentification.genereHMAC(sousCles[0], strMessageClair.getBytes());
			byte[] codeMac2 = Authentification.genereHMAC(sousCles[0], strMessageDecrypte.getBytes());
			
			System.out.println("Message encrypte: " + strMessageEncrypte);
			System.out.print("Message decrypte: " + strMessageDecrypte);
			
			if(Arrays.equals(codeMac, codeMac2))
				System.out.println("\n\nLe message n'a pas ete corrompu!");
			else
				System.out.println("\n\nLe message a ete corrompu!");
		*/
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		//scanneur.close();
		//System.exit(1);
	}
	

   
}
