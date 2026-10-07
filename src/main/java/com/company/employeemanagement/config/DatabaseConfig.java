package com.company.employeemanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:3306}")
    private String dbPort;

    @Value("${DB_NAME:employee_management}")
    private String dbName;

    @Value("${DB_USERNAME:root}")
    private String dbUsername;

    @Value("${DB_PASSWORD:Devansh@2004}")
    private String dbPassword;

    @Value("${SPRING_DATASOURCE_URL:}")
    private String customUrl;

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();

        String finalUrl = "";
        String username = dbUsername;
        String password = dbPassword;

        if (customUrl != null && !customUrl.isBlank()) {
            finalUrl = customUrl;
        } else if (dbHost != null && (dbHost.startsWith("mysql://") || dbHost.contains("@"))) {
            try {
                String cleanHost = dbHost;
                if (!cleanHost.startsWith("mysql://")) {
                    cleanHost = "mysql://" + cleanHost;
                }
                URI uri = new URI(cleanHost);
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 3306;
                String path = (uri.getPath() != null && uri.getPath().length() > 1) ? uri.getPath().substring(1) : dbName;
                if (path.contains("?")) {
                    path = path.substring(0, path.indexOf("?"));
                }

                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":");
                    if (userInfo.length > 0 && !userInfo[0].isBlank()) username = userInfo[0];
                    if (userInfo.length > 1 && !userInfo[1].isBlank()) password = userInfo[1];
                }

                finalUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, path);
            } catch (Exception e) {
                finalUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", dbHost, dbPort, dbName);
            }
        } else {
            finalUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", dbHost, dbPort, dbName);
        }

        properties.setUrl(finalUrl);
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return properties;
    }
}
