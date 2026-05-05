package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * W-F-03: Register (Đăng ký)
 * 
 * Purpose: Handle user registration workflow
 * URL: http://localhost/umbrella-corporation/register
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class RegisterPage extends BasePage {
    
    // Locators
    private static final By FULL_NAME_INPUT = By.name("name");
    private static final By EMAIL_INPUT = By.name("email");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By CONFIRM_PASSWORD = By.name("confirm_password");
    private static final By PHONE_INPUT = By.name("phone");
    private static final By TERMS_CHECKBOX = By.name("terms");
    private static final By REGISTER_BUTTON = By.xpath("//button[contains(text(), 'Đăng ký')]");
    private static final By ERROR_MESSAGE = By.xpath("//div[@class='alert alert-danger']");
    private static final By SUCCESS_MESSAGE = By.xpath("//div[@class='alert alert-success']");
    private static final By LOGIN_LINK = By.xpath("//a[contains(text(), 'Đăng nhập')]");
    
    public RegisterPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo RegisterPage");
    }
    
    /**
     * W-F-03-ENTER_FULL_NAME
     * Purpose: Enter full name
     */
    public void enterFullName(String name) {
        logger.info("Nhập tên đầy đủ: " + name);
        elementUtils.sendKeys(FULL_NAME_INPUT, name);
    }
    
    /**
     * W-F-03-ENTER_EMAIL
     * Purpose: Enter email address
     */
    public void enterEmail(String email) {
        logger.info("Nhập email: " + email);
        elementUtils.sendKeys(EMAIL_INPUT, email);
    }
    
    /**
     * W-F-03-ENTER_PASSWORD
     * Purpose: Enter password
     */
    public void enterPassword(String password) {
        logger.info("Nhập mật khẩu");
        elementUtils.sendKeys(PASSWORD_INPUT, password);
    }
    
    /**
     * W-F-03-ENTER_CONFIRM_PASSWORD
     * Purpose: Enter confirm password
     */
    public void enterConfirmPassword(String password) {
        logger.info("Nhập xác nhận mật khẩu");
        elementUtils.sendKeys(CONFIRM_PASSWORD, password);
    }
    
    /**
     * W-F-03-ENTER_PHONE
     * Purpose: Enter phone number
     */
    public void enterPhone(String phone) {
        logger.info("Nhập số điện thoại: " + phone);
        elementUtils.sendKeys(PHONE_INPUT, phone);
    }
    
    /**
     * W-F-03-ACCEPT_TERMS
     * Purpose: Check terms checkbox
     */
    public void acceptTerms() {
        logger.info("Chấp nhận điều khoản");
        if (!elementUtils.isElementSelected(TERMS_CHECKBOX)) {
            elementUtils.click(TERMS_CHECKBOX);
        }
    }
    
    /**
     * W-F-03-CLICK_REGISTER
     * Purpose: Click register button
     */
    public void clickRegister() {
        logger.info("Nhấn nút Đăng ký");
        elementUtils.click(REGISTER_BUTTON);
    }
    
    /**
     * W-F-03-REGISTER_WITH_DATA
     * Purpose: Complete registration process
     */
    public void registerWithData(String name, String email, String password, String phone) {
        logger.info("Bắt đầu quy trình đăng ký");
        enterFullName(name);
        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(password);
        enterPhone(phone);
        acceptTerms();
        clickRegister();
    }
    
    /**
     * W-F-03-IS_SUCCESS
     * Purpose: Verify registration success
     */
    public boolean isSuccessMessageVisible() {
        try {
            return elementUtils.isElementVisible(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-03-IS_ERROR
     * Purpose: Check for error messages
     */
    public boolean isErrorMessageVisible() {
        try {
            return elementUtils.isElementVisible(ERROR_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-03-BACK_TO_LOGIN
     * Purpose: Click login link
     */
    public void backToLogin() {
        logger.info("Quay lại trang đăng nhập");
        elementUtils.click(LOGIN_LINK);
    }
}
