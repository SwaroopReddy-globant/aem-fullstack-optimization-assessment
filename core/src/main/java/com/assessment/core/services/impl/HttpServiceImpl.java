package com.assessment.core.services.impl;

import com.assessment.core.services.HttpService;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service = HttpService.class, immediate = true)
public class HttpServiceImpl implements HttpService {

    private CloseableHttpClient httpClient;

    @Activate
    protected void activate() {
        PoolingHttpClientConnectionManager connManager =
                new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(50);
        connManager.setDefaultMaxPerRoute(20);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(15000)
                .build();
        httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(config)
                .build();
    }

    @Override
    public String execute(HttpGet request) {
        request.setHeader("User-Agent", "Mozilla/5.0");
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String result = entity != null ? EntityUtils.toString(entity) : "";
            return "STATUS: " + status + " DATA: " + result;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}