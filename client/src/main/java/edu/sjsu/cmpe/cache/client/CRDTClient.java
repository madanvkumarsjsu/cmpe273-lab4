package edu.sjsu.cmpe.cache.client;

import java.util.ArrayList;

public class CRDTClient {

	public static int iPutCount = 0;
	public static ArrayList<String> alSuccessServer = new ArrayList<String>();
	public static boolean revertLastCacheUpdate(long lKey){
		CacheServiceInterface cacheServer = null;
		for(int i =0;i< alSuccessServer.size(); i++){
			cacheServer = new DistributedCacheService(alSuccessServer.get(i));
			cacheServer.delete(lKey);
		}
		return true;
	}
}
