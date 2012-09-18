package it.cnr.isti.labse.glimpse.impl;

import java.io.File;
import java.net.URI;
import java.util.Collections;

import it.cnr.isti.labse.glimpse.transformer.Transformer;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

public class TransformerImpl implements Transformer {

	private String modelPath;

	public TransformerImpl(File modelFilePath) {
		this.modelPath = modelFilePath.getAbsolutePath();
	}
	
	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	public ComplexEventRuleActionListDocument convertInputModel(String inputModelText) {
		
//		String convertedString;
//		
//		URI modelURI = URI.create(modelPath);
//		File destinationFile = new File(convertedString);
//		
//		Generate generator = new Generate(modelURI, destinationFile, Collections.emptyList());
//		generator.doGenerate();
//		
//		return ComplexEventRuleActionListDocument.Factory.parse(convertedString);
		return null;
	}

}
