package edu.sjsu.cmpe.cache.client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
	private final String cacheServerUrl;

	public DistributedCacheService(String serverUrl) {
		this.cacheServerUrl = serverUrl;
	}
	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
	 */
	@Override
	public String get(long key) {
		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key)).asJson();
		} catch (UnirestException e) {
			System.err.println(e);
		}
		String value = response.getBody().getObject().getString("value");

		return value;
	}

	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
	 *      java.lang.String)
	 */
	@Override
	public void put(long key, String value) {
		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest
					.put(this.cacheServerUrl + "/cache/{key}/{value}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.routeParam("value", value).asJson();
			
		} catch (UnirestException e) {
			System.err.println(e);
		}

		if (response.getCode() != 200) {
			System.out.println("Failed to add to the cache.");
		}
	}
	//Lab 4 start
	@Override
	public Future<HttpResponse<JsonNode>> putAsync(long key, String value) {
		
		Future<HttpResponse<JsonNode>> future = null;
		try {
			future = Unirest
					.put(this.cacheServerUrl + "/cache/{key}/{value}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.routeParam("value", value)
					.asJsonAsync(new Callback<JsonNode>() {

						@Override
						public void cancelled() {
							System.out.println("Request to "+cacheServerUrl+" server failed");
						}

						@Override
						public void completed(HttpResponse<JsonNode> response) {
							System.out.println("Update completed in "+cacheServerUrl);
							int code = response.getCode();
							Map<String, List<String>> headers = response.getHeaders();
							JsonNode body = response.getBody();
							InputStream rawBody = response.getRawBody();
							CRDTClient.iPutCount++;
							CRDTClient.alSuccessServer.add(cacheServerUrl);
						}

						@Override
						public void failed(UnirestException arg0) {
							System.out.println("Request to "+cacheServerUrl+" server failed");
						}
					});
			
		} catch (Exception e) {
			System.err.println("Exception in DistributedCacheService:putAsync:::"+e+" "+e.getMessage());
			e.printStackTrace();
		}
		return future;
	}
	
	public void delete(long key){
		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest
					.delete(this.cacheServerUrl + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key)).asJson();
		} catch (Exception e) {
			System.err.println(e);
		}

		if (response.getCode() != 200) {
			System.out.println("Failed to delete to the cache.");
		}
	}
	//Lab 4 end
	@Override
	public Future<HttpResponse<JsonNode>> getAsync(long key) {
		Future<HttpResponse<JsonNode>> future = null;
		try{
			future = Unirest
					.get(this.cacheServerUrl + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key)).asJsonAsync();
		}
		catch(Exception ex){
			System.err.println("Exception in DistributedCacheService:getAsync:::"+ex+" "+ex.getMessage());
			ex.printStackTrace();
		}
		return future;
	}
}
