package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Doctor Management Page - Quản Lý Bác Sĩ
 * 
 * ID: W-F-12
 * Mục đích: Xử lý các thao tác liên quan đến quản lý bác sĩ
 * Chức năng:
 * - Tạo tài khoản bác sĩ
 * - Cập nhật thông tin bác sĩ
 * - Xóa bác sĩ
 * - Tìm kiếm bác sĩ
 * - Sắp xếp danh sách
 */
public class DoctorManagementPage extends BasePage {
    
    // Locators
    private static final By CREATE_DOCTOR_BUTTON = By.id("btn-create-doctor");
    private static final By DOCTOR_TABLE = By.id("doctor-table");
    private static final By DOCTOR_ROWS = By.xpath("//table[@id='doctor-table']//tbody//tr");
    
    private static final By NAME_INPUT = By.id("doctor-name");
    private static final By EMAIL_INPUT = By.id("doctor-email");
    private static final By PHONE_INPUT = By.id("doctor-phone");
    private static final By SPECIALITY_DROPDOWN = By.id("doctor-speciality");
    private static final By ROOM_INPUT = By.id("doctor-room");
    private static final By PRICE_INPUT = By.id("doctor-price");
    private static final By STATUS_SELECT = By.id("doctor-status");
    private static final By ROLE_SELECT = By.id("doctor-role");
    
    private static final By SAVE_BUTTON = By.id("btn-save");
    private static final By CANCEL_BUTTON = By.id("btn-cancel");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(text(), 'Xóa')]");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(text(), 'Sửa')]");
    private static final By DETAIL_BUTTON = By.xpath("//button[contains(text(), 'Chi tiết')]");
    
    private static final By SEARCH_INPUT = By.id("search-doctor");
    private static final By SEARCH_BUTTON = By.id("btn-search");
    private static final By SORT_DROPDOWN = By.id("sort-by");
    
    private static final By SUCCESS_MESSAGE = By.className("alert-success");
    private static final By ERROR_MESSAGE = By.className("alert-danger");
    private static final By VALIDATION_ERROR = By.className("field-error");
    private static final By PAGINATION_NEXT = By.id("pagination-next");
    
    /**
     * Constructor
     */
    public DoctorManagementPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Click nút tạo bác sĩ
     * TC-W-F-12-TC01
     */
    public void clickCreateDoctorButton() {
        logger.info("Click nút tạo bác sĩ");
        elementUtils.click(CREATE_DOCTOR_BUTTON);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Nhập tên bác sĩ
     */
    public void enterDoctorName(String name) {
        logger.info("Nhập tên bác sĩ: " + name);
        elementUtils.sendKeys(NAME_INPUT, name);
    }
    
    /**
     * Nhập email bác sĩ
     */
    public void enterDoctorEmail(String email) {
        logger.info("Nhập email bác sĩ: " + email);
        elementUtils.sendKeys(EMAIL_INPUT, email);
    }
    
    /**
     * Nhập số điện thoại
     */
    public void enterDoctorPhone(String phone) {
        logger.info("Nhập số điện thoại: " + phone);
        elementUtils.sendKeys(PHONE_INPUT, phone);
    }
    
    /**
     * Chọn chuyên khoa
     */
    public void selectSpeciality(String speciality) {
        logger.info("Chọn chuyên khoa: " + speciality);
        elementUtils.selectByVisibleText(SPECIALITY_DROPDOWN, speciality);
    }
    
    /**
     * Nhập phòng khám
     */
    public void enterRoom(String room) {
        logger.info("Nhập phòng khám: " + room);
        elementUtils.sendKeys(ROOM_INPUT, room);
    }
    
    /**
     * Nhập giá khám
     */
    public void enterPrice(String price) {
        logger.info("Nhập giá khám: " + price);
        elementUtils.sendKeys(PRICE_INPUT, price);
    }
    
    /**
     * Chọn trạng thái
     */
    public void selectStatus(String status) {
        logger.info("Chọn trạng thái: " + status);
        elementUtils.selectByVisibleText(STATUS_SELECT, status);
    }
    
    /**
     * Click nút lưu
     */
    public void clickSaveButton() {
        logger.info("Click nút lưu");
        elementUtils.click(SAVE_BUTTON);
        waitUtils.waitFor(2000);
    }
    
    /**
     * Tạo bác sĩ mới
     * TC-W-F-12-TC01
     */
    public void createNewDoctor(String name, String email, String phone, 
                               String speciality, String room, String price) {
        logger.info("Tạo bác sĩ mới: " + name);
        clickCreateDoctorButton();
        enterDoctorName(name);
        enterDoctorEmail(email);
        enterDoctorPhone(phone);
        selectSpeciality(speciality);
        enterRoom(room);
        enterPrice(price);
        selectStatus("Hoạt động");
        clickSaveButton();
    }
    
    /**
     * Tìm kiếm bác sĩ
     * TC-W-F-12-TC05
     */
    public void searchDoctor(String keyword) {
        logger.info("Tìm kiếm bác sĩ: " + keyword);
        elementUtils.sendKeys(SEARCH_INPUT, keyword);
        elementUtils.click(SEARCH_BUTTON);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Sắp xếp danh sách
     * TC-W-F-12-TC06
     */
    public void sortBy(String sortOption) {
        logger.info("Sắp xếp theo: " + sortOption);
        elementUtils.selectByVisibleText(SORT_DROPDOWN, sortOption);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Chuyển trang
     * TC-W-F-12-TC07
     */
    public void goToNextPage() {
        logger.info("Chuyển trang tiếp theo");
        if (elementUtils.isElementVisible(PAGINATION_NEXT) && elementUtils.isElementEnabled(PAGINATION_NEXT)) {
            elementUtils.click(PAGINATION_NEXT);
            waitUtils.waitFor(1000);
        }
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
     * Lấy error message
     */
    public String getErrorMessage() {
        try {
            waitUtils.waitForElementVisible(ERROR_MESSAGE);
            return elementUtils.getText(ERROR_MESSAGE);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Kiểm tra validation error
     */
    public boolean hasValidationError() {
        return elementUtils.isElementVisible(VALIDATION_ERROR);
    }
    
    /**
     * Kiểm tra bác sĩ đã tồn tại
     */
    public boolean isDoctorExists() {
        return elementUtils.isElementVisible(DOCTOR_TABLE);
    }
}
