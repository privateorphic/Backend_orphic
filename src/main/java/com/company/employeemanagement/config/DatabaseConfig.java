package com.company.employeemanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:3306}")
    private String dbPort;

    @Value("${DB_NAME:employee_management}")
    private String dbName;

    @Value("${spring.datasource.username:${DB_USERNAME:root}}")
    private String dbUsername;

    @Value("${spring.datasource.password:${DB_PASSWORD:password}}")
    private String dbPassword;

    @Value("${SPRING_DATASOURCE_URL:${MYSQL_URL:}}")
    private String customUrl;

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();

        String finalUrl = "";
        String username = dbUsername;
        String password = dbPassword;

        // 1. Process customUrl (SPRING_DATASOURCE_URL or MYSQL_URL) if provided
        if (customUrl != null && !customUrl.isBlank()) {
            String rawUrl = customUrl.trim();

            // Extract credentials from customUrl if present (e.g. mysql://user:pass@host or jdbc:mysql://user:pass@host)
            String uriFormat = rawUrl;
            if (uriFormat.startsWith("jdbc:mysql://")) {
                uriFormat = uriFormat.substring(5);
            }
            if (uriFormat.startsWith("mysql://") && uriFormat.contains("@")) {
                try {
                    URI uri = new URI(uriFormat);
                    if (uri.getUserInfo() != null) {
                        String[] userInfo = uri.getUserInfo().split(":");
                        if (userInfo.length > 0 && !userInfo[0].isBlank()) username = userInfo[0];
                        if (userInfo.length > 1 && !userInfo[1].isBlank()) password = userInfo[1];
                    }
                } catch (Exception e) {
                    log.warn("Could not extract user info from customUrl URI", e);
                }
            }
            finalUrl = sanitizeJdbcUrl(rawUrl);
        }
        // 2. Process DB_HOST if provided as a URI
        else if (dbHost != null && (dbHost.startsWith("mysql://") || dbHost.contains("@"))) {
            try {
                String cleanHost = dbHost;
                if (!cleanHost.startsWith("mysql://")) {
                    cleanHost = "mysql://" + cleanHost;
                }
                URI uri = new URI(cleanHost);
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : Integer.parseInt(dbPort);
                String path = (uri.getPath() != null && uri.getPath().length() > 1) ? uri.getPath().substring(1) : dbName;

                if (path.contains("?")) {
                    path = path.substring(0, path.indexOf("?"));
                }

                String queryParams = "allowPublicKeyRetrieval=true&serverTimezone=UTC";
                if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
                    queryParams = uri.getQuery() + "&" + queryParams;
                } else if (host != null && host.contains("aivencloud.com")) {
                    queryParams = "ssl-mode=REQUIRED&" + queryParams;
                } else {
                    queryParams = "useSSL=false&" + queryParams;
                }

                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":");
                    if (userInfo.length > 0 && !userInfo[0].isBlank()) username = userInfo[0];
                    if (userInfo.length > 1 && !userInfo[1].isBlank()) password = userInfo[1];
                }

                finalUrl = String.format("jdbc:mysql://%s:%d/%s?%s", host, port, path, queryParams);
            } catch (Exception e) {
                log.warn("Failed to parse DB_HOST URI, falling back to basic construction", e);
                finalUrl = buildStandardJdbcUrl(dbHost, dbPort, dbName);
            }
        }
        // 3. Fallback standard JDBC URL construction
        else {
            finalUrl = buildStandardJdbcUrl(dbHost, dbPort, dbName);
        }

        log.info("Configured Database JDBC URL: {} with user: {}", sanitizeUrlForLogging(finalUrl), username);

        properties.setUrl(finalUrl);
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return properties;
    }

    private String sanitizeJdbcUrl(String url) {
        String cleaned = url.trim();
        if (cleaned.startsWith("mysql://")) {
            cleaned = "jdbc:" + cleaned;
        }
        return cleaned;
    }

    private String buildStandardJdbcUrl(String host, String port, String name) {
        boolean isCloud = host != null && (host.contains("aivencloud.com") || host.contains("render.com"));
        String sslParam = isCloud ? "ssl-mode=REQUIRED" : "useSSL=false";
        return String.format("jdbc:mysql://%s:%s/%s?%s&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, name, sslParam);
    }

    private String sanitizeUrlForLogging(String url) {
        if (url == null) return null;
        return url.replaceAll(":[^/@]+@", ":***@");
    }
}
