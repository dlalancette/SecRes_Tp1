package algorithme;

import java.security.InvalidKeyException;
import java.util.Random;

public class RivestCipher4 {
		
    private static char[] cle;
    private static int[] sbox;
    private static Random rand;
    private static int graine;
    private static final int SBOX_LONG = 256;
    
    //Initialisation de la graine et de la clé.
    public RivestCipher4(String Cle,Integer Graine) throws InvalidKeyException {
    	graine = Graine ;
    	setRand();
        setCle(Cle);
    }
  
    public char[] decrypte(final char[] Msg) {
    	try {
			setRand();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        return encrypte(Msg);
    }
 
	  public static char[] encrypte(final char[] Msg) {
	        sbox = iniSBox(cle);
	        
	        char[] msgEncode = new char[Msg.length];
	        int i = 0,j = 0;
	        
	        for (int n = 0; n < Msg.length; n++) {
	            i = (i + 1) % SBOX_LONG;
	            j = (j + sbox[i]) % SBOX_LONG;
	            permute(i, j, sbox);
	          //  On encode le message avec le code avec le nombre random selon la graine
	            msgEncode[n] = (char) (rand.nextInt() ^ (int) Msg[n]);
	        }
	        return msgEncode;
	    }
	    
	    public static int[] iniSBox(char[] Cle) {
	        int[] sbox = new int[SBOX_LONG];
	        int j = 0;
	        
	        //Initialisation du vecteur état S:
	        for (int i = 0; i < SBOX_LONG; i++) {
	            sbox[i] = i;
	        }
	        
	        //Permutation initiale de S
	        for (int i = 0; i < SBOX_LONG; i++) {
	            j = (j + sbox[i] + Cle[i % Cle.length]) % SBOX_LONG;
	            permute(i, j, sbox);
	        }
	        return sbox;
	    }
	 
	    //Permet de permuter les valeurs du sbox.
	    private static void permute(int _i, int _j, int[] _sbox) {
	        int temp = _sbox[_i];
	        
	        _sbox[_i] = _sbox[_j];
	        _sbox[_j] = temp;
	    }
	 
	    public void setCle(String Cle) throws InvalidKeyException {
	    	cle = Cle.toCharArray();
	    }
	    
	    public void setRand() throws InvalidKeyException {
	        rand = new Random(graine) ;
	    }
	 
	}
