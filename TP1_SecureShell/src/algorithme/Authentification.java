package algorithme;

import algorithme.Hachage;

public class Authentification {
	static byte[][] bloc;
	static byte[] buffer;
	
	public static byte[] AuthentificationMessage(byte[] message){
		//Bourrage d'un bloc de donn�es
		buffer = Hachage.bourrage(message);
		
		//Initialisation des valeurs du tampon de hachage
		Hachage.initialisationValeurs();
		
		//Tant que toutes les donn�es n'auront pas �t� segment�es
		while(Hachage.k < buffer.length)
		{
			//Segmentation des donn�es
			bloc = Hachage.segmentation(buffer);
			
			//Preparation d'un nouveau bloc de donn�es de 80 mots de 64 bits
			bloc = Hachage.augmentationBloc(bloc);	//Augmentation du bloc de 16 � 80 mots
			bloc = Hachage.operationBits(bloc);		//Initialisation des mots 16 � 79
			
			//Hachage des valeurs
			Hachage.hachage(bloc);
		}
		//Ajout du bloc d'authentification de 512 bits � la fin du message
		message = Hachage.ajoutHachageMessage(message);
		Hachage.initialisationValeurs();
		return message;
		}
}
