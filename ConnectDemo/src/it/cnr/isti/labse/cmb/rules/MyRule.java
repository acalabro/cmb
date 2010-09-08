package it.cnr.isti.labse.cmb.rules;

public class MyRule implements ConnectBaseRule {

	private String engine;
	private String language;
	private String when;
	private String then;
	
	public MyRule()
	{
		
	}
	
	public MyRule(String engine, String language, String when, String then)
	{
		this.engine = engine;
		this.language = language;
		this.when = when;
		this.then = then;
	}
	
	public String getEngine() {
		return this.engine;
	}

	public void setEngine(String newEngine) {
		this.engine = newEngine;
	}
	
	public String getLanguage()
	{
		return this.language;
	}
	
	public void setLanguage(String newLanguage)
	{
		this.language = newLanguage;
	}
	
	public String getWhen()
	{
		return this.when;
	}
	
	public void setWhen(String newWhen)
	{
		this.when = newWhen;
	}
	
	public String getThen()
	{
		return this.then;
	}
	
	public void setThen(String newThen)
	{
		this.then = newThen;
	}
}
