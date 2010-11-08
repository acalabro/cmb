package it.cnr.isti.labse.glimpse.exceptions;

public class IncorrectRuleFormatException extends Exception {
	private static final long serialVersionUID = -2577929182751048650L;

	public IncorrectRuleFormatException()
	{
		System.out.println("Check rule format, unable to parse it");
	}
}
