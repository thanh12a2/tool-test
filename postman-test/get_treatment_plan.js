// ============================================================
// Test script cho request: get treatment plan
// Kịch bản A-F-05: Xem phác đồ điều trị / đơn thuốc của bệnh nhân
// 5 test case: A-F-05-TC01 -> A-F-05-TC05
// Một số test case được thiết kế để FAIL nhằm phát hiện lỗi dữ liệu
// ============================================================

let response = {};
try {
    response = pm.response.json();
} catch (e) {
    response = {};
}

const data = Array.isArray(response.data) ? response.data : [];

// ---------- A-F-05-TC01: Status code & cấu trúc response ----------
pm.test("A-F-05-TC01 - Response trả về 200 và có cấu trúc (result, quantity, data là mảng)", function () {
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(response, "response").to.have.property("result");
    pm.expect(response.result, "result = 1").to.eql(1);
    pm.expect(response, "response").to.have.property("quantity");
    pm.expect(response.quantity, "quantity là số").to.be.a("number");
    pm.expect(response, "response").to.have.property("data");
    pm.expect(response.data, "data phải là mảng").to.be.an("array");
});

// ---------- A-F-05-TC02: quantity phải khớp với độ dài data ----------
pm.test("A-F-05-TC02 - quantity phải khớp với số lượng phần tử trong data", function () {
    pm.expect(response.quantity, "quantity === data.length").to.eql(data.length);
});

// ---------- A-F-05-TC03: Mỗi thuốc phải có đầy đủ thông tin cần thiết ----------
pm.test("A-F-05-TC03 - Mỗi thuốc có đầy đủ: id, appointment_id, name, type, times, purpose, instruction, repeat_days, repeat_time", function () {
    pm.expect(data.length, "Phác đồ phải có ít nhất 1 thuốc").to.be.greaterThan(0);
    const requiredFields = ["id", "appointment_id", "name", "type", "times", "purpose", "instruction", "repeat_days", "repeat_time"];
    data.forEach(function (med, idx) {
        const ctx = "medication[" + idx + "]";
        requiredFields.forEach(function (field) {
            pm.expect(med, ctx + "." + field).to.have.property(field);
        });
        pm.expect(med.name, ctx + ".name không rỗng").to.be.a("string").and.not.empty;
        pm.expect(med.times, ctx + ".times là số >= 1").to.be.a("number").and.to.be.at.least(1);
    });
});

// ---------- A-F-05-TC04 (dự kiến FAIL): type phải thuộc danh sách chuẩn ----------
pm.test("A-F-05-TC04 - Loại thuốc (type) phải thuộc danh sách hợp lệ: Uống, Tiêm, Bôi, Ngậm, Xịt, Nhỏ mắt, Đặt", function () {
    const validTypes = ["Uống", "Tiêm", "Bôi", "Ngậm", "Xịt", "Nhỏ mắt", "Đặt"];
    data.forEach(function (med, idx) {
        pm.expect(validTypes, "medication[" + idx + "].type = '" + med.type + "' phải thuộc danh sách hợp lệ").to.include(med.type);
    });
});

// ---------- A-F-05-TC05 (dự kiến FAIL): repeat_time phải đúng format HH:mm 24h ----------
pm.test("A-F-05-TC05 - repeat_time phải đúng định dạng HH:mm (24h), không dùng AM/PM", function () {
    const time24Regex = /^([01]\d|2[0-3]):[0-5]\d$/;
    data.forEach(function (med, idx) {
        pm.expect(med.repeat_time, "medication[" + idx + "].repeat_time = '" + med.repeat_time + "' phải format HH:mm 24h (vd: 09:00)").to.match(time24Regex);
    });
});
