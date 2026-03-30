package com.assessment.core.models;

import com.assessment.core.config.WeatherCAConfig;
import com.assessment.core.services.WeatherService;
import com.assessment.core.utilis.TestUtils;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherModelTest {

    private WeatherModel weatherModel;
    private ResourceResolver resolver;
    private Page currentPage;
    private WeatherService weatherService;
    private Resource resource;
    private ConfigurationBuilder configBuilder;
    private WeatherCAConfig config;

    @BeforeEach
    void setUp() {
        weatherModel = new WeatherModel();

        resolver = mock(ResourceResolver.class);
        currentPage = mock(Page.class);
        weatherService = mock(WeatherService.class);
        resource = mock(Resource.class);
        configBuilder = mock(ConfigurationBuilder.class);
        config = mock(WeatherCAConfig.class);

        TestUtils.setField(weatherModel, "resourceResolver", resolver);
        TestUtils.setField(weatherModel, "currentPage", currentPage);
        TestUtils.setField(weatherModel, "weatherService", weatherService);
    }

    @Test
    void testWeatherServiceIsNull() {
        TestUtils.setField(weatherModel, "weatherService", null);
        weatherModel.init();
        assertEquals("WeatherService is NULL", weatherModel.getWeatherJson());
    }

    @Test
    void testConfigIsNull() {
        when(currentPage.getContentResource()).thenReturn(resource);
        when(resolver.getResource(anyString())).thenReturn(null);

        weatherModel.init();
        assertEquals("CAConfig is NULL", weatherModel.getWeatherJson());
    }

    @Test
    void testValidConfigWithCity() throws Exception {
        when(currentPage.getContentResource()).thenReturn(resource);
        when(resource.getPath()).thenReturn("/content/test");
        when(resolver.getResource("/content/test")).thenReturn(resource);
        when(resource.adaptTo(ConfigurationBuilder.class)).thenReturn(configBuilder);
        when(configBuilder.as(WeatherCAConfig.class)).thenReturn(config);
        when(config.endpoint()).thenReturn("https://goweather.xyz/weather");
        TestUtils.setField(weatherModel, "city", "London");

        when(weatherService.getWeather(new URL("https://goweather.xyz/weather/London"))).thenReturn("{\"temp\":20}");

        weatherModel.init();
        assertEquals("https://goweather.xyz/weather", weatherModel.getEndpoint());
        assertEquals("{\"temp\":20}", weatherModel.getWeatherJson());
    }

    @Test
    void testValidConfigWithoutCityDefaultsToBogota() throws Exception {
        when(currentPage.getContentResource()).thenReturn(resource);
        when(resource.getPath()).thenReturn("/content/test");
        when(resolver.getResource("/content/test")).thenReturn(resource);
        when(resource.adaptTo(ConfigurationBuilder.class)).thenReturn(configBuilder);
        when(configBuilder.as(WeatherCAConfig.class)).thenReturn(config);
        when(config.endpoint()).thenReturn("https://goweather.xyz/weather");

        TestUtils.setField(weatherModel, "city", null);

        when(weatherService.getWeather(new URL("https://goweather.xyz/weather/Bogota"))).thenReturn("{\"temp\":25}");

        weatherModel.init();
        assertEquals("{\"temp\":25}", weatherModel.getWeatherJson());
    }

    @Test
    void testWeatherServiceThrowsException() throws Exception {
        when(currentPage.getContentResource()).thenReturn(resource);
        when(resource.getPath()).thenReturn("/content/test");
        when(resolver.getResource("/content/test")).thenReturn(resource);
        when(resource.adaptTo(ConfigurationBuilder.class)).thenReturn(configBuilder);
        when(configBuilder.as(WeatherCAConfig.class)).thenReturn(config);
        when(config.endpoint()).thenReturn("https://goweather.xyz/weather");
        TestUtils.setField(weatherModel, "city", "Paris");

        when(weatherService.getWeather(any(URL.class))).thenThrow(new RuntimeException("Service down"));

        weatherModel.init();
        assertTrue(weatherModel.getWeatherJson().contains("ERROR: Service down"));
    }

    @Test
    void testGetPageTitle() {
        when(currentPage.getTitle()).thenReturn("My Weather Page");
        assertEquals("My Weather Page", weatherModel.getPageTitle());

        TestUtils.setField(weatherModel, "currentPage", null);
        assertEquals("Weather Page", weatherModel.getPageTitle());
    }
}
