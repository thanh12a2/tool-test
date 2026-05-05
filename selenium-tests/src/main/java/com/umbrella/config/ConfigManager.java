package com.umbrella.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration Manager - Quản lý các cấu hình của test suite
 * 
 * Mục đích: Đọc và cung cấp các giá trị cấu hình từ file properties
 * Chứa: URL ứng dụng, database config, browser settings, v.v.
 */
public class ConfigManager {
    
    private static Properties properties = new Properties();
    
    static {
        try {
            String configPath = System.getProperty("config.path", 
                "src/test/resources/config.properties");
            FileInputStream fis = new FileInputStream(configPath);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("Lỗi khi load file config: " + e.getMessage());
        }
    }
    
    /**
     * Lấy giá trị property
     * @param key: Tên property
     * @return Giá trị của property
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Lấy giá trị property với giá trị mặc định
     * @param key: Tên property
     * @param defaultValue: Giá trị mặc định nếu không tìm thấy
     * @return Giá trị property hoặc giá trị mặc định
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    // Getters for thường dùng
    
    public static String getAppUrl() {
        return getProperty("app.url", "http://localhost:8080/umbrella-corporation");
    }
    
    public static String getBrowser() {
        return getProperty("browser", "CHROME");
    }
    
    public static String getHeadlessMode() {
        return getProperty("headless", "false");
    }
    
    public static String getImplicitWaitTime() {
        return getProperty("implicit.wait", "10");
    }
    
    public static String getExplicitWaitTime() {
        return getProperty("explicit.wait", "20");
    }
    
    // Database config
    
    public static String getDbHost() {
        return getProperty("db.host", "localhost");
    }
    
    public static String getDbPort() {
        return getProperty("db.port", "3306");
    }
    
    public static String getDbName() {
        return getProperty("db.name", "umbrella_clinic");
    }
    
    public static String getDbUser() {
        return getProperty("db.user", "root");
    }
    
    public static String getDbPassword() {
        return getProperty("db.password", "");
    }
    
    public static String getDbDriver() {
        return getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
    
    // Test user credentials
    
    public static String getAdminUsername() {
        return getProperty("admin.username", "admin");
    }
    
    public static String getAdminPassword() {
        return getProperty("admin.password", "admin123");
    }
    
    public static String getDoctorUsername() {
        return getProperty("doctor.username", "doctor");
    }
    
    public static String getDoctorPassword() {
        return getProperty("doctor.password", "doctor123");
    }
}
