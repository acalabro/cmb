package it.cnr.isti.labse.cmb.settings;

public class DebugMessages {

	public static int lastMessageLength = 0;
	
	public static void print(String callerClass, String messageToPrint)
	{
		String message = callerClass + ": " + messageToPrint;
		System.out.print(message);
		lastMessageLength = message.length();
	}
	public static void ok()
	{
		int tab = 10 - (lastMessageLength / 8);
		String add="";
		for(int i = 0; i< tab;i++) {
			add +="\t"; 
		}
		System.out.println(add + "[ OK ]");
	}
	public static void line() {
		System.out.println("--------------------------------------------------------------------------------------");	
	}
}
