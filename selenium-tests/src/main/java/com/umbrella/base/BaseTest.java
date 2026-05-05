package com.umbrella.base;

import com.umbrella.config.ConfigManager;
import com.umbrella.config.WebDriverFactory;
import com.umbrella.utils.WaitUtils;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base Test Class - Lớp cơ sở cho tất cả các test cases
 * 
 * Mục đích: Cung cấp các phương thức chung cho tất cả test classes
 * Bao gồm: setUp, tearDown, logging, wait utilities
 * 
 * Chức năng:
 * - Khởi tạo WebDriver trước mỗi test
 * - Cleanup WebDriver sau mỗi test
 * - Cung cấp common utilities cho navigation, waiting, logging
 */
public class BaseTest {
    
    // Logger cho logging test execution
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    
    // WebDriver instance - dùng chung cho tất cả test methods
    protected WebDriver driver;
    
    // Wait utilities
    protected WaitUtils waitUtils;
    
    /**
     * setUp() - Chạy trước mỗi test method
     * 
     * Mục đích: Khởi tạo test environment
     * Chức năng:
     * - Tạo WebDriver instance theo cấu hình
     * - Điều hướng đến URL ứng dụng
     * - Khởi tạo Wait utilities
     */
    @Before
    public void setUp() {
        logger.info("========== Bắt đầu Test Setup ==========");
        
        try {
            // Tạo WebDriver instance theo loại browser từ config
            String browser = ConfigManager.getBrowser();
            logger.info("Khởi tạo WebDriver cho browser: " + browser);
            driver = WebDriverFactory.createWebDriver(browser);
            
            // Khởi tạo Wait utilities
            waitUtils = new WaitUtils(driver);
            
            // Điều hướng đến URL ứng dụng
            String appUrl = ConfigManager.getAppUrl();
            logger.info("Điều hướng đến URL: " + appUrl);
            driver.navigate().to(appUrl);
            
            logger.info("========== Test Setup Hoàn Thành ==========");
        } catch (Exception e) {
            logger.error("Lỗi trong setUp: " + e.getMessage(), e);
            throw new RuntimeException("Không thể khởi tạo test environment", e);
        }
    }
    
    /**
     * tearDown() - Chạy sau mỗi test method
     * 
     * Mục đích: Dọn dẹp test environment
     * Chức năng:
     * - Đóng WebDriver instance
     * - Clear session
     */
    @After
    public void tearDown() {
        logger.info("========== Bắt đầu Test Teardown ==========");
        
        try {
            if (driver != null) {
                logger.info("Đóng WebDriver");
                WebDriverFactory.closeDriver(driver);
            }
            logger.info("========== Test Teardown Hoàn Thành ==========");
        } catch (Exception e) {
            logger.error("Lỗi trong tearDown: " + e.getMessage(), e);
        }
    }
    
    /**
     * Điều hướng đến một URL
     * @param url: URL để điều hướng
     */
    protected void navigateTo(String url) {
        logger.info("Điều hướng đến URL: " + url);
        driver.navigate().to(url);
    }
    
    /**
     * Làm mới trang hiện tại
     */
    protected void refreshPage() {
        logger.info("Làm mới trang");
        driver.navigate().refresh();
    }
    
    /**
     * Quay lại trang trước
     */
    protected void goBack() {
        logger.info("Quay lại trang trước");
        driver.navigate().back();
    }
    
    /**
     * Lấy tiêu đề trang
     * @return Tiêu đề trang
     */
    protected String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Tiêu đề trang: " + title);
        return title;
    }
    
    /**
     * Lấy URL hiện tại
     * @return URL hiện tại
     */
    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.info("URL hiện tại: " + url);
        return url;
    }
    
    /**
     * In log message thông tin
     * @param message: Thông báo cần log
     */
    protected void logInfo(String message) {
        logger.info(message);
    }
    
    /**
     * In log message lỗi
     * @param message: Thông báo lỗi
     */
    protected void logError(String message) {
        logger.error(message);
    }
    
    /**
     * In log message cảnh báo
     * @param message: Thông báo cảnh báo
     */
    protected void logWarn(String message) {
        logger.warn(message);
    }
}
