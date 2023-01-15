package dev.kokud.recipepickerapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("recipepicker")
class AppConfig {
    private String serverUrl;

    @Bean
    public String getServerUrl() {
        return this.serverUrl;
    }
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
