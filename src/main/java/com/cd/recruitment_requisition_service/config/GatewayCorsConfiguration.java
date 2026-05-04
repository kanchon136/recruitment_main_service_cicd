//package com.cd.recruitment_requisition_service.config;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//public class GatewayCorsConfiguration {
//
//    private static final Logger log = LoggerFactory.getLogger(GatewayCorsConfiguration.class);
//
//    // Allow configuration from application.yml
//    @Value("${gateway.cors.allowed-origins:*}")
//    private List<String> allowedOrigins;
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration corsConfig = new CorsConfiguration();
//
//
//        corsConfig.setAllowedOrigins(List.of("*"));
//
//        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//
//        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin"));
//
//        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
//
//        corsConfig.setAllowCredentials(false);
//        corsConfig.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig);
//
//        log.info("✅ CORS configured for origins: {}", corsConfig.getAllowedOrigins());
//        return new CorsWebFilter(source);
//    }
//}