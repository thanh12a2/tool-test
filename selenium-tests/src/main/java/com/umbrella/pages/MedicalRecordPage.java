package com.umbrella.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * W-F-11: Medical Record (Bệnh án)
 * 
 * Purpose: Handle medical records
 * URL: http://localhost/umbrella-corporation/records
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class MedicalRecordPage extends BasePage {
    
    // Locators
    private static final By RECORDS_TABLE = By.xpath("//table[@class='table']");
    private static final By TABLE_ROWS = By.xpath("//table[@class='table']//tbody//tr");
    private static final By VIEW_BUTTON = By.xpath("//button[contains(@title, 'Xem')]");
    private static final By PRINT_BUTTON = By.xpath("//button[contains(@title, 'In')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Tìm kiếm']");
    private static final By FILTER_BUTTON = By.xpath("//button[contains(text(), 'Lọc')]");
    
    public MedicalRecordPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo MedicalRecordPage");
    }
    
    /**
     * W-F-11-IS_TABLE_VISIBLE
     */
    public boolean isRecordsTableVisible() {
        try {
            return elementUtils.isElementVisible(RECORDS_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-11-GET_ROW_COUNT
     */
    public int getRecordRowCount() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * W-F-11-SEARCH
     */
    public void search(String term) {
        logger.info("Tìm kiếm: " + term);
        elementUtils.sendKeys(SEARCH_INPUT, term);
    }
    
    /**
     * W-F-11-VIEW_FIRST
     */
    public void viewFirstRecord() {
        logger.info("Xem bệnh án đầu tiên");
        try {
            WebElement viewBtn = driver.findElement(VIEW_BUTTON);
            elementUtils.click(viewBtn);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
    
    /**
     * W-F-11-PRINT_FIRST
     */
    public void printFirstRecord() {
        logger.info("In bệnh án đầu tiên");
        try {
            WebElement printBtn = driver.findElement(PRINT_BUTTON);
            elementUtils.click(printBtn);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
}
