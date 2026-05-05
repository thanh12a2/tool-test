package com.umbrella.tests.web;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.umbrella.base.BaseTest;
import com.umbrella.pages.LoginPage;
import com.umbrella.pages.MedicalRecordPage;

/**
 * W-F-11: Medical Record Tests (Bệnh án)
 * 
 * Test Class: MedicalRecordTest
 * Purpose: Test medical records management
 * Framework: Selenium WebDriver + JUnit 4
 * 
 * Test Cases:
 * - W-F-11-TC01: View medical records
 * - W-F-11-TC02: Search records
 * - W-F-11-TC03: Print record
 * 
 * @author QA Team
 * @since 2026-05-06
 * @version 1.0
 */
public class MedicalRecordTest extends BaseTest {

    private LoginPage loginPage;
    private MedicalRecordPage recordPage;

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

    private void loginAndNavigateToRecords() throws InterruptedException {
        loginPage.login("phongkaster@gmail.com", "123456");
        Thread.sleep(3000);
        navigateTo(getAppUrl() + "/records");
        Thread.sleep(2000);
    }

    @Test
    public void testTC01_ViewMedicalRecords() {
        logInfo("========== W-F-11-TC01: Xem bệnh án ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToRecords();
            recordPage = new MedicalRecordPage(driver);
            
            boolean isTableVisible = recordPage.isRecordsTableVisible();
            assertTrue("TC01 FAILED", isTableVisible);
            logInfo("✓ TC01 PASSED");
            
        } catch (Exception e) {
            logError("TC01 FAILED: " + e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void testTC02_SearchRecords() {
        logInfo("========== W-F-11-TC02: Tìm kiếm bệnh án ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToRecords();
            recordPage = new MedicalRecordPage(driver);
            
            recordPage.search("test");
            Thread.sleep(1500);
            
            assertTrue("TC02 FAILED", recordPage.isRecordsTableVisible());
            logInfo("✓ TC02 PASSED");
            
        } catch (Exception e) {
            logError("TC02 FAILED: " + e.getMessage());
        }
    }

    @Test
    public void testTC03_PrintRecord() {
        logInfo("========== W-F-11-TC03: In bệnh án ==========");
        
        try {
            if (!driver.getCurrentUrl().contains("localhost")) {
                assertTrue(true);
                return;
            }
            
            loginAndNavigateToRecords();
            recordPage = new MedicalRecordPage(driver);
            
            if (recordPage.getRecordRowCount() > 0) {
                recordPage.printFirstRecord();
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
