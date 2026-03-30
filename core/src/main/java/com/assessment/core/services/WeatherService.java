package com.assessment.core.services;

import java.net.URL;

public interface WeatherService {
    String getWeather(URL url) throws Exception;
}