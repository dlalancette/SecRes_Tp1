package algorithme;

import java.security.InvalidKeyException;
import java.util.Random;

public class RivestCipher4 {
		
    private static char[] key;
    private static int[] sbox;
    private static Random rand;
    private static int seed;
    private static final int SBOX_LENGTH = 256;
    private static final int KEY_MIN_LENGTH = 25;
    
    public RivestCipher4(String _key,Integer _seed) throws InvalidKeyException {
    	seed = _seed ;
    	setRand();
        setKey(_key);
    }
  
    public char[] decrypt(final char[] _msg) {
    	try {
			setRand();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        return encrypt(_msg);
    }
 
	
	  public static char[] encrypt(final char[] _msg) {
	        sbox = initSBox(key);
	        char[] encodedMsg = new char[_msg.length];
	        
	        int i = 0,j = 0;
	        
	        for (int n = 0; n < _msg.length; n++) {
	            i = (i + 1) % SBOX_LENGTH;
	            j = (j + sbox[i]) % SBOX_LENGTH;
	            swap(i, j, sbox);
	          //  int rand = sbox[(sbox[i] + sbox[j]) % SBOX_LENGTH];
	            encodedMsg[n] = (char) (rand.nextInt() ^ (int) _msg[n]);
	        }
	        return encodedMsg;
	    }
	 
	    
	    public static int[] initSBox(char[] _key) {
	        int[] sbox = new int[SBOX_LENGTH];
	        int j = 0;
	        
	        //Initialisation du vecteur état S:
	        for (int i = 0; i < SBOX_LENGTH; i++) {
	            sbox[i] = i;
	        }
	        
	        //Permutation initiale de S
	        for (int i = 0; i < SBOX_LENGTH; i++) {
	            j = (j + sbox[i] + _key[i % _key.length]) % SBOX_LENGTH;
	            swap(i, j, sbox);
	        }
	        return sbox;
	    }
	 
	    private static void swap(int _i, int _j, int[] _sbox) {
	        int temp = _sbox[_i];
	        
	        _sbox[_i] = _sbox[_j];
	        _sbox[_j] = temp;
	    }
	 
	    public void setKey(String _key) throws InvalidKeyException {
	       // if (key.length() >= KEY_MIN_LENGTH && key.length() < SBOX_LENGTH) {
	       //     throw new InvalidKeyException("La clé doit etre entre :"
	       //             + KEY_MIN_LENGTH + " and " + (SBOX_LENGTH - 1));
	        //}
	 
	        key = _key.toCharArray();
	    }
	    
	    public void setRand() throws InvalidKeyException {
	        rand = new Random(seed) ;
	    }
	 
	}
