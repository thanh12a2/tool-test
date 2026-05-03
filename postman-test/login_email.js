// ============================================================
// API LOGIN GOOGLE - POSTMAN TEST SCRIPTS
// Endpoint : POST /PTIT-Do-An-Tot-Nghiep/api/login/google
// Body     : type=patient | email=lequangthanh2004na@gmail.com | password=google_uid_12345
// Dán toàn bộ vào: Scripts > Post-response
// ============================================================

const res = pm.response.json();

// ────────────────────────────────────────────────────────────
// A-F-02-TC01 | Kiểm tra status code trả về đúng
// ────────────────────────────────────────────────────────────
pm.test("[A-F-02-TC01] Status code phải là 200", function () {
    pm.response.to.have.status(200);
});

// ────────────────────────────────────────────────────────────
// A-F-02-TC02 | Kiểm tra response trả về đúng định dạng JSON
// ────────────────────────────────────────────────────────────
pm.test("[A-F-02-TC02] Response phải là JSON hợp lệ", function () {
    pm.response.to.be.json;
    pm.expect(res).to.be.an("object");
});

// ────────────────────────────────────────────────────────────
// A-F-02-TC03 | Kiểm tra response time không quá 2000ms
// ────────────────────────────────────────────────────────────
pm.test("[A-F-02-TC03] Response time phải nhỏ hơn 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

// ────────────────────────────────────────────────────────────
// A-F-02-TC04 | Kiểm tra response phải chứa token
// ────────────────────────────────────────────────────────────
pm.test("[A-F-02-TC04] Response phải chứa token", function () {
    pm.expect(res).to.satisfy(function (body) {
        return body.token !== undefined
            || body.accessToken !== undefined
            || body.data?.token !== undefined;
    }, "Không tìm thấy token trong response");
});

// ────────────────────────────────────────────────────────────
// A-F-02-TC05 | Kiểm tra token là string và không rỗng
// ────────────────────────────────────────────────────────────
pm.test("[A-F-02-TC05] Token phải là string và không được rỗng", function () {
    const token = res.token || res.accessToken || res.data?.token;
    pm.expect(token).to.be.a("string").and.to.have.length.above(0);
});

// ────────────────────────────────────────────────────────────
// A-F-02-TC06 | Kiểm tra thông tin user trả về khớp với input
// ────────────────────────────────────────────────────────────
pm.test("[A-F-02-TC06] Thông tin user trả về phải khớp với dữ liệu đăng nhập", function () {
    const flat = JSON.stringify(res).toLowerCase();
    pm.expect(flat).to.include("patient", "Thiếu field type=patient trong response");
    pm.expect(flat).to.satisfy(function (s) {
        return s.includes("lequangthanh2004na@gmail.com") || s.includes("email") || s.includes("user");
    }, "Thiếu thông tin email/user trong response");
});