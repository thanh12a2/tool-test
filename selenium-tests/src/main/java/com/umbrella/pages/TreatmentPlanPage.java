package com.umbrella.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * W-F-10: Treatment Plan (Phác đồ điều trị)
 * 
 * Purpose: Handle treatment plan management
 * URL: http://localhost/umbrella-corporation/treatment
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class TreatmentPlanPage extends BasePage {
    
    // Locators
    private static final By TREATMENT_TABLE = By.xpath("//table[@class='table']");
    private static final By TABLE_ROWS = By.xpath("//table[@class='table']//tbody//tr");
    private static final By ADD_BUTTON = By.xpath("//button[contains(text(), 'Thêm')]");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(@title, 'Sửa')]");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(@title, 'Xóa')]");
    private static final By VIEW_BUTTON = By.xpath("//button[contains(@title, 'Xem')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Tìm kiếm']");
    private static final By SAVE_BUTTON = By.xpath("//button[contains(text(), 'Lưu')]");
    
    public TreatmentPlanPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo TreatmentPlanPage");
    }
    
    /**
     * W-F-10-IS_TABLE_VISIBLE
     */
    public boolean isTreatmentTableVisible() {
        try {
            return elementUtils.isElementVisible(TREATMENT_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-10-GET_ROW_COUNT
     */
    public int getTreatmentRowCount() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * W-F-10-CLICK_ADD
     */
    public void clickAddTreatment() {
        logger.info("Thêm phác đồ điều trị");
        elementUtils.click(ADD_BUTTON);
    }
    
    /**
     * W-F-10-SEARCH
     */
    public void search(String term) {
        logger.info("Tìm kiếm: " + term);
        elementUtils.sendKeys(SEARCH_INPUT, term);
    }
    
    /**
     * W-F-10-EDIT_FIRST
     */
    public void editFirstTreatment() {
        logger.info("Sửa phác đồ đầu tiên");
        try {
            WebElement editBtn = driver.findElement(EDIT_BUTTON);
            elementUtils.click(editBtn);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
    
    /**
     * W-F-10-VIEW_FIRST
     */
    public void viewFirstTreatment() {
        logger.info("Xem phác đồ đầu tiên");
        try {
            WebElement viewBtn = driver.findElement(VIEW_BUTTON);
            elementUtils.click(viewBtn);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
}
