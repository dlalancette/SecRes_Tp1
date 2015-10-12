/*********************************************************************************/
/* Date de la derni�re modification: 2015-10-12                                  */
/* Sommaire: Classe impl�mentant un R�seau de Feistel simple et permettant       */
/* 			 l'encryprion et la d�cryption d'un message avec l'aide d'une cl�.   */
/* 		     Cette m�me cl� est d�compos�e en sous cl�s sur lesquels des modulos */
/* 		     sont effectu�es par le biais d'une fonction de tour (16.            */
/*********************************************************************************/
package algorithme;

import org.apache.commons.lang3.*;

public class ReseauFeistel 
{
	//Encryption des donn�es, segmentation en bloc de 16 bits (ou 2 bytes)  
	//Pour chacun des blocs de donn�es la fonction d'encryption est appel�e
	public static byte[] encryption(byte[] donnees, byte[] cle) throws Exception 
	{
		int tailleBourrage = 8 - donnees.length % 8; //Calcule du nb de bits de padding � ajouter
		int cptBourrage = 0;
		byte[] tabCipher = new byte[donnees.length + tailleBourrage];
		byte[] bloc = new byte[2];
		
		//On initialise une Tab de bits de bourrage
		byte[] tabBitsBourrage = Utilitaire.creerBourrage(tailleBourrage);
		
		//G�n�ration d'une Matrice de sous-cl� pour les 16 rounds de la fonction d'encryption
		byte[][] tabSousCles = creerSousCles(cle);
		
		//On segmente les donn�es en blocs de 16 bits
		for (int i = 0; i < donnees.length + tailleBourrage; i++) 
		{
			if (i > 0 && i % 2 == 0) //Si Mod 2
			{
				bloc = encrypterBloc(bloc, tabSousCles, false); //Alors on encrypte un bloc de donn�e
				System.arraycopy(bloc, 0, tabCipher, i - 2, bloc.length); //Ajout dans la table Cipher du bloc encrypt�
			}
			
			if (i < donnees.length) //On v�rifie que l'it�rateur n'a pas d�pass� la taille des donn�es
				bloc[i % 2] = donnees[i];
			else //Le cas �ch�ant on commence � ajouter des bits de bourrage au blocs de donn�es
			{														
				bloc[i % 2] = tabBitsBourrage[cptBourrage % 2];
				cptBourrage++;
			}
		}
		
		return tabCipher; //Retour des donn�es sous formes de bytes encrypt�s
	}
	
	//Decryption des donn�es, processus inverse � l'encryption
	public static byte[] decryption(byte[] donnees, byte[] cle) throws Exception 
	{
		byte[] tabCipher = new byte[donnees.length];
		byte[] bloc = new byte[2];
		
		//On g�n�re les sous cl�s � nouveau
		byte[][] tabSousCles = creerSousCles(cle);
		
		//On segmente les donn�es en blocs de 16 bits
		for (int i = 0; i < donnees.length; i++) 
		{
			if (i > 0 && i % 2 == 0) //Si mod 2
			{
				bloc = encrypterBloc(bloc,tabSousCles, true); //Alors on d�crypte les sous blocs
				System.arraycopy(bloc, 0, tabCipher, i - 2, bloc.length);
			}
			
			if (i < donnees.length)
				bloc[i % 2] = donnees[i];
		}

		tabCipher = Utilitaire.supprimerBourrage(tabCipher); //On supprime le bourrage du tableau

		return tabCipher; //Retour des donn�es sous forme de bytes d�crypt�s
	}
	
	//Impl�mentation d'une m�thode priv� permettant l'encryption ou la d�cryption d'un bloc de donn�es de 16 bits
	//Si "estDecrypt" = false, alors on effectue les XOR dans le bon ordre, pour encrypter les blocs
	//Sinon, on effectue les XOR dans l'ordre inverse pour d�crypter les blocs
	private static byte[] encrypterBloc(byte[] bloc, byte[][] tabSousCles, boolean estDecrypt) 
	{
	    byte[] tabTemp = bloc;
	    byte[] tabD = new byte[bloc.length / 2];
	    byte[] tabG = new byte[bloc.length / 2];
	    byte[] tabTempDroite;
	    
	    System.arraycopy(tabTemp, 0, tabG, 0, tabG.length); // On instancie un tableau avec la moiti� gauche du bloc
		System.arraycopy(tabTemp, tabG.length, tabD, 0, tabD.length); //On instancie un autre tableau avec la moit� droite du bloc
		
		//On effectue 16 tours
	    for (int i = 0; i < 16; i++) 
	    {
	    	tabTempDroite = tabD; //On instancie un tableau temporaire pour la partie droite du bloc
	    	
	    	//On effectue un XOR entre la partie droite du bloc et la sous cl� du tour
            tabD = estDecrypt ? Utilitaire.effectuerXOR(tabD, tabSousCles[15-i]) //Si "estDecrypt", on effectue les XOR dans le sens inverse
            				  : Utilitaire.effectuerXOR(tabD,tabSousCles[i]); //Sinon, on effectue les XOR dans le bon sens
            
	        tabD = Utilitaire.effectuerXOR(tabG, tabD); //On combine les deux moiti� avec l'aide d'un XOR
	        tabG = tabTempDroite; //La moiti� droite du tour pr�c�dent devient la moiti� gauche du prochain tour
	    }
	 
	    tabTemp = ArrayUtils.addAll(tabD, tabG); //Concat�nation des deux tableaux
	 
	    return tabTemp; 
	}
	
	//G�n�ration des 16 sous cl�s de tour
	private static byte[][] creerSousCles(byte[] cle) throws Exception 
	{
		byte[][] tabSousCles = new byte[16][];						
		byte[] tabTemp = cle;
		byte[] tabG = new byte[tabTemp.length / 2];
		byte[] tabD = new byte[tabTemp.length / 2];
		
		if(!Utilitaire.estTailleValide(cle)) //Si la taille de la cl� n'est pas de 16 bits
			throw new Exception("La cl� doit �tre d'une taille de 16 bits!"); //Alors on lance un exception
		
		System.arraycopy(tabTemp, 0, tabG, 0, tabG.length); //Initialisation d'un tableau contenant la partie gauche de la cl�
		System.arraycopy(tabTemp, tabG.length, tabD, 0, tabD.length); //Initialisation d'un tableau contenant la partie droite de la cl�
		
		//On g�n�re � la suite les seize sous cl�s
		//Rotation de la partie gauche/droite et fusion des deux tableaux afin de cr�er la sous cl� de tour
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