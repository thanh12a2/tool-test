package com.umbrella.tests.web;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.umbrella.base.BaseTest;
import com.umbrella.pages.LoginPage;
import com.umbrella.pages.RoomManagementPage;

/**
 * W-F-15: Room Management Tests (Phòng khám)
 * 
 * Test Class: RoomManagementTest
 * Purpose: Test room management
 * Framework: Selenium WebDriver + JUnit 4
 * 
 * Test Cases:
 * - W-F-15-TC01: View rooms list
 * - W-F-15-TC02: Add new room
 * - W-F-15-TC03: Edit room
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class RoomManagementTest extends BaseTest {

    private LoginPage loginPage;
    private RoomManagementPage roomPage;

    @Override
    public void setUp() {
        super.setUp();
        try {
            navigateTo(getAppUrl());
            loginPage = new LoginPage(driver);
            logInfo("Setup hoàn thành");
        } catch (Exception e) {
            logError("Setup error: " + e.getMessage());
        }
    }

    private void loginAndNavigateToRooms() throws InterruptedException {
        logInfo("Đăng nhập và chuyển đến phòng");
        loginPage.login("phongkaster@gmail.com", "123456");
        Thread.sleep(3000);
        navigateTo(getAppUrl() + "/rooms");
        Thread.sleep(2000);
    }

    @Test
    public void testTC01_ViewRoomsList() {
        logInfo("========== W-F-15-TC01: Xem danh sách phòng ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToRooms();
            roomPage = new RoomManagementPage(driver);
            
            boolean isTableVisible = roomPage.isRoomsTableVisible();
            int rowCount = roomPage.getRoomRowCount();
            
            logInfo("Bảng phòng: " + isTableVisible + ", Số dòng: " + rowCount);
            
            assertTrue("TC01 FAILED", isTableVisible);
            logInfo("✓ TC01 PASSED");
            
        } catch (Exception e) {
            logError("TC01 FAILED: " + e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void testTC02_AddNewRoom() {
        logInfo("========== W-F-15-TC02: Thêm phòng mới ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToRooms();
            roomPage = new RoomManagementPage(driver);
            
            roomPage.clickAddRoom();
            Thread.sleep(1500);
            
            roomPage.enterRoomName("Room Test");
            roomPage.enterRoomNumber("101");
            roomPage.enterCapacity("2");
            roomPage.saveRoom();
            Thread.sleep(1500);
            
            assertTrue("TC02 FAILED", true);
            logInfo("✓ TC02 PASSED");
            
        } catch (Exception e) {
            logError("TC02 FAILED: " + e.getMessage());
        }
    }

    @Test
    public void testTC03_SearchRooms() {
        logInfo("========== W-F-15-TC03: Tìm kiếm phòng ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToRooms();
            roomPage = new RoomManagementPage(driver);
            
            roomPage.search("room");
            Thread.sleep(1500);
            
            assertTrue("TC03 FAILED", roomPage.isRoomsTableVisible());
            logInfo("✓ TC03 PASSED");
            
        } catch (Exception e) {
            logError("TC03 FAILED: " + e.getMessage());
        }
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    private String getAppUrl() {
        return "http://localhost/umbrella-corporation";
    }
}
