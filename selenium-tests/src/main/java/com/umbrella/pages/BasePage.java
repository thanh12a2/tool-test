package com.umbrella.pages;

import com.umbrella.utils.ElementUtils;
import com.umbrella.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base Page - Lớp cơ sở cho tất cả Page Object
 * 
 * Mục đích: Cung cấp các chức năng chung cho tất cả page objects
 * Bao gồm: Utilities, Locators chung, Methods chung
 */
public class BasePage {
    
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    
    protected WebDriver driver;
    protected WaitUtils waitUtils;
    protected ElementUtils elementUtils;
    
    /**
     * Constructor
     * @param driver: WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.elementUtils = new ElementUtils(driver);
    }
    
    /**
     * Lấy tiêu đề trang
     * @return Tiêu đề trang
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Lấy URL hiện tại
     * @return URL hiện tại
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    /**
     * Làm mới trang
     */
    public void refreshPage() {
        driver.navigate().refresh();
    }
    
    /**
     * Đợi trang load xong
     */
    public void waitForPageLoad() {
        waitUtils.waitFor(1000);
    }
}
