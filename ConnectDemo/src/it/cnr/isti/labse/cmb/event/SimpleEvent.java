package it.cnr.isti.labse.cmb.event;

public class SimpleEvent implements ConnectBaseEvent<String>
{
	public Long timestamp = 0L;
	private String data = "";
	
	public String getData() {
		// TODO Auto-generated method stub
		return data;
	}

	public Long getTimestamp() {
		// TODO Auto-generated method stub
		return timestamp;
	}

	public void setTimestamp(Long ts) {
		timestamp = ts;
		
	}

	public void setData(String t) {
		data = t;
	}
	
}