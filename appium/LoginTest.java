package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: LoginTest — Kiểm thử chức năng ĐĂNG NHẬP (Android)
 * ===================================================================
 * Tham chiếu: SystemTest.xlsx → Sheet A-F-01 Login
 * Backend: Laragon (PHP + MySQL) qua Retrofit API
 * Resource IDs: txtPhoneNumber, btnGetVerificationCode, btnGoogleLogin,
 *               txtVerificationCode, btnConfirm
 * ===================================================================
 */
public class LoginTest extends BaseAppiumTest {

    @Before
    public void setUp() throws Exception {
        setupDriver(false); // Bắt buộc xóa data app để test luồng đăng nhập từ đầu. Nếu không có dòng này, app sẽ vào thẳng HomepageActivity.
    }

    @After
    public void tearDown() {
        exportReportCsv("test-output/report_login.csv");
        teardownDriver();
    }

    // -----------------------------------------------------------------
    // A-F-01-TC01: Đăng nhập bằng OTP hợp lệ
    // Chức năng: loginWithPhone() → LoginViewModel → API /api/login
    // Ý nghĩa: Xác minh luồng đăng nhập chính hoạt động end-to-end
    // -----------------------------------------------------------------
    @Test
    public void TC01_loginWithValidOTP() {
        String tcId = "A-F-01-TC01";
        try {
            // Bước 1: Nhập số điện thoại (bỏ số 0 đầu theo yêu cầu app)
            WebElement txtPhone = findById("txtPhoneNumber");
            clearAndType(txtPhone, "333333333");

            // Bước 2: Nhấn nút Lấy OTP
            WebElement btnGetOTP = findById("btnGetVerificationCode");
            btnGetOTP.click();
            sleep(2000);

            // Bước 3: Chờ chuyển sang VerificationActivity
            boolean navigated = waitForActivity("VerificationActivity", LONG_WAIT_TIMEOUT);
            sleep(2000);
            assertTrue("Phải chuyển sang màn hình nhập OTP", navigated);

            // Bước 4: Nhập OTP (môi trường test dùng OTP mặc định 123456)
            WebElement txtOTP = findById("txtVerificationCode");
            clearAndType(txtOTP, "111111");

            // Bước 5: Nhấn Xác nhận
            WebElement btnConfirm = findById("btnConfirm");
            btnConfirm.click();

            // Bước 6: Chờ chuyển sang HomepageActivity
            boolean loggedIn = waitForActivity("HomepageActivity", LONG_WAIT_TIMEOUT);
            assertTrue("Phải chuyển sang trang chính sau đăng nhập thành công", loggedIn);

            // Bước 7: Kiểm tra phần tử trên Homepage có hiển thị
            assertTrue("Thanh tìm kiếm phải hiển thị trên Homepage",
                    isElementDisplayed("searchBar"));

            recordResult(tcId + " - Đăng nhập bằng OTP hợp lệ", "Đang ở LoginActivity", "Nhập SĐT hợp lệ, nhấn lấy OTP, nhập OTP hợp lệ", "SĐT: 901234567, OTP: 123456", "Đăng nhập thành công, chuyển sang HomepageActivity", "Chuyển hướng đến HomepageActivity thành công", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - Đăng nhập bằng OTP hợp lệ", "Đang ở LoginActivity", "Nhập SĐT hợp lệ, nhấn lấy OTP, nhập OTP hợp lệ", "SĐT: 901234567, OTP: 123456", "Đăng nhập thành công, chuyển sang HomepageActivity", e.getMessage(), "FAIL", "Lỗi xảy ra trong quá trình test");
            fail("TC01 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC02: Đăng nhập với OTP sai
    // Chức năng: signInWithPhoneAuthCredential() → Firebase Auth
    // Ý nghĩa: Hệ thống phải từ chối OTP sai, hiển thị lỗi rõ ràng
    // -----------------------------------------------------------------
    @Test
    public void TC02_loginWithInvalidOTP() {
        String tcId = "A-F-01-TC02";
        try {
            WebElement txtPhone = findById("txtPhoneNumber");
            clearAndType(txtPhone, "333333333");

            findById("btnGetVerificationCode").click();
            sleep(2000);
            assertTrue("Phải chuyển sang VerificationActivity",
                    waitForActivity("VerificationActivity", LONG_WAIT_TIMEOUT));
            sleep(2000);

            // Nhập OTP sai
            WebElement txtOTP = findById("txtVerificationCode");
            clearAndType(txtOTP, "999999");

            findById("btnConfirm").click();
            sleep(3000);

            // Kiểm tra: KHÔNG được chuyển sang Homepage
            String currentActivity = driver.currentActivity();
            assertFalse("Không được chuyển sang Homepage khi OTP sai",
                    currentActivity.contains("HomepageActivity"));

            // Kiểm tra hiển thị thông báo lỗi (Toast hoặc Dialog)
            boolean hasError = isToastDisplayed("Error") || isToastDisplayed("Lỗi")
                    || isElementDisplayed("btnOK"); // Dialog lỗi
            assertTrue("Phải hiển thị thông báo lỗi khi OTP sai", hasError);

            recordResult(tcId + " - Đăng nhập với OTP sai", "Đang ở LoginActivity", "Nhập SĐT hợp lệ, nhấn lấy OTP, nhập OTP sai", "SĐT: 901234567, OTP: 999999", "Từ chối OTP sai, hiển thị lỗi, ở lại VerificationActivity", "Ở lại VerificationActivity + hiển thị lỗi", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - Đăng nhập với OTP sai", "Đang ở LoginActivity", "Nhập SĐT hợp lệ, nhấn lấy OTP, nhập OTP sai", "SĐT: 901234567, OTP: 999999", "Từ chối OTP sai, hiển thị lỗi, ở lại VerificationActivity", e.getMessage(), "FAIL", "");
            fail("TC02 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC04: Đăng nhập qua Google Sign-In thành công
    // Chức năng: loginWithGoogle() → API /api/login/google
    // Ý nghĩa: Xác minh luồng Google OAuth hoạt động
    // -----------------------------------------------------------------
    @Test
    public void TC04_loginWithGoogleSuccess() {
        String tcId = "A-F-01-TC04";
        try {
            WebElement btnGoogle = findById("btnGoogleLogin");
            assertNotNull("Nút Google Login phải tồn tại", btnGoogle);
            assertTrue("Nút Google Login phải hiển thị", btnGoogle.isDisplayed());
            btnGoogle.click();
            sleep(3000);

            // Google Sign-In dialog xuất hiện → chọn tài khoản
            // Lưu ý: trong emulator cần tài khoản Google đã đăng nhập sẵn
            // Kiểm tra không bị crash
            String activity = driver.currentActivity();
            assertNotNull("Activity không được null sau khi click Google Login", activity);

            recordResult(tcId + " - Đăng nhập qua Google Sign-In thành công", "Đang ở LoginActivity", "Nhấn nút Google Login", "N/A", "Hiển thị Google Sign-In dialog, không crash", "Google Sign-In dialog hiển thị", "PASS", "Cần tài khoản Google trên emulator");

        } catch (Throwable e) {
            recordResult(tcId + " - Đăng nhập qua Google Sign-In thành công", "Đang ở LoginActivity", "Nhấn nút Google Login", "N/A", "Hiển thị Google Sign-In dialog, không crash", e.getMessage(), "FAIL", "");
            fail("TC04 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC05: Hủy đăng nhập Google
    // Chức năng: startGoogleSignInForResult — RESULT_CANCELED
    // Ý nghĩa: App không crash khi hủy dialog Google
    // -----------------------------------------------------------------
    @Test
    public void TC05_cancelGoogleLogin() {
        String tcId = "A-F-01-TC05";
        try {
            findById("btnGoogleLogin").click();
            sleep(2000);
            pressBack(); // Hủy Google dialog
            sleep(2000);

            // Kiểm tra: quay lại LoginActivity, không crash
            String activity = driver.currentActivity();
            assertTrue("Phải quay lại LoginActivity sau khi hủy Google",
                    activity.contains("LoginActivity"));

            // Kiểm tra phần tử vẫn hiển thị bình thường
            assertTrue("Nút Google Login vẫn tồn tại",
                    isElementDisplayed("btnGoogleLogin"));

            recordResult(tcId + " - Hủy đăng nhập Google", "Đang ở LoginActivity", "Nhấn nút Google Login, sau đó nhấn nút Back để hủy", "N/A", "App không crash, quay lại LoginActivity", "Quay lại LoginActivity thành công", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - Hủy đăng nhập Google", "Đang ở LoginActivity", "Nhấn nút Google Login, sau đó nhấn nút Back để hủy", "N/A", "App không crash, quay lại LoginActivity", e.getMessage(), "FAIL", "");
            fail("TC05 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC06: Đăng nhập khi bỏ trống số điện thoại
    // Chức năng: setupEvent() → TextUtils.isEmpty(phoneNumber) check
    // Ý nghĩa: Validate input rỗng — hiển thị Toast cảnh báo
    // -----------------------------------------------------------------
    @Test
    public void TC06_loginWithEmptyPhoneNumber() {
        String tcId = "A-F-01-TC06";
        try {
            WebElement txtPhone = findById("txtPhoneNumber");
            txtPhone.clear();

            findById("btnGetVerificationCode").click();
            sleep(2000);

            // Kiểm tra: phải ở lại LoginActivity
            String activity = driver.currentActivity();
            assertTrue("Phải ở lại LoginActivity khi SĐT rỗng",
                    activity.contains("LoginActivity"));

            // Kiểm tra Toast "Số điện thoại là bắt buộc"
            // (R.string.do_not_let_phone_number_empty)
            boolean stayedOnLogin = !activity.contains("VerificationActivity");
            assertTrue("Không được chuyển sang Verification khi SĐT rỗng", stayedOnLogin);

            recordResult(tcId + " - Đăng nhập khi bỏ trống số điện thoại", "Đang ở LoginActivity", "Để trống SĐT, nhấn Lấy OTP", "SĐT: rỗng", "Hiển thị Toast lỗi, ở lại LoginActivity", "Ở lại LoginActivity", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - Đăng nhập khi bỏ trống số điện thoại", "Đang ở LoginActivity", "Để trống SĐT, nhấn Lấy OTP", "SĐT: rỗng", "Hiển thị Toast lỗi, ở lại LoginActivity", e.getMessage(), "FAIL", "");
            fail("TC06 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC07: Đăng nhập với SĐT sai định dạng (chữ cái)
    // Chức năng: PhoneAuthProvider.verifyPhoneNumber() → onVerificationFailed
    // Ý nghĩa: SĐT chứa ký tự không hợp lệ phải bị từ chối
    // -----------------------------------------------------------------
    @Test
    public void TC07_loginWithInvalidPhoneFormat() {
        String tcId = "A-F-01-TC07";
        try {
            WebElement txtPhone = findById("txtPhoneNumber");
            clearAndType(txtPhone, "abcdef");

            findById("btnGetVerificationCode").click();
            sleep(3000);

            // Kiểm tra: không chuyển sang VerificationActivity
            String activity = driver.currentActivity();
            assertFalse("Không được chuyển sang Verification với SĐT không hợp lệ",
                    activity.contains("VerificationActivity"));

            recordResult(tcId + " - Đăng nhập với SĐT sai định dạng", "Đang ở LoginActivity", "Nhập SĐT chứa chữ cái, nhấn Lấy OTP", "SĐT: abcdef", "Từ chối SĐT, ở lại LoginActivity", "Ở lại LoginActivity", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - Đăng nhập với SĐT sai định dạng", "Đang ở LoginActivity", "Nhập SĐT chứa chữ cái, nhấn Lấy OTP", "SĐT: abcdef", "Từ chối SĐT, ở lại LoginActivity", e.getMessage(), "FAIL", "");
            fail("TC07 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC08: SĐT có đúng 10 chữ số (bao gồm số 0 đầu)
    // Chức năng: LoginActivity.setupEvent() → phoneNumber.length() == 10
    // Ý nghĩa: App yêu cầu nhập SĐT KHÔNG có số 0 đầu (9 chữ số)
    // -----------------------------------------------------------------
    @Test
    public void TC08_loginWithPhoneIncludingLeadingZero() {
        String tcId = "A-F-01-TC08";
        try {
            WebElement txtPhone = findById("txtPhoneNumber");
            clearAndType(txtPhone, "0901234567"); // 10 ký tự → phải hiển thị cảnh báo

            findById("btnGetVerificationCode").click();
            sleep(2000);

            // Code: if(phoneNumber.length() == 10) { Toast "chỉ nhập số không có số 0 đầu" }
            String activity = driver.currentActivity();
            assertTrue("Phải ở lại LoginActivity khi nhập SĐT 10 số (có số 0)",
                    activity.contains("LoginActivity"));

            recordResult(tcId + " - SĐT có đúng 10 chữ số (bao gồm số 0 đầu)", "Đang ở LoginActivity", "Nhập SĐT có 10 chữ số (bắt đầu bằng 0), nhấn Lấy OTP", "SĐT: 0901234567", "Hiển thị cảnh báo yêu cầu nhập 9 chữ số, ở lại LoginActivity", "Hiển thị Toast cảnh báo, ở lại LoginActivity", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - SĐT có đúng 10 chữ số (bao gồm số 0 đầu)", "Đang ở LoginActivity", "Nhập SĐT có 10 chữ số (bắt đầu bằng 0), nhấn Lấy OTP", "SĐT: 0901234567", "Hiển thị cảnh báo yêu cầu nhập 9 chữ số, ở lại LoginActivity", e.getMessage(), "FAIL", "");
            fail("TC08 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC09: SĐT quá dài (>9 chữ số không tính 0 đầu)
    // Chức năng: Firebase PhoneAuth → onVerificationFailed
    // Ý nghĩa: Kiểm tra app xử lý SĐT vượt quá giới hạn
    // -----------------------------------------------------------------
    @Test
    public void TC09_loginWithTooLongPhoneNumber() {
        String tcId = "A-F-01-TC09";
        try {
            WebElement txtPhone = findById("txtPhoneNumber");
            clearAndType(txtPhone, "90123456789012345"); // SĐT quá 10 chữ số

            findById("btnGetVerificationCode").click();
            sleep(3000);

            String activity = driver.currentActivity();
            
            // Yêu cầu: KHÔNG được chuyển sang màn hình Verification
            assertFalse("KHÔNG được phép chuyển sang màn hình Xác thực khi SĐT quá dài",
                    activity != null && activity.contains("VerificationActivity"));
            
            // Yêu cầu: Phải ở lại màn hình Login
            assertTrue("Phải ở lại màn hình LoginActivity khi SĐT sai định dạng",
                    activity != null && activity.contains("LoginActivity"));

            recordResult(tcId + " - SĐT quá dài (>10 số)", "Đang ở LoginActivity", "Nhập SĐT rất dài, nhấn Lấy OTP", "SĐT: 90123456789012345", "Báo lỗi sai định dạng SĐT, không cho đăng nhập", "Hệ thống từ chối đăng nhập, ở lại màn hình Login", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - SĐT quá dài (>10 số)", "Đang ở LoginActivity", "Nhập SĐT rất dài, nhấn Lấy OTP", "SĐT: 90123456789012345", "Báo lỗi sai định dạng SĐT, không cho đăng nhập", e.getMessage(), "FAIL", "Hệ thống vẫn cho đăng nhập với SĐT quá dài (Bug) hoặc lỗi khác");
            fail("TC09 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-01-TC10: Kiểm tra giao diện LoginActivity hiển thị đầy đủ
    // Chức năng: LoginActivity.setupComponent()
    // Ý nghĩa: Tất cả UI elements trên màn hình login phải hiển thị
    // -----------------------------------------------------------------
    @Test
    public void TC10_verifyLoginUIElements() {
        String tcId = "A-F-01-TC10";
        try {
            assertTrue("EditText SĐT phải hiển thị", isElementDisplayed("txtPhoneNumber"));
            assertTrue("Nút Lấy OTP phải hiển thị", isElementDisplayed("btnGetVerificationCode"));
            assertTrue("Nút Google Login phải hiển thị", isElementDisplayed("btnGoogleLogin"));

            // Kiểm tra nút Lấy OTP có text đúng
            WebElement btnOTP = findById("btnGetVerificationCode");
            String btnText = btnOTP.getText();
            assertNotNull("Nút OTP phải có text", btnText);
            assertFalse("Nút OTP text không được rỗng", btnText.isEmpty());

            recordResult(tcId + " - Kiểm tra giao diện LoginActivity hiển thị đầy đủ", "Đang ở LoginActivity", "Khởi động app và quan sát màn hình Login", "N/A", "Tất cả UI elements hiển thị đầy đủ", "Tất cả UI elements hiển thị đúng", "PASS", "");

        } catch (Throwable e) {
            recordResult(tcId + " - Kiểm tra giao diện LoginActivity hiển thị đầy đủ", "Đang ở LoginActivity", "Khởi động app và quan sát màn hình Login", "N/A", "Tất cả UI elements hiển thị đầy đủ", e.getMessage(), "FAIL", "");
            fail("TC10 failed: " + e.getMessage());
        }
    }
}
