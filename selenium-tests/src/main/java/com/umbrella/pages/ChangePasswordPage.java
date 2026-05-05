package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * W-F-05: Change Password (Đổi mật khẩu)
 * 
 * Purpose: Handle password change workflow
 * URL: http://localhost/umbrella-corporation/security
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class ChangePasswordPage extends BasePage {
    
    // Locators
    private static final By OLD_PASSWORD_INPUT = By.name("old_password");
    private static final By NEW_PASSWORD_INPUT = By.name("new_password");
    private static final By CONFIRM_PASSWORD_INPUT = By.name("confirm_password");
    private static final By CHANGE_BUTTON = By.xpath("//button[contains(text(), 'Đổi')]");
    private static final By SUCCESS_MESSAGE = By.xpath("//div[@class='alert alert-success']");
    private static final By ERROR_MESSAGE = By.xpath("//div[@class='alert alert-danger']");
    private static final By PASSWORD_SECTION = By.xpath("//div[@class='password-section']");
    private static final By SECURITY_TITLE = By.xpath("//h2[contains(text(), 'Bảo')]");
    
    public ChangePasswordPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo ChangePasswordPage");
    }
    
    /**
     * W-F-05-IS_PAGE_LOADED
     * Purpose: Verify security page loaded
     */
    public boolean isSecurityPageLoaded() {
        try {
            return elementUtils.isElementVisible(PASSWORD_SECTION) || 
                   elementUtils.isElementVisible(SECURITY_TITLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-05-ENTER_OLD_PASSWORD
     * Purpose: Enter current password
     */
    public void enterOldPassword(String password) {
        logger.info("Nhập mật khẩu hiện tại");
        elementUtils.sendKeys(OLD_PASSWORD_INPUT, password);
    }
    
    /**
     * W-F-05-ENTER_NEW_PASSWORD
     * Purpose: Enter new password
     */
    public void enterNewPassword(String password) {
        logger.info("Nhập mật khẩu mới");
        elementUtils.sendKeys(NEW_PASSWORD_INPUT, password);
    }
    
    /**
     * W-F-05-ENTER_CONFIRM_PASSWORD
     * Purpose: Enter confirm password
     */
    public void enterConfirmPassword(String password) {
        logger.info("Xác nhận mật khẩu mới");
        elementUtils.sendKeys(CONFIRM_PASSWORD_INPUT, password);
    }
    
    /**
     * W-F-05-CLICK_CHANGE
     * Purpose: Click change password button
     */
    public void clickChangePassword() {
        logger.info("Nhấn nút Đổi mật khẩu");
        elementUtils.click(CHANGE_BUTTON);
    }
    
    /**
     * W-F-05-CHANGE_PASSWORD
     * Purpose: Complete password change process
     */
    public void changePassword(String oldPassword, String newPassword) {
        logger.info("Bắt đầu quy trình đổi mật khẩu");
        enterOldPassword(oldPassword);
        enterNewPassword(newPassword);
        enterConfirmPassword(newPassword);
        clickChangePassword();
    }
    
    /**
     * W-F-05-IS_SUCCESS
     * Purpose: Check if password changed successfully
     */
    public boolean isSuccessMessageVisible() {
        try {
            return elementUtils.isElementVisible(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-05-IS_ERROR
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
     * W-F-05-GET_ERROR_MESSAGE
     * Purpose: Get error message text
     */
    public String getErrorMessage() {
        try {
            return elementUtils.getText(ERROR_MESSAGE);
        } catch (Exception e) {
            return "";
        }
    }
}
