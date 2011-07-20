package it.cnr.isti.labse.glimpse.api.probe;

import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEvent;

public interface GlimpseProbe {

	void sendMessage(GlimpseBaseEvent<String> event, boolean debug);
}
