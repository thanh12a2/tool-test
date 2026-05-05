package com.umbrella.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import java.util.concurrent.TimeUnit;

/**
 * WebDriver Factory - Khởi tạo WebDriver theo loại browser
 * 
 * Mục đích: Quản lý việc tạo và cấu hình WebDriver cho các browser khác nhau
 * Hỗ trợ: Chrome, Firefox, Edge
 */
public class WebDriverFactory {
    
    /**
     * Tạo instance WebDriver dựa trên loại browser
     * @param browserType: CHROME, FIREFOX, EDGE
     * @return WebDriver instance
     */
    public static WebDriver createWebDriver(String browserType) {
        WebDriver driver = null;
        
        switch (browserType.toUpperCase()) {
            case "CHROME":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
                
            case "FIREFOX":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
                
            case "EDGE":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
                
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }
        
        // Cấu hình timeout chung
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        
        return driver;
    }
    
    /**
     * Đóng WebDriver instance
     */
    public static void closeDriver(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }
}
