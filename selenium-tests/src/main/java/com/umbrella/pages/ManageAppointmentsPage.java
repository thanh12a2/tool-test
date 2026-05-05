package com.umbrella.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * W-F-09: Quản Lý Lịch Hẹn (Manage Appointments)
 * 
 * Purpose: Handle appointment management workflow
 * URL: http://localhost/umbrella-corporation/appointments
 * 
 * Locators from Playwright: 
 * - Table with appointments
 * - Edit and delete buttons for each row
 * - Filter and search options
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class ManageAppointmentsPage extends BasePage {
    
    // Locators
    private static final By APPOINTMENTS_TABLE = By.xpath("//table[@class='table']");
    private static final By TABLE_ROWS = By.xpath("//table[@class='table']//tbody//tr");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(@title, 'Sửa')]");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(@title, 'Xóa')]");
    private static final By FILTER_BUTTON = By.xpath("//button[contains(text(), 'Lọc')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Tìm kiếm']");
    private static final By SEARCH_BUTTON = By.xpath("//button[contains(text(), 'Tìm kiếm')]");
    private static final By REFRESH_BUTTON = By.xpath("//button[contains(text(), 'Làm mới')]");
    private static final By MODAL_CLOSE = By.xpath("//button[@data-dismiss='modal']");
    private static final By DOCTOR_FILTER = By.name("doctor");
    private static final By DATE_RANGE = By.xpath("//input[@type='daterange']");
    private static final By STATUS_FILTER = By.name("status");
    
    public ManageAppointmentsPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo ManageAppointmentsPage");
    }
    
    /**
     * W-F-09-IS_TABLE_VISIBLE
     * Purpose: Verify appointments table is loaded
     */
    public boolean isAppointmentsTableVisible() {
        try {
            return elementUtils.isElementVisible(APPOINTMENTS_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-09-GET_ROW_COUNT
     * Purpose: Get number of appointments displayed
     */
    public int getAppointmentRowCount() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.size();
        } catch (Exception e) {
            logger.error("Error getting row count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * W-F-09-SEARCH_APPOINTMENT
     * Purpose: Search appointments by criteria
     */
    public void searchAppointment(String searchTerm) {
        logger.info("Tìm kiếm: " + searchTerm);
        elementUtils.sendKeys(SEARCH_INPUT, searchTerm);
        elementUtils.click(SEARCH_BUTTON);
    }
    
    /**
     * W-F-09-FILTER_BY_DOCTOR
     * Purpose: Filter appointments by doctor
     */
    public void filterByDoctor(String doctorName) {
        logger.info("Lọc theo bác sĩ: " + doctorName);
        elementUtils.selectByVisibleText(DOCTOR_FILTER, doctorName);
    }
    
    /**
     * W-F-09-FILTER_BY_STATUS
     * Purpose: Filter by appointment status
     */
    public void filterByStatus(String status) {
        logger.info("Lọc theo trạng thái: " + status);
        elementUtils.selectByVisibleText(STATUS_FILTER, status);
    }
    
    /**
     * W-F-09-APPLY_FILTER
     * Purpose: Apply filters
     */
    public void applyFilter() {
        logger.info("Áp dụng bộ lọc");
        elementUtils.click(FILTER_BUTTON);
    }
    
    /**
     * W-F-09-REFRESH_TABLE
     * Purpose: Refresh appointment list
     */
    public void refreshTable() {
        logger.info("Làm mới danh sách");
        elementUtils.click(REFRESH_BUTTON);
    }
    
    /**
     * W-F-09-EDIT_FIRST_APPOINTMENT
     * Purpose: Click edit on first appointment
     */
    public void editFirstAppointment() {
        logger.info("Sửa lịch hẹn đầu tiên");
        try {
            WebElement firstEditBtn = driver.findElement(EDIT_BUTTON);
            elementUtils.click(firstEditBtn);
        } catch (Exception e) {
            logger.error("Error clicking edit: " + e.getMessage());
        }
    }
    
    /**
     * W-F-09-DELETE_FIRST_APPOINTMENT
     * Purpose: Click delete on first appointment
     */
    public void deleteFirstAppointment() {
        logger.info("Xóa lịch hẹn đầu tiên");
        try {
            WebElement firstDeleteBtn = driver.findElement(DELETE_BUTTON);
            elementUtils.click(firstDeleteBtn);
        } catch (Exception e) {
            logger.error("Error clicking delete: " + e.getMessage());
        }
    }
}
