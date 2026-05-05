package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * W-F-04: Personal Info (Thông tin cá nhân)
 * 
 * Purpose: Handle personal information management
 * URL: http://localhost/umbrella-corporation/personal
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class PersonalInfoPage extends BasePage {
    
    // Locators
    private static final By FULL_NAME_INPUT = By.name("name");
    private static final By EMAIL_INPUT = By.name("email");
    private static final By PHONE_INPUT = By.name("phone");
    private static final By ADDRESS_INPUT = By.name("address");
    private static final By DOB_INPUT = By.name("dob");
    private static final By GENDER_SELECT = By.name("gender");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(text(), 'Sửa')]");
    private static final By SAVE_BUTTON = By.xpath("//button[contains(text(), 'Lưu')]");
    private static final By CANCEL_BUTTON = By.xpath("//button[contains(text(), 'Hủy')]");
    private static final By SUCCESS_MESSAGE = By.xpath("//div[@class='alert alert-success']");
    private static final By ERROR_MESSAGE = By.xpath("//div[@class='alert alert-danger']");
    private static final By PROFILE_SECTION = By.xpath("//div[@class='profile-section']");
    
    public PersonalInfoPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo PersonalInfoPage");
    }
    
    /**
     * W-F-04-IS_PAGE_LOADED
     * Purpose: Verify personal info page loaded
     */
    public boolean isPersonalInfoPageLoaded() {
        try {
            return elementUtils.isElementVisible(PROFILE_SECTION);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-04-CLICK_EDIT
     * Purpose: Click edit button
     */
    public void clickEdit() {
        logger.info("Nhấn nút Sửa");
        elementUtils.click(EDIT_BUTTON);
    }
    
    /**
     * W-F-04-ENTER_FULL_NAME
     * Purpose: Update full name
     */
    public void enterFullName(String name) {
        logger.info("Cập nhật tên: " + name);
        elementUtils.sendKeys(FULL_NAME_INPUT, name);
    }
    
    /**
     * W-F-04-ENTER_PHONE
     * Purpose: Update phone number
     */
    public void enterPhone(String phone) {
        logger.info("Cập nhật số điện thoại: " + phone);
        elementUtils.sendKeys(PHONE_INPUT, phone);
    }
    
    /**
     * W-F-04-ENTER_ADDRESS
     * Purpose: Update address
     */
    public void enterAddress(String address) {
        logger.info("Cập nhật địa chỉ: " + address);
        elementUtils.sendKeys(ADDRESS_INPUT, address);
    }
    
    /**
     * W-F-04-SELECT_GENDER
     * Purpose: Select gender
     */
    public void selectGender(String gender) {
        logger.info("Chọn giới tính: " + gender);
        elementUtils.selectByVisibleText(GENDER_SELECT, gender);
    }
    
    /**
     * W-F-04-SAVE_CHANGES
     * Purpose: Save changes
     */
    public void saveChanges() {
        logger.info("Lưu thay đổi");
        elementUtils.click(SAVE_BUTTON);
    }
    
    /**
     * W-F-04-CANCEL_EDIT
     * Purpose: Cancel editing
     */
    public void cancelEdit() {
        logger.info("Hủy chỉnh sửa");
        elementUtils.click(CANCEL_BUTTON);
    }
    
    /**
     * W-F-04-IS_SUCCESS
     * Purpose: Check save success
     */
    public boolean isSuccessMessageVisible() {
        try {
            return elementUtils.isElementVisible(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-04-GET_DISPLAYED_NAME
     * Purpose: Get displayed full name
     */
    public String getDisplayedName() {
        try {
            return elementUtils.getAttribute(FULL_NAME_INPUT, "value");
        } catch (Exception e) {
            return "";
        }
    }
}
