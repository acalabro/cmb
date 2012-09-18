package it.cnr.isti.labse.glimpse.transformer;

import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

public interface Transformer {
	
	public void setup();
	public ComplexEventRuleActionListDocument convertInputModel(String inputModelText);
	
}
