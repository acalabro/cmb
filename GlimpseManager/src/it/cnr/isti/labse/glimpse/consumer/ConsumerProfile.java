package it.cnr.isti.labse.glimpse.consumer;

public class ConsumerProfile {
	private String name;
	private String answerTopic;
	public ConsumerProfile(String name, String answerTopic)
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
