package it.cnr.isti.labse.cmb.event;

import java.io.Serializable;

public abstract class ConnectBaseEvent <T> implements Serializable{
	private static final long serialVersionUID = 1L;
	public ConnectBaseEvent(String ID, Long ts){};
	public abstract T getData();
	public abstract Long getTimestamp();
	public abstract void setData(T t);
	public abstract String getID();
}