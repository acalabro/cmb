package it.cnr.isti.labse.cmb.listener;

import javax.jms.Message;

public interface EventsEvaluator {

	public abstract void onMessage(Message arg0);

}