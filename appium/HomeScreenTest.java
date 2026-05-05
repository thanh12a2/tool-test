package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: HomeScreenTest — Kiểm thử MÀN HÌNH HOME / TRANG CHỦ
 * ===================================================================
 * Tham chiếu: SystemTest.xlsx → Sheet A-F-07 Màn hình Home
 * Hệ thống: Hỗ trợ Đặt lịch & Điều trị Y tế | PTIT | Nhóm QA
 *
 * Mô tả chức năng:
 *   Màn hình Home (HomepageActivity) là trang chủ sau khi bệnh nhân
 *   đăng nhập, bao gồm: nhiệt độ thời tiết, danh mục chuyên khoa,
 *   và danh sách bác sĩ nổi bật.
 *
 * Resource IDs chính:
 *   - tvWeatherTemp       : TextView hiển thị nhiệt độ
 *   - specialityRecyclerView : RecyclerView danh mục chuyên khoa
 *   - doctorRecyclerView  : RecyclerView danh sách bác sĩ nổi bật
 *
 * API liên quan:
 *   - GET /api/weather    : Lấy nhiệt độ theo vị trí
 *   - GET /api/specialities : Lấy danh mục chuyên khoa
 *   - GET /api/doctors    : Lấy danh sách bác sĩ nổi bật
 * ===================================================================
 */
public class HomeScreenTest extends BaseAppiumTest {

    // -----------------------------------------------------------------
    // SETUP & TEARDOWN
    // -----------------------------------------------------------------

    /**
     * Khởi tạo driver và đăng nhập trước mỗi test case.
     * noReset = true: giữ session đăng nhập, tránh mất thời gian re-login.
     */
    @Before
    public void setUp() throws Exception {
        setupDriver(true);
        performLogin();
    }

    /**
     * Xuất report CSV và đóng driver sau mỗi test case.
     */
    @After
    public void tearDown() {
        exportReportCsv("test-output/report_home_screen.csv");
        teardownDriver();
    }

    // -----------------------------------------------------------------
    // HELPER: Đăng nhập để đến HomepageActivity
    // -----------------------------------------------------------------

    /**
     * Thực hiện đăng nhập bằng OTP để đến được HomepageActivity.
     * Được gọi trong @Before để đảm bảo mọi test đều bắt đầu từ Home.
     */
    private void performLogin() {
        try {
            // Kiểm tra nếu đã ở HomepageActivity thì bỏ qua
            String currentActivity = driver.currentActivity();
            if (currentActivity != null && currentActivity.contains("HomepageActivity")) {
                return;
            }
            clearAndType(findById("txtPhoneNumber"), "901234567");
            findById("btnGetVerificationCode").click();
            waitForActivity("VerificationActivity", LONG_WAIT_TIMEOUT);
            sleep(2000);
            clearAndType(findById("txtVerificationCode"), "123456");
            findById("btnConfirm").click();
            waitForActivity("HomepageActivity", LONG_WAIT_TIMEOUT);
        } catch (Throwable e) {
            System.err.println("performLogin failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-07-TC01: Hiển thị nhiệt độ thời tiết trên màn hình Home
    // Chức năng: HomepageActivity.loadWeather() → API /api/weather
    //            hoặc dùng WeatherManager để lấy vị trí & nhiệt độ
    // Ý nghĩa: Đảm bảo widget thời tiết hoạt động khi có kết nối internet
    // Kỹ thuật: EP (Equivalence Partitioning)
    // -----------------------------------------------------------------
    @Test
    public void TC01_displayWeatherTemperatureOnHome() {
        String tcId = "A-F-07-TC01";
        try {
            // Bước 1: Đảm bảo đang ở HomepageActivity
            assertTrue("Phải đang ở HomepageActivity",
                    driver.currentActivity().contains("HomepageActivity"));

            // Bước 2: Chờ widget thời tiết load xong (API call bất đồng bộ)
            sleep(3000);

            // Bước 3: Kiểm tra TextView nhiệt độ có hiển thị
            boolean weatherDisplayed = isElementDisplayed("tvWeatherTemp");
            assertTrue("Widget nhiệt độ thời tiết phải hiển thị trên Home", weatherDisplayed);

            // Bước 4: Kiểm tra text không rỗng và có giá trị nhiệt độ hợp lệ
            WebElement tvTemp = findById("tvWeatherTemp");
            String tempText = tvTemp.getText();
            assertNotNull("Text nhiệt độ không được null", tempText);
            assertFalse("Text nhiệt độ không được rỗng", tempText.isEmpty());

            // Bước 5: Kiểm tra text chứa ký tự nhiệt độ (°) hoặc có số
            // Ví dụ dạng hiển thị: "28°C" hoặc "28°"
            boolean hasTemperatureValue = tempText.matches(".*\\d+.*");
            assertTrue("Nhiệt độ phải chứa giá trị số", hasTemperatureValue);

            recordResult(
                    tcId + " - Hiển thị nhiệt độ thời tiết trên màn hình Home",
                    "Bệnh nhân đã đăng nhập; Thiết bị có kết nối internet",
                    "1. Mở app → 2. Xem màn hình Home → 3. Kiểm tra nhiệt độ thời tiết",
                    "(không có đầu vào)",
                    "Hiển thị nhiệt độ thời tiết hiện tại theo vị trí",
                    "tvWeatherTemp hiển thị với giá trị: " + tempText,
                    "PASS", "");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            recordResult(
                    tcId + " - Hiển thị nhiệt độ thời tiết trên màn hình Home",
                    "Bệnh nhân đã đăng nhập; Thiết bị có kết nối internet",
                    "1. Mở app → 2. Xem màn hình Home → 3. Kiểm tra nhiệt độ thời tiết",
                    "(không có đầu vào)",
                    "Hiển thị nhiệt độ thời tiết hiện tại theo vị trí",
                    e.getMessage(),
                    "FAIL", "Kiểm tra kết nối internet hoặc quyền truy cập vị trí");
            fail("TC01 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-07-TC02: Hiển thị danh mục chuyên khoa trên Home
    // Chức năng: HomepageActivity.loadSpecialities() → API /api/specialities
    //            → bind vào specialityRecyclerView (adapter)
    // Ý nghĩa: Đảm bảo RecyclerView chuyên khoa render đúng dữ liệu từ API
    // Kỹ thuật: EP (Equivalence Partitioning)
    // -----------------------------------------------------------------
    @Test
    public void TC02_displaySpecialityCategoryOnHome() {
        String tcId = "A-F-07-TC02";
        try {
            // Bước 1: Đảm bảo đang ở HomepageActivity
            assertTrue("Phải đang ở HomepageActivity",
                    driver.currentActivity().contains("HomepageActivity"));

            // Bước 2: Chờ RecyclerView chuyên khoa load xong
            sleep(3000);

            // Bước 3: Kiểm tra RecyclerView chuyên khoa hiển thị
            boolean specialityVisible = isElementDisplayed("specialityRecyclerView");
            assertTrue("Danh mục chuyên khoa phải hiển thị trên Home", specialityVisible);

            // Bước 4: Lấy element và kiểm tra có ít nhất 1 item
            WebElement rvSpeciality = findById("specialityRecyclerView");
            assertNotNull("RecyclerView chuyên khoa phải tồn tại", rvSpeciality);
            assertTrue("RecyclerView chuyên khoa phải hiển thị", rvSpeciality.isDisplayed());

            // Bước 5: Kiểm tra có ít nhất 1 item con trong RecyclerView
            // Dùng UiAutomator để đếm số child
            java.util.List<org.openqa.selenium.WebElement> items = driver.findElements(
                    org.openqa.selenium.By.xpath(
                            "//*[@resource-id='" + APP_PACKAGE + ":id/specialityRecyclerView']" +
                            "/android.view.ViewGroup"));
            assertTrue("Phải có ít nhất 1 chuyên khoa trong danh mục", items.size() > 0);

            recordResult(
                    tcId + " - Hiển thị danh mục chuyên khoa trên Home",
                    "Bệnh nhân đã đăng nhập",
                    "1. Mở app → 2. Xem danh mục chuyên khoa trên Home",
                    "(không có đầu vào)",
                    "Danh mục chuyên khoa hiển thị đầy đủ và đúng dữ liệu",
                    "specialityRecyclerView hiển thị với " + items.size() + " chuyên khoa",
                    "PASS", "");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            recordResult(
                    tcId + " - Hiển thị danh mục chuyên khoa trên Home",
                    "Bệnh nhân đã đăng nhập",
                    "1. Mở app → 2. Xem danh mục chuyên khoa trên Home",
                    "(không có đầu vào)",
                    "Danh mục chuyên khoa hiển thị đầy đủ và đúng dữ liệu",
                    e.getMessage(),
                    "FAIL", "Kiểm tra API /api/specialities và adapter binding");
            fail("TC02 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-07-TC03: Hiển thị danh sách bác sĩ nổi bật trên Home
    // Chức năng: HomepageActivity.loadFeaturedDoctors() → API /api/doctors
    //            → bind vào doctorRecyclerView (adapter)
    // Ý nghĩa: Card bác sĩ nổi bật phải hiển thị: tên, chuyên khoa, ảnh
    // Kỹ thuật: EP (Equivalence Partitioning)
    // -----------------------------------------------------------------
    @Test
    public void TC03_displayFeaturedDoctorListOnHome() {
        String tcId = "A-F-07-TC03";
        try {
            // Bước 1: Đảm bảo đang ở HomepageActivity
            assertTrue("Phải đang ở HomepageActivity",
                    driver.currentActivity().contains("HomepageActivity"));

            // Bước 2: Chờ RecyclerView bác sĩ load xong
            sleep(3000);

            // Bước 3: Kiểm tra RecyclerView bác sĩ nổi bật hiển thị
            boolean doctorListVisible = isElementDisplayed("doctorRecyclerView");
            assertTrue("Danh sách bác sĩ nổi bật phải hiển thị trên Home", doctorListVisible);

            // Bước 4: Kiểm tra ít nhất 1 item bác sĩ trong RecyclerView
            java.util.List<org.openqa.selenium.WebElement> doctorItems = driver.findElements(
                    org.openqa.selenium.By.xpath(
                            "//*[@resource-id='" + APP_PACKAGE + ":id/doctorRecyclerView']" +
                            "/android.view.ViewGroup"));
            assertTrue("Phải có ít nhất 1 bác sĩ trong danh sách nổi bật",
                    doctorItems.size() > 0);

            // Bước 5: Kiểm tra card bác sĩ đầu tiên có tên, chuyên khoa, ảnh
            // tvDoctorName, tvSpeciality, imgDoctor là resource-id trong item layout
            boolean hasDoctorName    = isElementDisplayed("tvDoctorName");
            boolean hasSpeciality    = isElementDisplayed("tvSpeciality");
            boolean hasDoctorImage   = isElementDisplayed("imgDoctor");

            assertTrue("Tên bác sĩ phải hiển thị trong card", hasDoctorName);
            assertTrue("Chuyên khoa phải hiển thị trong card", hasSpeciality);
            assertTrue("Ảnh bác sĩ phải hiển thị trong card", hasDoctorImage);

            recordResult(
                    tcId + " - Hiển thị danh sách bác sĩ nổi bật trên Home",
                    "Bệnh nhân đã đăng nhập; Có bác sĩ trong hệ thống",
                    "1. Mở app → 2. Xem phần bác sĩ nổi bật",
                    "(không có đầu vào)",
                    "Danh sách bác sĩ nổi bật hiển thị với tên, chuyên khoa, ảnh",
                    "doctorRecyclerView hiển thị " + doctorItems.size() + " bác sĩ với đầy đủ thông tin",
                    "PASS", "");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            recordResult(
                    tcId + " - Hiển thị danh sách bác sĩ nổi bật trên Home",
                    "Bệnh nhân đã đăng nhập; Có bác sĩ trong hệ thống",
                    "1. Mở app → 2. Xem phần bác sĩ nổi bật",
                    "(không có đầu vào)",
                    "Danh sách bác sĩ nổi bật hiển thị với tên, chuyên khoa, ảnh",
                    e.getMessage(),
                    "FAIL", "Kiểm tra API /api/doctors và resource-id: tvDoctorName, tvSpeciality, imgDoctor");
            fail("TC03 failed: " + e.getMessage());
        }
    }
}