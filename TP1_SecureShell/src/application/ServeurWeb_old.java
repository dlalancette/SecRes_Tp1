package application;

//import java.io.UnsupportedEncodingException;

/*public class ServeurWeb {

	public static void main(String[] args) throws UnsupportedEncodingException 
	{

		String key = "RETTRY76UIMN3RTE";	
		String msg = "ceci est une message encrypte et je fais le test pour voir combien de caractere max ca prend";
		String longMsg = "ceci est une message encrypte et je fais le test pour voir combien de caractere max ca prend";
		Integer seed;

		try{
		SecureRandom random = new SecureRandom();
		RivestCipher4 rv = new RivestCipher4(key,random.nextInt());

		//byte[] data = ReseauFeistel.encryption(msg.getBytes(), key.getBytes());

		//String encryptedMSG = new String(data, "UTF-8");
		//String decryptedMSG = new String(ReseauFeistel.decryption(data, key.getBytes()), "UTF-8");


		String encryptedMSG = new String(rv.encrypt(longMsg.toCharArray()));
		String decryptedMSG = new String(rv.decrypt(encryptedMSG.toCharArray()));

		String test = decryptedMSG;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		
		
		try{

			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
			
		return;

	}

}*/
