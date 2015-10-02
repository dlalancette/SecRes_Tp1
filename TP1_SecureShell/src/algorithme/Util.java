package algorithme;

public class Util {
	
	public static void setBit(byte[] data, int pos, int val) {
	     int posByte = pos / 8;
	     int posBit = pos % 8;
	     byte tmpB = data[posByte];
	     tmpB = (byte) (((0xFF7F >> posBit) & tmpB) & 0x00FF);
	     byte newByte = (byte) ((val << (8 - (posBit + 1))) | tmpB);
	     data[posByte] = newByte;
	}
	 
	public static int extractBit(byte[] data, int pos) {
	     int posByte = pos / 8;
	     int posBit = pos % 8;
	     byte tmpB = data[posByte];
	     int bit = tmpB >> (8 - (posBit + 1)) & 0x0001;
	     return bit;
	}
	 
	public static byte[] extractBits(byte[] input, int pos, int n) {
	     int numOfBytes = (n - 1) / 8 + 1;
	     byte[] out = new byte[numOfBytes];
	     for (int i = 0; i < n; i++) {
	           int val = extractBit(input, pos + i);
	           setBit(out, i, val);
	     }
	     return out;
	 
	}
	
	public static byte[] rotLeft(byte[] input, int len, int pas) {
	     int nrBytes = (len - 1) / 8 + 1;
	     byte[] out = new byte[nrBytes];
	     for (int i = 0; i < len; i++) {
	          int val = extractBit(input, (i + pas) % len);
	          setBit(out, i, val);
	     }
	     return out;
	}
	
	public static byte[] xor_func(byte[] a, byte[] b) {
	     byte[] out = new byte[a.length];
	     for (int i = 0; i < a.length; i++) {
	          out[i] = (byte) (a[i] ^ b[i]);
	     }
	     return out;
	}
	
	public static byte[] separateBytes(byte[] in, int len) {
	     int numOfBytes = (8 * in.length - 1) / len + 1;
	     byte[] out = new byte[numOfBytes];
	     for (int i = 0; i < numOfBytes; i++) {
	          for (int j = 0; j < len; j++) {
	               int val = extractBit(in, len * i + j);
	               setBit(out, 8 * i + j, val);
	          }
	     }
	     return out;
	}
	
	public static byte[] concatBits(byte[] a, int aLen, byte[] b, int bLen) {
	     int numOfBytes = (aLen + bLen - 1) / 8 + 1;
	     byte[] out = new byte[numOfBytes];
	     int j = 0;
	     for (int i = 0; i < aLen; i++) {
	          int val = extractBit(a, i);
	          setBit(out, j, val);
	          j++;
	     }
	     for (int i = 0; i < bLen; i++) {
	          int val = extractBit(b, i);
	          setBit(out, j, val);
	          j++;
	     }
	      return out;
	}
	
	public static byte[] deletePadding(byte[] input) {
		int count = 0;

		int i = input.length - 1;
		while (input[i] == 0) {
			count++;
			i--;
		}

		byte[] tmp = new byte[input.length - count - 1];
		System.arraycopy(input, 0, tmp, 0, tmp.length);
		return tmp;
	}
}
