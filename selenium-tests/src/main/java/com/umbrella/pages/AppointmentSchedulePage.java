package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Appointment Schedule Page - Tạo/Sửa lịch khám
 * 
 * ID: W-F-07
 * Mục đích: Xử lý các thao tác liên quan đến tạo và sửa lịch khám
 * Chức năng:
 * - Tạo lịch khám phương thức 1 (Tạo theo nhu cầu khám bệnh)
 * - Tạo lịch khám phương thức 2 (Chỉ định bác sĩ)
 * - Sửa lịch khám
 * - Validate các trường bắt buộc
 * - Kiểm tra điều kiện tiên quyết
 */
public class AppointmentSchedulePage extends BasePage {
    
    // Locators cho Appointment Schedule page
    private static final By CREATE_SCHEDULE_BUTTON = By.id("btn-create-schedule");
    private static final By METHOD_1_RADIO = By.id("method-1");
    private static final By METHOD_2_RADIO = By.id("method-2");
    
    private static final By SPECIALITY_DROPDOWN = By.id("speciality");
    private static final By DOCTOR_DROPDOWN = By.id("doctor");
    private static final By DATE_INPUT = By.id("appointment-date");
    private static final By TIME_INPUT = By.id("appointment-time");
    private static final By INSURANCE_CODE_INPUT = By.id("insurance-code");
    private static final By PHONE_INPUT = By.id("phone");
    
    private static final By SAVE_BUTTON = By.id("btn-save");
    private static final By CANCEL_BUTTON = By.id("btn-cancel");
    private static final By SUCCESS_MESSAGE = By.className("alert-success");
    private static final By ERROR_MESSAGE = By.className("alert-error");
    private static final By VALIDATION_ERROR = By.className("field-error");
    
    private static final By SCHEDULE_LIST_TABLE = By.id("schedule-table");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(text(), 'Sửa')]");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(text(), 'Xóa')]");
    
    /**
     * Constructor
     * @param driver: WebDriver instance
     */
    public AppointmentSchedulePage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Click nút tạo lịch khám
     * 
     * Mục đích: Mở form tạo lịch khám
     */
    public void clickCreateScheduleButton() {
        logger.info("Click nút tạo lịch khám");
        elementUtils.click(CREATE_SCHEDULE_BUTTON);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Chọn phương thức 1 - Tạo theo nhu cầu khám bệnh
     * 
     * TC-W-F-07-TC01: Tạo lịch khám mới với đầy đủ thông tin
     */
    public void selectMethod1() {
        logger.info("Chọn phương thức 1 - Tạo theo nhu cầu khám bệnh");
        elementUtils.click(METHOD_1_RADIO);
        waitUtils.waitFor(500);
    }
    
    /**
     * Chọn phương thức 2 - Chỉ định bác sĩ
     * 
     * TC-W-F-07-TC03: Tạo lịch khám từ lịch hẹn bệnh nhân
     */
    public void selectMethod2() {
        logger.info("Chọn phương thức 2 - Chỉ định bác sĩ");
        elementUtils.click(METHOD_2_RADIO);
        waitUtils.waitFor(500);
    }
    
    /**
     * Chọn chuyên khoa
     * 
     * @param specialityName: Tên chuyên khoa cần chọn (VD: "Nội tổng hợp")
     */
    public void selectSpeciality(String specialityName) {
        logger.info("Chọn chuyên khoa: " + specialityName);
        elementUtils.selectByVisibleText(SPECIALITY_DROPDOWN, specialityName);
        waitUtils.waitFor(500);
    }
    
    /**
     * Chọn bác sĩ
     * 
     * @param doctorName: Tên bác sĩ cần chọn
     */
    public void selectDoctor(String doctorName) {
        logger.info("Chọn bác sĩ: " + doctorName);
        elementUtils.selectByVisibleText(DOCTOR_DROPDOWN, doctorName);
        waitUtils.waitFor(500);
    }
    
    /**
     * Nhập ngày khám
     * 
     * @param date: Ngày khám (format: yyyy-MM-dd)
     */
    public void enterAppointmentDate(String date) {
        logger.info("Nhập ngày khám: " + date);
        elementUtils.sendKeys(DATE_INPUT, date);
    }
    
    /**
     * Nhập giờ khám
     * 
     * @param time: Giờ khám (format: HH:mm)
     */
    public void enterAppointmentTime(String time) {
        logger.info("Nhập giờ khám: " + time);
        elementUtils.sendKeys(TIME_INPUT, time);
    }
    
    /**
     * Nhập mã BHYT
     * 
     * @param insuranceCode: Mã BHYT
     */
    public void enterInsuranceCode(String insuranceCode) {
        logger.info("Nhập mã BHYT: " + insuranceCode);
        elementUtils.sendKeys(INSURANCE_CODE_INPUT, insuranceCode);
    }
    
    /**
     * Nhập số điện thoại
     * 
     * @param phone: Số điện thoại
     */
    public void enterPhone(String phone) {
        logger.info("Nhập số điện thoại: " + phone);
        elementUtils.sendKeys(PHONE_INPUT, phone);
    }
    
    /**
     * Click nút Lưu
     * 
     * Mục đích: Lưu lịch khám
     */
    public void clickSaveButton() {
        logger.info("Click nút Lưu");
        elementUtils.click(SAVE_BUTTON);
        waitUtils.waitFor(2000);
    }
    
    /**
     * Tạo lịch khám mới
     * 
     * TC-W-F-07-TC01: Tạo lịch khám mới với đầy đủ thông tin
     * 
     * @param speciality: Chuyên khoa
     * @param doctor: Bác sĩ
     * @param date: Ngày
     * @param time: Giờ
     * @param phone: Số điện thoại
     */
    public void createNewSchedule(String speciality, String doctor, String date, String time, String phone) {
        logger.info("Bắt đầu tạo lịch khám mới");
        selectMethod2();
        selectSpeciality(speciality);
        selectDoctor(doctor);
        enterAppointmentDate(date);
        enterAppointmentTime(time);
        enterPhone(phone);
        clickSaveButton();
        logger.info("Hoàn thành tạo lịch khám");
    }
    
    /**
     * Lấy success message
     * 
     * @return Success message text
     */
    public String getSuccessMessage() {
        try {
            waitUtils.waitForElementVisible(SUCCESS_MESSAGE);
            String message = elementUtils.getText(SUCCESS_MESSAGE);
            logger.info("Success message: " + message);
            return message;
        } catch (Exception e) {
            logger.warn("Không tìm thấy success message");
            return "";
        }
    }
    
    /**
     * Lấy error message
     * 
     * @return Error message text
     */
    public String getErrorMessage() {
        try {
            waitUtils.waitForElementVisible(ERROR_MESSAGE);
            String message = elementUtils.getText(ERROR_MESSAGE);
            logger.info("Error message: " + message);
            return message;
        } catch (Exception e) {
            logger.warn("Không tìm thấy error message");
            return "";
        }
    }
    
    /**
     * Kiểm tra validation error
     * 
     * TC-W-F-07-TC02: Tạo lịch khám khi bỏ trống một trường bắt buộc
     * 
     * @return Boolean - true nếu có validation error
     */
    public boolean hasValidationError() {
        return elementUtils.isElementVisible(VALIDATION_ERROR);
    }
    
    /**
     * Kiểm tra lịch khám đã được tạo thành công
     * 
     * @return Boolean
     */
    public boolean isScheduleCreated() {
        try {
            waitUtils.waitForElementVisible(SCHEDULE_LIST_TABLE);
            return true;
        } catch (Exception e) {
            logger.warn("Lịch khám chưa được tạo");
            return false;
        }
    }
}
