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
        Scanner scanneur = new Scanner(System.in); //Cr�ation d'un scanneur pour la lecture des entr�es utilisateurs
        KeyGenerator generateurCle = null;

		String strMessageClair = null;
		String strMessageEncrypte = null;
		String strMessageDecrypte = null;
		
		byte[] donnees;
		byte[][] sousCles = new byte[3][];
		
		try 
		{
			//G�n�ration d'une cl� al�atoire de 16 bits
			generateurCle = KeyGenerator.getInstance("HmacSHA1");
	        generateurCle.init(16);
	        sousCles[0] = generateurCle.generateKey().getEncoded();
	        sousCles[1] = generateurCle.generateKey().getEncoded();
	        sousCles[2] = generateurCle.generateKey().getEncoded();
	        
	        System.out.print("Entrer un message � encrypter: "); //On demande � l'usager de saisir un message � encrypter
	        strMessageClair = scanneur.next(); //On r�cup�re le message saisi par l'usager
	        
			donnees = ReseauFeistel.tripleEncryption(strMessageClair.getBytes(), sousCles);
			strMessageEncrypte = new String(donnees, "UTF-8");
			strMessageDecrypte = new String(ReseauFeistel.tripleDecryption(donnees, sousCles), "UTF-8");
			
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
