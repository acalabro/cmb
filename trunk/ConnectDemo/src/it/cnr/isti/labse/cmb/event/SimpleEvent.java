package it.cnr.isti.labse.cmb.event;

public class SimpleEvent extends ConnectBaseEvent<String>
{
	private static final long serialVersionUID = 1L;
	public Long timestamp = 0L;
	private String data = "";
	private String id;
	private String sourceID;
	private int eventID;
	
	public SimpleEvent(String sourceID, String ID, int eventID, Long ts)
	{
		super(sourceID, ID, eventID, ts);
		this.timestamp = ts;
		this.id = ID;
		this.sourceID = sourceID;
		this.eventID = eventID;
	}

	public String getData() {
		return data;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public String getID() {
		return id;
	}

	public void setData(String t) {
		this.data = t;
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;		
	}

	public String getSourceID() {
		return this.sourceID;
	}

	@Override
	public int getEventID() {
		return this.eventID;
	}

	@Override
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}
}