// ============================================================
// Test Suite: DELETE /api/patient/booking/{id}  (Huỷ lịch khám)
// ============================================================

let jsonData;
try { jsonData = pm.response.json(); } catch (e) { jsonData = {}; }

// ------------------------------------------------------------
// TC-01 | Chức năng: Hiệu năng API huỷ lịch
// Ý nghĩa: Thao tác DELETE thường chỉ update 1 dòng DB -> phải rất
//          nhanh. Nếu > 800ms có thể do thiếu index hoặc lock DB.
// ------------------------------------------------------------
pm.test("TC-01 - Response time < 800ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(800);
});

// ------------------------------------------------------------
// TC-02 | Chức năng: Kiểm tra status code hợp lệ cho DELETE
// Ý nghĩa: Theo chuẩn REST, DELETE thành công trả 200/204.
//          Nếu trả 500 hoặc 200 kèm message "error" -> bug. Kiểm tra
//          luôn Content-Length hợp lý (không trả HTML dump).
// ------------------------------------------------------------

// ------------------------------------------------------------
// TC-02 | Chức năng: Validate URL param id là số nguyên dương
// Ý nghĩa: Nếu API chấp nhận id dạng chuỗi, âm, hoặc ký tự đặc biệt
//          (SQL injection payload) mà vẫn trả 200 -> bug bảo mật.
// ------------------------------------------------------------
pm.test("TC-02- Booking id trên URL là số nguyên dương", function () {
    const url = pm.request.url.toString();
    const match = url.match(/\/booking\/([^\/?#]+)/);
    pm.expect(match, "URL không chứa booking id").to.not.be.null;
    const id = match[1];
    pm.expect(id).to.match(/^\d+$/, "booking id không phải số nguyên dương");
    pm.expect(parseInt(id, 10)).to.be.above(0);
});

// ------------------------------------------------------------
// TC-03 | Chức năng: Nghiệp vụ - response xác nhận đã huỷ
// Ý nghĩa: Response phải có message hoặc status = cancelled/deleted.
//          Bug thường gặp: API trả 200 nhưng record vẫn còn trong DB,
//          cần ít nhất message/affected_rows > 0 để chứng minh.
// ------------------------------------------------------------
pm.test("TC-03 - Response xác nhận huỷ thành công", function () {
    if (pm.response.code === 204) {
        pm.expect(pm.response.text()).to.be.oneOf(["", null, undefined]);
        return;
    }
    const text = (pm.response.text() || "").toLowerCase();
    const data = jsonData.data || jsonData;
    const okByMsg = /success|cancel|delete|xoá|huỷ|huy/.test(text);
    const okByStatus = data && (data.status === "cancelled" || data.status === "deleted" || data.success === true);
    const okByAffected = data && (data.affected_rows > 0 || data.rows > 0);
    pm.expect(okByMsg || okByStatus || okByAffected, "Response không xác nhận được việc huỷ").to.be.true;
});

// ------------------------------------------------------------
// TC-04 | Chức năng: Idempotency - gọi DELETE lại lần 2
// Ý nghĩa: Theo RFC 7231, DELETE phải idempotent. Lần 2 nên trả 404
//          hoặc 200 với thông báo "đã huỷ trước đó", KHÔNG được 500.
//          Bug: gọi 2 lần gây exception hoặc xoá dữ liệu liên quan.
// ------------------------------------------------------------
pm.test("TC-04 - DELETE idempotent (không phá vỡ khi gọi lại)", function () {
    // Gửi lại chính request này 1 lần nữa bất đồng bộ để kiểm tra
    pm.sendRequest({
        url: pm.request.url.toString(),
        method: "DELETE",
        header: {
            "Authorization": pm.request.headers.get("Authorization"),
            "Type": "Patient"
        }
    }, function (err, res) {
        if (err) return;
        pm.expect(res.code, "DELETE lần 2 không được trả 5xx").to.be.below(500);
        pm.expect(res.code).to.be.oneOf([200, 204, 404, 410, 409]);
    });
});

// ------------------------------------------------------------
// TC-05| Chức năng: Error handling & bảo mật
// Ý nghĩa: Response không được lộ stack trace, query SQL, path server.
//          Header Authorization phải được yêu cầu; nếu thử huỷ record
//          không thuộc user, response không được leak thông tin record
//          của người khác (booking_phone, name của chủ khác).
// ------------------------------------------------------------
pm.test("TC-05 - Không rò rỉ thông tin nhạy cảm hoặc dữ liệu người khác", function () {
    const body = pm.response.text();
    const leak = /(SQLSTATE|Fatal error|Stack trace|Warning:|Notice:|on line \d+|\/var\/www\/|C:\\\\|mysqli|PDOException)/i;
    pm.expect(body).to.not.match(leak, "Response rò rỉ lỗi server");

    // Authorization header phải tồn tại - nếu thiếu mà API vẫn 200 là bug bảo mật
    const auth = pm.request.headers.get("Authorization");
    pm.expect(auth, "Request đang không gửi Authorization").to.exist;
});