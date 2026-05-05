package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * W-F-02: Forgot Password Page
 * 
 * Purpose: Handle password recovery workflow
 * URL: http://localhost/umbrella-corporation/recovery
 * 
 * Test Scenarios:
 * - W-F-02-TC01: Request recovery email with valid email
 * - W-F-02-TC02: Request recovery with non-existent email
 * - W-F-02-TC03: Validation when email field empty
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class ForgotPasswordPage extends BasePage {
    
    // Locators
    private static final By EMAIL_INPUT = By.name("email");
    private static final By SEND_BUTTON = By.xpath("//button[contains(text(), 'Gửi')]");
    private static final By SUCCESS_MESSAGE = By.xpath("//div[@class='alert alert-success']");
    private static final By ERROR_MESSAGE = By.xpath("//div[@class='alert alert-danger']");
    private static final By BACK_TO_LOGIN_LINK = By.xpath("//a[contains(text(), 'Quay lại')]");
    
    public ForgotPasswordPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo ForgotPasswordPage");
    }
    
    /**
     * W-F-02-ENTER_EMAIL
     * Purpose: Enter email to recover password
     */
    public void enterEmail(String email) {
        logger.info("Nhập email recovery: " + email);
        elementUtils.sendKeys(EMAIL_INPUT, email);
    }
    
    /**
     * W-F-02-CLICK_SEND
     * Purpose: Submit recovery request
     */
    public void clickSendButton() {
        logger.info("Nhấn nút Gửi");
        elementUtils.click(SEND_BUTTON);
    }
    
    /**
     * W-F-02-SEND_RECOVERY
     * Purpose: Complete recovery workflow
     */
    public void sendRecoveryEmail(String email) {
        logger.info("Bắt đầu quy trình recovery");
        enterEmail(email);
        clickSendButton();
    }
    
    /**
     * W-F-02-IS_SUCCESS_MESSAGE
     * Purpose: Verify recovery email sent successfully
     */
    public boolean isSuccessMessageVisible() {
        try {
            return elementUtils.isElementVisible(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-02-IS_ERROR_MESSAGE
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
     * W-F-02-GET_SUCCESS_MESSAGE
     */
    public String getSuccessMessage() {
        return elementUtils.getText(SUCCESS_MESSAGE);
    }
    
    /**
     * W-F-02-BACK_TO_LOGIN
     * Purpose: Return to login page
     */
    public void backToLogin() {
        logger.info("Quay lại trang login");
        elementUtils.click(BACK_TO_LOGIN_LINK);
    }
}
