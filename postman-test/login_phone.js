// ============================================================
// API LOGIN - POSTMAN TEST SCRIPTS
// Endpoint : POST /PTIT-Do-An-Tot-Nghiep/api/login
// Body     : phone=081853250822 | password=123456 | type=patient
// Dán toàn bộ vào: Scripts > Post-response
// ============================================================

const res = pm.response.json();

// ────────────────────────────────────────────────────────────
// A-F-01-TC01 | Kiểm tra status code trả về đúng
// ────────────────────────────────────────────────────────────
pm.test("[A-F-01-TC01] Status code phải là 200", function () {
    pm.response.to.have.status(200);
});

// ────────────────────────────────────────────────────────────
// A-F-01-TC02 | Kiểm tra response trả về đúng định dạng JSON
// ────────────────────────────────────────────────────────────
pm.test("[A-F-01-TC02] Response phải là JSON hợp lệ", function () {
    pm.response.to.be.json;
    pm.expect(res).to.be.an("object");
});

// ────────────────────────────────────────────────────────────
// A-F-01-TC03 | Kiểm tra response time không quá 2000ms
// ────────────────────────────────────────────────────────────
pm.test("[A-F-01-TC03] Response time phải nhỏ hơn 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

// ────────────────────────────────────────────────────────────
// A-F-01-TC04 | Kiểm tra response phải chứa token
// ────────────────────────────────────────────────────────────
pm.test("[A-F-01-TC04] Response phải chứa token", function () {
    pm.expect(res).to.satisfy(function (body) {
        return body.token !== undefined
            || body.accessToken !== undefined
            || body.data?.token !== undefined;
    }, "Không tìm thấy token trong response");
});

// ────────────────────────────────────────────────────────────
// A-F-01-TC05 | [BUG] Hệ thống KHÔNG được cấp token khi SĐT
//              có nhiều hơn 10 chữ số (phone=081853250822 → 12 số)
//              Kết quả mong đợi : 400 hoặc không có token
//              Kết quả thực tế  : vẫn trả token → TEST SẼ FAIL
// ────────────────────────────────────────────────────────────
pm.test("[A-F-01-TC05] [BUG] Hệ thống phải từ chối SĐT có hơn 10 chữ số", function () {
    // SĐT dùng để test: "081853250822" — dài 12 ký tự, vượt chuẩn 10 số
    const PHONE_USED = "081853250822";

    // Xác nhận đây đúng là SĐT quá 10 chữ số (tiền đề của bug)
    pm.expect(PHONE_USED.length).to.be.above(10,
        `SĐT test (${PHONE_USED}) phải có hơn 10 chữ số — hiện có ${PHONE_USED.length} số`
    );

    // ── Kịch bản lý tưởng (expected behaviour) ──────────────
    // Hệ thống phải trả về lỗi validation:
    //   • Status 400 / 422, HOẶC
    //   • Không tồn tại token trong response
    // ────────────────────────────────────────────────────────
    const hasToken =
        res.token !== undefined ||
        res.accessToken !== undefined ||
        res.data?.token !== undefined;

    const statusOk = pm.response.code === 200;

    // Nếu status 200 VÀ có token → hệ thống đã bỏ qua validation → BUG
    pm.expect(statusOk && hasToken).to.equal(false,
        `[BUG CONFIRMED] SĐT "${PHONE_USED}" có ${PHONE_USED.length} chữ số (> 10) ` +
        `nhưng hệ thống vẫn trả HTTP ${pm.response.code} kèm token — ` +
        `thiếu validation độ dài số điện thoại!`
    );
});

// ────────────────────────────────────────────────────────────
// A-F-01-TC06 | Kiểm tra thông tin user trả về khớp với input
// ────────────────────────────────────────────────────────────
pm.test("[A-F-01-TC06] Thông tin user trả về phải khớp với dữ liệu đăng nhập", function () {
    const flat = JSON.stringify(res).toLowerCase();
    pm.expect(flat).to.include("patient",  "Thiếu field type=patient trong response");
    pm.expect(flat).to.satisfy(function (s) {
        return s.includes("081853250822") || s.includes("phone") || s.includes("user");
    }, "Thiếu thông tin user trong response");
});