package com.umbrella.utils;

import com.umbrella.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;

/**
 * Wait Utils - Cung cấp các phương thức chờ (Wait) cho Selenium
 * 
 * Mục đích: Xử lý các tình huống cần chờ element load
 * Bao gồm: Explicit waits, Expected conditions
 */
public class WaitUtils {
    
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    
    private WebDriver driver;
    private WebDriverWait wait;
    private int explicitWaitTime;
    
    /**
     * Constructor
     * @param driver: WebDriver instance
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.explicitWaitTime = Integer.parseInt(ConfigManager.getExplicitWaitTime());
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitTime));
    }
    
    /**
     * Chờ cho element xuất hiện (visible)
     * @param locator: By locator của element
     * @return WebElement khi element xuất hiện
     */
    public WebElement waitForElementVisible(By locator) {
        logger.debug("Chờ element visible: " + locator);
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Timeout chờ element visible: " + locator);
            throw e;
        }
    }
    
    /**
     * Chờ cho element xuất hiện (visible)
     * @param element: WebElement
     * @return WebElement khi element xuất hiện
     */
    public WebElement waitForElementVisible(WebElement element) {
        logger.debug("Chờ element visible");
        try {
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.error("Timeout chờ element visible");
            throw e;
        }
    }
    
    /**
     * Chờ cho element có thể click được
     * @param locator: By locator của element
     * @return WebElement khi element có thể click
     */
    public WebElement waitForElementClickable(By locator) {
        logger.debug("Chờ element clickable: " + locator);
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            logger.error("Timeout chờ element clickable: " + locator);
            throw e;
        }
    }
    
    /**
     * Chờ cho element có thể click được
     * @param element: WebElement
     * @return WebElement khi element có thể click
     */
    public WebElement waitForElementClickable(WebElement element) {
        logger.debug("Chờ element clickable");
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.error("Timeout chờ element clickable");
            throw e;
        }
    }
    
    /**
     * Chờ cho element xuất hiện (present) trong DOM
     * @param locator: By locator của element
     * @return WebElement khi element xuất hiện
     */
    public WebElement waitForElementPresent(By locator) {
        logger.debug("Chờ element present: " + locator);
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Timeout chờ element present: " + locator);
            throw e;
        }
    }
    
    /**
     * Chờ cho element biến mất (invisible/stale)
     * @param locator: By locator của element
     * @return Boolean - true nếu element biến mất
     */
    public Boolean waitForElementInvisible(By locator) {
        logger.debug("Chờ element invisible: " + locator);
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Timeout chờ element invisible: " + locator);
            throw e;
        }
    }
    
    /**
     * Chờ cho text xuất hiện trong element
     * @param locator: By locator của element
     * @param text: Text cần tìm
     * @return Boolean - true nếu text xuất hiện
     */
    public Boolean waitForTextPresent(By locator, String text) {
        logger.debug("Chờ text '" + text + "' trong element: " + locator);
        try {
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (Exception e) {
            logger.error("Timeout chờ text '" + text + "' trong element: " + locator);
            throw e;
        }
    }
    
    /**
     * Chờ cho số lượng elements nhất định xuất hiện
     * @param locator: By locator của elements
     * @param count: Số lượng elements cần chờ
     * @return WebElement list khi đủ số lượng
     */
    public void waitForNumberOfElementsPresent(By locator, int count) {
        logger.debug("Chờ " + count + " elements xuất hiện: " + locator);
        try {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, count - 1));
        } catch (Exception e) {
            logger.error("Timeout chờ " + count + " elements: " + locator);
            throw e;
        }
    }
    
    /**
     * Chờ cho URL chứa một text nhất định
     * @param urlPart: Phần URL cần chờ
     * @return Boolean - true nếu URL chứa text
     */
    public Boolean waitForUrlContains(String urlPart) {
        logger.debug("Chờ URL chứa: " + urlPart);
        try {
            return wait.until(ExpectedConditions.urlContains(urlPart));
        } catch (Exception e) {
            logger.error("Timeout chờ URL chứa: " + urlPart);
            throw e;
        }
    }
    
    /**
     * Chờ đơn giản (Thread.sleep)
     * @param miliseconds: Số mili giây cần chờ
     */
    public void waitFor(long miliseconds) {
        try {
            logger.debug("Chờ " + miliseconds + " mili giây");
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            logger.error("Lỗi khi chờ: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
