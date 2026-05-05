package com.umbrella.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * W-F-13: Specialty Management (Chuyên khoa)
 * 
 * Purpose: Handle specialty management
 * URL: http://localhost/umbrella-corporation/specialities
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class SpecialtyPage extends BasePage {
    
    // Locators
    private static final By SPECIALTIES_TABLE = By.xpath("//table[@class='table']");
    private static final By TABLE_ROWS = By.xpath("//table[@class='table']//tbody//tr");
    private static final By ADD_BUTTON = By.xpath("//button[contains(text(), 'Thêm')]");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(@title, 'Sửa')]");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(@title, 'Xóa')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Tìm kiếm']");
    private static final By NAME_INPUT = By.name("name");
    private static final By DESCRIPTION_INPUT = By.name("description");
    private static final By SAVE_BUTTON = By.xpath("//button[contains(text(), 'Lưu')]");
    
    public SpecialtyPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo SpecialtyPage");
    }
    
    /**
     * W-F-13-IS_TABLE_VISIBLE
     */
    public boolean isSpecialtiesTableVisible() {
        try {
            return elementUtils.isElementVisible(SPECIALTIES_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-13-GET_ROW_COUNT
     */
    public int getSpecialtyRowCount() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * W-F-13-CLICK_ADD
     */
    public void clickAddSpecialty() {
        logger.info("Thêm chuyên khoa");
        elementUtils.click(ADD_BUTTON);
    }
    
    /**
     * W-F-13-ENTER_NAME
     */
    public void enterSpecialtyName(String name) {
        logger.info("Nhập tên chuyên khoa: " + name);
        elementUtils.sendKeys(NAME_INPUT, name);
    }
    
    /**
     * W-F-13-ENTER_DESCRIPTION
     */
    public void enterDescription(String description) {
        logger.info("Nhập mô tả");
        elementUtils.sendKeys(DESCRIPTION_INPUT, description);
    }
    
    /**
     * W-F-13-SAVE
     */
    public void saveSpecialty() {
        logger.info("Lưu chuyên khoa");
        elementUtils.click(SAVE_BUTTON);
    }
    
    /**
     * W-F-13-SEARCH
     */
    public void search(String term) {
        logger.info("Tìm kiếm: " + term);
        elementUtils.sendKeys(SEARCH_INPUT, term);
    }
    
    /**
     * W-F-13-EDIT_FIRST
     */
    public void editFirstSpecialty() {
        logger.info("Sửa chuyên khoa đầu tiên");
        try {
            WebElement editBtn = driver.findElement(EDIT_BUTTON);
            elementUtils.click(editBtn);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
}
