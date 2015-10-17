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
		donnees[0] = (byte) 1;
		
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
	
	public static byte[] addition(byte[] motX, byte[] motY){
		byte[] addition = new byte[8];
		for(int i = 0; i < motX.length; i++)
		{
			addition[i] = (byte) (motX[i] + motY[i]);
			
		}
		return addition;
	}
	
	public static byte[] addition(byte[] mot1, byte[] mot2, byte[] mot3, byte[] mot4){
		byte[] addition = new byte[8];
		for(int i = 0; i < addition.length; i++)
		{
			addition[i] = (byte) (mot1[i] + mot2[i] + mot3[i] + mot4[i]);
			
		}
		return addition;
	}
	
	public static byte[] operationCH(byte[] motX, byte[] motY, byte[] motZ){
		byte[] nouveauMot = new byte[8];
		byte[] complement = new byte[8];
		for(int x = 0; x < nouveauMot.length-1; x++){
				nouveauMot[x] = (byte)(motX[x] & motY[x]);
				complement[x] = (byte)((~motX[x]) & motZ[x]);
				nouveauMot[x] = (byte) (nouveauMot[x] ^ complement[x]);
			}
		return nouveauMot;
	}
		
	public static byte[] operationMaj(byte[] motX, byte[] motY, byte[] motZ){
		byte[] nouveauMot = new byte[8];
		byte[] temporaire2 = new byte[8];
		byte[] temporaire3 = new byte[8];
		for(int x = 0; x < nouveauMot.length-1; x++){
				nouveauMot[x] = (byte)(motX[x] & motY[x]);
				temporaire2[x] = (byte)(motX[x] & motZ[x]);
				temporaire3[x] = (byte)((motY[x]) & motZ[x]);
				nouveauMot[x] = (byte) (nouveauMot[x] ^ temporaire2[x] ^ temporaire3[x]);
			}
		return nouveauMot;
	}
	
	public static byte[] operationRotation0(byte[] mot){
		mot = rotationBits(mot,28,34,39);
		return mot;
	}

	public static byte[] operationRotation1(byte[] mot){
		mot = rotationBits(mot,14,18,41);
		return mot;
	}
	
	public static byte[] operationShift0(byte[] mot){
		mot = shiftBits(mot ,1,8,7);
		return mot;
	}
		
	public static byte[] operationShift1(byte[] mot){
		mot = shiftBits(mot,19,61,6);
		return mot;
	}
	
	public static byte[] rotationBits(byte[] mot, int x, int y, int z){
		byte[] mot1 = new byte[8];
		byte[] mot2 = new byte[8];
		byte[] mot3 = new byte[8];
		byte[] rotation = new byte[8];
		mot1 = rotation(mot,x);
		mot2 = rotation(mot,y);
		mot3 = rotation(mot,z);
		
		for(int i = 0; i < rotation.length; i++){
			rotation[i] = (byte)(mot1[i] & mot2[i] & mot3[i]);
		}
		return rotation;
	}
	
	public static byte[] shiftBits(byte[] mot, int x, int y, int z){
		byte[] mot1,mot2,mot3,shift = new byte[8];
		mot1 = rotation(mot,x);
		mot2 = rotation(mot,y);
		mot3 = shift(mot,7);
		for(int i = 0; i < 8; i++){
			shift[i] = (byte)(mot1[i] & mot2[i] & mot3[i]);
		}
		return shift;
	}
	
	public static byte[] rotation(byte[] mot, int x){
		byte[] mot1, mot2, rotation = new byte[8];
		mot1 = shift(mot,x);
		mot2 = shift(mot,((mot.length-1)- x));
		
		for(int i = 0; i < 8; i++){
			rotation[i] = (byte)(mot1[i] | mot2[i]);
		}
		return rotation;
	}
	
	public static byte[] shift(byte[] mot, int x){
		byte[] shift = new byte[8];
		int val;
		for(int i =0; i< shift.length-1; i++)
		{
			val = Utilitaire.extraireBits(mot, (i + x) % 8);
			Utilitaire.ajouterBits(shift, i, val);	
		}
		return shift;
	}
	
}
