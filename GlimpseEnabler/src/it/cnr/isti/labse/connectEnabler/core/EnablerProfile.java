package it.cnr.isti.labse.connectEnabler.core;

public class EnablerProfile {
	private String name;
	private String answerTopic;
	public EnablerProfile(String name, String answerTopic)
	{
		this.name = name;
		this.answerTopic = answerTopic;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getAnswerTopic()
	{
		return this.answerTopic;
	}
		
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setAnswerTopic(String answerTopic)
	{
		this.answerTopic = answerTopic;
	}
}
