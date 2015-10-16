package application;
import java.io.*;

public class Buffer {
	static byte[] MessageOriginal;
	
	//Chargement du fichier
	public static void chargementFicher(){
		try{
		File fichier = new File("Message.txt");
		MessageOriginal = new byte[(int)fichier.length()];
		DataInputStream in = new DataInputStream(new FileInputStream(fichier));
		in.readFully(MessageOriginal);
		in.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
