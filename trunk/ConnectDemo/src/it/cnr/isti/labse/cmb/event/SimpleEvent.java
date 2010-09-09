package it.cnr.isti.labse.cmb.event;

public class SimpleEvent extends ConnectBaseEvent<String>
{
	private static final long serialVersionUID = 1L;
	public Long timestamp = 0L;
	private String data = "";
	private String id;
	
	public SimpleEvent(String ID, Long ts)
	{
		super(ID, ts);
		this.timestamp = ts;
		this.id = ID;
	}
	
	/* (non-Javadoc)
	 * @see it.cnr.isti.labse.cmb.event.ConnectBaseEvents#getData()
	 */
	@Override
	public String getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see it.cnr.isti.labse.cmb.event.ConnectBaseEvents#getTimestamp()
	 */
	@Override
	public Long getTimestamp() {
		return timestamp;
	}

	/* (non-Javadoc)
	 * @see it.cnr.isti.labse.cmb.event.ConnectBaseEvents#setData(java.lang.String)
	 */

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void setData(String t) {
		this.data = t;
	}

	@Override
	public boolean isConsumed() {
		return false;
	}

	@Override
	public void setConsumed(boolean consumed) {
		// TODO Auto-generated method stub
		
	}
}