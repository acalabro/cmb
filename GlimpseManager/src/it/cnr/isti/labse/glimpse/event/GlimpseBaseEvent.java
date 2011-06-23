package it.cnr.isti.labse.glimpse.event;

import java.io.Serializable;

/**
 * @author  acalabro
 */
public abstract class GlimpseBaseEvent <T> implements Serializable {
	private static final long serialVersionUID = 1L;
	protected boolean consumed;
	protected String connectorID;
	protected String connectorInstanceID;
	protected String connectorInstanceExecutionID;
	protected int eventID;
	protected int eventInResponseToID;
	protected String networkedSystemSource;
	protected String sourceState;
	
	public GlimpseBaseEvent(String connectorID, String connectorInstanceID, String connectorInstanceExecutionID, int eventID, int eventInResponseToID, Long ts, String networkedSystemSource) {
		consumed = false;
	};
	
	public abstract T getData();
	public abstract void setData(T t);
	
	public abstract Long getTimestamp();
	
	public abstract boolean getConsumed();
	public abstract void setConsumed(boolean consumed);
	
	public abstract String getNetworkedSystemSource();
	public abstract void setNetworkedSystemSource(String networkedSystemSource);
	
	public abstract int getEventID();
	public abstract void setEventID(int eventID);
	
	public abstract int getEventInResponseToID();
	public abstract void setEventInResponseToID(int eventInResponseToID);
	
	public abstract String getConnectorID();
	public abstract void setConnectorID(String connectorID);
		
	public abstract String getConnectorInstanceID();
	public abstract void setConnectorInstanceID(String connectorInstanceID);
	
	public abstract String getConnectorInstanceExecutionID();
	public abstract void setConnectorInstanceExecutionID(String connectorInstanceExecutionID);
}