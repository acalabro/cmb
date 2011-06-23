package it.cnr.isti.labse.glimpse.event;

public class SimpleEvent extends ConnectBaseEvent<String>
{
	private static final long serialVersionUID = 1L;
	public Long timestamp = 0L;
	private String data;
	private boolean consumed;
	private String connectorID;
	private String connectorInstanceID;
	private String connectorInstanceExecutionID;
	private int sequenceID;
	private String sourceState;
	private String networkedSystemSource;
	
	public SimpleEvent(String connectorID, String connectorInstanceID, String connectorInstanceExecutionID, int sequenceID, Long ts, String sourceState, String networkedSystemSource)
	{
		super(connectorID, connectorInstanceID, connectorInstanceExecutionID, sequenceID, ts, sourceState, networkedSystemSource);
		this.connectorID = connectorID;
		this.connectorInstanceID = connectorInstanceID;
		this.connectorInstanceExecutionID = connectorInstanceExecutionID;
		this.sequenceID = sequenceID;
		this.timestamp = ts;
		this.sourceState = sourceState;
	}

	public String getData() {
		return this.data;
	}
	
	public void setData(String t) {
		this.data = t;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}

	public boolean getConsumed() {
		return this.consumed;
	}

	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}
	
	public String getSourceState() {
		return this.sourceState;
	}
	
	public void setSourceState(String sourceState) {
		this.sourceState = sourceState;
	}
	
	public int getSequenceID() {
		return this.sequenceID;
	}

	public void setSequenceID(int eventID) {
		this.sequenceID = eventID;
	}
	
	public String getConnectorID() {
		return this.connectorID;
	}
	
	public void setConnectorID(String connectorID) {
		this.connectorID = connectorID;		
	}

	public String getConnectorInstanceID() {
		return this.connectorInstanceID;
	}

	public void setConnectorInstanceID(String connectorInstanceID) {
		this.connectorInstanceID = connectorInstanceID;	
	}
	
	public String getConnectorInstanceExecutionID(){
		return this.connectorInstanceExecutionID;
	}
	
	public void setConnectorInstanceExecutionID(String connectorInstanceExecutionID){
		this.connectorInstanceExecutionID = connectorInstanceExecutionID;
	}

	public String getNetworkedSystemSource() {
		return networkedSystemSource;
	}

	@Override
	public void setNetworkedSystemSource(String networkedSystemSource) {
		this.networkedSystemSource = networkedSystemSource;
	}
}