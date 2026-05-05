package com.umbrella.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * W-F-06: Sắp Xếp Thứ Tự Lịch Khám (Reorder Appointments)
 * 
 * Purpose: Handle appointment reordering
 * URL: http://localhost/umbrella-corporation/schedules
 * 
 * Locators from Playwright: page.getByRole('link', { name: ' Sắp xếp thứ tự' })
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class AppointmentReorderPage extends BasePage {
    
    // Locators
    private static final By REORDER_LINK = By.xpath("//a[contains(text(), 'Sắp xếp thứ tự')]");
    private static final By APPOINTMENTS_TABLE = By.xpath("//table[@class='table']");
    private static final By SEARCH_BUTTON = By.xpath("//button[contains(text(), 'Tìm kiếm')]");
    private static final By REFRESH_BUTTON = By.xpath("//button[contains(text(), 'Làm mới')]");
    private static final By FILTER_BUTTON = By.xpath("//button[contains(text(), 'Lọc')]");
    private static final By DATE_INPUT = By.name("date");
    private static final By SORT_DROPDOWN = By.xpath("//select[@name='sort']");
    
    public AppointmentReorderPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo AppointmentReorderPage");
    }
    
    /**
     * W-F-06-NAVIGATE_REORDER
     * Purpose: Navigate to reorder page
     */
    public void navigateToReorder() {
        logger.info("Chuyển đến trang sắp xếp thứ tự");
        elementUtils.click(REORDER_LINK);
    }
    
    /**
     * W-F-06-SEARCH_APPOINTMENTS
     * Purpose: Search appointments
     */
    public void searchAppointments(String searchTerm) {
        logger.info("Tìm kiếm: " + searchTerm);
        // Implementation based on actual search field
    }
    
    /**
     * W-F-06-CLICK_SEARCH
     */
    public void clickSearch() {
        logger.info("Nhấn nút Tìm kiếm");
        elementUtils.click(SEARCH_BUTTON);
    }
    
    /**
     * W-F-06-REFRESH
     */
    public void refresh() {
        logger.info("Làm mới danh sách");
        elementUtils.click(REFRESH_BUTTON);
    }
    
    /**
     * W-F-06-FILTER
     */
    public void clickFilter() {
        logger.info("Nhấn nút Lọc");
        elementUtils.click(FILTER_BUTTON);
    }
    
    /**
     * W-F-06-SELECT_DATE
     */
    public void selectDate(String date) {
        logger.info("Chọn ngày: " + date);
        elementUtils.sendKeys(DATE_INPUT, date);
    }
    
    /**
     * W-F-06-IS_TABLE_VISIBLE
     */
    public boolean isAppointmentsTableVisible() {
        try {
            return elementUtils.isElementVisible(APPOINTMENTS_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
}
