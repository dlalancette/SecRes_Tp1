/*********************************************************************************/
/* Date de la dernière modification: 2015-10-12                                  */
/* Sommaire: Classe implémentant un Réseau de Feistel simple et permettant       */
/* 			 l'encryprion et la décryption d'un message avec l'aide d'une clé.   */
/* 		     Cette même clé est décomposée en sous clés sur lesquels des modulos */
/* 		     sont effectuées par le biais d'une fonction de tour (16.            */
/*********************************************************************************/
package algorithme;

import org.apache.commons.lang3.*;

public class ReseauFeistel 
{
	//Encryption des données, segmentation en bloc de 16 bits (ou 2 bytes)  
	//Pour chacun des blocs de données la fonction d'encryption est appelée
	public static byte[] encryption(byte[] donnees, byte[] cle) throws Exception 
	{
		int tailleBourrage = 8 - donnees.length % 8; //Calcule du nb de bits de padding à ajouter
		int cptBourrage = 0;
		byte[] tabCipher = new byte[donnees.length + tailleBourrage];
		byte[] bloc = new byte[2];
		
		//On initialise une Tab de bits de bourrage
		byte[] tabBitsBourrage = Utilitaire.creerBourrage(tailleBourrage);
		
		//Génération d'une Matrice de sous-clé pour les 16 rounds de la fonction d'encryption
		byte[][] tabSousCles = creerSousCles(cle);
		
		//On segmente les données en blocs de 16 bits
		for (int i = 0; i < donnees.length + tailleBourrage; i++) 
		{
			if (i > 0 && i % 2 == 0) //Si Mod 2
			{
				bloc = encrypterBloc(bloc, tabSousCles, false); //Alors on encrypte un bloc de donnée
				System.arraycopy(bloc, 0, tabCipher, i - 2, bloc.length); //Ajout dans la table Cipher du bloc encrypté
			}
			
			if (i < donnees.length) //On vérifie que l'itérateur n'a pas dépassé la taille des données
				bloc[i % 2] = donnees[i];
			else //Le cas échéant on commence à ajouter des bits de bourrage au blocs de données
			{														
				bloc[i % 2] = tabBitsBourrage[cptBourrage % 2];
				cptBourrage++;
			}
		}
		
		return tabCipher; //Retour des données sous formes de bytes encryptés
	}
	
	//Decryption des données, processus inverse à l'encryption
	public static byte[] decryption(byte[] donnees, byte[] cle) throws Exception 
	{
		byte[] tabCipher = new byte[donnees.length];
		byte[] bloc = new byte[2];
		
		//On génère les sous clés à nouveau
		byte[][] tabSousCles = creerSousCles(cle);
		
		//On segmente les données en blocs de 16 bits
		for (int i = 0; i < donnees.length; i++) 
		{
			if (i > 0 && i % 2 == 0) //Si mod 2
			{
				bloc = encrypterBloc(bloc,tabSousCles, true); //Alors on décrypte les sous blocs
				System.arraycopy(bloc, 0, tabCipher, i - 2, bloc.length);
			}
			
			if (i < donnees.length)
				bloc[i % 2] = donnees[i];
		}

		tabCipher = Utilitaire.supprimerBourrage(tabCipher); //On supprime le bourrage du tableau

		return tabCipher; //Retour des données sous forme de bytes décryptés
	}
	
	//Implémentation d'une méthode privé permettant l'encryption ou la décryption d'un bloc de données de 16 bits
	//Si "estDecrypt" = false, alors on effectue les XOR dans le bon ordre, pour encrypter les blocs
	//Sinon, on effectue les XOR dans l'ordre inverse pour décrypter les blocs
	private static byte[] encrypterBloc(byte[] bloc, byte[][] tabSousCles, boolean estDecrypt) 
	{
	    byte[] tabTemp = bloc;
	    byte[] tabD = new byte[bloc.length / 2];
	    byte[] tabG = new byte[bloc.length / 2];
	    byte[] tabTempDroite;
	    
	    System.arraycopy(tabTemp, 0, tabG, 0, tabG.length); // On instancie un tableau avec la moitié gauche du bloc
		System.arraycopy(tabTemp, tabG.length, tabD, 0, tabD.length); //On instancie un autre tableau avec la moité droite du bloc
		
		//On effectue 16 tours
	    for (int i = 0; i < 16; i++) 
	    {
	    	tabTempDroite = tabD; //On instancie un tableau temporaire pour la partie droite du bloc
	    	
	    	//On effectue un XOR entre la partie droite du bloc et la sous clé du tour
            tabD = estDecrypt ? Utilitaire.effectuerXOR(tabD, tabSousCles[15-i]) //Si "estDecrypt", on effectue les XOR dans le sens inverse
            				  : Utilitaire.effectuerXOR(tabD,tabSousCles[i]); //Sinon, on effectue les XOR dans le bon sens
            
	        tabD = Utilitaire.effectuerXOR(tabG, tabD); //On combine les deux moitié avec l'aide d'un XOR
	        tabG = tabTempDroite; //La moitié droite du tour précédent devient la moitié gauche du prochain tour
	    }
	 
	    tabTemp = ArrayUtils.addAll(tabD, tabG); //Concaténation des deux tableaux
	 
	    return tabTemp; 
	}
	
	//Génération des 16 sous clés de tour
	private static byte[][] creerSousCles(byte[] cle) throws Exception 
	{
		byte[][] tabSousCles = new byte[16][];						
		byte[] tabTemp = cle;
		byte[] tabG = new byte[tabTemp.length / 2];
		byte[] tabD = new byte[tabTemp.length / 2];
		
		if(!Utilitaire.estTailleValide(cle)) //Si la taille de la clé n'est pas de 16 bits
			throw new Exception("La clé doit être d'une taille de 16 bits!"); //Alors on lance un exception
		
		System.arraycopy(tabTemp, 0, tabG, 0, tabG.length); //Initialisation d'un tableau contenant la partie gauche de la clé
		System.arraycopy(tabTemp, tabG.length, tabD, 0, tabD.length); //Initialisation d'un tableau contenant la partie droite de la clé
		
		//On génère à la suite les seize sous clés
		//Rotation de la partie gauche/droite et fusion des deux tableaux afin de créer la sous clé de tour
		for (int i = 0; i < 16; i++) 
			tabSousCles[i] = ArrayUtils.addAll(rotation(tabG, tabG.length * 2), 
					  				           rotation(tabD, tabD.length * 2));

		return tabSousCles;
	}
	
	private static byte[] rotation(byte[] donnees, int taille) 
	{
		byte[] tblDonneesDecalees = new byte[2];
		int val = 0;
		
		for (int i = 0; i < taille; i++) 
		{
			val = Utilitaire.extraireBits(donnees, (i + 1) % taille);
			Utilitaire.ajouterBits(tblDonneesDecalees, i, val);
		}
		
		return tblDonneesDecalees;
	}
}