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
	private String eventName;
	
	public GlimpseBaseEventImpl(String eventName, String connectorID, String connectorInstanceID, String connectorInstanceExecutionID, int eventID, int eventInResponseToID, Long ts, String networkedSystemSource)
	{
		super(eventName, connectorID, connectorInstanceID, connectorInstanceExecutionID, eventID, eventInResponseToID, ts, networkedSystemSource);
		this.connectorID = connectorID;
		this.connectorInstanceID = connectorInstanceID;
		this.connectorInstanceExecutionID = connectorInstanceExecutionID;
		this.eventID = eventID;
		this.eventInResponseToID = eventInResponseToID;
		this.timestamp = ts;
		this.networkedSystemSource = networkedSystemSource;
		this.eventName = eventName;
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

	@Override
	public String getName() {
		return this.eventName;
	}

	@Override
	public void setName(String eventName) {
		this.eventName = eventName;		
	}
}