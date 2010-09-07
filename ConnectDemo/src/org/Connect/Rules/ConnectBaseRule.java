package org.Connect.Rules;

public interface ConnectBaseRule {
	public String getEngine();
	public void setEngine(String newEngine);
	public String getLanguage();
	public void setLanguage(String newLang);
	public String getWhen();
	public void setWhen(String newWhen);
	public String getThen();
	public void setThen(String newThen);
}
