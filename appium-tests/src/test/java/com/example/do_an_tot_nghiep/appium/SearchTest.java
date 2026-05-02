package com.example.do_an_tot_nghiep.appium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

/**
 * ===================================================================
 * TEST CLASS: SearchTest — Kiểm thử chức năng TÌM KIẾM (Android)
 * ===================================================================
 * Tham chiếu: SystemTest.xlsx → Sheet A-F-15 Search
 * Resource IDs: searchBar, searchView, sprFilter,
 *   doctorRecyclerView, specialityRecyclerView, serviceRecyclerView
 * API: GET /api/doctors, /api/specialities, /api/services
 * ===================================================================
 */
public class SearchTest extends BaseAppiumTest {

    @Before
    public void setUp() throws Exception {
        setupDriver();
        performLogin();
    }

    @After
    public void tearDown() {
        exportReportCsv("test-output/report_search.csv");
        teardownDriver();
    }

    private void performLogin() {
        try {
            clearAndType(findById("txtPhoneNumber"), "901234567");
            findById("btnGetVerificationCode").click();
            waitForActivity("VerificationActivity", LONG_WAIT_TIMEOUT);
            clearAndType(findById("txtVerificationCode"), "123456");
            findById("btnConfirm").click();
            waitForActivity("HomepageActivity", LONG_WAIT_TIMEOUT);
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
        }
    }

    private void navigateToSearchPage() {
        try {
            WebElement searchBar = findById("searchBar");
            searchBar.click();
            sleep(2000);
            waitForActivity("SearchpageActivity", WAIT_TIMEOUT);
        } catch (Exception e) {
            System.err.println("Navigate to search failed: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // A-F-15-TC01: Tìm kiếm theo tên bác sĩ hợp lệ
    // Chức năng: SearchpageActivity.setupEvent() → searchView query
    // Ý nghĩa: Thanh tìm kiếm phải hoạt động với filter Doctor
    // -----------------------------------------------------------------
    @Test
    public void TC01_searchByDoctorName() {
        String tcId = "A-F-15-TC01";
        try {
            navigateToSearchPage();

            // Chọn filter "Bác sĩ" (position 2 trong spinner)
            WebElement spinner = findById("sprFilter");
            spinner.click();
            sleep(1000);
            // Chọn option Doctor
            findByText("Bác sĩ").click();
            sleep(1000);

            // Nhập từ khóa tìm kiếm
            WebElement searchView = findById("searchView");
            searchView.click();
            searchView.sendKeys("Nguyễn");
            sleep(3000);

            // Kiểm tra: RecyclerView doctor phải hiển thị
            boolean hasResults = isElementDisplayed("doctorRecyclerView");

            recordResult(tcId, "setupEvent() → onQueryTextSubmit",
                    "Tìm kiếm bác sĩ theo tên — kết quả hiển thị",
                    hasResults ? "PASS" : "FAIL",
                    hasResults ? "Danh sách bác sĩ hiển thị" : "Thanh tìm kiếm không hoạt động",
                    "Ghi chú từ SystemTest: thanh tìm kiếm có thể không hoạt động");

        } catch (Exception e) {
            recordResult(tcId, "setupEvent()", "Tìm kiếm bác sĩ",
                    "FAIL", e.getMessage(), "Có thể thanh tìm kiếm bị lỗi");
        }
    }

    // -----------------------------------------------------------------
    // A-F-15-TC02: Tìm kiếm theo chuyên khoa
    // Chức năng: sendRequestWithFilterKey() → viewModel.specialityReadAll
    // Ý nghĩa: Filter chuyên khoa phải hiển thị kết quả đúng
    // -----------------------------------------------------------------
    @Test
    public void TC02_searchBySpeciality() {
        String tcId = "A-F-15-TC02";
        try {
            navigateToSearchPage();

            WebElement spinner = findById("sprFilter");
            spinner.click();
            sleep(1000);
            findByText("Chuyên khoa").click();
            sleep(2000);

            // Kiểm tra RecyclerView speciality hiển thị
            boolean hasResults = isElementDisplayed("specialityRecyclerView");

            recordResult(tcId, "sendRequestWithFilterKey() → specialityReadAll",
                    "Chọn filter chuyên khoa → danh sách hiển thị",
                    hasResults ? "PASS" : "FAIL",
                    hasResults ? "Danh sách chuyên khoa hiển thị" : "Không hiển thị",
                    "");

        } catch (Exception e) {
            recordResult(tcId, "sendRequestWithFilterKey()", "Tìm theo chuyên khoa",
                    "FAIL", e.getMessage(), "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-15-TC03: Tìm kiếm không có kết quả
    // Chức năng: onQueryTextSubmit → API trả danh sách rỗng
    // Ý nghĩa: Phải xử lý graceful khi không tìm thấy
    // -----------------------------------------------------------------
    @Test
    public void TC03_searchWithNoResults() {
        String tcId = "A-F-15-TC03";
        try {
            navigateToSearchPage();

            WebElement searchView = findById("searchView");
            searchView.click();
            searchView.sendKeys("zzz_khongton_999");
            sleep(3000);

            // App không được crash
            String activity = driver.currentActivity();
            assertNotNull("App không crash khi tìm từ khóa không tồn tại", activity);

            recordResult(tcId, "onQueryTextSubmit → API",
                    "Tìm kiếm từ khóa không tồn tại — không crash",
                    "PASS", "App xử lý gracefully, không crash",
                    "Nên hiển thị 'Không tìm thấy kết quả'");

        } catch (Exception e) {
            recordResult(tcId, "onQueryTextSubmit()", "Tìm kiếm không kết quả",
                    "FAIL", e.getMessage(), "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-15-TC04: Tìm kiếm với chuỗi rỗng
    // Chức năng: onQueryTextChange → newText.equals("") → clearFocus
    // Ý nghĩa: Clear search → hiển thị tất cả
    // -----------------------------------------------------------------
    @Test
    public void TC04_searchWithEmptyString() {
        String tcId = "A-F-15-TC04";
        try {
            navigateToSearchPage();

            // Nhập text rồi xóa
            WebElement searchView = findById("searchView");
            searchView.click();
            searchView.sendKeys("test");
            sleep(1000);
            // Xóa text
            searchView.clear();
            sleep(2000);

            // Kiểm tra: danh sách service hiển thị (default filter)
            boolean hasContent = isElementDisplayed("serviceRecyclerView")
                    || isElementDisplayed("doctorRecyclerView")
                    || isElementDisplayed("specialityRecyclerView");

            recordResult(tcId, "onQueryTextChange → clear",
                    "Xóa từ khóa → hiển thị lại tất cả",
                    hasContent ? "PASS" : "FAIL",
                    hasContent ? "Danh sách hiển thị sau khi clear" : "Không hiển thị",
                    "");

        } catch (Exception e) {
            recordResult(tcId, "onQueryTextChange()", "Tìm kiếm rỗng",
                    "FAIL", e.getMessage(), "");
        }
    }

    // -----------------------------------------------------------------
    // A-F-15-TC06: Tìm kiếm với ký tự đặc biệt / XSS
    // Chức năng: onQueryTextSubmit → API request
    // Ý nghĩa: Input phải được sanitize, không XSS, không crash
    // -----------------------------------------------------------------
    @Test
    public void TC06_searchWithXSSPayload() {
        String tcId = "A-F-15-TC06";
        try {
            navigateToSearchPage();

            WebElement searchView = findById("searchView");
            searchView.click();
            searchView.sendKeys("<script>alert(1)</script>");
            sleep(3000);

            // Kiểm tra: app không crash
            String activity = driver.currentActivity();
            assertTrue("App không crash với XSS payload",
                    activity.contains("SearchpageActivity"));

            recordResult(tcId, "onQueryTextSubmit → API",
                    "XSS payload phải bị sanitize — app không crash",
                    "PASS", "App xử lý XSS input không crash",
                    "Input nên được làm sạch phía server");

        } catch (Exception e) {
            recordResult(tcId, "onQueryTextSubmit()", "XSS injection test",
                    "FAIL", e.getMessage(), "App có thể crash với XSS");
        }
    }

    // -----------------------------------------------------------------
    // A-F-15-TC07: Kiểm tra giao diện SearchActivity đầy đủ
    // Chức năng: SearchpageActivity.setupComponent()
    // Ý nghĩa: Tất cả UI elements trang tìm kiếm phải hiển thị
    // -----------------------------------------------------------------
    @Test
    public void TC07_verifySearchUIElements() {
        String tcId = "A-F-15-TC07";
        try {
            navigateToSearchPage();

            assertTrue("Nút Back hiển thị", isElementDisplayed("btnBack"));
            assertTrue("Spinner filter hiển thị", isElementDisplayed("sprFilter"));
            assertTrue("SearchView hiển thị", isElementDisplayed("searchView"));

            recordResult(tcId, "setupComponent()",
                    "Kiểm tra giao diện Search đầy đủ",
                    "PASS", "btnBack, sprFilter, searchView đều hiển thị", "");

        } catch (Exception e) {
            recordResult(tcId, "setupComponent()", "Kiểm tra UI Search",
                    "FAIL", e.getMessage(), "");
        }
    }
}
