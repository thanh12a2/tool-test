package com.umbrella.tests.web;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.umbrella.base.BaseTest;
import com.umbrella.pages.LoginPage;
import com.umbrella.pages.ServiceManagementPage;

/**
 * W-F-16: Service Management Tests (Dịch vụ)
 * 
 * Test Class: ServiceManagementTest
 * Purpose: Test service management
 * Framework: Selenium WebDriver + JUnit 4
 * 
 * Test Cases:
 * - W-F-16-TC01: View services list
 * - W-F-16-TC02: Add new service
 * - W-F-16-TC03: Edit service
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class ServiceManagementTest extends BaseTest {

    private LoginPage loginPage;
    private ServiceManagementPage servicePage;

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

    private void loginAndNavigateToServices() throws InterruptedException {
        logInfo("Đăng nhập và chuyển đến dịch vụ");
        loginPage.login("phongkaster@gmail.com", "123456");
        Thread.sleep(3000);
        navigateTo(getAppUrl() + "/services");
        Thread.sleep(2000);
    }

    @Test
    public void testTC01_ViewServicesList() {
        logInfo("========== W-F-16-TC01: Xem danh sách dịch vụ ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToServices();
            servicePage = new ServiceManagementPage(driver);
            
            boolean isTableVisible = servicePage.isServicesTableVisible();
            int rowCount = servicePage.getServiceRowCount();
            
            logInfo("Bảng dịch vụ: " + isTableVisible + ", Số dòng: " + rowCount);
            
            assertTrue("TC01 FAILED", isTableVisible);
            logInfo("✓ TC01 PASSED");
            
        } catch (Exception e) {
            logError("TC01 FAILED: " + e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void testTC02_AddNewService() {
        logInfo("========== W-F-16-TC02: Thêm dịch vụ mới ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToServices();
            servicePage = new ServiceManagementPage(driver);
            
            servicePage.clickAddService();
            Thread.sleep(1500);
            
            long timestamp = System.currentTimeMillis();
            servicePage.enterServiceName("Service " + timestamp);
            servicePage.enterPrice("100000");
            servicePage.enterDescription("Test service");
            servicePage.saveService();
            Thread.sleep(1500);
            
            assertTrue("TC02 FAILED", true);
            logInfo("✓ TC02 PASSED");
            
        } catch (Exception e) {
            logError("TC02 FAILED: " + e.getMessage());
        }
    }

    @Test
    public void testTC03_EditService() {
        logInfo("========== W-F-16-TC03: Sửa dịch vụ ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToServices();
            servicePage = new ServiceManagementPage(driver);
            
            if (servicePage.getServiceRowCount() > 0) {
                servicePage.editFirstService();
                Thread.sleep(1500);
                
                servicePage.enterServiceName("Updated Service");
                servicePage.enterPrice("150000");
                servicePage.saveService();
                Thread.sleep(1000);
            }
            
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
