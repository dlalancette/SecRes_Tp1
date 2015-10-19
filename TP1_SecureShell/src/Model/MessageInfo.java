package Model;

public class MessageInfo implements java.io.Serializable {

	public int typeChoix = 0;
	public String infoAlgo = null;
	public  String strmessageEncrypte = null;
	public  byte[] bytemessageEncrypte;
	public  byte[] sousCle1;
	public  byte[] sousCle2 ;
	public  byte[] sousCle3 ;
	public  int graine = 0;
	public  String cle = null;
	public  byte[] hMac;

	public void Clear()
	{
		typeChoix = 0;
		infoAlgo = null;
		strmessageEncrypte = null;
		graine = 0;
		cle = null;
		hMac = null;

	}

}
