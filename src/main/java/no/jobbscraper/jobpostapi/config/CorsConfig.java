package no.jobbscraper.jobpostapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    @Value("#{'${cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;

    @Value("#{'${cors.exposed-headers}'.split(',')}")
    private List<String> expectedHeaders;

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedHeaders(allowedHeaders);
        config.setAllowedMethods(allowedMethods);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
