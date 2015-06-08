package it.cnr.isti.labsedc.glimpse.impl;

import org.apache.commons.net.ntp.TimeStamp;

import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventAbstract;
import it.cnr.isti.labsedc.glimpse.storage.StorageManager;

public class StorageManagerImpl extends StorageManager {

	@Override
	public StorageManager getStorageManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(String dbAddress, String dbName, String dbType,
			String loginName, String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeMessage(GlimpseBaseEventAbstract<?> anEventToStore) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveMessages(TimeStamp from, TimeStamp to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveMessages(String sessionID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveMessages(String userID, TimeStamp from, TimeStamp to) {
		// TODO Auto-generated method stub
		
	}
}
