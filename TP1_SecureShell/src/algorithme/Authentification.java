package algorithme;

import javax.xml.bind.DatatypeConverter;


public class Authentification {
	static byte[][] bloc;
	static byte[] buffer;
	static int k;
	static byte [][] H;
	static byte [] a, b, c, d, e, f, g, h, T1, T2;	
	
	public static byte[] AuthentificationMessage(byte[] message){
		//Bourrage d'un bloc de donn�es
		buffer = bourrage(message);
		
		//Initialisation des valeurs du tampon de hachage
		initialisationValeurs();
		
		//Tant que toutes les donn�es n'auront pas �t� segment�es
		while(k < buffer.length)
		{
			//Segmentation des donn�es
			bloc = segmentation(buffer);
			
			//Preparation d'un nouveau bloc de donn�es de 80 mots de 64 bits
			bloc = augmentationTailleBloc(bloc);	//Augmentation du bloc de 16 � 80 mots
			bloc = initialisationMots(bloc);		//Initialisation des mots 16 � 79
			
			//Hachage des valeurs
			hachage(bloc);
		}
		//Ajout du bloc d'authentification de 512 bits � la fin du message
		message = ajoutMessageAuthentification(message);
		return message;
		}
	
	
	
	//Initialisation des valeurs permettant l'initialisation des valeurs initiales du tampon de hachage
	public static void initialisationValeurs(){
		k = 0;
		H = new byte [8][8];
		
		a = DatatypeConverter.parseHexBinary("6a09e667f3bcc908");
		b = DatatypeConverter.parseHexBinary("bb67ae8584caa73b");
		c = DatatypeConverter.parseHexBinary("3c6ef372fe94f82b");
		d = DatatypeConverter.parseHexBinary("a54ff53a5f1d36f1");
		e = DatatypeConverter.parseHexBinary("510e527fade682d1");
		f = DatatypeConverter.parseHexBinary("9b05688c2b3e6c1f");
		g = DatatypeConverter.parseHexBinary("1f83d9abfb41bd6b");
		h = DatatypeConverter.parseHexBinary("5be0cd19137e2179");
		
		H[0] = a;
		H[1] = b;
		H[2] = c;
		H[3] = d;
		H[4] = e;
		H[5] = f;
		H[6] = g;
		H[7] = h;
	}
	
	
	
	// Fonction d'ajout de bits de bourrage et d'un bloc de 128 bits suppl�mentaire
	public static byte[] bourrage(byte[] buffer){
		//Cr�ation d'un array d'une nouvelle taille
		int nouvelleTaille = nouvelleTaille(buffer.length);
		byte[] bufferBourr� = new byte[nouvelleTaille];
		
		//Copies des donn�es dans le nouveau bloc bourr�
		for(int i = 0; i< buffer.length; i++){
			bufferBourr�[i] = buffer[i];
		}
		
		//Ajout d'un bit "1" � la fin du message 
		bufferBourr�[(buffer.length)] = 1;
		
		//Ajout de bits "0" jusqu'� la fin du bloc
		for(int i = (buffer.length+1); i < (bufferBourr�.length - 128) ; i++)
		{
			bufferBourr�[i] = 0;
		}
		return bufferBourr�;
	}
	
	
	
	//D�termination d'une nouvelle taille
	public static int nouvelleTaille(int tailleBuffer){
		int nouvelleTaille;
		
		//Si la taille du bloc de donn�es est plus �lev� que 896 modulo 1024, il y a augmentation de la taille du bloc de 128 bits.
		if((128 - (tailleBuffer % 128)) <= 16)
		{
			tailleBuffer = tailleBuffer + 16;	
		}
		
		// Augmentation de la taille du bloc � un multiple de 1024 bits
		nouvelleTaille = tailleBuffer + (128 - (tailleBuffer%128));
		
		return nouvelleTaille;
	}
	
	
	
	// Segmentation des donn�es en bloc de 1024 bits
	public static byte[][] segmentation(byte[] buffer){
		byte[][] buffersegmente = new byte[16][8];
		
		// Segmentation des donn�es en bloc de 1024 bits
		for(int i = 0; i < buffersegmente.length; i++)
		{ 
			for(int j = 0; j < 8; j++){
				buffersegmente[i][j] = buffer[k];
				k++;
			}
		}
		return buffersegmente;
	}
	
	
	
	//Augmentation du bloc de donn�es de 16 mots � 80 mots
	public static byte[][] augmentationTailleBloc(byte[][] buffer){
		byte[][] nouveaubloc = new byte[80][8];
		
		//Copies des donn�es de l'ancien bloc, dans le nouveau bloc.
		for(int i = 0; i <= buffer.length-1; i++)
		{
			for(int j = 0; j < 8; j++){
			nouveaubloc[i][j] = buffer[i][j];
			}
		}
		return nouveaubloc;
	}
	
	
	
	//Initialisation des mots 16 � 79
	public static byte[][] initialisationMots(byte[][] buffer){
		
		//Op�rations sur certains blocs et sommation de l'ensemble des blocs
		for(int i = 16; i < 80; i++)
		{
			buffer[i] = Utilitaire.addition(Utilitaire.operationShift1(buffer[i-2]), buffer[i-7],Utilitaire.operationShift0(buffer[i-15]), buffer[i-16]);
		}
		
		return buffer;
	}
	
	
	
	//Fonction de hachage
	public static void hachage(byte[][] bloc){
		for(int t = 0; t < bloc.length-1; t++)
		{ 
			T1 = Utilitaire.addition(h, Utilitaire.operationRotation1(H[4]), Utilitaire.operationCH(H[4], H[5], H[6]), bloc[t]);
			T2 = Utilitaire.addition(Utilitaire.operationRotation0(H[0]), Utilitaire.operationMaj(H[0], H[1], H[2]));
			h = g;
			g = f;
			f = e;
			e = Utilitaire.addition(d,  T1);
			d = c;
			c = b;
			b = a;
			a = Utilitaire.addition(T1, T2);
		}
		additionH();
	}
	
	
	
	//Calcul des nouvelles valeurs H
	public static void additionH(){
		H[0] = Utilitaire.addition(a,  H[0]);
		H[1] = Utilitaire.addition(b,  H[1]);
		H[2] = Utilitaire.addition(c,  H[2]);
		H[3] = Utilitaire.addition(d,  H[3]);
		H[4] = Utilitaire.addition(e,  H[4]);
		H[5] = Utilitaire.addition(f,  H[5]);
		H[6] = Utilitaire.addition(g,  H[6]);
		H[7] = Utilitaire.addition(h,  H[7]);
	}
	
	
	
	
	
	
	//Ajout du bloc d'authentification de 512 bits � la fin du message
	public static byte[] ajoutMessageAuthentification(byte[] message){
		//Cr�ation d'un nouveau bloc de donn�es ayant 512 bits de plus que le pr�c�dent
		byte[] nouveauMessage = new byte [(message.length+64)];
		
		//Copies des donn�es dans le nouveau bloc
		for(int i = 0; i < message.length; i++)
		{
				nouveauMessage[i] = message[i];
		}
		int i = message.length;
		
		//Copie des donn�es contenues dans le tampon de hachage � la fin du message
		for(int k = 0; i < nouveauMessage.length; k++)
		{
			for(int j = 0; j < 8; j++)
			{	
				nouveauMessage[i] = H[k][j];
				i++;
			}
		}
		return nouveauMessage;
		}
	
	
	
	
	
}
