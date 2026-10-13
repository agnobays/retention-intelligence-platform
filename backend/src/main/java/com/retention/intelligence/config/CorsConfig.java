package com.retention.intelligence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
            "https://eliteaurabeautysalon.com",
            "https://www.eliteaurabeautysalon.com",
            "http://eliteaurabeautysalon.com",
            "https://*.pages.dev",
            "http://localhost:3000",
            "http://localhost:5173",
            "*"
        ));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Tenant-ID", "Accept", "Origin", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization", "X-Tenant-ID", "Content-Disposition"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
