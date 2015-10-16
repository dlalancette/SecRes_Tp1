package algorithme;

import javax.xml.bind.DatatypeConverter;

public class Authentification {
	static byte[][] bloc;
	static byte[] buffer;
	static int k;
	static byte [][] H;
	static byte [] a = DatatypeConverter.parseHexBinary("6a09e667f3bcc908");
	static byte [] b = DatatypeConverter.parseHexBinary("bb67ae8584caa73b");
	static byte [] c = DatatypeConverter.parseHexBinary("3c6ef372fe94f82b");
	static byte [] d = DatatypeConverter.parseHexBinary("a54ff53a5f1d36f1");
	static byte [] e = DatatypeConverter.parseHexBinary("510e527fade682d1");
	static byte [] f = DatatypeConverter.parseHexBinary("9b05688c2b3e6c1f");
	static byte [] g = DatatypeConverter.parseHexBinary("1f83d9abfb41bd6b");
	static byte [] h = DatatypeConverter.parseHexBinary("5be0cd19137e2179");
	static byte [] T1;
	static byte [] T2;
	
	public static byte[] authentificationMessage(byte[] message){
		//Bourrage d'un bloc de données
		buffer = bourrage(message);
		
		//Initialisation des valeurs du tampon de hachage
		initialisationValeurs();
		
		//Tant que toutes les données n'auront pas été segmentées
		while(k < buffer.length)
		{
			//Segmentation des données
			bloc = segmentation(buffer);
			
			//Preparation d'un nouveau bloc de données de 80 mots de 64 bits
			bloc = augmentationTailleBloc(bloc);	//Augmentation du bloc de 16 à 80 mots
			bloc = initialisationMotsBloc(bloc);		//Initialisation des mots 16 à 79
			
			//Hachage des valeurs
			hachage(bloc);
		}
		//Ajout du bloc d'authentification de 512 bits à la fin du message
		message = ajoutHachageMessage(message);
		return message;
		}
	
	public static byte[] bourrage(byte[] buffer){
		int nouvelleTaille = nouvelleTaille(buffer.length);
		
		byte[] bufferBourré = new byte[nouvelleTaille];
		for(int i = 0; i< buffer.length; i++){
			bufferBourré[i] = buffer[i];
		}
		bufferBourré[(buffer.length)] = 1;
		for(int i = (buffer.length+1); i < (bufferBourré.length - 128) ; i++)
		{
			bufferBourré[i] = 0;
		}
		return bufferBourré;
	}
	
	public static byte[][] segmentation(byte[] buffer){
		byte[][] buffersegmente = new byte[16][8];
		for(int i = 0; i < buffersegmente.length; i++)
		{ 
			for(int j = 0; j < 8; j++){
				buffersegmente[i][j] = buffer[k];
				k++;
			}
		}
		return buffersegmente;
	}
	
	public static byte[][] augmentationTailleBloc(byte[][] buffer){
		byte[][] nouveaubloc = new byte[80][8];
		for(int i = 0; i <= buffer.length-1; i++)
		{
			for(int j = 0; j < 8; j++){
			nouveaubloc[i][j] = buffer[i][j];
			}
		}
		return nouveaubloc;
	}
	
	public static byte[][] initialisationMotsBloc(byte[][] buffer){
		byte[] temporaire = new byte[8];
		for(int i = 16; i < 80; i++)
		{
			buffer[i] = Operations.addition(Operations.operationShift1(buffer[i-2]), buffer[i-7],Operations.operationShift0(buffer[i-15]), buffer[i-16]);
		}
		
		return buffer;
	}
	
	public static int nouvelleTaille(int tailleBuffer){
		int nouvelleTaille;
		if((128 - (tailleBuffer % 128)) <= 16)
		{
			tailleBuffer = tailleBuffer + 16;	
		}
		
		nouvelleTaille = tailleBuffer + (128 - (tailleBuffer%128));
		
		return nouvelleTaille;
	}
	
	public static void hachage(byte[][] bloc){
		for(int t = 0; t < bloc.length-1; t++)
		{ 
			T1 = Operations.addition(h, Operations.operationRotation1(H[4]), Operations.operationCH(H[4], H[5], H[6]), bloc[t]);
			T2 = Operations.addition(Operations.operationRotation0(H[0]), Operations.operationMaj(H[0], H[1], H[2]));
			h = g;
			g = f;
			f = e;
			e = Operations.addition(d,  T1);
			d = c;
			c = b;
			b = a;
			a = Operations.addition(T1, T2);
		}
		additionH();
	}

	public static void additionH(){
		H[0] = Operations.addition(a,  H[0]);
		H[1] = Operations.addition(b,  H[1]);
		H[2] = Operations.addition(c,  H[2]);
		H[3] = Operations.addition(d,  H[3]);
		H[4] = Operations.addition(e,  H[4]);
		H[5] = Operations.addition(f,  H[5]);
		H[6] = Operations.addition(g,  H[6]);
		H[7] = Operations.addition(h,  H[7]);
	}
	
	public static void initialisationValeurs(){
		H = new byte [8][8];
		H[0] = a;
		H[1] = b;
		H[2] = c;
		H[3] = d;
		H[4] = e;
		H[5] = f;
		H[6] = g;
		H[7] = h;
	}
	
	public static byte[] ajoutHachageMessage(byte[] message){
		byte[] nouveauMessage = new byte [(message.length+64)];
		for(int i = 0; i < message.length; i++)
		{
				nouveauMessage[i] = message[i];
		}
		int i = message.length;
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
