package com.assessment.core.models;

import com.assessment.core.config.WeatherCAConfig;
import com.assessment.core.services.WeatherService;
import com.day.cq.wcm.api.Page;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.net.URL;

@Model(adaptables = SlingHttpServletRequest.class, resourceType = WeatherModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WeatherModel {

    protected static final String RESOURCE_TYPE = "assessment/components/weather";

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private Page currentPage;

    @OSGiService
    private WeatherService weatherService;

    @ValueMapValue
    @Getter
    private String city;

    @Getter
    private String endpoint;
    @Getter
    private String apiKey;

    @Getter
    private String weatherJson;

    @PostConstruct
    protected void init() {

        try {
            if (weatherService == null) {
                weatherJson = "WeatherService is NULL";
                return;
            }
            WeatherCAConfig config = getContextAwareConfigsForWeather(currentPage.getContentResource().getPath(), resourceResolver);
            if (config == null) {
                weatherJson = "CAConfig is NULL";
                return;
            }
            endpoint = config.endpoint();
            apiKey = config.apiKey();
            String requestedCity = (city != null && !city.trim().isEmpty()) ? city : "Bogota";
            String finalUrlString = endpoint + "/" + requestedCity;
            URL finalUrl = new URL(finalUrlString);
            weatherJson = weatherService.getWeather(finalUrl);
        } catch (Exception e) {
            weatherJson = "ERROR: " + e.getMessage();
        }
    }

    private WeatherCAConfig getContextAwareConfigsForWeather(String path, ResourceResolver resolver) {
        Resource resource = resolver.getResource(path);
        if (resource != null) {
            ConfigurationBuilder cb = resource.adaptTo(ConfigurationBuilder.class);
            if (cb != null) {
                return cb.as(WeatherCAConfig.class);
            }
        }
        return null;
    }
    public String getPageTitle() {
        return currentPage != null ? currentPage.getTitle() : "Weather Page";
    }
}

//package com.assessment.core.models;
//
//import com.assessment.core.config.WeatherCAConfig;
//
//
//import com.day.cq.wcm.api.Page;
//import java.net.URL;
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
//
//import lombok.Getter;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpResponse;
//import org.apache.sling.api.SlingHttpServletRequest;
//import org.apache.sling.api.resource.Resource;
//import org.apache.sling.api.resource.ResourceResolver;
//import org.apache.sling.caconfig.ConfigurationBuilder;
//import org.apache.sling.models.annotations.DefaultInjectionStrategy;
//import org.apache.sling.models.annotations.Model;
//import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
//import org.apache.sling.models.annotations.injectorspecific.SlingObject;
//import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
//import org.osgi.service.component.annotations.Reference;
//
//@Model(
//        adaptables = {SlingHttpServletRequest.class},
//        resourceType = {WeatherModel.RESOURCE_TYPE},
//        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
//public class WeatherModel {
//    protected static final String RESOURCE_TYPE = "assessment/components/weather";
//
//    @SlingObject
//    ResourceResolver resourceResolver;
//
//    @ScriptVariable
//    Page currentPage;
//
//    @Getter
//    @ValueMapValue
//    private String city;
//
//    @Getter
//    private String apiKey;
//
//    @Getter
//    private String endpoint;
//
//
//    private String weatherJson;
//
//    @PostConstruct
//    public void postConstruct() throws Exception {
//        WeatherCAConfig config = getContextAwareConfigsForWeather(
//                currentPage.getContentResource().getPath(),
//                resourceResolver
//        );
//        apiKey = config.apiKey();
//         endpoint = config.endpoint();
//        String requestedCity = city != null ? city : "Bogota";
//    }
//
//    public WeatherCAConfig getContextAwareConfigsForWeather (String currentPage, ResourceResolver resourceResolver) {
//        String currentPath = currentPage != null ? currentPage : StringUtils.EMPTY;
//        Resource contentResource = resourceResolver.getResource(currentPath);
//        if (contentResource!=null){
//            ConfigurationBuilder configurationBuilder = contentResource.adaptTo(ConfigurationBuilder.class);
//            if (configurationBuilder!=null){
//                return configurationBuilder.as(WeatherCAConfig.class);
//            }
//        }
//        return null;
//    }
//
////
//    public String getPageTitle() {
//        return currentPage != null ? currentPage.getTitle() : "Weather Page";
//    }
//}
