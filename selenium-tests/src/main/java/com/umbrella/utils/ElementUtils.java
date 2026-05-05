package com.umbrella.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Element Utils - Công cụ tương tác với các element trên web
 * 
 * Mục đích: Cung cấp các phương thức chung cho tương tác với elements
 * Bao gồm: Click, Type, Select dropdown, Get text, v.v.
 */
public class ElementUtils {
    
    private static final Logger logger = LogManager.getLogger(ElementUtils.class);
    private WebDriver driver;
    
    /**
     * Constructor
     * @param driver: WebDriver instance
     */
    public ElementUtils(WebDriver driver) {
        this.driver = driver;
    }
    
    /**
     * Tìm element bằng By locator
     * @param locator: By locator
     * @return WebElement
     */
    public WebElement findElement(By locator) {
        logger.debug("Tìm element: " + locator);
        return driver.findElement(locator);
    }
    
    /**
     * Click vào element
     * @param locator: By locator của element
     */
    public void click(By locator) {
        logger.info("Click vào element: " + locator);
        WebElement element = driver.findElement(locator);
        element.click();
    }
    
    /**
     * Click vào element (WebElement)
     * @param element: WebElement
     */
    public void click(WebElement element) {
        logger.info("Click vào element");
        element.click();
    }
    
    /**
     * Nhập text vào input field
     * @param locator: By locator của input field
     * @param text: Text cần nhập
     */
    public void sendKeys(By locator, String text) {
        logger.info("Nhập text '" + text + "' vào: " + locator);
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Nhập text vào input field (WebElement)
     * @param element: WebElement
     * @param text: Text cần nhập
     */
    public void sendKeys(WebElement element, String text) {
        logger.info("Nhập text '" + text + "'");
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Lấy text từ element
     * @param locator: By locator
     * @return Text của element
     */
    public String getText(By locator) {
        WebElement element = driver.findElement(locator);
        String text = element.getText();
        logger.info("Lấy text: " + text);
        return text;
    }
    
    /**
     * Lấy text từ element (WebElement)
     * @param element: WebElement
     * @return Text của element
     */
    public String getText(WebElement element) {
        String text = element.getText();
        logger.info("Lấy text: " + text);
        return text;
    }
    
    /**
     * Lấy giá trị attribute từ element
     * @param locator: By locator
     * @param attributeName: Tên attribute
     * @return Giá trị attribute
     */
    public String getAttribute(By locator, String attributeName) {
        WebElement element = driver.findElement(locator);
        String value = element.getAttribute(attributeName);
        logger.info("Lấy attribute '" + attributeName + "': " + value);
        return value;
    }
    
    /**
     * Lấy giá trị attribute từ element (WebElement)
     * @param element: WebElement
     * @param attributeName: Tên attribute
     * @return Giá trị attribute
     */
    public String getAttribute(WebElement element, String attributeName) {
        String value = element.getAttribute(attributeName);
        logger.info("Lấy attribute '" + attributeName + "': " + value);
        return value;
    }
    
    /**
     * Chọn option trong dropdown bằng visible text
     * @param locator: By locator của select element
     * @param optionText: Text của option cần chọn
     */
    public void selectByVisibleText(By locator, String optionText) {
        logger.info("Chọn option '" + optionText + "' trong dropdown: " + locator);
        WebElement element = driver.findElement(locator);
        Select select = new Select(element);
        select.selectByVisibleText(optionText);
    }
    
    /**
     * Chọn option trong dropdown bằng value
     * @param locator: By locator của select element
     * @param value: Value của option cần chọn
     */
    public void selectByValue(By locator, String value) {
        logger.info("Chọn option với value '" + value + "' trong dropdown: " + locator);
        WebElement element = driver.findElement(locator);
        Select select = new Select(element);
        select.selectByValue(value);
    }
    
    /**
     * Chọn option trong dropdown bằng index
     * @param locator: By locator của select element
     * @param index: Index của option cần chọn
     */
    public void selectByIndex(By locator, int index) {
        logger.info("Chọn option tại index " + index + " trong dropdown: " + locator);
        WebElement element = driver.findElement(locator);
        Select select = new Select(element);
        select.selectByIndex(index);
    }
    
    /**
     * Xóa hết text trong input field
     * @param locator: By locator
     */
    public void clearField(By locator) {
        logger.info("Xóa text trong field: " + locator);
        WebElement element = driver.findElement(locator);
        element.clear();
    }
    
    /**
     * Kiểm tra element có visible không
     * @param locator: By locator
     * @return Boolean
     */
    public boolean isElementVisible(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element không visible: " + locator);
            return false;
        }
    }
    
    /**
     * Kiểm tra element có enabled không
     * @param locator: By locator
     * @return Boolean
     */
    public boolean isElementEnabled(By locator) {
        WebElement element = driver.findElement(locator);
        return element.isEnabled();
    }
    
    /**
     * Kiểm tra element có selected không (checkbox/radio)
     * @param locator: By locator
     * @return Boolean
     */
    public boolean isElementSelected(By locator) {
        WebElement element = driver.findElement(locator);
        return element.isSelected();
    }
    
    /**
     * Double click vào element
     * @param locator: By locator
     */
    public void doubleClick(By locator) {
        logger.info("Double click vào element: " + locator);
        WebElement element = driver.findElement(locator);
        Actions actions = new Actions(driver);
        actions.doubleClick(element).perform();
    }
    
    /**
     * Right click vào element
     * @param locator: By locator
     */
    public void rightClick(By locator) {
        logger.info("Right click vào element: " + locator);
        WebElement element = driver.findElement(locator);
        Actions actions = new Actions(driver);
        actions.contextClick(element).perform();
    }
    
    /**
     * Hover vào element
     * @param locator: By locator
     */
    public void hover(By locator) {
        logger.info("Hover vào element: " + locator);
        WebElement element = driver.findElement(locator);
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }
    
    /**
     * Drag và drop element
     * @param sourceLocator: By locator của source element
     * @param targetLocator: By locator của target element
     */
    public void dragAndDrop(By sourceLocator, By targetLocator) {
        logger.info("Drag and drop từ " + sourceLocator + " đến " + targetLocator);
        WebElement source = driver.findElement(sourceLocator);
        WebElement target = driver.findElement(targetLocator);
        Actions actions = new Actions(driver);
        actions.dragAndDrop(source, target).perform();
    }
    
    /**
     * Scroll tới element
     * @param locator: By locator
     */
    public void scrollToElement(By locator) {
        logger.info("Scroll tới element: " + locator);
        WebElement element = driver.findElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    /**
     * Execute JavaScript
     * @param script: JavaScript code
     * @return Kết quả thực thi
     */
    public Object executeScript(String script) {
        logger.debug("Execute JavaScript: " + script);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(script);
    }
    
    /**
     * Execute JavaScript với arguments
     * @param script: JavaScript code
     * @param args: Arguments
     * @return Kết quả thực thi
     */
    public Object executeScript(String script, Object... args) {
        logger.debug("Execute JavaScript với arguments");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(script, args);
    }
}
