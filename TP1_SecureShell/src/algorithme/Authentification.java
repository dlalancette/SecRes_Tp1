package algorithme;

import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.*;

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
	
	public static byte[] genereHMAC(byte[] cle, byte[] donnees) 
	{
        byte[] i_key_pad = new byte[64];
        byte[] o_key_pad = new byte[64];
		byte[] tblTemp1, tblTemp2;
        byte[] cleHache = new byte[64];
        
        //Si plus grand que 64 bits, alors on hache
        if (cle.length > 64) 
        	cle = authentificationMessage(cle);
        
        System.arraycopy(cle, 0, cleHache, 0, cle.length);
        
        //Pour toutes les valeurs après 64 bits, on bourre avec des 0
        for (int i = cle.length;  i < 64;  i ++) 
            cleHache[i] = 0;
        
        //Pour toutes les bits de la clés, XOR afin de générer IPad et OPad
        for (int i = 0;  i < 64;  i ++) 
        {
            i_key_pad[i] = (byte)(cleHache[i] ^ 0x36);
            o_key_pad[i] = (byte)(cleHache[i] ^ 0x5c);
        }
        
        //On concatene le message avec IPAD et on applique la fonction de hachage 
        tblTemp1 = authentificationMessage(ArrayUtils.addAll(i_key_pad, donnees));
        
        //On concatene le message prcédent avec Opad et on applique la fonction de hache
        tblTemp2 = authentificationMessage(ArrayUtils.addAll(o_key_pad, tblTemp1));
        
        return tblTemp2; //On retourne le code HMac
	}
	
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
	
	//Ajout d'un bloc de 128 bits au message
	public static byte[] bourrage(byte[] buffer){
		//Détermination de la nouvelle taille du buffer qui doit être un multiple de 1024
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
	
	//nouvelle taille de buffer
	public static int nouvelleTaille(int tailleBuffer){
			int nouvelleTaille;
			//Si la taille modulo 1024 est plus grande que 896, ajout de 128 bits à la taille du bloc
			if((128 - (tailleBuffer % 128)) <= 16)
			{
				tailleBuffer = tailleBuffer + 16;	
			}
			
			//augmentation de la taille du bloc au prochain multiple de 1024
			nouvelleTaille = tailleBuffer + (128 - (tailleBuffer%128));
			
			return nouvelleTaille;
		}
	
	//Initialisation du tampon de hachage
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
	
	//Segmentation du buffer en blocs de 1024 bits contenant 16 mots de 64 bits
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
	
	//Augentation de la taille du bloc de 16 à 80 mots
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
	
	//Opérations sur bits initialisant les mots 17 à 80
	public static byte[][] initialisationMotsBloc(byte[][] buffer){
		for(int i = 16; i < 80; i++)
		{
			buffer[i] = Utilitaire.addition(Utilitaire.operationShift1(buffer[i-2]), buffer[i-7],Utilitaire.operationShift0(buffer[i-15]), buffer[i-16]);
		}
		
		return buffer;
	}
	
	
	
	//Hachage du bloc de données donnant un nouveau tampon de hachage H de 512 bits
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

	//Mise à jour du tampon de hachage
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
	
	//Ajout du tampon de hachage à la fin du message
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
