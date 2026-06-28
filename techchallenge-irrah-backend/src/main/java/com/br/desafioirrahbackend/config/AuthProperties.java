package com.br.desafioirrahbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(Duration tokenTtl) {

    public AuthProperties {
        tokenTtl = tokenTtl == null ? Duration.ofHours(24) : tokenTtl;
    }
}
