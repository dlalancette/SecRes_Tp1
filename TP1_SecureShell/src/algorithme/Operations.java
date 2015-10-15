package algorithme;

public class Operations {
	
	//Addition de deux segments de données
	public static byte[] addition(byte[] mot1, byte[] mot2){
		byte[] addition = new byte[8];
		for(int i = 0; i < mot1.length; i++)
		{
			addition[i] = (byte) (mot1[i] + mot2[i]);
			
		}
		return addition;
	}
	
	//Addition de quatre segments de données
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
		byte[] temp1 = new byte[8];
		byte[] temp2 = new byte[8];
		for(int x = 0; x < nouveauMot.length-1; x++){
				temp1[x] = (byte)(motX[x] & motY[x]);
				temp2[x] = (byte)((~motX[x]) & motZ[x]);
				nouveauMot[x] = (byte) (temp1[x] ^ temp2[x]);
			}
		return nouveauMot;
	}
	
	
	
	public static byte[] operationMaj(byte[] motX, byte[] motY, byte[] motZ){
		byte[] nouveauMot = new byte[8];
		byte[] temp1 = new byte[8];
		byte[] temp2 = new byte[8];
		byte[] temp3 = new byte[8];
		for(int x = 0; x < nouveauMot.length-1; x++){
				temp1[x] = (byte)(motX[x] & motY[x]);
				temp2[x] = (byte)(motX[x] & motZ[x]);
				temp3[x] = (byte)((motY[x]) & motZ[x]);
				nouveauMot[x] = (byte) (temp1[x] ^ temp2[x] ^ temp3[x]);
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
		mot3 = rotation(mot,7);
		
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
