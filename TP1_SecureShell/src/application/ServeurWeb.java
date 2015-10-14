package application;

import algorithme.*;
import net.jxta.exception.PeerGroupException;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ServeurWeb   
{

	@SuppressWarnings("unused")
	public static void main(String[] args) throws PeerGroupException, IOException 
	{
        // Randomize a port to use with a number over 1000 (for non root on unix)
        // JXTA uses TCP for incoming connections which will conflict if more than
        // one Hello runs at the same time on one computer.
        int port = 9000 + new Random().nextInt(100);
        
        Scanner scanneur = new Scanner(System.in); //Création d'un scanneur pour la lecture des entrées utilisateurs
        KeyGenerator generateurCle = null;
        SecretKey cleSecrete = null;

		String strMessageClair = null;
		String strMessageEncrypte = null;
		String strMessageDecrypte = null;
		
		byte[] donnees;
		
		// JXTA logs a lot, you can configure it setting level here
//        Logger.getLogger("net.jxta").setLevel(Level.ALL);
		
        //Gestionnaire_PaireAPaire hello = new Gestionnaire_PaireAPaire(port, "", "");
        //hello.start(); 
        //hello.fetch_advertisements();
		
		try 
		{
			//Génération d'une clé aléatoire de 16 bits
			generateurCle = KeyGenerator.getInstance("HmacSHA1");
	        generateurCle.init(16);
	        cleSecrete = generateurCle.generateKey();
	        
	        System.out.print("Entrer un message à encrypter: "); //On demande à l'usager de saisir un message à encrypter
	        strMessageClair = scanneur.next(); //On récupère le message saisi par l'usager
	        
			donnees = ReseauFeistel.encryption(strMessageClair.getBytes(), cleSecrete.getEncoded());
			strMessageEncrypte = new String(donnees, "UTF-8");
			strMessageDecrypte = new String(ReseauFeistel.decryption(donnees, cleSecrete.getEncoded()), "UTF-8");
			
			System.out.println("Message encrypte: " + strMessageEncrypte);
			System.out.print("Message decrypte: " + strMessageDecrypte);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		scanneur.close();
		System.exit(1);
	}
	

   
}
