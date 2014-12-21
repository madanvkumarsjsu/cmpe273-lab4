package edu.sjsu.cmpe.cache.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

public class Client {

	static ArrayList<CacheServiceInterface> servers;
	public static void main(String[] args) throws Exception {
		System.out.println("Starting Cache Client...");
		long key = 1;
		servers = new ArrayList<CacheServiceInterface>();

		Future<HttpResponse<JsonNode>> callBack1 = null;
		Future<HttpResponse<JsonNode>> callBack2 = null;
		Future<HttpResponse<JsonNode>> callBack3 = null;

		CacheServiceInterface cache1 = new DistributedCacheService("http://localhost:3000");
		CacheServiceInterface cache2 = new DistributedCacheService("http://localhost:3001");
		CacheServiceInterface cache3 = new DistributedCacheService("http://localhost:3002");

		servers.add(cache1);
		servers.add(cache2);
		servers.add(cache3);

		System.out.println("Step 1: Writing values to cache server");
		String value = "a";
		CRDTClient.iPutCount = 0;
		callBack1 = cache1.putAsync(key, value);
		callBack2 = cache2.putAsync(key, value);
		callBack3 = cache3.putAsync(key, value);

		while(!callBack1.isDone()){

		}
		while(!callBack2.isDone()){

		}
		while(!callBack3.isDone()){

		}
		if(CRDTClient.iPutCount > 1){
			System.out.println("Value updated successfully");
		}
		else{
			boolean bRevertSuccess = CRDTClient.revertLastCacheUpdate(key);
		}

		//Thread sleep start
		System.out.println("Thread sleeps for 1 minute");
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Thread resumes");
		//Thread sleep end

		//Updating - start
		System.out.println("Step 2: Updating values to cache server");

		value = "b";
		CRDTClient.iPutCount = 0;
		CRDTClient.alSuccessServer = new ArrayList<String>();


		callBack1 = cache1.putAsync(key, value);
		callBack2 = cache2.putAsync(key, value);
		callBack3 = cache3.putAsync(key, value);

		while(!callBack1.isDone()){

		}
		while(!callBack2.isDone()){

		}
		while(!callBack3.isDone()){

		}
		if(CRDTClient.iPutCount > 1){
			System.out.println("Value updated successfully");
		}
		else{
			boolean bRevertSuccess = CRDTClient.revertLastCacheUpdate(key);
		}
		//Updating - end
		//Thread sleep start
		System.out.println("Thread sleeps for 1 minute");
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Thread resumes");
		//		Thread sleep end
		//		Reading
		System.out.println("Step 3: Reading values from cache server");

		callBack1 = cache1.getAsync(key);
		callBack2 = cache2.getAsync(key);
		callBack3 = cache3.getAsync(key);

		while(!callBack1.isDone()){

		}
		while(!callBack2.isDone()){

		}
		while(!callBack3.isDone()){

		}
		String strval1 = null;
		String strval2 = null;
		String strval3 = null;
		try{
			//need to check the condition for getting value - if condition
			JsonNode nodeBody1 = callBack1.get().getBody();
			JsonNode nodeBody2 = callBack2.get().getBody();
			JsonNode nodeBody3 = callBack3.get().getBody();
			if(nodeBody1 != null)
				strval1 = nodeBody1.getObject().getString("value");
			if(nodeBody2 != null)
				strval2 = nodeBody2.getObject().getString("value");
			if(nodeBody3 != null)
				strval3 = nodeBody3.getObject().getString("value");

			if(strval1 == null)
				strval1 ="";
			if(strval2 == null)
				strval2 ="";
			if(strval3 == null)
				strval3 ="";
			ArrayList<String> alValues = new ArrayList<String>(); 
			alValues.add(strval1);
			alValues.add(strval2);
			alValues.add(strval3);

			Map<String,Integer> counts = new LinkedHashMap(alValues.size());
			for (String strVal : alValues) {
				counts.put(strVal, counts.containsKey(strVal) ? counts.get(strVal) + 1 : 1); 
			}

			String newVal = "";
			CacheServiceInterface csiInstaceToModify = null;
			for (Entry<String,Integer> entry : counts.entrySet()) { 
				if (entry.getValue() == 1) { 

					csiInstaceToModify = servers.get(alValues.indexOf(entry.getKey())); 
				}
				else{
					newVal = entry.getKey(); 
				}
			}
			csiInstaceToModify.putAsync(key, newVal);
			System.out.println("Value from cache server:"+newVal);
		}
		catch(Exception ex){
			System.out.println("Exception in Read repair"+ex.getMessage() +"\n"+ex.getStackTrace());
		}
		System.out.println("Existing Cache Client...");
	}
}