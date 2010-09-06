package org.Connect.Event;

public class MyEvent implements ConnectBaseEvent<String>
{
	public Long timestamp = 0L;
	private String data = "";
	
	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	public Long getTimestamp() {
		// TODO Auto-generated method stub
		return timestamp;
	}

	@Override
	public void setTimestamp(Long ts) {
		timestamp = ts;
		
	}

	@Override
	public void setData(String t) {
		data = t;
	}
	
}