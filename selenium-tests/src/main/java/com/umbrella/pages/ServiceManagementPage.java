package com.umbrella.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * W-F-16: Service Management (Dịch vụ)
 * 
 * Purpose: Handle service management
 * URL: http://localhost/umbrella-corporation/services
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class ServiceManagementPage extends BasePage {
    
    // Locators
    private static final By SERVICES_TABLE = By.xpath("//table[@class='table']");
    private static final By TABLE_ROWS = By.xpath("//table[@class='table']//tbody//tr");
    private static final By ADD_BUTTON = By.xpath("//button[contains(text(), 'Thêm')]");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(@title, 'Sửa')]");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(@title, 'Xóa')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Tìm kiếm']");
    private static final By SERVICE_NAME_INPUT = By.name("name");
    private static final By PRICE_INPUT = By.name("price");
    private static final By DESCRIPTION_INPUT = By.name("description");
    private static final By SAVE_BUTTON = By.xpath("//button[contains(text(), 'Lưu')]");
    
    public ServiceManagementPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo ServiceManagementPage");
    }
    
    /**
     * W-F-16-IS_TABLE_VISIBLE
     */
    public boolean isServicesTableVisible() {
        try {
            return elementUtils.isElementVisible(SERVICES_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-16-GET_ROW_COUNT
     */
    public int getServiceRowCount() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * W-F-16-CLICK_ADD
     */
    public void clickAddService() {
        logger.info("Thêm dịch vụ");
        elementUtils.click(ADD_BUTTON);
    }
    
    /**
     * W-F-16-ENTER_SERVICE_NAME
     */
    public void enterServiceName(String name) {
        logger.info("Nhập tên dịch vụ: " + name);
        elementUtils.sendKeys(SERVICE_NAME_INPUT, name);
    }
    
    /**
     * W-F-16-ENTER_PRICE
     */
    public void enterPrice(String price) {
        logger.info("Nhập giá: " + price);
        elementUtils.sendKeys(PRICE_INPUT, price);
    }
    
    /**
     * W-F-16-ENTER_DESCRIPTION
     */
    public void enterDescription(String description) {
        logger.info("Nhập mô tả");
        elementUtils.sendKeys(DESCRIPTION_INPUT, description);
    }
    
    /**
     * W-F-16-SAVE
     */
    public void saveService() {
        logger.info("Lưu dịch vụ");
        elementUtils.click(SAVE_BUTTON);
    }
    
    /**
     * W-F-16-SEARCH
     */
    public void search(String term) {
        logger.info("Tìm kiếm: " + term);
        elementUtils.sendKeys(SEARCH_INPUT, term);
    }
    
    /**
     * W-F-16-EDIT_FIRST
     */
    public void editFirstService() {
        logger.info("Sửa dịch vụ đầu tiên");
        try {
            WebElement editBtn = driver.findElement(EDIT_BUTTON);
            elementUtils.click(editBtn);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
}
