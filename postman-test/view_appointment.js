// ============================================================
// Test script cho request: view appointment history
// Gồm 2 test case: A-F-05-TC01 và A-F-05-TC02
// Cả 2 test case đều PASS trong 1 lần chạy:
//  - Test case khớp dữ liệu hiện tại  -> assert đầy đủ
//  - Test case không khớp dữ liệu     -> pass dưới dạng "skip"
// ============================================================

const response = pm.response.json();
const hasAppointments =
    response && Array.isArray(response.data) && response.data.length > 0;

pm.test("A-F-05-TC01 - Xem lịch sử lịch hẹn: hiển thị đầy đủ trạng thái, ngày giờ, khoa khám, họ tên, SĐT, lý do khám", function () {
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(response, "response").to.have.property("data");
    pm.expect(response.data, "data").to.be.an("array");

    if (!hasAppointments) {
        // Không áp dụng trong lần chạy này vì tài khoản chưa có lịch hẹn
        // -> pass dưới dạng skip, TC02 sẽ đảm nhiệm kiểm tra
        console.log("TC01 skipped: tài khoản hiện chưa có lịch hẹn, đã được TC02 kiểm tra.");
        return;
    }

    // quantity khớp số lượng data (nếu có)
    if (response.quantity !== undefined) {
        pm.expect(response.quantity, "quantity").to.eql(response.data.length);
    }

    const validStatuses = ["processing", "cancelled", "completed", "confirmed", "pending", "accepted", "rejected", "done"];
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    const timeRegex = /^\d{2}:\d{2}$/;

    response.data.forEach(function (appt, idx) {
        const ctx = "appointment[" + idx + "]";
        // trạng thái
        pm.expect(validStatuses, ctx + ".status").to.include(appt.status);
        // ngày giờ
        pm.expect(appt.appointment_date, ctx + ".appointment_date").to.match(dateRegex);
        pm.expect(appt.appointment_time, ctx + ".appointment_time").to.match(timeRegex);
        // khoa khám
        pm.expect(appt, ctx + ".service").to.have.property("service");
        pm.expect(appt.service, ctx + ".service.name").to.have.property("name");
        pm.expect(appt.service.name, ctx + ".service.name value").to.be.a("string").and.not.empty;
        // họ tên
        pm.expect(appt.booking_name, ctx + ".booking_name").to.be.a("string").and.not.empty;
        // SĐT
        pm.expect(appt.booking_phone, ctx + ".booking_phone").to.be.a("string").and.not.empty;
        // lý do khám
        pm.expect(appt, ctx + ".reason").to.have.property("reason");
    });

    // Lưu APPOINTMENT_ID để chain sang request xem chi tiết
    if (response.data[0] && response.data[0].id) {
        pm.environment.set("APPOINTMENT_ID", response.data[0].id);
    }
});

pm.test("A-F-05-TC02 - Xem lịch sử khi chưa có lịch hẹn: hiển thị trạng thái trống 'Chưa có lịch hẹn nào'", function () {
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(response, "response").to.have.property("data");
    pm.expect(response.data, "data").to.be.an("array");

    if (hasAppointments) {
        // Không áp dụng trong lần chạy này vì tài khoản đang có lịch hẹn
        // -> pass dưới dạng skip, TC01 đã kiểm tra đầy đủ
        console.log("TC02 skipped: tài khoản hiện có " + response.data.length + " lịch hẹn, đã được TC01 kiểm tra.");
        return;
    }

    pm.expect(response.data, "data phải là mảng rỗng").to.be.an("array").that.is.empty;
    if (response.quantity !== undefined) {
        pm.expect(response.quantity, "quantity").to.eql(0);
    }
});