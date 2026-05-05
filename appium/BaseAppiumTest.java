package com.example.do_an_tot_nghiep.appium;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ==========================================================================
 *  BASE APPIUM TEST CLASS
 *  -----------------------------------------------------------------------
 *  Lớp cơ sở cho tất cả các test Appium trên ứng dụng Umbrella Health.
 *  Cung cấp:
 *   - Khởi tạo / hủy AndroidDriver
 *   - Hàm tiện ích tìm phần tử theo resource-id
 *   - Đọc CSV test data
 *   - Ghi kết quả report CSV
 *   - Hàm rollback (hủy booking, back screen)
 * ==========================================================================
 *  Hệ thống: Android App kết nối backend qua Laragon (PHP + MySQL)
 *  Package: com.example.do_an_tot_nghiep
 * ==========================================================================
 */
public class BaseAppiumTest {

    /** ==== CẤU HÌNH APPIUM SERVER ==== */
    // Appium 2.x mặc định dùng http://127.0.0.1:4723/ (không có /wd/hub như bản 1.x)
    protected static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723/";

    /** ==== CẤU HÌNH THIẾT BỊ ==== */
    protected static final String DEVICE_NAME       = "R83Y10HBGXZ";
    protected static final String PLATFORM_NAME     = "Android";
    protected static final String PLATFORM_VERSION  = "16";
    protected static final String APP_PACKAGE       = "com.example.do_an_tot_nghiep";
    protected static final String APP_ACTIVITY      = ".MainActivity";

    /** ==== TIMEOUT (giây) ==== */
    protected static final int WAIT_TIMEOUT         = 15;
    protected static final int LONG_WAIT_TIMEOUT    = 30;
    protected static final int SHORT_WAIT_TIMEOUT   = 5;

    /** ==== DRIVER & WAIT ==== */
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    protected WebDriverWait longWait;
    protected WebDriverWait shortWait;

    /** ==== REPORT ==== */
    protected List<Map<String, String>> testResults = new ArrayList<>();
    protected long testStartTime;

    // =====================================================================
    //  SETUP / TEARDOWN
    // =====================================================================

    /**
     * Khởi tạo AndroidDriver với UiAutomator2 (Mặc định noReset = true).
     */
    protected void setupDriver() throws MalformedURLException {
        setupDriver(true);
    }

    /**
     * Khởi tạo AndroidDriver với UiAutomator2 và cấu hình noReset tùy chỉnh.
     * @param noReset true: giữ lại dữ liệu app, false: xóa trắng data app.
     */
    protected void setupDriver(boolean noReset) throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(DEVICE_NAME)
                .setPlatformName(PLATFORM_NAME)
                .setPlatformVersion(PLATFORM_VERSION)
                .setAppPackage(APP_PACKAGE)
                .setAppActivity(APP_ACTIVITY)
                .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                .setNoReset(noReset)          
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(300));

        driver   = new AndroidDriver(new URL(APPIUM_SERVER_URL), options);
        wait     = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
        longWait = new WebDriverWait(driver, Duration.ofSeconds(LONG_WAIT_TIMEOUT));
        shortWait= new WebDriverWait(driver, Duration.ofSeconds(SHORT_WAIT_TIMEOUT));

        testStartTime = System.currentTimeMillis();
    }

    /**
     * Đóng driver sau khi test xong.
     */
    protected void teardownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    // =====================================================================
    //  TÌM PHẦN TỬ THEO RESOURCE-ID
    // =====================================================================

    /**
     * Tìm phần tử bằng resource-id (đầy đủ: package:id/xxx).
     * @param resourceId  phần tử id trong layout XML (vd: "txtPhoneNumber")
     */
    protected WebElement findById(String resourceId) {
        String fullId = APP_PACKAGE + ":id/" + resourceId;
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(fullId)));
    }

    /**
     * Tìm phần tử bằng resource-id, chờ ngắn (SHORT_WAIT_TIMEOUT).
     */
    protected WebElement findByIdShort(String resourceId) {
        String fullId = APP_PACKAGE + ":id/" + resourceId;
        return shortWait.until(ExpectedConditions.presenceOfElementLocated(By.id(fullId)));
    }

    /**
     * Tìm phần tử bằng resource-id, chờ dài (LONG_WAIT_TIMEOUT).
     */
    protected WebElement findByIdLong(String resourceId) {
        String fullId = APP_PACKAGE + ":id/" + resourceId;
        return longWait.until(ExpectedConditions.presenceOfElementLocated(By.id(fullId)));
    }

    /**
     * Kiểm tra phần tử có hiển thị hay không.
     */
    protected boolean isElementDisplayed(String resourceId) {
        try {
            String fullId = APP_PACKAGE + ":id/" + resourceId;
            WebElement element = shortWait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id(fullId)));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tìm phần tử bằng text hiển thị (UiAutomator2).
     */
    protected WebElement findByText(String text) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@text='" + text + "']")));
    }

    /**
     * Kiểm tra Toast message có hiển thị hay không.
     * Lưu ý: Toast khó bắt, dùng xpath tìm text trên toàn bộ hierarchy.
     */
    protected boolean isToastDisplayed(String expectedText) {
        try {
            WebElement toast = shortWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(@text, '" + expectedText + "')]")));
            return toast != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Nhập text vào EditText (xóa text cũ trước).
     */
    protected void clearAndType(WebElement element, String text) {
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    /**
     * Chờ Activity cụ thể xuất hiện.
     */
    protected boolean waitForActivity(String activityName, int timeoutSeconds) {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        while (System.currentTimeMillis() < endTime) {
            String currentActivity = driver.currentActivity();
            if (currentActivity != null && currentActivity.contains(activityName)) {
                return true;
            }
            sleep(500);
        }
        return false;
    }

    /**
     * Nhấn nút Back của Android.
     */
    protected void pressBack() {
        driver.navigate().back();
    }

    /**
     * Sleep (mili giây).
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =====================================================================
    //  NGÀY GIỜ HỖ TRỢ
    // =====================================================================

    /**
     * Trả về ngày hôm nay dạng yyyy-MM-dd.
     */
    protected String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Trả về ngày mai dạng yyyy-MM-dd.
     */
    protected String getTomorrow() {
        return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Trả về ngày hôm qua dạng yyyy-MM-dd.
     */
    protected String getYesterday() {
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Chuyển đổi placeholder ngày (TOMORROW, YESTERDAY, TODAY) thành ngày thực.
     */
    protected String resolveDatePlaceholder(String dateValue) {
        if (dateValue == null) return getToday();
        switch (dateValue.toUpperCase()) {
            case "TOMORROW":  return getTomorrow();
            case "YESTERDAY": return getYesterday();
            case "TODAY":     return getToday();
            default:          return dateValue;
        }
    }

    // =====================================================================
    //  ĐỌC CSV TEST DATA
    // =====================================================================

    /**
     * Đọc file CSV từ classpath (src/test/resources/).
     * @param resourcePath  đường dẫn tương đối, vd: "testdata/login_test_data.csv"
     * @return danh sách Map, mỗi Map là 1 dòng dữ liệu (header → value)
     */
    protected List<Map<String, String>> readCsvTestData(String resourcePath) {
        List<Map<String, String>> data = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {

            String headerLine = br.readLine();
            if (headerLine == null) return data;
            String[] headers = headerLine.split(",", -1);

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i].trim(), i < values.length ? values[i].trim() : "");
                }
                data.add(row);
            }
        } catch (Exception e) {
            System.err.println("Lỗi đọc CSV: " + resourcePath + " - " + e.getMessage());
        }
        return data;
    }

    // =====================================================================
    //  REPORT - GHI KẾT QUẢ TEST
    // =====================================================================

    /**
     * Ghi nhận kết quả 1 test case (Mới - 8 cột).
     */
    protected void recordResult(String moTaTest, String dieuKienTienQuyet, String cacBuoc,
                                String duLieuDauVao, String ketQuaMongDoi, String ketQuaThucTe,
                                String trangThai, String ghiChu) {
        Map<String, String> result = new HashMap<>();
        result.put("moTaTest", moTaTest);
        result.put("dieuKienTienQuyet", dieuKienTienQuyet);
        result.put("cacBuoc", cacBuoc);
        result.put("duLieuDauVao", duLieuDauVao);
        result.put("ketQuaMongDoi", ketQuaMongDoi);
        result.put("ketQuaThucTe", ketQuaThucTe);
        result.put("trangThai", trangThai);
        result.put("ghiChu", ghiChu);
        testResults.add(result);
    }

    /**
     * Ghi nhận kết quả (Tương thích ngược cho các Test class cũ).
     */
    protected void recordResult(String testCaseId, String function, String meaning,
                                String status, String actualResult, String note) {
        // Map 6 tham số cũ sang 8 cột mới để không làm vỡ cấu trúc file CSV
        recordResult(
            testCaseId + " - " + meaning, 
            "N/A", 
            function, 
            "N/A", 
            "N/A", 
            actualResult, 
            status, 
            note
        );
    }

    /**
     * Xuất toàn bộ kết quả test sang file CSV.
     * @param filePath đường dẫn file xuất ra, vd: "test_report_login.csv"
     */
    protected void exportReportCsv(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            boolean isNewFile = !file.exists();
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            // Mở file ở chế độ append (true) với chuẩn UTF-8
            try (java.io.OutputStreamWriter fw = new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(file, true), java.nio.charset.StandardCharsets.UTF_8)) {
                
                // Chỉ ghi Header và BOM (Byte Order Mark) để Excel nhận diện tiếng Việt
                if (isNewFile) {
                    fw.write('\ufeff'); // Thêm BOM
                    fw.write("Mô tả Test,Điều kiện tiên quyết,Các bước,Dữ liệu đầu vào,Kết quả mong đợi,Kết quả thực tế,Trạng thái,Ghi chú\n");
                }
                for (Map<String, String> r : testResults) {
                    fw.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                            escapeCsv(r.get("moTaTest")),
                            escapeCsv(r.get("dieuKienTienQuyet")),
                            escapeCsv(r.get("cacBuoc")),
                            escapeCsv(r.get("duLieuDauVao")),
                            escapeCsv(r.get("ketQuaMongDoi")),
                            escapeCsv(r.get("ketQuaThucTe")),
                            escapeCsv(r.get("trangThai")),
                            escapeCsv(r.get("ghiChu"))
                    ));
                }
                System.out.println("✅ Report đã xuất: " + filePath);
            }
        } catch (java.io.IOException e) {
            System.err.println("❌ Lỗi xuất report: " + e.getMessage());
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // =====================================================================
    //  ROLLBACK HELPERS
    // =====================================================================

    /**
     * Rollback: Quay về màn hình chính bằng cách back nhiều lần.
     */
    protected void rollbackToHome() {
        for (int i = 0; i < 5; i++) {
            try {
                String activity = driver.currentActivity();
                if (activity != null && activity.contains("HomepageActivity")) {
                    // Nếu đang ở trang chủ, kiểm tra xem có popup thoát app không (btnCancel), nếu có thì đóng
                    try {
                        WebElement btnCancel = findByIdShort("btnCancel");
                        if (btnCancel != null && btnCancel.isDisplayed()) {
                            btnCancel.click();
                        }
                    } catch (Exception ignored) {}
                    return;
                }
                pressBack();
                sleep(500);
            } catch (Exception e) {
                break;
            }
        }
    }

    /**
     * Rollback: Hủy booking vừa tạo (nếu có nút Cancel).
     */
    protected boolean rollbackCancelBooking() {
        try {
            WebElement btnCancel = findByIdShort("btnCancel");
            if (btnCancel.isDisplayed()) {
                btnCancel.click();
                sleep(1000);
                // Confirm dialog
                WebElement btnOK = findByIdShort("btnOK");
                if (btnOK != null && btnOK.isDisplayed()) {
                    btnOK.click();
                    sleep(2000);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Rollback cancel booking: không tìm thấy nút Cancel - " + e.getMessage());
        }
        return false;
    }

    /**
     * Chụp ảnh màn hình khi test fail (để debug).
     */
    protected String captureScreenshot(String testCaseId) {
        try {
            java.io.File screenshot = driver.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
            String fileName = "screenshot_" + testCaseId + "_"
                    + System.currentTimeMillis() + ".png";
            java.io.File destFile = new java.io.File("test-output/screenshots/" + fileName);
            destFile.getParentFile().mkdirs();
            java.nio.file.Files.copy(screenshot.toPath(), destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            System.err.println("Lỗi chụp screenshot: " + e.getMessage());
            return null;
        }
    }
}
