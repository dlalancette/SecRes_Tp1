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
		try 
		{
			//Peut etre changer si utilisé.
			Integer port = 4444;

			Client client = new Client(port);
			Serveur serveur = new Serveur(port);

			//Permet de faire simuler client-serveur a l'aide de thread.
			serveur.start();
			client.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}



}
