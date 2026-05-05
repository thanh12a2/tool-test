package com.umbrella.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * W-F-14: Quản Lý Bệnh Nhân (Patient Management)
 * 
 * Purpose: Handle patient management workflow
 * URL: http://localhost/umbrella-corporation/patients
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class PatientManagementPageNew extends BasePage {
    
    // Locators
    private static final By PATIENTS_TABLE = By.xpath("//table[@class='table']");
    private static final By TABLE_ROWS = By.xpath("//table[@class='table']//tbody//tr");
    private static final By ADD_BUTTON = By.xpath("//button[contains(text(), 'Thêm')]");
    private static final By VIEW_BUTTON = By.xpath("//button[contains(@title, 'Xem')]");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(@title, 'Sửa')]");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(@title, 'Xóa')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Tìm kiếm']");
    private static final By SEARCH_BUTTON = By.xpath("//button[contains(text(), 'Tìm kiếm')]");
    private static final By FILTER_BUTTON = By.xpath("//button[contains(text(), 'Lọc')]");
    private static final By PATIENT_NAME_INPUT = By.name("name");
    private static final By PATIENT_EMAIL_INPUT = By.name("email");
    private static final By PATIENT_PHONE_INPUT = By.name("phone");
    private static final By SAVE_BUTTON = By.xpath("//button[contains(text(), 'Lưu')]");
    
    public PatientManagementPageNew(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo PatientManagementPage");
    }
    
    /**
     * W-F-14-IS_TABLE_VISIBLE
     */
    public boolean isPatientsTableVisible() {
        try {
            return elementUtils.isElementVisible(PATIENTS_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-14-GET_ROW_COUNT
     */
    public int getPatientRowCount() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * W-F-14-SEARCH_PATIENT
     */
    public void searchPatient(String searchTerm) {
        logger.info("Tìm kiếm: " + searchTerm);
        elementUtils.sendKeys(SEARCH_INPUT, searchTerm);
        elementUtils.click(SEARCH_BUTTON);
    }
    
    /**
     * W-F-14-CLICK_ADD
     */
    public void clickAddPatient() {
        logger.info("Thêm bệnh nhân");
        elementUtils.click(ADD_BUTTON);
    }
    
    /**
     * W-F-14-ENTER_PATIENT_NAME
     */
    public void enterPatientName(String name) {
        logger.info("Nhập tên: " + name);
        elementUtils.sendKeys(PATIENT_NAME_INPUT, name);
    }
    
    /**
     * W-F-14-ENTER_PATIENT_EMAIL
     */
    public void enterPatientEmail(String email) {
        logger.info("Nhập email: " + email);
        elementUtils.sendKeys(PATIENT_EMAIL_INPUT, email);
    }
    
    /**
     * W-F-14-ENTER_PATIENT_PHONE
     */
    public void enterPatientPhone(String phone) {
        logger.info("Nhập số điện thoại: " + phone);
        elementUtils.sendKeys(PATIENT_PHONE_INPUT, phone);
    }
    
    /**
     * W-F-14-SAVE_PATIENT
     */
    public void savePatient() {
        logger.info("Lưu bệnh nhân");
        elementUtils.click(SAVE_BUTTON);
    }
    
    /**
     * W-F-14-VIEW_PATIENT_DETAILS
     */
    public void viewPatientDetails() {
        logger.info("Xem chi tiết bệnh nhân");
        try {
            WebElement viewBtn = driver.findElement(VIEW_BUTTON);
            elementUtils.click(viewBtn);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }
}
