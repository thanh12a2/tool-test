package com.umbrella.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * W-F-15: Room Management (Phòng khám)
 * 
 * Purpose: Handle room management
 * URL: http://localhost/umbrella-corporation/rooms
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class RoomManagementPage extends BasePage {
    
    // Locators
    private static final By ROOMS_TABLE = By.xpath("//table[@class='table']");
    private static final By TABLE_ROWS = By.xpath("//table[@class='table']//tbody//tr");
    private static final By ADD_BUTTON = By.xpath("//button[contains(text(), 'Thêm')]");
    private static final By EDIT_BUTTON = By.xpath("//button[contains(@title, 'Sửa')]");
    private static final By DELETE_BUTTON = By.xpath("//button[contains(@title, 'Xóa')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Tìm kiếm']");
    private static final By ROOM_NAME_INPUT = By.name("name");
    private static final By ROOM_NUMBER_INPUT = By.name("room_number");
    private static final By CAPACITY_INPUT = By.name("capacity");
    private static final By SAVE_BUTTON = By.xpath("//button[contains(text(), 'Lưu')]");
    
    public RoomManagementPage(WebDriver driver) {
        super(driver);
        logger.info("Khởi tạo RoomManagementPage");
    }
    
    /**
     * W-F-15-IS_TABLE_VISIBLE
     */
    public boolean isRoomsTableVisible() {
        try {
            return elementUtils.isElementVisible(ROOMS_TABLE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * W-F-15-GET_ROW_COUNT
     */
    public int getRoomRowCount() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * W-F-15-CLICK_ADD
     */
    public void clickAddRoom() {
        logger.info("Thêm phòng");
        elementUtils.click(ADD_BUTTON);
    }
    
    /**
     * W-F-15-ENTER_ROOM_NAME
     */
    public void enterRoomName(String name) {
        logger.info("Nhập tên phòng: " + name);
        elementUtils.sendKeys(ROOM_NAME_INPUT, name);
    }
    
    /**
     * W-F-15-ENTER_ROOM_NUMBER
     */
    public void enterRoomNumber(String number) {
        logger.info("Nhập số phòng: " + number);
        elementUtils.sendKeys(ROOM_NUMBER_INPUT, number);
    }
    
    /**
     * W-F-15-ENTER_CAPACITY
     */
    public void enterCapacity(String capacity) {
        logger.info("Nhập sức chứa: " + capacity);
        elementUtils.sendKeys(CAPACITY_INPUT, capacity);
    }
    
    /**
     * W-F-15-SAVE
     */
    public void saveRoom() {
        logger.info("Lưu phòng");
        elementUtils.click(SAVE_BUTTON);
    }
    
    /**
     * W-F-15-SEARCH
     */
    public void search(String term) {
        logger.info("Tìm kiếm: " + term);
        elementUtils.sendKeys(SEARCH_INPUT, term);
    }
}
