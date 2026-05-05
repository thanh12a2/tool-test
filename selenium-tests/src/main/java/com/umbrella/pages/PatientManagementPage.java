package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Patient Management Page - Quản Lý Bệnh Nhân
 * 
 * ID: W-F-14
 * Mục đích: Xử lý các thao tác liên quan đến quản lý bệnh nhân
 * Chức năng:
 * - Tạo bệnh nhân mới
 * - Cập nhật thông tin bệnh nhân
 * - Xóa bệnh nhân
 * - Tìm kiếm bệnh nhân
 * - Sắp xếp danh sách
 * - Xem chi tiết bệnh nhân
 */
public class PatientManagementPage extends BasePage {
    
    // Locators
    private static final By CREATE_PATIENT_BUTTON = By.id("btn-create-patient");
    private static final By PATIENT_TABLE = By.id("patient-table");
    private static final By PATIENT_ROWS = By.xpath("//table[@id='patient-table']//tbody//tr");
    
    private static final By NAME_INPUT = By.id("patient-name");
    private static final By EMAIL_INPUT = By.id("patient-email");
    private static final By PHONE_INPUT = By.id("patient-phone");
    private static final By DATE_OF_BIRTH = By.id("patient-dob");
    private static final By ADDRESS_INPUT = By.id("patient-address");
    private static final By GENDER_SELECT = By.id("patient-gender");
    
    private static final By SAVE_BUTTON = By.id("btn-save");
    private static final By CANCEL_BUTTON = By.id("btn-cancel");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(text(), 'Xóa')]");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(text(), 'Sửa')]");
    private static final By DETAIL_BUTTON = By.xpath("//button[contains(text(), 'Chi tiết')]");
    
    private static final By SEARCH_INPUT = By.id("search-patient");
    private static final By SEARCH_BUTTON = By.id("btn-search");
    private static final By SORT_DROPDOWN = By.id("sort-by");
    private static final By PAGINATION_NEXT = By.id("pagination-next");
    
    private static final By SUCCESS_MESSAGE = By.className("alert-success");
    private static final By ERROR_MESSAGE = By.className("alert-danger");
    private static final By VALIDATION_ERROR = By.className("field-error");
    
    /**
     * Constructor
     */
    public PatientManagementPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Click nút tạo bệnh nhân
     * TC-W-F-14-TC02
     */
    public void clickCreatePatientButton() {
        logger.info("Click nút tạo bệnh nhân");
        elementUtils.click(CREATE_PATIENT_BUTTON);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Nhập tên bệnh nhân
     */
    public void enterPatientName(String name) {
        logger.info("Nhập tên bệnh nhân: " + name);
        elementUtils.sendKeys(NAME_INPUT, name);
    }
    
    /**
     * Nhập email
     */
    public void enterPatientEmail(String email) {
        logger.info("Nhập email: " + email);
        elementUtils.sendKeys(EMAIL_INPUT, email);
    }
    
    /**
     * Nhập số điện thoại
     */
    public void enterPatientPhone(String phone) {
        logger.info("Nhập số điện thoại: " + phone);
        elementUtils.sendKeys(PHONE_INPUT, phone);
    }
    
    /**
     * Nhập ngày sinh
     */
    public void enterDateOfBirth(String dob) {
        logger.info("Nhập ngày sinh: " + dob);
        elementUtils.sendKeys(DATE_OF_BIRTH, dob);
    }
    
    /**
     * Nhập địa chỉ
     */
    public void enterAddress(String address) {
        logger.info("Nhập địa chỉ: " + address);
        elementUtils.sendKeys(ADDRESS_INPUT, address);
    }
    
    /**
     * Chọn giới tính
     */
    public void selectGender(String gender) {
        logger.info("Chọn giới tính: " + gender);
        elementUtils.selectByVisibleText(GENDER_SELECT, gender);
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
     * Tạo bệnh nhân mới
     * TC-W-F-14-TC02
     */
    public void createNewPatient(String name, String email, String phone, 
                                 String dob, String address, String gender) {
        logger.info("Tạo bệnh nhân mới: " + name);
        clickCreatePatientButton();
        enterPatientName(name);
        enterPatientEmail(email);
        enterPatientPhone(phone);
        enterDateOfBirth(dob);
        enterAddress(address);
        selectGender(gender);
        clickSaveButton();
    }
    
    /**
     * Tìm kiếm bệnh nhân
     * TC-W-F-14-TC01
     */
    public void searchPatient(String keyword) {
        logger.info("Tìm kiếm bệnh nhân: " + keyword);
        elementUtils.sendKeys(SEARCH_INPUT, keyword);
        elementUtils.click(SEARCH_BUTTON);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Sắp xếp danh sách
     * TC-W-F-14-TC05
     */
    public void sortBy(String sortOption) {
        logger.info("Sắp xếp theo: " + sortOption);
        elementUtils.selectByVisibleText(SORT_DROPDOWN, sortOption);
        waitUtils.waitFor(1000);
    }
    
    /**
     * Chuyển trang
     * TC-W-F-14-TC06
     */
    public void goToNextPage() {
        logger.info("Chuyển trang tiếp theo");
        if (elementUtils.isElementVisible(PAGINATION_NEXT) && elementUtils.isElementEnabled(PAGINATION_NEXT)) {
            elementUtils.click(PAGINATION_NEXT);
            waitUtils.waitFor(1000);
        }
    }
    
    /**
     * Sửa bệnh nhân
     * TC-W-F-14-TC03
     */
    public void editPatientName(String newName) {
        logger.info("Sửa tên bệnh nhân: " + newName);
        elementUtils.click(EDIT_BUTTON);
        waitUtils.waitFor(500);
        elementUtils.clearField(NAME_INPUT);
        elementUtils.sendKeys(NAME_INPUT, newName);
        clickSaveButton();
    }
    
    /**
     * Xem chi tiết bệnh nhân
     * TC-W-F-14-TC07
     */
    public void viewPatientDetail() {
        logger.info("Xem chi tiết bệnh nhân");
        elementUtils.click(DETAIL_BUTTON);
        waitUtils.waitFor(1000);
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
     * TC-W-F-14-TC04
     */
    public boolean hasValidationError() {
        return elementUtils.isElementVisible(VALIDATION_ERROR);
    }
    
    /**
     * Kiểm tra bệnh nhân đã tồn tại
     */
    public boolean isPatientExists() {
        return elementUtils.isElementVisible(PATIENT_TABLE);
    }
}
