package it.cnr.isti.connect.cmb.eventprocessing;

public class SimpleEvent{// implements ConnectBaseEvent<String> {

	private String data;
	
	public SimpleEvent()
	{
		data = "default payload";
	}
	public SimpleEvent(String p)
	{
		data = p;
	}
	
	public String getData() {
		return data;
	}

//	public Long getTimestamp() {
//		return 10L;
//	}
	
}
