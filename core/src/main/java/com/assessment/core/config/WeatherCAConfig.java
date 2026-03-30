package com.assessment.core.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(
        label = "Weather Service Configuration",
        description = "Context-aware configuration for Weather API service"
)
public @interface WeatherCAConfig {

    @Property(
            label = "API Key",
            description = "The API key for accessing the weather service"
    )
    String apiKey() default "legacy-weather-api-key-12345";

    @Property(
            label = "API Endpoint",
            description = "The base endpoint URL for the weather service"
    )
    String endpoint() default "https://goweather.xyz/weather";

    @Property(
            label = "Country",
            description = "Default country for weather requests (ISO 3166-1 alpha-2 code)"
    )
    String country() default "us";

    @Property(
            label = "Language",
            description = "Default language for weather responses (ISO 639-1 code)"
    )
    String language() default "en";
}