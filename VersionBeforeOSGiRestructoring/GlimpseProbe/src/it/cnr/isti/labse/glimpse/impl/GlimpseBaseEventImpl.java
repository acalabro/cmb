package it.cnr.isti.labse.glimpse.impl;

import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;

public class GlimpseBaseEventImpl extends GlimpseBaseEvent<String>
{
	private static final long serialVersionUID = 1L;
	public Long timestamp = 0L;
	private String data;
	private boolean consumed;
	private String connectorID;
	private String connectorInstanceID;
	private String connectorInstanceExecutionID;
	private int eventID;
	private int eventInResponseToID;
	private String sourceState;
	private String networkedSystemSource;
	
	public GlimpseBaseEventImpl(String connectorID, String connectorInstanceID, String connectorInstanceExecutionID, int eventID, int eventInResponseToID, Long ts, String networkedSystemSource)
	{
		super(connectorID, connectorInstanceID, connectorInstanceExecutionID, eventID, eventInResponseToID, ts, networkedSystemSource);
		this.connectorID = connectorID;
		this.connectorInstanceID = connectorInstanceID;
		this.connectorInstanceExecutionID = connectorInstanceExecutionID;
		this.eventID = eventID;
		this.eventInResponseToID = eventInResponseToID;
		this.timestamp = ts;
		this.networkedSystemSource = networkedSystemSource;
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

	@Override
	public int getEventID() {
		return this.eventID;
	}

	@Override
	public void setEventID(int eventID) {
		this.eventID = eventID;		
	}

	@Override
	public int getEventInResponseToID() {
		return this.eventInResponseToID;
	}

	@Override
	public void setEventInResponseToID(int eventInResponseToID) {
		this.eventInResponseToID = eventInResponseToID;		
	}
}