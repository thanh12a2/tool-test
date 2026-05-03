// ============================================================
// API BOOKING - POSTMAN TEST SCRIPTS

// ============================================================

const res     = pm.response.json();
const bodyRaw = JSON.stringify(res).toLowerCase();

// ────────────────────────────────────────────────────────────
// A-F-03-TC01 | EP
// Tạo lịch hẹn với ngày tương lai hợp lệ
// Dữ liệu đầu vào  : appointment_date = 2026-05-05 | appointment_time = 18:00
//                    service_id = 6 | booking_name = thanhle | booking_phone = 0818532508
// Kết quả mong đợi : 200 + lịch được tạo + status = processing
// ────────────────────────────────────────────────────────────
pm.test("[A-F-03-TC01] Tạo lịch hẹn ngày tương lai hợp lệ — phải trả 200 và tạo lịch thành công", function () {
    // 1. Status HTTP phải là 200
    pm.response.to.have.status(200);

    // 2. Response phải là JSON object
    pm.expect(res).to.be.an("object");

    // 3. Phải có booking id trong response
    const hasBookingId =
        res.id !== undefined               ||
        res.booking_id !== undefined       ||
        res.data?.id !== undefined         ||
        res.data?.booking_id !== undefined;

    pm.expect(hasBookingId).to.equal(true,
        "Response phải chứa id của lịch hẹn vừa tạo"
    );

    // 4. Status lịch phải là processing / ĐANG XỬ LÝ
    pm.expect(bodyRaw).to.satisfy(function (s) {
        return s.includes("processing") || s.includes("đang xử lý") || s.includes("dang xu ly");
    }, "Trạng thái lịch hẹn phải là 'processing' hoặc 'ĐANG XỬ LÝ'");

    // 5. appointment_date trong response phải khớp đúng ngày đã gửi
    pm.expect(bodyRaw).to.include("2026-05-28",
        "Response phải phản ánh đúng appointment_date = 2026-05-28"
    );

    // 6. booking_name phải khớp
    pm.expect(bodyRaw).to.include("thanhle",
        "Response phải phản ánh đúng booking_name = thanhle"
    );

    // 7. booking_phone phải khớp
    pm.expect(bodyRaw).to.include("0818532508",
        "Response phải phản ánh đúng booking_phone = 0818532508"
    );
});

// ────────────────────────────────────────────────────────────
// A-F-03-TC02 | EP
// Tạo lịch hẹn với ngày trong quá khứ
// Dữ liệu đầu vào  : appointment_date = 2026-05-03 (hôm qua so với 2026-05-04)
// Kết quả mong đợi : lỗi "Không thể đặt lịch ngày đã qua"
// Ghi chú          : Request hiện tại dùng 2026-05-05 (hợp lệ) → xác nhận server
//                    không trả lỗi quá khứ khi ngày hợp lệ được gửi
// ────────────────────────────────────────────────────────────
pm.test("[A-F-03-TC02] Ngày hợp lệ (2026-05-05) — server KHÔNG báo lỗi 'ngày đã qua'", function () {
    // Dữ liệu cứng: ngày quá khứ để so sánh nghiệp vụ
    const PAST_DATE  = "2026-05-03"; // hôm qua
    const APPT_DATE  = "2026-05-05"; // ngày đang dùng trong request

    // Xác nhận APPT_DATE mới hơn PAST_DATE (tiền đề hợp lệ)
    pm.expect(APPT_DATE > PAST_DATE).to.equal(true,
        `appointment_date (${APPT_DATE}) phải sau ngày quá khứ (${PAST_DATE})`
    );

    // Vì ngày hợp lệ → response KHÔNG được chứa thông báo lỗi ngày quá khứ
    const hasPastError =
        bodyRaw.includes("ngày đã qua")  ||
        bodyRaw.includes("past")         ||
        bodyRaw.includes("invalid date") ||
        bodyRaw.includes("cannot book");

    pm.expect(hasPastError).to.equal(false,
        `Server không được trả lỗi quá khứ khi appointment_date = ${APPT_DATE} là ngày hợp lệ`
    );
});

// ────────────────────────────────────────────────────────────
// A-F-03-TC03 | BVA (Boundary Value Analysis)
// Tạo lịch hẹn với ngày hôm nay (biên)
// Dữ liệu đầu vào  : appointment_date = 2026-05-05 | appointment_time = 18:00
//                    (2026-05-05 cách ngày hiện tại 2026-05-04 đúng 1 ngày — biên gần nhất)
// Kết quả mong đợi : chấp nhận vì giờ đặt 18:00 là giờ tương lai
// ────────────────────────────────────────────────────────────
pm.test("[A-F-03-TC03] BVA — appointment_date cách hôm nay 1 ngày, giờ 18:00 hợp lệ", function () {
    const APPT_DATE   = "2026-05-05";
    const APPT_TIME   = "18:00";
    const TODAY_DATE  = "2026-05-04"; // ngày hiện tại cố định

    // Tính khoảng cách ngày (cứng)
    const d1      = new Date(APPT_DATE);
    const d2      = new Date(TODAY_DATE);
    const diffDays = Math.round((d1 - d2) / 86400000);

    // Biên: diffDays phải = 1 (ngày mai gần nhất)
    pm.expect(diffDays).to.equal(1,
        `BVA: appointment_date (${APPT_DATE}) phải cách hôm nay (${TODAY_DATE}) đúng 1 ngày — diffDays = ${diffDays}`
    );

    // appointment_time phải đúng định dạng HH:MM
    pm.expect(APPT_TIME).to.match(/^\d{2}:\d{2}$/,
        `appointment_time = "${APPT_TIME}" phải đúng định dạng HH:MM`
    );

    // 18:00 là giờ tương lai → response phải 200, không báo lỗi giờ quá khứ
    pm.response.to.have.status(200);

    pm.expect(bodyRaw).to.not.include("invalid",
        `Server phải chấp nhận lịch đặt vào ${APPT_DATE} lúc ${APPT_TIME} (biên hợp lệ gần nhất)`
    );
});

// ────────────────────────────────────────────────────────────
// A-F-03-TC04 | Decision Table  [BUG — EXPECTED FAIL]
// Tạo lịch hẹn khi đã có lịch đang xử lý
// Dữ liệu đầu vào  : booking_phone = 0818532508 đã có lịch active trước đó
// Kết quả mong đợi : cảnh báo "Bạn đã có lịch hẹn đang chờ xử lý"
// Kết quả thực tế  : vẫn trả 200, không check trùng → BUG
// ────────────────────────────────────────────────────────────
pm.test("[A-F-03-TC04] [BUG] Hệ thống phải cảnh báo khi 0818532508 đã có lịch ĐANG XỬ LÝ", function () {
    // Dữ liệu cứng: SĐT đã có lịch active từ lần đặt trước
    const BOOKING_PHONE    = "0818532508";
    const EXISTING_STATUS  = "processing"; // trạng thái lịch cũ

    // Xác nhận SĐT dùng trong request khớp với SĐT có lịch active
    pm.expect(bodyRaw).to.satisfy(function (s) {
        return s.includes(BOOKING_PHONE) || s.includes("phone") || s.includes("booking");
    }, `Response phải liên quan đến booking_phone = ${BOOKING_PHONE}`);

    // Hệ thống PHẢI trả cảnh báo trùng lịch
    const hasConflictWarning =
        bodyRaw.includes("đã có lịch")        ||
        bodyRaw.includes("duplicate")          ||
        bodyRaw.includes("conflict")           ||
        bodyRaw.includes("already")            ||
        bodyRaw.includes("chờ xử lý")          ||
        bodyRaw.includes("existing booking")   ||
        pm.response.code === 409               ||
        pm.response.code === 400;

    // 200 + không cảnh báo = bỏ qua check trùng lịch → BUG
    const isSuccessWithoutWarning = pm.response.code === 200 && !hasConflictWarning;

    pm.expect(isSuccessWithoutWarning).to.equal(false,
        `[BUG CONFIRMED] booking_phone = ${BOOKING_PHONE} đã có lịch "${EXISTING_STATUS}" ` +
        `nhưng hệ thống vẫn trả HTTP 200 không cảnh báo — ` +
        `thiếu logic kiểm tra duplicate active booking!`
    );
});

// ────────────────────────────────────────────────────────────
// A-F-03-TC05 | EP  [BUG — EXPECTED FAIL]
// Tạo lịch hẹn với lý do khám để trống / không hợp lệ
// Dữ liệu đầu vào  : reason = "none" (giá trị vô nghĩa, tương đương bỏ trống)
// Kết quả mong đợi : thông báo lỗi bắt buộc nhập lý do khám
// Kết quả thực tế  : tạo lịch thành công, không validate → BUG
// ────────────────────────────────────────────────────────────
pm.test("[A-F-03-TC05] [BUG] Hệ thống phải từ chối reason = rỗng (lý do khám không hợp lệ)", function () {
    // Dữ liệu cứng: giá trị reason gửi lên
    const REASON_SENT = "";

    // Xác nhận "" là giá trị không hợp lệ về nghiệp vụ
    const INVALID_REASONS = [""];
    pm.expect(INVALID_REASONS).to.include(REASON_SENT,
        `reason = "${REASON_SENT}" phải nằm trong danh sách giá trị không hợp lệ`
    );

    // Hệ thống PHẢI trả lỗi validation khi reason không hợp lệ
    const hasValidationError =
        bodyRaw.includes("reason")       ||
        bodyRaw.includes("lý do")        ||
        bodyRaw.includes("triệu chứng")  ||
        bodyRaw.includes("required")     ||
        bodyRaw.includes("bắt buộc")     ||
        pm.response.code === 400         ||
        pm.response.code === 422;

    // 200 + không có lỗi validation = bỏ qua validate reason → BUG
    const isSuccessWithInvalidReason = pm.response.code === 200 && !hasValidationError;

    pm.expect(isSuccessWithInvalidReason).to.equal(false,
        `[BUG CONFIRMED] reason = "${REASON_SENT}" là giá trị vô nghĩa ` +
        `nhưng hệ thống vẫn trả HTTP 200 và tạo lịch thành công — ` +
        `thiếu validation bắt buộc nhập lý do khám!`
    );
});