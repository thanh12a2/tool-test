package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * W-F-17: Dashboard
 * 
 * Purpose: Handle dashboard navigation and verification
 * URL: http://localhost/umbrella-corporation/dashboard
 * 
 * From Playwright: Multiple navigation links available
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class DashboardPage extends BasePage {
    
    // Locators
    private static final By DASHBOARD_TITLE = By.xpath("//h1[contains(text(), 'Dashboard')]");
    private static final By STATS_SECTION = By.xpath("//div[@class='dashboard-stats']");
    private static final By APPOINTMENTS_LINK = By.xpath("//a[contains(text(), 'Lịch hẹn')]");
    private static final By DOCTORS_LINK = By.xpath("//a[contains(text(), 'Bác sĩ')]");
    private static final By PATIENTS_LINK = By.xpath("//a[contains(text(), 'Bệnh nhân')]");
    private static final By SERVICES_LINK = By.xpath("//a[contains(text(), 'Dịch vụ')]");
    private static final By ROOMS_LINK = By.xpath("//a[contains(text(), 'Phòng')]");
    private static final By SPECIALTIES_LINK = By.xpath("//a[contains(text(), 'Chuyên khoa')]");
    private static final By MENU_TOGGLE = By.xpath("//button[@class='navbar-toggler']");
    private static final By PROFILE_MENU = By.xpath("//a[@class='user-profile']");
    private static final By LOGOUT_LINK = By.xpath("//a[contains(@href, 'logout')]");
    private static final By WELCOME_MESSAGE = By.xpath("//h2[contains(text(), 'Chào')]");
    private static final By STATISTICS_CARDS = By.xpath("//div[@class='stat-card']");
    
    public DashboardPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo DashboardPage");
    }
    
    /**
     * W-F-17-IS_DASHBOARD_LOADED
     * Purpose: Verify dashboard is loaded
     */
    public boolean isDashboardLoaded() {
        try {
            return elementUtils.isElementVisible(DASHBOARD_TITLE) ||
                   elementUtils.isElementVisible(WELCOME_MESSAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-17-GET_WELCOME_MESSAGE
     * Purpose: Get welcome message text
     */
    public String getWelcomeMessage() {
        try {
            return elementUtils.getText(WELCOME_MESSAGE);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * W-F-17-NAVIGATE_TO_APPOINTMENTS
     * Purpose: Click appointments link
     */
    public void navigateToAppointments() {
        logger.info("Chuyển đến Lịch hẹn");
        elementUtils.click(APPOINTMENTS_LINK);
    }
    
    /**
     * W-F-17-NAVIGATE_TO_DOCTORS
     * Purpose: Click doctors link
     */
    public void navigateToDoctors() {
        logger.info("Chuyển đến Bác sĩ");
        elementUtils.click(DOCTORS_LINK);
    }
    
    /**
     * W-F-17-NAVIGATE_TO_PATIENTS
     * Purpose: Click patients link
     */
    public void navigateToPatients() {
        logger.info("Chuyển đến Bệnh nhân");
        elementUtils.click(PATIENTS_LINK);
    }
    
    /**
     * W-F-17-NAVIGATE_TO_SERVICES
     * Purpose: Click services link
     */
    public void navigateToServices() {
        logger.info("Chuyển đến Dịch vụ");
        elementUtils.click(SERVICES_LINK);
    }
    
    /**
     * W-F-17-NAVIGATE_TO_ROOMS
     * Purpose: Click rooms link
     */
    public void navigateToRooms() {
        logger.info("Chuyển đến Phòng");
        elementUtils.click(ROOMS_LINK);
    }
    
    /**
     * W-F-17-NAVIGATE_TO_SPECIALTIES
     * Purpose: Click specialties link
     */
    public void navigateToSpecialties() {
        logger.info("Chuyển đến Chuyên khoa");
        elementUtils.click(SPECIALTIES_LINK);
    }
    
    /**
     * W-F-17-TOGGLE_MENU
     * Purpose: Toggle navigation menu
     */
    public void toggleMenu() {
        logger.info("Chuyển đổi menu");
        elementUtils.click(MENU_TOGGLE);
    }
    
    /**
     * W-F-17-OPEN_PROFILE
     * Purpose: Click profile menu
     */
    public void openProfile() {
        logger.info("Mở menu tài khoản");
        elementUtils.click(PROFILE_MENU);
    }
    
    /**
     * W-F-17-LOGOUT
     * Purpose: Logout from system
     */
    public void logout() {
        logger.info("Đăng xuất");
        elementUtils.click(LOGOUT_LINK);
    }
    
    /**
     * W-F-17-IS_STATISTICS_VISIBLE
     * Purpose: Check statistics cards visible
     */
    public boolean isStatisticsVisible() {
        try {
            return elementUtils.isElementVisible(STATISTICS_CARDS);
        } catch (Exception e) {
            return false;
        }
    }
}
