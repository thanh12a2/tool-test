package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Appointment Management Page - Quản Lý Lịch Hẹn
 * 
 * ID: W-F-09
 * Mục đích: Xử lý các thao tác liên quan đến quản lý lịch hẹn
 * Chức năng:
 * - Xem danh sách lịch hẹn
 * - Cập nhật trạng thái lịch hẹn
 * - Lọc lịch hẹn
 * - Phân công bác sĩ
 */
public class AppointmentManagementPage extends BasePage {
    
    // Locators
    private static final By APPOINTMENT_LIST_TABLE = By.id("appointment-table");
    private static final By APPOINTMENT_ROWS = By.xpath("//table[@id='appointment-table']//tbody//tr");
    private static final By STATUS_FILTER = By.id("status-filter");
    private static final By DATE_FILTER = By.id("date-filter");
    private static final By APPLY_FILTER_BUTTON = By.id("btn-apply-filter");
    private static final By CLEAR_FILTER_BUTTON = By.id("btn-clear-filter");
    
    private static final By EDIT_BUTTON = By.xpath("//button[contains(text(), 'Sửa')]");
    private static final By VIEW_BUTTON = By.xpath("//button[contains(text(), 'Xem')]");
    private static final By CONFIRM_BUTTON = By.id("btn-confirm");
    
    private static final By SPECIALITY_INPUT = By.id("speciality-input");
    private static final By DOCTOR_DROPDOWN = By.id("doctor-select");
    private static final By SAVE_BUTTON = By.id("btn-save");
    
    private static final By SUCCESS_MESSAGE = By.className("alert-success");
    private static final By ERROR_MESSAGE = By.className("alert-danger");
    
    /**
     * Constructor
     */
    public AppointmentManagementPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * TC-W-F-09-TC01: Xem danh sách lịch hẹn
     */
    public void viewAppointmentList() {
        logger.info("Xem danh sách lịch hẹn");
        waitUtils.waitForElementVisible(APPOINTMENT_LIST_TABLE);
    }
    
    /**
     * Lọc lịch hẹn theo ngày
     */
    public void filterByDate(String date) {
        logger.info("Lọc lịch hẹn theo ngày: " + date);
        elementUtils.sendKeys(DATE_FILTER, date);
        elementUtils.click(APPLY_FILTER_BUTTON);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Lọc lịch hẹn theo trạng thái
     */
    public void filterByStatus(String status) {
        logger.info("Lọc lịch hẹn theo trạng thái: " + status);
        elementUtils.selectByVisibleText(STATUS_FILTER, status);
        elementUtils.click(APPLY_FILTER_BUTTON);
        waitUtils.waitFor(1000);
    }
    
    /**
     * TC-W-F-09-TC02: Cập nhật lịch hẹn
     */
    public void updateAppointment(String speciality, String doctor) {
        logger.info("Cập nhật lịch hẹn - Chuyên khoa: " + speciality + ", Bác sĩ: " + doctor);
        elementUtils.sendKeys(SPECIALITY_INPUT, speciality);
        elementUtils.selectByVisibleText(DOCTOR_DROPDOWN, doctor);
        elementUtils.click(SAVE_BUTTON);
        waitUtils.waitFor(2000);
    }
    
    /**
     * Lấy success message
     */
    public String getSuccessMessage() {
        try {
            waitUtils.waitForElementVisible(SUCCESS_MESSAGE);
            return elementUtils.getText(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Kiểm tra có lịch hẹn trong danh sách
     */
    public boolean hasAppointments() {
        return elementUtils.isElementVisible(APPOINTMENT_LIST_TABLE);
    }
}
