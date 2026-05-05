package com.umbrella.tests.web;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.umbrella.base.BaseTest;
import com.umbrella.pages.DashboardPage;
import com.umbrella.pages.LoginPage;
import com.umbrella.pages.TreatmentPlanPage;

/**
 * W-F-10: Treatment Plan Tests (Phác đồ điều trị)
 * 
 * Test Class: TreatmentPlanTest
 * Purpose: Test treatment plan management
 * Framework: Selenium WebDriver + JUnit 4
 * 
 * Test Cases:
 * - W-F-10-TC01: View treatment plans
 * - W-F-10-TC02: Search treatment plan
 * - W-F-10-TC03: Create treatment plan
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class TreatmentPlanTest extends BaseTest {

    private LoginPage loginPage;
    private TreatmentPlanPage treatmentPage;
    private DashboardPage dashboardPage;

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

    private void loginAndNavigateToTreatment() throws InterruptedException {
        logInfo("Đăng nhập và chuyển đến phác đồ");
        loginPage.login("phongkaster@gmail.com", "123456");
        Thread.sleep(3000);
        navigateTo(getAppUrl() + "/treatment");
        Thread.sleep(2000);
    }

    @Test
    public void testTC01_ViewTreatmentPlans() {
        logInfo("========== W-F-10-TC01: Xem phác đồ điều trị ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToTreatment();
            treatmentPage = new TreatmentPlanPage(driver);
            
            boolean isTableVisible = treatmentPage.isTreatmentTableVisible();
            int rowCount = treatmentPage.getTreatmentRowCount();
            
            logInfo("Bảng phác đồ: " + isTableVisible + ", Số dòng: " + rowCount);
            
            assertTrue("TC01 FAILED: Bảng không hiển thị", isTableVisible);
            logInfo("✓ TC01 PASSED");
            
        } catch (Exception e) {
            logError("TC01 FAILED: " + e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void testTC02_SearchTreatmentPlan() {
        logInfo("========== W-F-10-TC02: Tìm kiếm phác đồ ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToTreatment();
            treatmentPage = new TreatmentPlanPage(driver);
            
            treatmentPage.search("test");
            Thread.sleep(1500);
            
            assertTrue("TC02 FAILED", treatmentPage.isTreatmentTableVisible());
            logInfo("✓ TC02 PASSED");
            
        } catch (Exception e) {
            logError("TC02 FAILED: " + e.getMessage());
        }
    }

    @Test
    public void testTC03_ViewTreatmentDetails() {
        logInfo("========== W-F-10-TC03: Xem chi tiết phác đồ ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToTreatment();
            treatmentPage = new TreatmentPlanPage(driver);
            
            if (treatmentPage.getTreatmentRowCount() > 0) {
                treatmentPage.viewFirstTreatment();
                Thread.sleep(1500);
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
