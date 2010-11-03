package it.cnr.isti.labse.glimpse.event;

import java.io.Serializable;

public abstract class ConnectBaseEvent <T> implements Serializable {
	private static final long serialVersionUID = 1L;
	protected boolean consumed;
	protected String sourceID;
	protected int sequenceID;
	protected String sourceState;
	public ConnectBaseEvent(String sourceID, String ID, int eventID, Long ts, String sourceState) {
		consumed = false;
	};
	public abstract T getData();
	public abstract Long getTimestamp();
	public abstract void setData(T t);
	public abstract String getID();
	public abstract boolean isConsumed();
	public abstract void setConsumed(boolean consumed);
	public abstract void setSourceID(String sourceID);
	public abstract String getSourceID();
	public abstract int getSequenceID();
	public abstract void setSequenceID(int sequenceID);
	public abstract void setSourceState(String sourceState);
	public abstract String getSourceState();
}