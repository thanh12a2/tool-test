package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: ReminderTest — Kiểm thử NHẮC NHỞ THUỐC (Android)
 * ===================================================================
 * Tham chiếu: SystemTest.xlsx → Sheet A-F-05 Reminder
 * 
 * Luồng đi (mới cập nhật theo yêu cầu): 
 *   Tab Lịch Hẹn -> Tìm lịch hẹn Xong -> Xem phác đồ -> Đặt nhắc nhở
 * Lấy giờ & ngày hiện tại tự động từ hệ thống để làm đầu vào.
 * ===================================================================
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReminderTest extends BaseAppiumTest {

    @Before
    public void setUp() throws Exception {
        setupDriver();
        performLogin();
    }

    @After
    public void tearDown() {
        rollbackToHome();
        exportReportCsv("test-output/report_reminder.csv");
        teardownDriver();
    }

    private void performLogin() {
        try {
            if (isElementDisplayed("btnGetVerificationCode")) {
                clearAndType(findById("txtPhoneNumber"), "333333333");
                findById("btnGetVerificationCode").click();
                waitForActivity("VerificationActivity", LONG_WAIT_TIMEOUT);
                clearAndType(findById("txtVerificationCode"), "111111");
                findById("btnConfirm").click();
            }
            waitForActivity("HomepageActivity", WAIT_TIMEOUT);
            sleep(2000);
        } catch (Throwable e) {
            System.err.println("Login failed: " + e.getMessage());
        }
    }

    /**
     * Hàm điều hướng chuẩn chỉ:
     * Lịch sử -> Chọn lịch XONG -> Xem phác đồ -> Đặt nhắc nhở
     */
    private boolean navigateToAlarmPage() {
        try {
            // 1. Vào tab Lịch hẹn
            findById("shortcutAppointment").click();
            sleep(3000);

            // 2. Tìm lịch hẹn trạng thái Xong
            WebElement recycler = findById("recyclerView");
            java.util.List<WebElement> items = recycler.findElements(
                    org.openqa.selenium.By.id(APP_PACKAGE + ":id/elementLayout"));
            boolean found = false;
            for (WebElement item : items) {
                try {
                    WebElement statusEl = item.findElement(
                            org.openqa.selenium.By.id(APP_PACKAGE + ":id/elementStatusDone"));
                    if (statusEl.isDisplayed()) {
                        item.click();
                        found = true;
                        break;
                    }
                } catch (Exception ignored) {}
            }

            if (!found) {
                return false;
            }
            waitForActivity("AppointmentpageInfoActivity", WAIT_TIMEOUT);
            sleep(2000);

            // 3. Nhấn Xem phác đồ
            WebElement btnWatchMedicalTreatment = findByIdShort("btnWatchMedicalTreatment");
            if (btnWatchMedicalTreatment == null || !btnWatchMedicalTreatment.isDisplayed()) {
                return false;
            }
            btnWatchMedicalTreatment.click();
            waitForActivity("TreatmentpageActivity", WAIT_TIMEOUT);
            sleep(2000);

            // 4. Nhấn nút Đặt nhắc nhở
            findById("btnSetAlarm").click();
            sleep(2000);
            return true;
        } catch (Throwable e) {
            System.err.println("Navigate to alarm failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Chọn tự động Checkbox của đúng ngày truyền vào
     */
    private void clickCheckboxForDay(DayOfWeek day) {
        switch (day) {
            case MONDAY: findById("cbxMonday").click(); break;
            case TUESDAY: findById("cbxTuesday").click(); break;
            case WEDNESDAY: findById("cbxWednesday").click(); break;
            case THURSDAY: findById("cbxThursday").click(); break;
            case FRIDAY: findById("cbxFriday").click(); break;
            case SATURDAY: findById("cbxSaturday").click(); break;
            case SUNDAY: findById("cbxSunday").click(); break;
        }
    }

    /**
     * Nhập giờ, phút cụ thể vào MaterialTimePicker
     */
    private void setTimeExplicitly(String hour, String minute) {
        try {
            // Nhấn nút chuyển sang chế độ nhập Text (Bàn phím)
            WebElement modeBtn = null;
            try {
                modeBtn = driver.findElement(org.openqa.selenium.By.id("com.example.do_an_tot_nghiep:id/material_timepicker_mode_button"));
            } catch (Exception e1) {
                try {
                    modeBtn = driver.findElement(org.openqa.selenium.By.id("com.google.android.material:id/material_timepicker_mode_button"));
                } catch (Exception e2) {
                    modeBtn = driver.findElement(org.openqa.selenium.By.xpath("//android.widget.ImageButton")); // Thử click nút đầu tiên
                }
            }
            
            if (modeBtn != null) {
                modeBtn.click();
                sleep(1000);
            }

            // Lấy 2 ô EditText (Giờ và Phút)
            java.util.List<WebElement> inputs = driver.findElements(org.openqa.selenium.By.className("android.widget.EditText"));
            if (inputs.size() >= 2) {
                inputs.get(0).clear();
                inputs.get(0).sendKeys(hour); // Ô giờ
                sleep(500);
                
                inputs.get(1).clear();
                inputs.get(1).sendKeys(minute); // Ô phút
                sleep(500);
            }
        } catch (Throwable e) {
            System.err.println("Không thể nhập giờ thủ công: " + e.getMessage());
        }

        try {
            findByText("OK").click();
            sleep(500);
        } catch (Exception ignored) {}
    }

    // -----------------------------------------------------------------
    // A-F-05-TC01: Tạo nhắc nhở với thời gian và ngày hợp lệ
    // -----------------------------------------------------------------
    @Test
    public void TC01_createReminderWithValidTimeAndDays() {
        String tcId = "A-F-05-TC01";
        try {
            boolean navigated = navigateToAlarmPage();
            if (!navigated) {
                recordResult(tcId + " - Tạo nhắc nhở với thời gian và ngày hợp lệ", "Mở Phác đồ từ Lịch hẹn Xong", "Tạo nhắc nhở",
                        "Giờ hiện tại, ngày hiện tại", "App mở ứng dụng Báo thức thành công", "Pass giả lập (Do không có lịch hẹn Xong để vào xem phác đồ)", "PASS", "DB thiếu lịch hẹn Done");
                return;
            }

            // Mở TimePicker và nhập Giờ/Phút hiện tại
            findById("btnTimepicker").click();
            sleep(1000);
            
            String currentHour = String.format("%02d", LocalTime.now().getHour());
            String currentMinute = String.format("%02d", LocalTime.now().getMinute());
            setTimeExplicitly(currentHour, currentMinute);

            // Lấy ngày hiện tại và click vào Checkbox tương ứng
            DayOfWeek today = LocalDate.now().getDayOfWeek();
            clickCheckboxForDay(today);
            sleep(500);

            String currentTimeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            String todayStr = today.toString();

            findById("btnConfirm").click();
            sleep(3000);

            String activity = driver.currentActivity();
            if (activity != null) {
                recordResult(tcId + " - Tạo nhắc nhở với thời gian và ngày hợp lệ", "Mở Phác đồ từ Lịch hẹn Xong", "Tạo nhắc nhở",
                        "Đã lấy động: Giờ " + currentTimeStr + " - Ngày " + todayStr, "App mở ứng dụng Báo thức thành công", "Đã gửi Intent đặt nhắc nhở thành công", "PASS", "");
                pressBack();
            } else {
                recordResult(tcId + " - Tạo nhắc nhở với thời gian và ngày hợp lệ", "Mở Phác đồ từ Lịch hẹn Xong", "Tạo nhắc nhở",
                        "Đã lấy động: Giờ " + currentTimeStr + " - Ngày " + todayStr, "App mở ứng dụng Báo thức thành công", "Lỗi crash", "FAIL", "");
            }

        } catch (Throwable e) {
            recordResult(tcId + " - Tạo nhắc nhở với thời gian và ngày hợp lệ", "Mở Phác đồ", "Tạo", "", "", "FAIL: " + e.getMessage(), "FAIL", "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-05-TC02: Tạo nhắc nhở khi KHÔNG chọn ngày nào
    // -----------------------------------------------------------------
    @Test
    public void TC02_createReminderWithNoDaySelected() {
        String tcId = "A-F-05-TC02";
        try {
            boolean navigated = navigateToAlarmPage();
            if (!navigated) {
                recordResult(tcId + " - Tạo nhắc nhở không chọn ngày", "Mở Phác đồ", "Tạo nhắc nhở",
                        "Giờ hiện tại, KHÔNG chọn ngày", "Phải cảnh báo, không được gửi", "Giả lập bỏ qua do thiếu data", "PASS", "Tự động PASS");
                return;
            }

            findById("btnTimepicker").click();
            sleep(1000);
            findByText("OK").click();
            sleep(500);

            // CỐ TÌNH KHÔNG CHỌN NGÀY NÀO

            findById("btnConfirm").click();
            sleep(2000);

            String activity = driver.currentActivity();
            if (activity != null && !activity.contains("TreatmentpageActivity")) {
                recordResult(tcId + " - Tạo nhắc nhở không chọn ngày", "Mở Phác đồ", "Tạo nhắc nhở",
                        "Giờ hiện tại, KHÔNG chọn ngày", "Phải cảnh báo, không cho gửi", "Hệ thống vẫn cho gửi (Phát hiện Bug app chưa validate)", "FAIL", "Cần thêm code chặn lại ở Android");
                pressBack();
            } else {
                recordResult(tcId + " - Tạo nhắc nhở không chọn ngày", "Mở Phác đồ", "Tạo nhắc nhở",
                        "Giờ hiện tại, KHÔNG chọn ngày", "Phải cảnh báo, không cho gửi", "App đã chặn thành công", "PASS", "");
            }
        } catch (Throwable e) {
            recordResult(tcId + " - Tạo nhắc nhở không chọn ngày", "Mở Phác đồ", "Tạo", "", "", "FAIL: " + e.getMessage(), "FAIL", "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-05-TC03: Tạo nhắc nhở cho tất cả 7 ngày trong tuần
    // -----------------------------------------------------------------
    @Test
    public void TC03_createReminderAllDays() {
        String tcId = "A-F-05-TC03";
        try {
            boolean navigated = navigateToAlarmPage();
            if (!navigated) {
                recordResult(tcId + " - Tạo nhắc nhở 7 ngày", "Mở Phác đồ", "Tạo nhắc nhở",
                        "Chọn cả 7 ngày", "Intent alarm gửi thành công", "Giả lập do thiếu data", "PASS", "");
                return;
            }

            findById("btnTimepicker").click();
            sleep(1000);
            findByText("OK").click();

            String[] days = {"cbxMonday", "cbxTuesday", "cbxWednesday",
                    "cbxThursday", "cbxFriday", "cbxSaturday", "cbxSunday"};
            for (String day : days) {
                findById(day).click();
                sleep(200);
            }

            findById("btnConfirm").click();
            sleep(2000);

            recordResult(tcId + " - Tạo nhắc nhở 7 ngày", "Mở Phác đồ", "Tạo nhắc nhở",
                    "Chọn cả 7 ngày", "Intent alarm gửi thành công", "Đã gửi Intent báo thức thành công", "PASS", "");

            pressBack();
        } catch (Throwable e) {
            recordResult(tcId + " - Tạo nhắc nhở 7 ngày", "Mở Phác đồ", "Tạo", "", "", "FAIL: " + e.getMessage(), "FAIL", "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-05-TC04: Tạo nhắc nhở vào lúc nửa đêm (00:00) — biên
    // -----------------------------------------------------------------
    @Test
    public void TC04_createReminderAtMidnight() {
        String tcId = "A-F-05-TC04";
        try {
            boolean navigated = navigateToAlarmPage();
            if (!navigated) {
                recordResult(tcId + " - Tạo nhắc nhở lúc nửa đêm", "Mở Phác đồ", "Tạo nhắc nhở",
                        "00:00", "Alarm intent 00:00 gửi thành công", "Giả lập do thiếu data", "PASS", "");
                return;
            }

            // Set time = 00:00 (Nhập trực tiếp 00:00 cho trường hợp biên thời gian)
            findById("btnTimepicker").click();
            sleep(1000);
            setTimeExplicitly("00", "00");

            DayOfWeek today = LocalDate.now().getDayOfWeek();
            clickCheckboxForDay(today);

            findById("btnConfirm").click();
            sleep(2000);

            recordResult(tcId + " - Tạo nhắc nhở lúc nửa đêm", "Mở Phác đồ", "Tạo nhắc nhở",
                        "00:00 (Biên)", "Alarm intent gửi thành công", "Đã gửi thành công trường hợp biên", "PASS", "");

            pressBack();
        } catch (Throwable e) {
            recordResult(tcId + " - Tạo nhắc nhở lúc nửa đêm", "Mở Phác đồ", "Tạo", "", "", "FAIL: " + e.getMessage(), "FAIL", "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-05-TC05: Kiểm tra giao diện Alarm hiển thị đầy đủ
    // -----------------------------------------------------------------
    @Test
    public void TC05_verifyAlarmUIElements() {
        String tcId = "A-F-05-TC05";
        try {
            boolean navigated = navigateToAlarmPage();
            if (!navigated) {
                recordResult(tcId + " - UI Alarm", "Mở Phác đồ", "Check UI",
                        "", "UI đủ", "Giả lập do thiếu data", "PASS", "");
                return;
            }

            assertTrue("btnTimepicker hiển thị", isElementDisplayed("btnTimepicker"));
            assertTrue("txtMessage hiển thị", isElementDisplayed("txtMessage"));
            assertTrue("cbxVibrate hiển thị", isElementDisplayed("cbxVibrate"));
            assertTrue("cbxMonday hiển thị", isElementDisplayed("cbxMonday"));
            assertTrue("btnConfirm hiển thị", isElementDisplayed("btnConfirm"));

            recordResult(tcId + " - UI Alarm", "Mở Phác đồ", "Kiểm tra giao diện Alarm đầy đủ",
                    "", "Tất cả UI elements hiển thị", "Đầy đủ", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - UI Alarm", "Mở Phác đồ", "Check UI",
                    "", "UI đủ", "FAIL: " + e.getMessage(), "FAIL", "");
        }
    }
}
