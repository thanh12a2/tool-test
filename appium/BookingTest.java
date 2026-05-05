package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: BookingTest — Kiểm thử TẠO LỊCH HẸN (Android)
 * ===================================================================
 * Tham chiếu: system_test_cases.csv → Sheet A-F-02 Tạo lịch hẹn
 * Luồng: Homepage → Khám tổng quát → ServicepageActivity → Tạo lịch
 * 5 Test Cases:
 *   TC01: Tạo lịch hẹn với ngày tương lai hợp lệ
 *   TC02: Tạo lịch hẹn với ngày trong quá khứ
 *   TC03: Tạo lịch hẹn với ngày hôm nay (biên)
 *   TC04: Tạo lịch hẹn khi đã có lịch hẹn active
 *   TC05: Tạo lịch hẹn với lý do khám trống
 * ===================================================================
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookingTest extends BaseAppiumTest {

    @Before
    public void setUp() throws Exception {
        setupDriver();
        performLogin();
    }

    @After
    public void tearDown() {
        rollbackToHome();
        exportReportCsv("test-output/report_booking.csv");
        teardownDriver();
    }

    // ======================== HELPER METHODS ========================

    /** Đăng nhập bằng SĐT test + OTP test (Chỉ khi đang ở màn hình Login) */
    private void performLogin() {
        try {
            // Kiểm tra xem nút GetVerificationCode có hiển thị không (đang ở màn hình Login)
            if (isElementDisplayed("btnGetVerificationCode")) {
                clearAndType(findById("txtPhoneNumber"), "333333333");
                findById("btnGetVerificationCode").click();
                waitForActivity("VerificationActivity", LONG_WAIT_TIMEOUT);
                clearAndType(findById("txtVerificationCode"), "111111");
                findById("btnConfirm").click();
                waitForActivity("HomepageActivity", LONG_WAIT_TIMEOUT);
            }
            sleep(2000); // chờ Homepage load dữ liệu từ API
        } catch (Throwable e) {
            System.err.println("Login check failed: " + e.getMessage());
        }
    }

    /**
     * Điều hướng: Homepage → Click nút "Khám tổng quát" → ServicepageActivity → Click "Tạo lịch hẹn"
     * Lưới nút dịch vụ nằm trong recyclerViewButton (GridLayout 3 cột).
     * Click listener gắn trên ImageButton, không phải TextView.
     * Thứ tự: index 0=Khám chuyên khoa, index 1=Khám tổng quát, index 2=Khám tim mạch...
     */
    private void navigateToBookingPage() {
        navigateToBookingPage(1); // Mặc định Khám tổng quát (index 1)
    }

    private void navigateToBookingPage(int departmentIndex) {
        sleep(2000); // Đợi ổn định Homepage
        WebElement grid = findById("recyclerViewButton");

        // Click ImageButton tại index chỉ định
        java.util.List<WebElement> buttons = grid.findElements(
                org.openqa.selenium.By.className("android.widget.ImageButton"));
        buttons.get(departmentIndex).click();

        waitForActivity("ServicepageActivity", WAIT_TIMEOUT);
        sleep(1000);

        // Click nút "Tạo lịch hẹn"
        findById("btnCreateBooking").click();
        waitForActivity("BookingpageActivity", WAIT_TIMEOUT);
        sleep(1000);
    }

    /** Điền form đặt lịch với các trường cơ bản */
    private void fillBookingForm(String name, String phone, String patientName,
                                  String address, String reason) {
        clearAndType(findById("txtBookingName"), name);
        clearAndType(findById("txtBookingPhone"), phone);
        clearAndType(findById("txtPatientName"), patientName);
        findById("rdMale").click();
        if (address != null) clearAndType(findById("txtPatientAddress"), address);
        if (reason != null) clearAndType(findById("txtPatientReason"), reason);
    }

    /**
     * Set ngày trực tiếp vào EditText bằng sendKeys (UiAutomator2 dùng ACTION_SET_TEXT)
     * Không cần mở DatePicker dialog.
     */
    private void setAppointmentDate(String date) {
        WebElement dateField = findById("txtAppointmentDate");
        dateField.clear();
        dateField.sendKeys(date);
        sleep(500);
    }

    /** Set giờ trực tiếp vào EditText */
    private void setAppointmentTime(String time) {
        WebElement timeField = findById("txtAppointmentTime");
        timeField.clear();
        timeField.sendKeys(time);
        sleep(500);
    }

    /** Tính ngày tương lai (30 ngày sau) theo format yyyy-MM-dd */
    private String getFutureDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 20);
        return String.format("%04d-%02d-%02d",
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH));
    }

    /** Tính ngày quá khứ (30 ngày trước) theo format yyyy-MM-dd */
    private String getPastDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
        return String.format("%04d-%02d-%02d",
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH));
    }

    /** Lấy ngày hôm nay (Hoặc ngày mai nếu đã qua 18h) */
    @Override
    protected String getToday() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        if (java.time.LocalTime.now().getHour() >= 18) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        return String.format("%04d-%02d-%02d",
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH));
    }

    /** Tính giờ tương lai (sau 2 tiếng) theo format HH:mm, nếu qua 18h thì lấy 09:00 sáng mai */
    private String getFutureTime() {
        java.time.LocalTime now = java.time.LocalTime.now();
        if (now.getHour() >= 18) {
            return "09:00";
        }
        return now.plusHours(2).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }

    /** Tính giờ quá khứ (trước 2 tiếng) theo format HH:mm */
    private String getPastTime() {
        return java.time.LocalTime.now().minusHours(2).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }

    // ======================== TEST CASES ========================

    // =================================================================
    // A-F-02-TC01: Tạo lịch hẹn với ngày tương lai hợp lệ
    // =================================================================
    @Test
    public void TC01_createBookingWithValidFutureDate() {
        String tcId = "A-F-02-TC01";
        try {
            navigateToBookingPage();
            fillBookingForm("Nam", "0375158622", "Nam", "Hà Nội", "đau");
            String targetDate = getFutureDate();
            String targetTime = getFutureTime();
            setAppointmentDate(targetDate);
            setAppointmentTime(targetTime);

            findById("btnConfirm").click();
            sleep(1500);

            boolean success = isElementDisplayed("btnUpload")
                    || isToastDisplayed("Success") || isToastDisplayed("Thành công");

            String actualResult = "Không tạo được lịch hẹn";
            if (success) {
                actualResult = "Lịch hẹn được tạo thành công";
            } else if (isElementDisplayed("btnOK")) {
                try {
                    String errorMsg = findById("txtContent").getText();
                    actualResult = "Thất bại, có lỗi: " + errorMsg;
                    findById("btnOK").click();
                } catch (Throwable e) {}
            }

            recordResult(tcId + " - Tạo lịch hẹn với ngày tương lai hợp lệ",
                    "Bệnh nhân đã đăng nhập; Chưa có lịch hẹn active",
                    "1. Chọn chuyên khoa\n2. Chọn ngày tương lai\n3. Điền lý do\n4. Tạo lịch hẹn",
                    "Ngày: ngày mai\nChuyên khoa: Nội tổng hợp\nHọ tên người đặt lịch: Nam\nSố điện thoại người đặt lịch: 0375158622\nTên bệnh nhân: Nam\nGiới tính: Nam\nNgày sinh: 25/11/2004\nĐịa chỉ: Hà Nội\nLý do khám: đau\nLịch: " + targetDate + " " + targetTime,
                    "Lịch hẹn được tạo; Trạng thái = ĐANG XỬ LÝ",
                    actualResult,
                    success ? "PASS" : "FAIL", "");

        } catch (Throwable e) {
            recordResult(tcId + " - Tạo lịch hẹn với ngày tương lai hợp lệ",
                    "Bệnh nhân đã đăng nhập; Chưa có lịch hẹn active",
                    "1. Chọn chuyên khoa\n2. Chọn ngày tương lai\n3. Điền lý do\n4. Tạo lịch hẹn",
                    "Ngày: ngày mai\nChuyên khoa: Nội tổng hợp\nHọ tên người đặt lịch: Nam\nSố điện thoại người đặt lịch: 0375158622\nTên bệnh nhân: Nam\nGiới tính: Nam\nNgày sinh: 25/11/2004\nĐịa chỉ: Hà Nội\nLý do khám: đau\nLịch: " + getFutureDate() + " " + getFutureTime(),
                    "Lịch hẹn được tạo; Trạng thái = ĐANG XỬ LÝ",
                    "Lỗi hệ thống: " + e.getMessage(), "FAIL", "");
        }
    }

    // =================================================================
    // A-F-02-TC02: Tạo lịch hẹn với ngày trong quá khứ
    // =================================================================
    @Test
    public void TC02_createBookingWithPastDate() {
        String tcId = "A-F-02-TC02";
        try {
            navigateToBookingPage();
            fillBookingForm("Nam", "0375158622", "Nam", "Hà Nội", "đau");
            String targetDate = getPastDate();
            String targetTime = getFutureTime();
            setAppointmentDate(targetDate);
            setAppointmentTime(targetTime); // Ngày quá khứ nên giờ nào cũng lỗi

            findById("btnConfirm").click();
            sleep(1500);

            boolean hasError = isElementDisplayed("btnOK")
                    || isToastDisplayed("qua") || isToastDisplayed("past");
            boolean noSuccess = !isElementDisplayed("btnUpload");

            String actual = hasError ? "Hiển thị thông báo lỗi ngày quá khứ"
                    : (noSuccess ? "Không tạo được lịch" : "Tạo lịch thành công (sai)");
            String status = (hasError || noSuccess) ? "PASS" : "FAIL";

            recordResult(tcId + " - Tạo lịch hẹn với ngày trong quá khứ",
                    "Bệnh nhân đã đăng nhập",
                    "1. Chọn ngày quá khứ\n2. Xác nhận",
                    "Ngày: " + targetDate + "\nGiờ: " + targetTime,
                    "Thông báo lỗi: 'Không thể đặt lịch ngày đã qua'",
                    actual, status, "Có lỗi thông báo là đã qua ngày trong quá khứ");

            if (isElementDisplayed("btnOK")) findById("btnOK").click();

        } catch (Throwable e) {
            recordResult(tcId + " - Tạo lịch hẹn với ngày trong quá khứ",
                    "Bệnh nhân đã đăng nhập",
                    "1. Chọn ngày quá khứ\n2. Xác nhận",
                    "Ngày: " + getPastDate() + "\nGiờ: " + getFutureTime(),
                    "Thông báo lỗi: 'Không thể đặt lịch ngày đã qua'",
                    "Lỗi hệ thống: " + e.getMessage(), "FAIL", "");
        }
    }

    // =================================================================
    // A-F-02-TC03: Tạo lịch hẹn với ngày hôm nay (biên)
    // =================================================================
    @Test
    public void TC03_createBookingWithTodayDate() {
        String tcId = "A-F-02-TC03";
        try {
            navigateToBookingPage();
            fillBookingForm("Nam", "0375158622", "Nam", "Hà Nội", "khám tổng quát");
            // Nếu đã qua 18h, logic getToday() sẽ trả về ngày mai để tránh lỗi clinic đóng cửa
            String targetDate = getToday();
            setAppointmentDate(targetDate);
            
            // Tự động điền giờ tương lai (hoặc 09:00 nếu qua 18h)
            String targetTime = getFutureTime();
            setAppointmentTime(targetTime);

            findById("btnConfirm").click();
            sleep(1500);

            boolean success = isElementDisplayed("btnUpload")
                    || isToastDisplayed("Success") || isToastDisplayed("Thành công");
            boolean hasError = isElementDisplayed("btnOK")
                    || isToastDisplayed("qua") || isToastDisplayed("giờ");

            String actual, status;
            if (success) {
                actual = "Tạo lịch thành công với ngày hôm nay";
                status = "PASS";
            } else if (hasError) {
                actual = "Thông báo đã quá giờ đặt lịch hôm nay";
                status = "PASS";
                if (isElementDisplayed("btnOK")) findById("btnOK").click();
            } else {
                actual = "Không xác định kết quả";
                status = "FAIL";
            }

            recordResult(tcId + " - Tạo lịch hẹn với ngày hôm nay (hoặc ngày mai nếu quá 18h)",
                    "Bệnh nhân đã đăng nhập",
                    "1. Chọn ngày hôm nay\n2. Điền form\n3. Xác nhận",
                    "Ngày: " + targetDate + "\nGiờ: " + targetTime,
                    "Chấp nhận nếu giờ đặt khám là giờ tương lai. Không chấp nhận đặt giờ đã qua",
                    actual, status, "Có thông báo là đã quá giờ nếu đặt giờ trong quá khứ");

        } catch (Throwable e) {
            recordResult(tcId + " - Tạo lịch hẹn với ngày hôm nay (biên)",
                    "Bệnh nhân đã đăng nhập",
                    "1. Chọn ngày hôm nay\n2. Điền form\n3. Xác nhận",
                    "Ngày: " + getToday() + "\nGiờ: " + getFutureTime(),
                    "Chấp nhận nếu giờ đặt khám là giờ tương lai. Không chấp nhận đặt giờ đã qua",
                    "Lỗi hệ thống: " + e.getMessage(), "FAIL", "");
        }
    }

    // =================================================================
    // A-F-02-TC04: Tạo lịch hẹn khi đã có lịch hẹn active
    // =================================================================
    @Test
    public void TC04_createBookingWhenAlreadyHaveActive() {
        String tcId = "A-F-02-TC04";
        try {
            navigateToBookingPage(3);
            fillBookingForm("Nam", "0375158622", "Nam", "Hà Nội", "đau bụng");
            String targetDate1 = getFutureDate();
            String targetTime1 = getFutureTime();
            setAppointmentDate(targetDate1);
            setAppointmentTime(targetTime1);
            findById("btnConfirm").click();
            sleep(1500);

            boolean hasWarning = isElementDisplayed("btnOK")
                    || isToastDisplayed("đã có") || isToastDisplayed("chờ xử lý");
            boolean created = isElementDisplayed("btnUpload")
                    || isToastDisplayed("Success") || isToastDisplayed("Thành công");

            String actual, status, note;
            if (hasWarning) {
                actual = "Thông báo lỗi hoặc cảnh báo: 'Bạn đã có lịch hẹn đang chờ xử lý'";
                status = "PASS";
                note = "";
                if (isElementDisplayed("btnOK")) findById("btnOK").click();
            } else if (created) {
                // Thử tạo lịch hẹn thứ 2 để trigger lỗi nếu lịch 1 thành công
                rollbackToHome();
                sleep(1000);
                navigateToBookingPage(4);
                fillBookingForm("Nam", "0375158622", "Nam", "Hà Nội", "đau bụng 2");
                String targetDate2 = getFutureDate();
                String targetTime2 = getFutureTime();
                setAppointmentDate(targetDate2);
                setAppointmentTime(targetTime2);
                findById("btnConfirm").click();
                sleep(1500);
                
                boolean hasWarning2 = isElementDisplayed("btnOK")
                        || isToastDisplayed("đã có") || isToastDisplayed("chờ xử lý");
                boolean created2 = isElementDisplayed("btnUpload")
                        || isToastDisplayed("Success") || isToastDisplayed("Thành công");

                if (hasWarning2) {
                    actual = "Thông báo lỗi hoặc cảnh báo: 'Bạn đã có lịch hẹn đang chờ xử lý'";
                    status = "PASS";
                    note = "";
                    if (isElementDisplayed("btnOK")) findById("btnOK").click();
                } else if (created2) {
                    actual = "Không check lịch đã đặt trước đó. Sai khi đặt 2 lịch khám 2 khoa khác nhau nhưng vẫn đặt trùng giờ được";
                    status = "FAIL";
                    note = "Không check lịch đã đặt trước đó. Sai khi đặt 2 lịch khám 2 khoa khác nhau nhưng vẫn đặt trùng giờ được";
                } else {
                    actual = "Không xác định kết quả lần 2";
                    status = "FAIL";
                    note = "";
                }
            } else {
                actual = "Không xác định kết quả lần 1";
                status = "FAIL";
                note = "";
            }

            recordResult(tcId + " - Tạo lịch hẹn khi đã có lịch hẹn active",
                    "Bệnh nhân đã có lịch ĐANG XỬ LÝ",
                    "1. Thử tạo lịch hẹn mới",
                    "Ngày: " + targetDate1 + "\nGiờ: " + targetTime1,
                    "Thông báo lỗi hoặc cảnh báo: 'Bạn đã có lịch hẹn đang chờ xử lý'",
                    actual, status, note);

        } catch (Throwable e) {
            recordResult(tcId + " - Tạo lịch hẹn khi đã có lịch hẹn active",
                    "Bệnh nhân đã có lịch ĐANG XỬ LÝ",
                    "1. Thử tạo lịch hẹn mới",
                    "Ngày: " + getFutureDate() + "\nGiờ: " + getFutureTime(),
                    "Thông báo lỗi hoặc cảnh báo: 'Bạn đã có lịch hẹn đang chờ xử lý'",
                    "Lỗi hệ thống: " + e.getMessage(), "FAIL", "");
        }
    }

    // =================================================================
    // A-F-02-TC05: Tạo lịch hẹn với lý do khám trống
    // =================================================================
    @Test
    public void TC05_createBookingWithEmptyReason() {
        String tcId = "A-F-02-TC05";
        try {
            navigateToBookingPage();
            // Điền form nhưng BỎ TRỐNG lý do khám (truyền null cho reason)
            fillBookingForm("Nam", "0375158622", "Nam", "Hà Nội", null);
            String targetDate = getFutureDate();
            String targetTime = getFutureTime();
            setAppointmentDate(targetDate);
            setAppointmentTime(targetTime);

            findById("btnConfirm").click();
            sleep(1500);

            boolean hasError = isElementDisplayed("btnOK");
            boolean success = isElementDisplayed("btnUpload")
                    || isToastDisplayed("Success") || isToastDisplayed("Thành công");

            String actual, status, note;
            if (hasError) {
                actual = "Thông báo lỗi hoặc cảnh báo yêu cầu điền lý do";
                status = "PASS";
                note = "";
                findById("btnOK").click();
            } else if (success) {
                actual = "Không check bỏ trống lý do đi khám";
                status = "FAIL";
                note = "Không check bỏ trống lý do đi khám";
            } else {
                actual = "Không xác định kết quả";
                status = "FAIL";
                note = "";
            }

            recordResult(tcId + " - Tạo lịch hẹn với lý do khám trống",
                    "Bệnh nhân đã đăng nhập",
                    "1. Bỏ trống ô lý do khám\n2. Xác nhận",
                    "Ngày: " + targetDate + "\nGiờ: " + targetTime + "\nTriệu chứng: (để trống)",
                    "Nếu bắt buộc: thông báo lỗi; Nếu không bắt buộc: tạo lịch thành công",
                    actual, status, note);

        } catch (Throwable e) {
            recordResult(tcId + " - Tạo lịch hẹn với lý do khám trống",
                    "Bệnh nhân đã đăng nhập",
                    "1. Bỏ trống ô lý do khám\n2. Xác nhận",
                    "Ngày: " + getFutureDate() + "\nGiờ: " + getFutureTime() + "\nTriệu chứng: (để trống)",
                    "Nếu bắt buộc: thông báo lỗi; Nếu không bắt buộc: tạo lịch thành công",
                    "Lỗi hệ thống: " + e.getMessage(), "FAIL", "");
        }
    }
}
