package com.assessment.core.services;

import org.apache.http.client.methods.HttpGet;

public interface HttpService {
    String execute(HttpGet request);
}