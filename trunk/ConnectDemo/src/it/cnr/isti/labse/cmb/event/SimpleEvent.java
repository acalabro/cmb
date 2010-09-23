package it.cnr.isti.labse.cmb.event;

public class SimpleEvent extends ConnectBaseEvent<String>
{
	private static final long serialVersionUID = 1L;
	public Long timestamp = 0L;
	private String data = "";
	private String id;
	private String sourceID;
	private int sequenceID;
	
	public SimpleEvent(String sourceID, String ID, int eventID, Long ts)
	{
		super(sourceID, ID, eventID, ts);
		this.timestamp = ts;
		this.id = ID;
		this.sourceID = sourceID;
		this.sequenceID = eventID;
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

	public int getSequenceID() {
		return this.sequenceID;
	}

	public void setSequenceID(int eventID) {
		this.sequenceID = eventID;
	}
}