package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.Future;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

/**
 * Cache Service Interface
 * 
 */
public interface CacheServiceInterface {
    public String get(long key);

    public void put(long key, String value);
    //Changes for lab4 Start
    public Future<HttpResponse<JsonNode>> putAsync(long key, String value);
    public Future<HttpResponse<JsonNode>> getAsync(long key);
    public void delete(long lKey);
    //Changes for lab4 End

	
}
