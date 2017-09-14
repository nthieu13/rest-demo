package com.example.restdemo.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
@Configuration
public class JerseyConfiguration extends ResourceConfig {
    public JerseyConfiguration() {
        packages("com.example.restdemo");
    }
}
