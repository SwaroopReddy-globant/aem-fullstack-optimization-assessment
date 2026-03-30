package com.assessment.core.services.impl;

import com.assessment.core.services.HttpService;
import com.assessment.core.services.WeatherService;
import org.apache.http.client.methods.HttpGet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = WeatherService.class)
public class WeatherServiceImpl implements WeatherService {

    @Reference
    private HttpService httpService;

    private final Map<String, CacheObject> cache = new ConcurrentHashMap<>();

    private static final long TTL = 5 * 60 * 1000;

    private static class CacheObject {
        String data;
        long timestamp;
    }

    @Override
    public String getWeather(URL url) {

        String key = url.toString();

        try {
            CacheObject cached = cache.get(key);
            if (cached != null && (System.currentTimeMillis() - cached.timestamp) < TTL) {
                return "CACHE: " + cached.data;
            }
            HttpGet request = new HttpGet(key);
            request.setHeader("User-Agent", "Mozilla/5.0");
            String response = httpService.execute(request);
            CacheObject obj = new CacheObject();
            obj.data = response;
            obj.timestamp = System.currentTimeMillis();
            cache.put(key, obj);
            return response;
        } catch (Exception e) {
            CacheObject fallback = cache.get(key);
            if (fallback != null) {
                return "FALLBACK CACHE: " + fallback.data;
            }
            return "ERROR: " + e.getMessage();
        }
    }
}