/**
 * 
 */
package it.cnr.isti.labsedc.glimpse.storage;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventAbstract;

import org.apache.commons.net.ntp.TimeStamp;

/**
 * @author acalabro
 *
 */
public abstract class StorageManager extends Thread {

	public StorageManager storageManager;
	public abstract StorageManager getStorageManager();
	
	public abstract void init(String dbAddress, String dbName, String dbType, String loginName, String password);
	public abstract void storeMessage(GlimpseBaseEventAbstract<?> anEventToStore);	
	public abstract void retrieveMessages(TimeStamp from, TimeStamp to);
	public abstract void retrieveMessages(String sessionID);
	public abstract void retrieveMessages(String userID, TimeStamp from, TimeStamp to);
}
