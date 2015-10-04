package algorithme;

import org.apache.commons.lang3.*;

public class ReseauFeistel {
	
	private static int[] IP = 
	{ 
		58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36,
		28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32,
		24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19,
		11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 
	};
	
	private static int[] invIP = 
	{ 
		40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47,
		15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13,
		53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51,
		19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17,
		57, 25 
	};

	public static byte[] encryption(byte[] data, byte[] cle) 
	{
		int length = 8 - data.length % 8;
		int i;
		int count = 0;
		byte[] padding = Utilitaire.creerBourrage(length);
		byte[] tmp = new byte[data.length + length];
		byte[] bloc = new byte[8];

		byte[][] tabSousCles = creerSousCles(cle);
		
		for (i = 0; i < data.length + length; i++) 
		{
			if (i > 0 && i % 8 == 0) 
			{
				bloc = encrypterBloc(bloc,tabSousCles, false);
				System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);
			}
			
			if (i < data.length)
				bloc[i % 8] = data[i];
			else
			{														
				bloc[i % 8] = padding[count % 8];
				count++;
			}
		}
		
		if(bloc.length == 8){
			bloc = encrypterBloc(bloc,tabSousCles, false);
			System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);
		}
		
		return tmp;
	}
	
	public static byte[] decryption(byte[] data, byte[] cle) 
	{
		int i;
		byte[] tmp = new byte[data.length];
		byte[] bloc = new byte[8];
		
		byte[][] tabSousCles = creerSousCles(cle);

		for (i = 0; i < data.length; i++) 
		{
			if (i > 0 && i % 8 == 0) {
				bloc = encrypterBloc(bloc,tabSousCles, true);
				System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);
			}
			if (i < data.length)
				bloc[i % 8] = data[i];
		}
		
		bloc = encrypterBloc(bloc,tabSousCles, true);
		System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);

		tmp = Utilitaire.supprimerBourrage(tmp);

		return tmp;
	}
	
	private static byte[] encrypterBloc(byte[] bloc, byte[][] tabSousCles, boolean isDecrypt) 
	{
	    byte[] tmp = new byte[bloc.length];
	    byte[] R = new byte[bloc.length / 2];
	    byte[] L = new byte[bloc.length / 2];
	 
	    tmp = permutation(bloc, IP);
	 
	    L = Utilitaire.extraireBits(tmp, 0, IP.length/2);
	    R = Utilitaire.extraireBits(tmp, IP.length/2, IP.length/2);
	 
	    for (int i = 0; i < 16; i++) 
	    {
	        byte[] tmpR = R;
	        if(isDecrypt)
	            R = f_Feistel(R, tabSousCles[15-i]);
	        else
	            R = f_Feistel(R,tabSousCles[i]);
	 
	        R = Utilitaire.effectuerXOR(L, R);
	        L = tmpR;
	    }
	 
	    tmp = ArrayUtils.addAll(R,L);
	 
	    tmp = permutation(tmp, invIP);
	    return tmp;
	}
	
	private static byte[] permutation(byte[] input, int[] table) 
	{
		int pos = 0;
		int val = 0;
		int nrBytes = (table.length - 1) / 8 + 1;
		byte[] out = new byte[nrBytes];
	     
		for (int i = 0; i < table.length; i++) 
		{
			pos = table[i] - 1;
			val = (input[pos / 8] >> (8 - (pos % 8 + 1)) & 1);
			Utilitaire.ajouterBits(out, i, val);
		}
	     
		return out;
	}
	
	private static byte[] f_Feistel(byte[] R, byte[] sousCles) 
	{
	     byte[] tmp = R;
	     tmp = Utilitaire.effectuerXOR(tmp, sousCles);
	     return tmp;
	}
	
	private static byte[][] creerSousCles(byte[] cle) 
	{
		byte[][] tmp = new byte[16][];						
		byte[] tmpK = cle;
		byte[] C = new byte[tmpK.length/2];
		byte[] D = new byte[tmpK.length/2];
		
		System.arraycopy(tmpK, 0, C, 0, C.length);
		System.arraycopy(tmpK, C.length, D, 0, D.length);

		for (int i = 0; i < 16; i++) 
		{
			byte[] cd = ArrayUtils.addAll(rotation(C, C.length * 2), 
										  rotation(D, D.length * 2));
			tmp[i] = cd;
		}

		return tmp;
	}
	
	public static byte[] rotation(byte[] input, int len) 
	{
		int nrBytes = (len - 1) / 8 + 1;
		byte[] out = new byte[nrBytes];
		
		for (int i = 0; i < len; i++) 
		{
			int val = Utilitaire.extraireBits(input, (i + 1) % len);
			Utilitaire.ajouterBits(out, i, val);
		}
		
		return out;
	}
}