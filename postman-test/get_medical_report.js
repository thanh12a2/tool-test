// ============================================================
// Test script cho request: get medical report
// 5 test case: A-F-06-TC01 -> A-F-06-TC05
// Cố ý để 1-2 test case FAIL để kiểm tra khả năng bắt lỗi
// ============================================================

let response = {};
try {
    response = pm.response.json();
} catch (e) {
    response = {};
}

const data = response && response.data ? response.data : {};

// ------------------------------------------------------------
// A-F-06-TC01: Status code = 200 và response có cấu trúc cơ bản
// (Kỳ vọng: PASS)
// ------------------------------------------------------------
pm.test("A-F-06-TC01 - Xem hồ sơ khám bệnh: trả về 200 và có data hợp lệ", function () {
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(response, "response").to.have.property("result");
    pm.expect(response.result, "result").to.eql(1);
    pm.expect(response, "response").to.have.property("data");
    pm.expect(data, "data").to.be.an("object");
});

// ------------------------------------------------------------
// A-F-06-TC02: Thông tin cuộc hẹn (appointment) đầy đủ
// (Kỳ vọng: PASS)
// ------------------------------------------------------------
pm.test("A-F-06-TC02 - Hồ sơ chứa thông tin cuộc hẹn: id, bệnh nhân, ngày khám, trạng thái", function () {
    pm.expect(data, "data.appointment").to.have.property("appointment");
    const appt = data.appointment;
    pm.expect(appt, "appointment").to.be.an("object");
    pm.expect(appt.id, "appointment.id").to.be.a("number");
    pm.expect(appt.patient_name, "appointment.patient_name").to.be.a("string").and.not.empty;
    pm.expect(appt.patient_birthday, "appointment.patient_birthday").to.match(/^\d{4}-\d{2}-\d{2}$/);
    pm.expect(appt.date, "appointment.date").to.match(/^\d{4}-\d{2}-\d{2}$/);
    const validStatuses = ["processing", "cancelled", "completed", "confirmed", "pending", "accepted", "rejected", "done"];
    pm.expect(validStatuses, "appointment.status").to.include(appt.status);
});

// ------------------------------------------------------------
// A-F-06-TC03: Thông tin bác sĩ và chuyên khoa
// (Kỳ vọng: PASS)
// ------------------------------------------------------------
pm.test("A-F-06-TC03 - Hồ sơ chứa thông tin bác sĩ và chuyên khoa khám", function () {
    pm.expect(data, "data.doctor").to.have.property("doctor");
    pm.expect(data.doctor.id, "doctor.id").to.be.a("number");
    pm.expect(data.doctor.name, "doctor.name").to.be.a("string").and.not.empty;

    pm.expect(data, "data.speciality").to.have.property("speciality");
    pm.expect(data.speciality.id, "speciality.id").to.be.a("number");
    pm.expect(data.speciality.name, "speciality.name").to.be.a("string").and.not.empty;
});

// ------------------------------------------------------------
// A-F-06-TC04: Nội dung chẩn đoán (reason, description, status_before/after)
// (Kỳ vọng: FAIL - để kiểm tra khả năng bắt lỗi dữ liệu bẩn)
// Lý do: API hiện trả về status_before/status_after rất ngắn (ví dụ "đ", "ád")
//        và description chứa HTML lỗi ("&aacute;..."). Test yêu cầu độ dài
//        tối thiểu và không chứa HTML entity -> sẽ FAIL với dữ liệu hiện tại.
// ------------------------------------------------------------
pm.test("A-F-06-TC04 - Nội dung chẩn đoán phải đầy đủ và hợp lệ (không chứa HTML entity lỗi)", function () {
    pm.expect(data.reason, "reason").to.be.a("string").and.not.empty;
    pm.expect(data.reason.length, "reason length >= 5").to.be.at.least(5);

    pm.expect(data.description, "description").to.be.a("string").and.not.empty;
    pm.expect(data.description, "description không chứa HTML entity lỗi")
        .to.not.match(/&[a-z]+;/i);

    pm.expect(data.status_before, "status_before length >= 3").to.have.length.of.at.least(3);
    pm.expect(data.status_after, "status_after length >= 3").to.have.length.of.at.least(3);
});

// ------------------------------------------------------------
// A-F-06-TC05: Thời gian tạo/cập nhật hợp lệ và create_at <= update_at
// (Kỳ vọng: PASS với dữ liệu hiện tại, nhưng có thể FAIL nếu backend
//  ghi sai thứ tự create_at / update_at - đây là guard test)
// ------------------------------------------------------------
pm.test("A-F-06-TC05 - Thời gian tạo và cập nhật hồ sơ hợp lệ (create_at <= update_at)", function () {
    const dtRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;
    pm.expect(data.create_at, "create_at format").to.match(dtRegex);
    pm.expect(data.update_at, "update_at format").to.match(dtRegex);

    const createTs = new Date(data.create_at.replace(" ", "T")).getTime();
    const updateTs = new Date(data.update_at.replace(" ", "T")).getTime();
    pm.expect(createTs, "create_at <= update_at").to.be.at.most(updateTs);

    // Ràng buộc bổ sung: update_at phải KHÔNG ở tương lai so với thời điểm chạy test
    // (Có thể FAIL nếu đồng hồ server lệch với máy chạy test - đây là kịch bản
    //  test "cố tình khắt khe" để phát hiện lệch thời gian.)
    const nowTs = Date.now();
    pm.expect(updateTs, "update_at không ở tương lai").to.be.at.most(nowTs);
});
