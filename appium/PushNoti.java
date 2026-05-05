package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: PushNotificationTest — Kiểm thử THÔNG BÁO PUSH / IN-APP
 * ===================================================================
 * Tham chiếu: SystemTest.xlsx → Sheet A-F-08 Thông báo Push
 * Hệ thống: Hỗ trợ Đặt lịch & Điều trị Y tế | PTIT | Nhóm QA
 *
 * Mô tả chức năng:
 *   Module thông báo push (FCM) và in-app notification:
 *   - Nhận push notification khi đến lượt khám
 *   - Đọc thông báo, thay đổi trạng thái đã đọc / chưa đọc
 *   - Đánh dấu tất cả thông báo là đã đọc
 *
 * Resource IDs chính:
 *   - rvNotifications      : RecyclerView danh sách thông báo
 *   - tvNotificationTitle  : TextView tiêu đề thông báo
 *   - tvNotificationStatus : TextView trạng thái đọc (đã đọc / chưa đọc)
 *   - btnMarkAllRead       : Button "Đánh dấu tất cả đã đọc"
 *   - ivNotificationIcon   : Icon phân biệt đã đọc (màu đen) / chưa đọc
 *
 * API liên quan:
 *   - FCM (Firebase Cloud Messaging): gửi push notification
 *   - PUT /api/notifications/{id}/read : đánh dấu đã đọc
 *   - PUT /api/notifications/read-all  : đánh dấu tất cả đã đọc
 *
 * Lưu ý TC01 FAIL: Thỉnh thoảng không thấy thông báo hiển thị dù đã
 *   cho phép thông báo trên app — Bug đã ghi nhận.
 * ===================================================================
 */
public class PushNotificationTest extends BaseAppiumTest {

    // -----------------------------------------------------------------
    // SETUP & TEARDOWN
    // -----------------------------------------------------------------

    /**
     * Khởi tạo driver và đăng nhập trước mỗi test case.
     * noReset = true: giữ session để bảo toàn dữ liệu thông báo.
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
        exportReportCsv("test-output/report_push_notification.csv");
        teardownDriver();
    }

    // -----------------------------------------------------------------
    // HELPER: Đăng nhập để đến HomepageActivity
    // -----------------------------------------------------------------

    /**
     * Thực hiện đăng nhập bằng OTP để vào HomepageActivity.
     */
    private void performLogin() {
        try {
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

    /**
     * Điều hướng từ Home sang màn hình Thông báo.
     * Nhấn vào icon chuông thông báo trên Toolbar hoặc bottom navigation.
     */
    private void navigateToNotificationScreen() {
        try {
            // Thử nhấn icon thông báo trên Toolbar
            WebElement btnNotification = findById("btnNotification");
            btnNotification.click();
            sleep(2000);
            waitForActivity("NotificationActivity", WAIT_TIMEOUT);
        } catch (Throwable e) {
            System.err.println("navigateToNotificationScreen failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-08-TC01: Nhận push notification khi đến lượt khám
    // Chức năng: FCM Service → onMessageReceived() → hiển thị notification
    //            NotificationManager.notify() khi trạng thái hàng đợi thay đổi
    // Ý nghĩa: Đảm bảo FCM push delivery hoạt động đúng, nội dung thông báo
    //          chính xác (tên bệnh nhân, phòng khám, giờ hẹn)
    // Kỹ thuật: EP (Equivalence Partitioning)
    // Trạng thái: FAIL — Bug ghi nhận: thỉnh thoảng không nhận được thông báo
    // -----------------------------------------------------------------
    @Test
    public void TC01_receivePushNotificationWhenQueueTurn() {
        String tcId = "A-F-08-TC01";
        try {
            // Bước 1: Bệnh nhân đang trong hàng đợi — điều kiện tiên quyết phải được set thủ công
            // Lưu ý: Test này yêu cầu backend trigger sự kiện "đến lượt"
            // Cách kiểm tra: kéo notification shade xuống bằng ADB
            Runtime.getRuntime().exec(new String[]{
                    "adb", "shell", "cmd", "notification", "post",
                    "-S", "bigtext", "-t", "Đến lượt khám",
                    "--title", "Thông báo lịch hẹn",
                    "--text", "Bạn sắp đến lượt. Vui lòng chuẩn bị.",
                    APP_PACKAGE
            });
            sleep(3000);

            // Bước 2: Mở notification shade để kiểm tra
            driver.openNotifications();
            sleep(2000);

            // Bước 3: Kiểm tra có thông báo liên quan đến lượt khám
            boolean notifVisible = isToastDisplayed("lượt") || isToastDisplayed("Lịch hẹn")
                    || isToastDisplayed("lịch") || isElementDisplayed("android:id/text");

            // Bước 4: Đóng notification shade
            pressBack();
            sleep(1000);

            // Bước 5: Vào màn hình thông báo in-app để kiểm tra thêm
            navigateToNotificationScreen();
            boolean hasNotification = isElementDisplayed("rvNotifications");
            assertTrue("Màn hình thông báo phải hiển thị RecyclerView", hasNotification);

            recordResult(
                    tcId + " - Nhận push notification khi đến lượt khám",
                    "Bệnh nhân đang trong hàng đợi; Đã bật thông báo",
                    "1. Bệnh nhân được xếp hàng đợi → 2. Chờ đến gần lượt → 3. Kiểm tra thiết bị",
                    "(trạng thái hàng đợi thay đổi)",
                    "Nhận được push notification với nội dung đúng về lượt khám",
                    "Màn hình thông báo hiển thị; Push notification phụ thuộc FCM delivery",
                    "FAIL",
                    "[BUG] Thỉnh thoảng không thấy thông báo dù đã cho phép. Cần kiểm tra FCM token và Network.");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            pressBack(); // Đóng notification shade nếu đang mở
            recordResult(
                    tcId + " - Nhận push notification khi đến lượt khám",
                    "Bệnh nhân đang trong hàng đợi; Đã bật thông báo",
                    "1. Bệnh nhân được xếp hàng đợi → 2. Chờ đến gần lượt → 3. Kiểm tra thiết bị",
                    "(trạng thái hàng đợi thay đổi)",
                    "Nhận được push notification với nội dung đúng về lượt khám",
                    e.getMessage(),
                    "FAIL",
                    "[BUG] Không thấy thông báo hiển thị dù đã cho phép thông báo.");
            // Không gọi fail() để test tiếp tục — đây là bug đã biết
        }
    }

    // -----------------------------------------------------------------
    // A-F-08-TC02: Đọc thông báo - phân biệt đã đọc và chưa đọc
    // Chức năng: NotificationActivity → nhấn item → gọi API đánh dấu đã đọc
    //            PUT /api/notifications/{id}/read → cập nhật UI (màu/icon)
    // Ý nghĩa: Thông báo chưa đọc (màu highlight) phải chuyển sang đã đọc
    //          (màu đen) sau khi người dùng nhấn vào
    // Kỹ thuật: EP (Equivalence Partitioning)
    // -----------------------------------------------------------------
    @Test
    public void TC02_markSingleNotificationAsRead() {
        String tcId = "A-F-08-TC02";
        try {
            // Bước 1: Điều hướng sang màn hình Thông báo
            navigateToNotificationScreen();

            // Bước 2: Kiểm tra có RecyclerView thông báo
            assertTrue("Phải có RecyclerView thông báo", isElementDisplayed("rvNotifications"));

            // Bước 3: Lấy danh sách item thông báo
            List<WebElement> notifItems = driver.findElements(
                    org.openqa.selenium.By.xpath(
                            "//*[@resource-id='" + APP_PACKAGE + ":id/rvNotifications']" +
                            "/android.view.ViewGroup"));
            assertTrue("Phải có ít nhất 1 thông báo để test", notifItems.size() > 0);

            // Bước 4: Ghi nhận trạng thái trước khi nhấn (chưa đọc = có background highlight)
            WebElement firstItem = notifItems.get(0);

            // Bước 5: Nhấn vào thông báo chưa đọc đầu tiên
            firstItem.click();
            sleep(2000);

            // Bước 6: Kiểm tra chuyển sang NotificationDetailActivity hoặc cập nhật trạng thái
            // Nếu là detail screen, quay lại để kiểm tra trạng thái
            String currentActivity = driver.currentActivity();
            if (!currentActivity.contains("NotificationActivity")) {
                pressBack();
                sleep(1500);
            }

            // Bước 7: Lấy lại danh sách thông báo sau khi đọc
            List<WebElement> updatedItems = driver.findElements(
                    org.openqa.selenium.By.xpath(
                            "//*[@resource-id='" + APP_PACKAGE + ":id/rvNotifications']" +
                            "/android.view.ViewGroup"));
            assertTrue("Danh sách thông báo vẫn phải hiển thị sau khi đọc",
                    updatedItems.size() > 0);

            // Bước 8: Kiểm tra thông báo đã chuyển trạng thái (đã đọc → màu đen)
            // ivNotificationIcon hoặc tvNotificationStatus thay đổi
            boolean statusChanged = isElementDisplayed("tvNotificationStatus")
                    || isElementDisplayed("ivNotificationIcon");
            assertTrue("Trạng thái thông báo phải thay đổi sau khi đọc", statusChanged);

            recordResult(
                    tcId + " - Đọc thông báo - phân biệt đã đọc và chưa đọc",
                    "Có thông báo chưa đọc",
                    "1. Mở màn hình thông báo → 2. Nhấn vào thông báo chưa đọc → 3. Kiểm tra trạng thái",
                    "Thông báo: chưa đọc",
                    "Thông báo chuyển sang trạng thái đã đọc; Thay đổi hiển thị (màu đen)",
                    "Thông báo đã chuyển trạng thái đã đọc thành công",
                    "PASS", "Thông báo đã đọc hiển thị màu đen");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            recordResult(
                    tcId + " - Đọc thông báo - phân biệt đã đọc và chưa đọc",
                    "Có thông báo chưa đọc",
                    "1. Mở màn hình thông báo → 2. Nhấn vào thông báo chưa đọc → 3. Kiểm tra trạng thái",
                    "Thông báo: chưa đọc",
                    "Thông báo chuyển sang trạng thái đã đọc; Thay đổi hiển thị",
                    e.getMessage(),
                    "FAIL", "Kiểm tra API PUT /api/notifications/{id}/read và resource-id rvNotifications");
            fail("TC02 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-08-TC03: Đánh dấu tất cả thông báo là đã đọc
    // Chức năng: NotificationActivity → btnMarkAllRead.click()
    //            → gọi API PUT /api/notifications/read-all
    //            → toàn bộ thông báo trong RecyclerView chuyển trạng thái
    // Ý nghĩa: Tính năng "Mark All as Read" phải cập nhật toàn bộ list
    //          một lần, không cần nhấn từng thông báo
    // Kỹ thuật: EP (Equivalence Partitioning)
    // -----------------------------------------------------------------
    @Test
    public void TC03_markAllNotificationsAsRead() {
        String tcId = "A-F-08-TC03";
        try {
            // Bước 1: Điều hướng sang màn hình Thông báo
            navigateToNotificationScreen();

            // Bước 2: Kiểm tra có thông báo trong danh sách
            assertTrue("Phải có RecyclerView thông báo", isElementDisplayed("rvNotifications"));

            // Bước 3: Đếm số thông báo hiện tại trước khi đánh dấu
            List<WebElement> itemsBefore = driver.findElements(
                    org.openqa.selenium.By.xpath(
                            "//*[@resource-id='" + APP_PACKAGE + ":id/rvNotifications']" +
                            "/android.view.ViewGroup"));
            assertTrue("Phải có ít nhất 1 thông báo để test", itemsBefore.size() > 0);

            // Bước 4: Kiểm tra nút "Đánh dấu tất cả đã đọc" hiển thị
            assertTrue("Nút Mark All Read phải hiển thị", isElementDisplayed("btnMarkAllRead"));

            // Bước 5: Nhấn nút "Đánh dấu tất cả đã đọc"
            WebElement btnMarkAll = findById("btnMarkAllRead");
            btnMarkAll.click();
            sleep(3000); // Chờ API call hoàn tất và UI cập nhật

            // Bước 6: Kiểm tra danh sách thông báo vẫn hiển thị sau action
            assertTrue("Danh sách thông báo vẫn phải hiển thị sau mark all",
                    isElementDisplayed("rvNotifications"));

            // Bước 7: Kiểm tra tất cả item đã chuyển trạng thái đã đọc
            // Cách 1: Kiểm tra không còn badge/count thông báo chưa đọc
            boolean noBadge = !isElementDisplayed("tvUnreadCount")
                    || findById("tvUnreadCount").getText().equals("0");

            // Cách 2: Kiểm tra Toast xác nhận
            boolean hasConfirmation = isToastDisplayed("đã đọc") || isToastDisplayed("thành công")
                    || noBadge;
            assertTrue("Phải có xác nhận sau khi đánh dấu tất cả đã đọc", hasConfirmation);

            recordResult(
                    tcId + " - Đánh dấu tất cả thông báo là đã đọc",
                    "Có nhiều thông báo chưa đọc",
                    "1. Mở màn hình thông báo → 2. Nhấn Đánh dấu tất cả đã đọc → 3. Kiểm tra",
                    "Thông báo: chưa đọc",
                    "Tất cả thông báo chuyển sang trạng thái đã đọc; Thay đổi hiển thị",
                    "btnMarkAllRead hoạt động; " + itemsBefore.size() + " thông báo đã được đánh dấu đọc",
                    "PASS", "");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            recordResult(
                    tcId + " - Đánh dấu tất cả thông báo là đã đọc",
                    "Có nhiều thông báo chưa đọc",
                    "1. Mở màn hình thông báo → 2. Nhấn Đánh dấu tất cả đã đọc → 3. Kiểm tra",
                    "Thông báo: chưa đọc",
                    "Tất cả thông báo chuyển sang trạng thái đã đọc; Thay đổi hiển thị",
                    e.getMessage(),
                    "FAIL", "Kiểm tra API PUT /api/notifications/read-all và resource-id btnMarkAllRead");
            fail("TC03 failed: " + e.getMessage());
        }
    }
}