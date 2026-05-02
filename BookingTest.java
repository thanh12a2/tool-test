package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: BookingTest — Kiểm thử TẠO / HỦY LỊCH HẸN (Android)
 * ===================================================================
 * Tham chiếu: SystemTest.xlsx → Sheet A-F-07, A-F-09
 * Resource IDs: txtBookingName, txtBookingPhone, txtPatientName,
 *   rdMale, rdFemale, txtPatientBirthday, txtPatientAddress,
 *   txtPatientReason, txtAppointmentDate, txtAppointmentTime,
 *   btnConfirm, btnCancel, txtBookingStatus
 * API: POST /api/patient/booking, DELETE /api/patient/booking/{id}
 * ===================================================================
 */
public class BookingTest extends BaseAppiumTest {

    @Before
    public void setUp() throws Exception {
        setupDriver();
        // Đăng nhập trước (giả lập OTP test mode)
        performLogin();
    }

    @After
    public void tearDown() {
        rollbackToHome();
        exportReportCsv("test-output/report_booking.csv");
        teardownDriver();
    }

    /** Helper: thực hiện đăng nhập để vào Homepage */
    private void performLogin() {
        try {
            WebElement txtPhone = findById("txtPhoneNumber");
            clearAndType(txtPhone, "901234567");
            findById("btnGetVerificationCode").click();
            waitForActivity("VerificationActivity", LONG_WAIT_TIMEOUT);
            clearAndType(findById("txtVerificationCode"), "123456");
            findById("btnConfirm").click();
            waitForActivity("HomepageActivity", LONG_WAIT_TIMEOUT);
        } catch (Exception e) {
            System.err.println("Login failed in setup: " + e.getMessage());
        }
    }

    /** Helper: điều hướng vào trang đặt lịch qua chuyên khoa đầu tiên */
    private void navigateToBookingPage() {
        try {
            // Click vào chuyên khoa đầu tiên trên Homepage
            WebElement speciality = findById("recyclerViewSpeciality");
            speciality.findElements(org.openqa.selenium.By.className("android.view.ViewGroup"))
                      .get(0).click();
            sleep(2000);
            // Click vào dịch vụ đầu tiên
            waitForActivity("SpecialitypageActivity", WAIT_TIMEOUT);
            // Tìm và click dịch vụ
            sleep(2000);
        } catch (Exception e) {
            System.err.println("Navigate to booking failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-07-TC01: Tạo lịch hẹn với ngày tương lai hợp lệ
    // Chức năng: BookingFragment1.sendBookingCreate() → API bookingCreate
    // Ý nghĩa: Luồng tạo lịch hẹn chính phải hoạt động end-to-end
    // -----------------------------------------------------------------
    @Test
    public void TC01_createBookingWithValidFutureDate() {
        String tcId = "A-F-07-TC01";
        try {
            navigateToBookingPage();
            waitForActivity("BookingpageActivity", WAIT_TIMEOUT);

            // Điền form đặt lịch
            clearAndType(findById("txtBookingName"), "Nguyen Van Nam");
            clearAndType(findById("txtBookingPhone"), "0375158622");
            clearAndType(findById("txtPatientName"), "Nguyen Van Nam");
            findById("rdMale").click(); // Chọn giới tính Nam

            // Chọn ngày mai (phải dùng DatePicker vì focusable=false)
            WebElement txtDate = findById("txtAppointmentDate");
            txtDate.click();
            sleep(1000);
            // Chấp nhận ngày đã chọn trên DatePicker dialog
            findByText("OK").click();
            sleep(500);

            clearAndType(findById("txtPatientReason"), "Đau bụng kiểm tra");

            // Nhấn Xác nhận
            findById("btnConfirm").click();
            sleep(5000);

            // Kiểm tra: chuyển sang BookingFragment3 (upload ảnh)
            // hoặc hiển thị Toast thành công
            boolean success = isElementDisplayed("btnUpload")
                    || isToastDisplayed("Success") || isToastDisplayed("Thành công");

            if (success) {
                recordResult(tcId, "sendBookingCreate()",
                        "Tạo lịch hẹn ngày tương lai → trạng thái ĐANG XỬ LÝ",
                        "PASS", "Lịch hẹn được tạo thành công", "");
                // ROLLBACK: Hủy lịch vừa tạo
                rollbackCancelBooking();
            } else {
                recordResult(tcId, "sendBookingCreate()",
                        "Tạo lịch hẹn", "FAIL", "Không chuyển sang bước tiếp theo", "");
                fail("TC01 failed: booking not created");
            }
        } catch (Exception e) {
            recordResult(tcId, "sendBookingCreate()", "Tạo lịch hẹn",
                    "FAIL", e.getMessage(), "");
            fail("TC01: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-07-TC05: Tạo lịch hẹn bỏ trống trường bắt buộc (bookingName)
    // Chức năng: areMandatoryFieldsFilledUp() validation
    // Ý nghĩa: Kiểm tra client-side validation trường bắt buộc
    // -----------------------------------------------------------------
    @Test
    public void TC05_createBookingWithEmptyMandatoryField() {
        String tcId = "A-F-07-TC05";
        try {
            navigateToBookingPage();
            waitForActivity("BookingpageActivity", WAIT_TIMEOUT);

            // Bỏ trống tên người đặt lịch
            WebElement txtBookingName = findById("txtBookingName");
            txtBookingName.clear();

            // Điền các trường khác
            clearAndType(findById("txtBookingPhone"), "0375158622");
            clearAndType(findById("txtPatientName"), "Test Patient");
            clearAndType(findById("txtPatientReason"), "Test reason");

            findById("btnConfirm").click();
            sleep(2000);

            // Kiểm tra: Dialog lỗi phải hiển thị (areMandatoryFieldsFilledUp returns false)
            boolean hasDialog = isElementDisplayed("btnOK");
            assertTrue("Phải hiển thị dialog cảnh báo khi bỏ trống trường bắt buộc", hasDialog);

            // Đóng dialog
            if (hasDialog) findById("btnOK").click();

            recordResult(tcId, "areMandatoryFieldsFilledUp()",
                    "Bỏ trống trường bắt buộc → hiển thị dialog cảnh báo",
                    "PASS", "Dialog 'Bạn chưa điền đầy đủ thông tin' hiển thị", "");

        } catch (Exception e) {
            recordResult(tcId, "areMandatoryFieldsFilledUp()",
                    "Validate trường bắt buộc", "FAIL", e.getMessage(), "");
            fail("TC05: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-07-TC06: Kiểm tra SĐT bỏ trống → validate bắt buộc
    // Chức năng: areMandatoryFieldsFilledUp()
    // Ý nghĩa: SĐT người đặt là mandatory
    // -----------------------------------------------------------------
    @Test
    public void TC06_createBookingWithEmptyPhone() {
        String tcId = "A-F-07-TC06";
        try {
            navigateToBookingPage();
            waitForActivity("BookingpageActivity", WAIT_TIMEOUT);

            clearAndType(findById("txtBookingName"), "Test Name");
            WebElement txtPhone = findById("txtBookingPhone");
            txtPhone.clear();
            clearAndType(findById("txtPatientName"), "Patient");

            findById("btnConfirm").click();
            sleep(2000);

            boolean hasDialog = isElementDisplayed("btnOK");
            assertTrue("Phải hiển thị cảnh báo khi bỏ trống SĐT", hasDialog);
            if (hasDialog) findById("btnOK").click();

            recordResult(tcId, "areMandatoryFieldsFilledUp()",
                    "Bỏ trống SĐT → dialog cảnh báo",
                    "PASS", "Validation chặn submit thành công", "");

        } catch (Exception e) {
            recordResult(tcId, "areMandatoryFieldsFilledUp()",
                    "Validate SĐT", "FAIL", e.getMessage(), "");
            fail("TC06: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-09-TC01: Hủy lịch hẹn ở trạng thái "processing"
    // Chức năng: BookingpageInfoActivity.sendCancelRequest() → API DELETE
    // Ý nghĩa: Bệnh nhân có thể hủy lịch đang xử lý
    // -----------------------------------------------------------------
    @Test
    public void TC09_cancelBookingProcessing() {
        String tcId = "A-F-09-TC01";
        try {
            // Vào tab lịch sử booking (bottom nav → booking history)
            // Giả sử đã có lịch processing
            findById("shortcutAppointment").click();
            sleep(2000);

            // Tìm lịch hẹn đang xử lý
            boolean hasCancelBtn = isElementDisplayed("btnCancel");

            if (hasCancelBtn) {
                WebElement btnCancel = findById("btnCancel");
                assertTrue("Nút Hủy phải hiển thị cho lịch processing",
                        btnCancel.isDisplayed());
                btnCancel.click();
                sleep(1000);

                // Confirm dialog
                WebElement btnOK = findById("btnOK");
                assertNotNull("Dialog xác nhận hủy phải hiển thị", btnOK);
                btnOK.click();
                sleep(3000);

                // Kiểm tra: nút Cancel biến mất sau khi hủy
                boolean cancelGone = !isElementDisplayed("btnCancel");

                recordResult(tcId, "sendCancelRequest()",
                        "Hủy lịch hẹn processing → trạng thái HỦY",
                        cancelGone ? "PASS" : "FAIL",
                        cancelGone ? "Nút Cancel biến mất sau hủy" : "Nút Cancel vẫn hiển thị",
                        "");
            } else {
                recordResult(tcId, "sendCancelRequest()",
                        "Hủy lịch processing", "SKIP",
                        "Không tìm thấy lịch hẹn processing để test", "");
            }
        } catch (Exception e) {
            recordResult(tcId, "sendCancelRequest()",
                    "Hủy lịch hẹn", "FAIL", e.getMessage(), "");
            fail("TC09-01: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-09-TC02: Không thể hủy lịch hẹn đã xác nhận (verified)
    // Chức năng: printBookingInfo() → status != "processing" → GONE
    // Ý nghĩa: Nút Hủy phải ẩn cho lịch đã xác nhận
    // -----------------------------------------------------------------
    @Test
    public void TC09_02_cannotCancelVerifiedBooking() {
        String tcId = "A-F-09-TC02";
        try {
            // Tìm lịch hẹn đã xác nhận
            findById("shortcutAppointment").click();
            sleep(2000);

            // Nếu tìm được lịch verified, kiểm tra nút Cancel không hiển thị
            WebElement status = findById("txtBookingStatus");
            String statusText = status.getText();

            if (statusText != null && (statusText.contains("Đã xác nhận")
                    || statusText.contains("verified"))) {
                boolean cancelHidden = !isElementDisplayed("btnCancel");
                assertTrue("Nút Hủy phải ẩn cho lịch đã xác nhận", cancelHidden);

                recordResult(tcId, "printBookingInfo() → btnCancel.GONE",
                        "Lịch đã xác nhận → không có nút Hủy",
                        "PASS", "Nút Cancel ẩn đúng cho trạng thái verified", "");
            } else {
                recordResult(tcId, "printBookingInfo()",
                        "Kiểm tra nút hủy ẩn", "SKIP",
                        "Không tìm thấy lịch verified để test", "");
            }
        } catch (Exception e) {
            recordResult(tcId, "printBookingInfo()",
                    "Không hủy được lịch verified", "FAIL", e.getMessage(), "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-07-TC10: Kiểm tra giao diện form đặt lịch hiển thị đầy đủ
    // Chức năng: BookingFragment1.setupComponent()
    // Ý nghĩa: Tất cả input fields trên form phải hiển thị
    // -----------------------------------------------------------------
    @Test
    public void TC10_verifyBookingFormUIElements() {
        String tcId = "A-F-07-TC10";
        try {
            navigateToBookingPage();
            waitForActivity("BookingpageActivity", WAIT_TIMEOUT);

            assertTrue("txtBookingName hiển thị", isElementDisplayed("txtBookingName"));
            assertTrue("txtBookingPhone hiển thị", isElementDisplayed("txtBookingPhone"));
            assertTrue("txtPatientName hiển thị", isElementDisplayed("txtPatientName"));
            assertTrue("rdPatientGender hiển thị", isElementDisplayed("rdPatientGender"));
            assertTrue("txtPatientBirthday hiển thị", isElementDisplayed("txtPatientBirthday"));
            assertTrue("txtPatientAddress hiển thị", isElementDisplayed("txtPatientAddress"));
            assertTrue("txtPatientReason hiển thị", isElementDisplayed("txtPatientReason"));
            assertTrue("txtAppointmentDate hiển thị", isElementDisplayed("txtAppointmentDate"));
            assertTrue("txtAppointmentTime hiển thị", isElementDisplayed("txtAppointmentTime"));
            assertTrue("btnConfirm hiển thị", isElementDisplayed("btnConfirm"));

            recordResult(tcId, "setupComponent()",
                    "Kiểm tra giao diện form đặt lịch đầy đủ",
                    "PASS", "Tất cả 10 input fields hiển thị", "");

        } catch (Exception e) {
            recordResult(tcId, "setupComponent()",
                    "Kiểm tra UI form đặt lịch", "FAIL", e.getMessage(), "");
            fail("TC10: " + e.getMessage());
        }
    }
}
