 /*
  * GLIMPSE: A generic and flexible monitoring infrastructure.
  * For further information: http://labsewiki.isti.cnr.it/labse/tools/glimpse/public/main
  * 
  * Copyright (C) 2011  Software Engineering Laboratory - ISTI CNR - Pisa - Italy
  * 
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
*/
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
	protected String eventName;
	
	public GlimpseBaseEvent(String eventName, String connectorID, String connectorInstanceID, String connectorInstanceExecutionID, int eventID, int eventInResponseToID, Long ts, String networkedSystemSource) {
		consumed = false;
	};
	
	public abstract T getData();
	public abstract void setData(T t);
	
	public abstract String getName();
	public abstract void setName(String eventName);
	
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