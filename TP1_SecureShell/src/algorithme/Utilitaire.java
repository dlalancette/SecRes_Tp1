/*********************************************************************************/
/* Date de la dernière modification: 2015-10-12                                  */
/* Sommaire: Contient les fonctions d'utilitaires fournissant des services aux   */
/* 			 autres classes de l'application.                                    */
/*********************************************************************************/

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
	 
	public static byte[] effectuerXOR(byte[] tabByte1, byte[] tabByte2) 
	{
		byte[] tabTemp1 = tabByte1;
		byte[] tabTemp2 = tabByte2;
		byte[] tabXOR = new byte[tabTemp1.length];
		
		for (int i = 0; i < tabTemp1.length; i++) 
		{
			tabXOR[i] = (byte)(tabTemp1[i] ^ tabTemp2[i]);
		}
		
		return tabXOR;
	}
	
	public static byte[] creerBourrage(int taille)
	{
		byte[] donnees = new byte[taille];
		donnees[0] = (byte) 128;
		
		for (int i = 1; i < taille; i++)
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
	
	public static boolean estTailleValide(byte[] donnees)
	{
		return donnees.length * 8 == 16 ? true : false;
	}
}
