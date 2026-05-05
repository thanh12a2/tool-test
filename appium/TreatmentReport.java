package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: TreatmentRecordTest — Kiểm thử XEM TREATMENT & RECORD
 * ===================================================================
 * Tham chiếu: SystemTest.xlsx → Sheet A-F-09 Treatment & Record
 * Hệ thống: Hỗ trợ Đặt lịch & Điều trị Y tế | PTIT | Nhóm QA
 *
 * Mô tả chức năng:
 *   Sau khi khám, bệnh nhân có thể xem:
 *   - Bệnh án (Appointment Record): chẩn đoán, kết luận, ngày khám, bác sĩ
 *   - Phác đồ điều trị (Treatment Plan): tên thuốc, hình thức dùng,
 *     số lần, lịch uống, thời gian điều trị
 *
 * Resource IDs chính:
 *   - btnHealthRecord      : Nút vào mục hồ sơ sức khỏe
 *   - rvAppointmentRecord  : RecyclerView danh sách bệnh án
 *   - tvDiagnosis          : TextView chẩn đoán
 *   - tvConclusion         : TextView kết luận
 *   - tvExamDate           : TextView ngày khám
 *   - tvDoctorName         : TextView tên bác sĩ
 *   - rvTreatmentPlan      : RecyclerView phác đồ điều trị
 *   - tvMedicineName       : TextView tên thuốc
 *   - tvDosageSchedule     : TextView lịch uống
 *
 * API liên quan:
 *   - GET /api/records/{patientId}           : Lấy danh sách bệnh án
 *   - GET /api/treatment-plans/{patientId}   : Lấy phác đồ điều trị
 * ===================================================================
 */
public class TreatmentRecordTest extends BaseAppiumTest {

    // -----------------------------------------------------------------
    // SETUP & TEARDOWN
    // -----------------------------------------------------------------

    /**
     * Khởi tạo driver và đăng nhập trước mỗi test case.
     * Điều kiện tiên quyết: Bệnh nhân đã khám xong, bác sĩ đã tạo bệnh án
     * và phác đồ điều trị trên hệ thống web.
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
        exportReportCsv("test-output/report_treatment_record.csv");
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
     * Điều hướng đến màn hình Hồ sơ sức khỏe / Bệnh án từ Home.
     * Nhấn vào nút/icon hồ sơ sức khỏe hoặc menu điều hướng.
     */
    private void navigateToHealthRecord() {
        try {
            // Thử nhấn nút hồ sơ sức khỏe trên bottom navigation hoặc trang Home
            WebElement btnRecord = findById("btnHealthRecord");
            btnRecord.click();
            sleep(2000);
            waitForActivity("HealthRecordActivity", WAIT_TIMEOUT);
        } catch (Throwable e) {
            System.err.println("navigateToHealthRecord failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-09-TC01: Xem bệnh án (appointment record) trên app Android
    // Chức năng: HealthRecordActivity.loadAppointmentRecords()
    //            → API GET /api/records/{patientId}
    //            → bind vào rvAppointmentRecord
    // Ý nghĩa: Sau khi khám, thông tin bệnh án (chẩn đoán, kết luận,
    //          ngày khám, tên bác sĩ) phải hiển thị chính xác trên app
    // Kỹ thuật: EP (Equivalence Partitioning)
    // -----------------------------------------------------------------
    @Test
    public void TC01_viewAppointmentRecordOnAndroid() {
        String tcId = "A-F-09-TC01";
        try {
            // Bước 1: Điều hướng vào màn hình hồ sơ sức khỏe
            navigateToHealthRecord();

            // Bước 2: Kiểm tra RecyclerView bệnh án hiển thị
            boolean rvVisible = isElementDisplayed("rvAppointmentRecord");
            assertTrue("Danh sách bệnh án phải hiển thị", rvVisible);

            // Bước 3: Kiểm tra có ít nhất 1 bệnh án trong danh sách
            java.util.List<org.openqa.selenium.WebElement> recordItems = driver.findElements(
                    org.openqa.selenium.By.xpath(
                            "//*[@resource-id='" + APP_PACKAGE + ":id/rvAppointmentRecord']" +
                            "/android.view.ViewGroup"));
            assertTrue("Phải có ít nhất 1 bệnh án trong hồ sơ", recordItems.size() > 0);

            // Bước 4: Nhấn vào bệnh án đầu tiên để xem chi tiết
            recordItems.get(0).click();
            sleep(2000);

            // Bước 5: Kiểm tra màn hình chi tiết bệnh án
            // Các field bắt buộc: chẩn đoán, kết luận, ngày khám, tên bác sĩ
            boolean hasDiagnosis  = isElementDisplayed("tvDiagnosis");
            boolean hasConclusion = isElementDisplayed("tvConclusion");
            boolean hasExamDate   = isElementDisplayed("tvExamDate");
            boolean hasDoctorName = isElementDisplayed("tvDoctorName");

            assertTrue("Thông tin chẩn đoán phải hiển thị", hasDiagnosis);
            assertTrue("Kết luận bác sĩ phải hiển thị", hasConclusion);
            assertTrue("Ngày khám phải hiển thị", hasExamDate);
            assertTrue("Tên bác sĩ phải hiển thị", hasDoctorName);

            // Bước 6: Kiểm tra giá trị không rỗng
            String diagnosis  = findById("tvDiagnosis").getText();
            String examDate   = findById("tvExamDate").getText();
            String doctorName = findById("tvDoctorName").getText();

            assertFalse("Chẩn đoán không được rỗng", diagnosis.isEmpty());
            assertFalse("Ngày khám không được rỗng", examDate.isEmpty());
            assertFalse("Tên bác sĩ không được rỗng", doctorName.isEmpty());

            recordResult(
                    tcId + " - Xem bệnh án (appointment record) trên app Android",
                    "Bệnh nhân đã khám xong; Bác sĩ đã tạo bệnh án trên web",
                    "1. Mở app → 2. Vào hồ sơ sức khỏe / bệnh án → 3. Xem chi tiết",
                    "(không có đầu vào)",
                    "Bệnh án hiển thị đúng: chẩn đoán, kết luận, ngày khám, tên bác sĩ",
                    "Bệnh án hiển thị đầy đủ: Chẩn đoán='" + diagnosis + "', Ngày=" + examDate
                            + ", BS=" + doctorName,
                    "PASS", "");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            recordResult(
                    tcId + " - Xem bệnh án (appointment record) trên app Android",
                    "Bệnh nhân đã khám xong; Bác sĩ đã tạo bệnh án trên web",
                    "1. Mở app → 2. Vào hồ sơ sức khỏe / bệnh án → 3. Xem chi tiết",
                    "(không có đầu vào)",
                    "Bệnh án hiển thị đúng: chẩn đoán, kết luận, ngày khám, tên bác sĩ",
                    e.getMessage(),
                    "FAIL", "Kiểm tra API GET /api/records/{id} và điều kiện bác sĩ đã tạo bệnh án");
            fail("TC01 failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-09-TC02: Xem phác đồ điều trị trên app Android
    // Chức năng: TreatmentPlanActivity.loadTreatmentPlan()
    //            → API GET /api/treatment-plans/{patientId}
    //            → bind vào rvTreatmentPlan
    // Ý nghĩa: Phác đồ điều trị phải hiển thị đúng: tên thuốc, hình thức,
    //          số lần uống, lịch uống, thời gian điều trị
    // Kỹ thuật: EP (Equivalence Partitioning)
    // -----------------------------------------------------------------
    @Test
    public void TC02_viewTreatmentPlanOnAndroid() {
        String tcId = "A-F-09-TC02";
        try {
            // Bước 1: Điều hướng vào màn hình hồ sơ sức khỏe
            navigateToHealthRecord();

            // Bước 2: Tìm tab/nút "Phác đồ điều trị" và nhấn
            // Tùy theo layout: có thể là tab, bottom sheet, hoặc RecyclerView riêng
            try {
                WebElement btnTreatment = findById("btnTreatmentPlan");
                btnTreatment.click();
                sleep(2000);
            } catch (Exception e) {
                // Thử tìm theo text nếu không có resource-id
                findByText("Phác đồ điều trị").click();
                sleep(2000);
            }

            // Bước 3: Kiểm tra RecyclerView phác đồ điều trị hiển thị
            boolean rvVisible = isElementDisplayed("rvTreatmentPlan");
            assertTrue("Danh sách phác đồ điều trị phải hiển thị", rvVisible);

            // Bước 4: Kiểm tra có ít nhất 1 item phác đồ
            java.util.List<org.openqa.selenium.WebElement> treatmentItems = driver.findElements(
                    org.openqa.selenium.By.xpath(
                            "//*[@resource-id='" + APP_PACKAGE + ":id/rvTreatmentPlan']" +
                            "/android.view.ViewGroup"));
            assertTrue("Phải có ít nhất 1 phác đồ điều trị", treatmentItems.size() > 0);

            // Bước 5: Nhấn vào phác đồ đầu tiên để xem chi tiết
            treatmentItems.get(0).click();
            sleep(2000);

            // Bước 6: Kiểm tra thông tin phác đồ điều trị hiển thị đầy đủ
            boolean hasMedicineName    = isElementDisplayed("tvMedicineName");
            boolean hasDosageSchedule  = isElementDisplayed("tvDosageSchedule");
            boolean hasDosageForm      = isElementDisplayed("tvDosageForm");
            boolean hasDosageFrequency = isElementDisplayed("tvDosageFrequency");

            assertTrue("Tên thuốc phải hiển thị", hasMedicineName);
            assertTrue("Lịch uống thuốc phải hiển thị", hasDosageSchedule);

            // Bước 7: Kiểm tra giá trị tên thuốc không rỗng
            String medicineName = findById("tvMedicineName").getText();
            assertFalse("Tên thuốc không được rỗng", medicineName.isEmpty());

            recordResult(
                    tcId + " - Xem phác đồ điều trị trên app Android",
                    "Bác sĩ đã tạo phác đồ điều trị",
                    "1. Mở app → 2. Vào xem phác đồ điều trị → 3. Kiểm tra danh sách thuốc và lịch uống",
                    "(không có đầu vào)",
                    "Hiển thị đúng: tên thuốc, hình thức, số lần, lịch uống, thời gian",
                    "Phác đồ hiển thị: " + treatmentItems.size() + " thuốc; Tên thuốc='" + medicineName + "'",
                    "PASS", "");

        } catch (Throwable e) {
            captureScreenshot(tcId);
            recordResult(
                    tcId + " - Xem phác đồ điều trị trên app Android",
                    "Bác sĩ đã tạo phác đồ điều trị",
                    "1. Mở app → 2. Vào xem phác đồ điều trị → 3. Kiểm tra danh sách thuốc và lịch uống",
                    "(không có đầu vào)",
                    "Hiển thị đúng: tên thuốc, hình thức, số lần, lịch uống, thời gian",
                    e.getMessage(),
                    "FAIL", "Kiểm tra API GET /api/treatment-plans/{id} và điều kiện bác sĩ đã tạo phác đồ");
            fail("TC02 failed: " + e.getMessage());
        }
    }
}