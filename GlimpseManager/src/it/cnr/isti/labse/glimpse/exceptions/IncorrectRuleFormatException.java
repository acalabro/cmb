package it.cnr.isti.labse.glimpse.exceptions;

import it.cnr.isti.labse.glimpse.utils.DebugMessages;

public class IncorrectRuleFormatException extends Exception {
	private static final long serialVersionUID = -2577929182751048650L;

	public IncorrectRuleFormatException()
	{
		DebugMessages.line();
		System.out.println("Check rule format, may contains errors");
		DebugMessages.line();
	}
}
