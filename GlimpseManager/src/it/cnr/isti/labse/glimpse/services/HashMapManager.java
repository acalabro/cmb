package it.cnr.isti.labse.glimpse.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class HashMapManager {


//	public HashMap<String, InetAddress> serviceTypeTable = new HashMap<String, InetAddress>();
//	public HashMap<String, InetAddress> serviceRoleTable = new HashMap<String, InetAddress>();
	public HashMap<String, InetAddress> theCommonMapTable;
	public InetAddress test;
	
	public HashMapManager() {
		try {
			test = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		theCommonMapTable = new HashMap<String, InetAddress>();
		theCommonMapTable.put("serviceA", test);
	}
	
	public InetAddress getMachine(String serviceName, String serviceType, String serviceRole) {
		return localSearchName(serviceName);
	}

	private InetAddress localSearchName(String serviceName) {
		return theCommonMapTable.get(serviceName);
	}
	
	public boolean insertLocalTable(int ruleInsertionID, String serviceName,
			InetAddress machineIP, String serviceType, String serviceRole) {
		
		//TODO: here we should manage the insertion based on several kind of parameter
				
		theCommonMapTable.put(serviceName, machineIP);
		
		return false;
	}

	public boolean deleteLocalTable(int ruleInsertionID, String serviceName,
			InetAddress machineIP, String serviceType, String serviceRole) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean updateLocalTable(int ruleInsertionID, String serviceName,
			InetAddress machineIP, String serviceType, String serviceRole) {
		// TODO Auto-generated method stub
		return false;
	}
}
