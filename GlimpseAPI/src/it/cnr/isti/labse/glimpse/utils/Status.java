package it.cnr.isti.labse.glimpse.utils;

/**
 * @author acalabro
 *
 */
public enum Status {
		/**
		 * STARTING - The monitoring is booting up
		 * ACTIVE - The monitoring is ready to accept incoming requests
		 * STOPPING - The monitoring is shutting down
		 * UNAVAILABLE - The monitoring is not available for evaluations
		 */
		STARTING, ACTIVE, STOPPING, UNAVAILABLE
}
