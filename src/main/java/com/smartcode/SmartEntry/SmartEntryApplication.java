package com.smartcode.SmartEntry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartEntryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartEntryApplication.class, args);
    }

   /* @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/products").allowedOrigins("http://localhost:8080");
            }
        };
    }*/

}
