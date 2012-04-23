package it.cnr.isti.labse.glimpse.event;

public class GlimpseBaseEventChoreos<T> extends GlimpseBaseEventAbstract<T> {

	private static final long serialVersionUID = 534588849735258143L;
	private String choreographySource;
	private String serviceSource;

	public GlimpseBaseEventChoreos(T data, Long timeStamp,
			String eventName, boolean isException, String choreographySource, String serviceSource) {
		super(data, timeStamp, eventName, isException);
		
		this.choreographySource = choreographySource;
		this.serviceSource = serviceSource;
	}

	public String getChoreographySource() {
		return choreographySource;
	}

	public void setChoreographySource(String choreographySource) {
		this.choreographySource = choreographySource;
	}

	public String getServiceSource() {
		return serviceSource;
	}

	public void setServiceSource(String serviceSource) {
		this.serviceSource = serviceSource;
	}
}
