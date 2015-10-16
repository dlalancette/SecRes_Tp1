package application;

import algorithme.*;

import java.io.IOException;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ServeurWeb   
{
	public static void main(String[] args) throws IOException 
	{
        Scanner scanneur = new Scanner(System.in); //Création d'un scanneur pour la lecture des entrées utilisateurs
        KeyGenerator generateurCle = null;
        SecretKey cleSecrete = null;

		String strMessageClair = null;
		String strMessageEncrypte = null;
		String strMessageDecrypte = null;
		
		byte[] donnees;

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
