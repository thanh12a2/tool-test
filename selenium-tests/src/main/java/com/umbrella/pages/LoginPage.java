package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * W-F-01: Login Page - Đăng nhập vào hệ thống
 * 
 * ID: LOGIN
 * Page URL: http://localhost/umbrella-corporation/login
 * 
 * Purpose: Handle all login-related interactions and verifications
 * 
 * Page Elements:
 * - Email input field
 * - Password input field
 * - Login button
 * - Forgot password link
 * - Register button
 * - Error message display
 * 
 * Test Scenarios Covered:
 * - W-F-01-TC01: Đăng nhập với email và mật khẩu hợp lệ
 * - W-F-01-TC02: Đăng nhập với mật khẩu sai
 * - W-F-01-TC03: Đăng nhập với email không tồn tại
 * - W-F-01-TC04: Đăng nhập khi bỏ trống email
 * - W-F-01-TC05: Đăng nhập khi bỏ trống mật khẩu
 * - W-F-01-TC06: Đăng nhập với ký tự đặc biệt trong email
 * - W-F-01-TC07: Kiểm tra chuyển hướng đúng vai trò
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class LoginPage extends BasePage {
    
    // ===== LOCATORS - Based on login.fragment.php HTML =====
    
    // Email input: <input type="email" name="email" ... required autofocus>
    private static final By EMAIL_INPUT = By.name("email");
    
    // Password input: <input type="password" name="password" ...>
    private static final By PASSWORD_INPUT = By.name("password");
    
    // Login button: <button class="btn btn-primary" type="submit">
    private static final By LOGIN_BUTTON = By.xpath("//button[@class='btn btn-primary'][@type='submit']");
    
    // Error message: <p class="bg-danger" style="padding: 10px">...
    private static final By ERROR_MESSAGE = By.xpath("//p[@class='bg-danger']");
    
    // Forgot password link: <a href=".../recovery" ...>
    private static final By FORGOT_PASSWORD_LINK = By.xpath("//a[contains(@href, '/recovery')]");
    
    // Register button: <button onclick="location.href='.../register'" ...>
    private static final By REGISTER_BUTTON = By.xpath("//button[contains(@onclick, '/register')]");
    
    // Logout button (to verify login success)
    private static final By LOGOUT_BUTTON = By.xpath("//a[contains(text(), 'Logout')] | //button[contains(text(), 'Logout')]");
    
    /**
     * Constructor - Initialize LoginPage with WebDriver
     * 
     * Function ID: LoginPage-INIT
     * Purpose: Set up page object with WebDriver instance
     * 
     * @param driver: WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo LoginPage");
    }
    
    // ===== CORE LOGIN INTERACTIONS =====
    
    /**
     * Enter Email - Type email address into email field
     * 
     * Function ID: W-F-01-ENTER_EMAIL
     * Purpose: Input email address for login
     * 
     * @param email: Email address to enter
     */
    public void enterEmail(String email) {
        logger.info("Nhập email: " + email);
        elementUtils.sendKeys(EMAIL_INPUT, email);
    }
    
    /**
     * Clear Email Field - Clear any existing email text
     * 
     * Function ID: W-F-01-CLEAR_EMAIL
     * Purpose: Clear email field before entering new value
     */
    public void clearEmailField() {
        logger.info("Xóa nội dung ô email");
        driver.findElement(EMAIL_INPUT).clear();
    }
    
    /**
     * Enter Password - Type password into password field
     * 
     * Function ID: W-F-01-ENTER_PASSWORD
     * Purpose: Input password for login
     * 
     * @param password: Password to enter
     */
    public void enterPassword(String password) {
        logger.info("Nhập password");
        elementUtils.sendKeys(PASSWORD_INPUT, password);
    }
    
    /**
     * Clear Password Field - Clear any existing password text
     * 
     * Function ID: W-F-01-CLEAR_PASSWORD
     * Purpose: Clear password field before entering new value
     */
    public void clearPasswordField() {
        logger.info("Xóa nội dung ô password");
        driver.findElement(PASSWORD_INPUT).clear();
    }
    
    /**
     * Click Login Button - Submit login form
     * 
     * Function ID: W-F-01-CLICK_LOGIN
     * Purpose: Click the login button to submit credentials
     */
    public void clickLoginButton() {
        logger.info("Nhấn nút Đăng nhập");
        elementUtils.click(LOGIN_BUTTON);
    }
    
    /**
     * Complete Login Flow - Enter credentials and submit
     * 
     * Function ID: W-F-01-LOGIN_FLOW
     * Purpose: Execute complete login workflow
     * 
     * @param email: Email address
     * @param password: Password
     */
    public void login(String email, String password) {
        logger.info("Bắt đầu quy trình login với email: " + email);
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
        logger.info("Hoàn thành quy trình login");
    }
    
    // ===== ERROR & VALIDATION VERIFICATION =====
    
    /**
     * Get Error Message Text - Retrieve error message content
     * 
     * Function ID: W-F-01-GET_ERROR_MSG
     * Purpose: Get the text of error message displayed
     * 
     * @return Error message text or empty string if not found
     */
    public String getErrorMessage() {
        try {
            String message = elementUtils.getText(ERROR_MESSAGE);
            logger.info("Thông báo lỗi: " + message);
            return message;
        } catch (Exception e) {
            logger.warn("Không tìm thấy thông báo lỗi");
            return "";
        }
    }
    
    /**
     * Is Error Message Visible - Check if error message is displayed
     * 
     * Function ID: W-F-01-ERROR_VISIBLE
     * Purpose: Verify error message is visible to user
     * 
     * @return True if error message is visible, False otherwise
     */
    public boolean isErrorMessageVisible() {
        try {
            return elementUtils.isElementVisible(ERROR_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get Email Input Value - Retrieve current email field value
     * 
     * Function ID: W-F-01-GET_EMAIL_VALUE
     * Purpose: Get the current value in email field
     * 
     * @return Email value
     */
    public String getEmailValue() {
        return driver.findElement(EMAIL_INPUT).getAttribute("value");
    }
    
    /**
     * Get Password Input Value - Retrieve current password field value
     * 
     * Function ID: W-F-01-GET_PASSWORD_VALUE
     * Purpose: Get the current value in password field
     * 
     * @return Password value
     */
    public String getPasswordValue() {
        return driver.findElement(PASSWORD_INPUT).getAttribute("value");
    }
    
    // ===== LOGIN SUCCESS VERIFICATION =====
    
    /**
     * Is Logged In - Verify successful login
     * 
     * Function ID: W-F-01-IS_LOGGED_IN
     * Purpose: Check if user is currently logged in
     * 
     * @return True if logged in (logout button visible), False otherwise
     */
    public boolean isLoggedIn() {
        try {
            waitUtils.waitForElementVisible(LOGOUT_BUTTON);
            logger.info("Người dùng đã đăng nhập thành công");
            return true;
        } catch (Exception e) {
            logger.warn("Người dùng chưa đăng nhập");
            return false;
        }
    }
    
    /**
     * Is Login Page - Verify current page is login page
     * 
     * Function ID: W-F-01-IS_LOGIN_PAGE
     * Purpose: Check if we're still on login page (used after failed login)
     * 
     * @return True if both email and password fields are visible
     */
    public boolean isLoginPage() {
        try {
            return elementUtils.isElementVisible(EMAIL_INPUT) && 
                   elementUtils.isElementVisible(PASSWORD_INPUT);
        } catch (Exception e) {
            return false;
        }
    }
    
    // ===== NAVIGATION LINKS =====
    
    /**
     * Click Forgot Password Link - Navigate to password recovery
     * 
     * Function ID: W-F-01-FORGOT_PASSWORD
     * Purpose: Click the "Forgot Password?" link
     */
    public void clickForgotPasswordLink() {
        logger.info("Nhấn link 'Quên mật khẩu?'");
        elementUtils.click(FORGOT_PASSWORD_LINK);
    }
    
    /**
     * Click Register Button - Navigate to registration page
     * 
     * Function ID: W-F-01-REGISTER
     * Purpose: Click the "Register" button to go to sign up page
     */
    public void clickRegisterButton() {
        logger.info("Nhấn nút 'Đăng ký'");
        elementUtils.click(REGISTER_BUTTON);
    }
    
    // ===== PAGE VALIDATION =====
    
    /**
     * Verify All Elements Present - Check all login page elements are displayed
     * 
     * Function ID: W-F-01-VERIFY_ELEMENTS
     * Purpose: Verify all expected elements on login page are present
     * 
     * @return True if all elements present, False otherwise
     */
    public boolean verifyAllElementsPresent() {
        logger.info("Kiểm tra tất cả UI elements");
        try {
            boolean emailVisible = elementUtils.isElementVisible(EMAIL_INPUT);
            boolean passwordVisible = elementUtils.isElementVisible(PASSWORD_INPUT);
            boolean loginBtnVisible = elementUtils.isElementVisible(LOGIN_BUTTON);
            boolean forgotVisible = elementUtils.isElementVisible(FORGOT_PASSWORD_LINK);
            boolean registerVisible = elementUtils.isElementVisible(REGISTER_BUTTON);
            
            logger.info("Email field: " + emailVisible);
            logger.info("Password field: " + passwordVisible);
            logger.info("Login button: " + loginBtnVisible);
            logger.info("Forgot password link: " + forgotVisible);
            logger.info("Register button: " + registerVisible);
            
            return emailVisible && passwordVisible && loginBtnVisible && 
                   forgotVisible && registerVisible;
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra elements: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verify Page Title - Check login page title
     * 
     * Function ID: W-F-01-PAGE_TITLE
     * Purpose: Verify correct page title
     * 
     * @return Page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
}
