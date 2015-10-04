package algorithme;


public class Utilitaire 
{
	public static void ajouterBits(byte[] donnees, int pos, int val) 
	{
		byte bit = donnees[pos / 8];
		bit = (byte) (((0xFF7F >> (pos % 8)) & bit) & 0x00FF);
		donnees[pos / 8] = (byte) ((val << (8 - ((pos % 8) + 1))) | bit);
	}
	 
	public static int extraireBits(byte[] donnees, int pos) 
	{
		return (donnees[pos / 8] >> (8 - (pos % 8 + 1)) & 1);
	}
	 
	public static byte[] extraireBits(byte[] input, int pos, int n) 
	{
		int numOfBytes = (n - 1) / 8 + 1;
		byte[] out = new byte[numOfBytes];
		for (int i = 0; i < n; i++) {
			int val = extraireBits(input, pos + i);
			ajouterBits(out, i, val);
		}
		return out;
	}
	
	public static byte[] effectuerXOR(byte[] tabByte1, byte[] tabByte2) 
	{
		byte[] out = new byte[tabByte1.length];
		
		for (int i = 0; i < tabByte1.length; i++) 
		{
			out[i] = (byte) (tabByte1[i] ^ tabByte2[i]);
		}
		
		return out;
	}
	
	public static byte[] fragmenterBytes(byte[] in, int len) 
	{
		int numOfBytes = (8 * in.length - 1) / len + 1;
		byte[] out = new byte[numOfBytes];
		
		for (int i = 0; i < numOfBytes; i++) 
		{
			for (int j = 0; j < len; j++) 
			{
				int val = extraireBits(in, len * i + j);
				ajouterBits(out, 8 * i + j, val);
			}
		}
		return out;
	}
	
	public static byte[] permutation(byte[] input, byte[] table) 
	{
	     int nrBytes = (table.length - 1) / 8 + 1;
	     byte[] out = new byte[nrBytes];
	     for (int i = 0; i < table.length; i++) {
	          int val = extraireBits(input, table[i] - 1);
	          ajouterBits(out, i, val);
	     }
	     return out;
	}
	
	public static byte[] rotLeft(byte[] input, int len, int pas) 
	{
		int nrBytes = (len - 1) / 8 + 1;
		byte[] out = new byte[nrBytes];
		for (int i = 0; i < len; i++) {
			int val = extraireBits(input, (i + pas) % len);
			ajouterBits(out, i, val);
		}
		return out;
	}

	
	public static byte[] creerBourrage(int length)
	{
		byte[] donnees = new byte[length];
		donnees[0] = (byte) 128;
		
		for (int i = 1; i < length; i++)
			donnees[i] = 0;
		
		return donnees;
	}
	
	public static byte[] supprimerBourrage(byte[] donnees) 
	{
		int count = 0;
		int i = donnees.length - 1;
		
		while (donnees[i] == 0) 
		{
			count++;
			i--;
		}

		byte[] tmp = new byte[donnees.length - count - 1];
		System.arraycopy(donnees, 0, tmp, 0, tmp.length);
		
		return tmp;
	}
}
