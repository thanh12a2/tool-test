// ============================================================
// Test script cho request: view details appointment
// Gồm 1 test case: A-F-04-TC03
// ============================================================

const response = pm.response.json();

pm.test("A-F-05-TC03 - Xem chi tiết từng lịch sử khám: hiển thị chi tiết ngày khám, bác sĩ/khoa khám, phác đồ điều trị, bệnh án", function () {
    // Điều kiện áp dụng: bệnh nhân có lịch sử khám
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(response.result, "result").to.eql(1);
    pm.expect(response, "response").to.have.property("data");
    pm.expect(response.data, "data").to.be.an("object");

    const data = response.data;

    // ID chi tiết khớp ID trên URL
    const urlParts = pm.request.url.getPath().split("/");
    const requestedId = urlParts[urlParts.length - 1];
    pm.expect(String(data.id), "data.id").to.eql(String(requestedId));

    // Ngày khám
    pm.expect(data.appointment_date, "appointment_date").to.match(/^\d{4}-\d{2}-\d{2}$/);
    pm.expect(data.appointment_time, "appointment_time").to.match(/^\d{2}:\d{2}$/);

    // Khoa khám / bác sĩ (service)
    pm.expect(data, "service").to.have.property("service");
    pm.expect(data.service, "service.id").to.have.property("id");
    pm.expect(data.service, "service.name").to.have.property("name");
    pm.expect(data.service.name, "service.name value").to.be.a("string").and.not.empty;

    // Thông tin bệnh nhân
    pm.expect(data.booking_name, "booking_name").to.be.a("string").and.not.empty;
    pm.expect(data.booking_phone, "booking_phone").to.be.a("string").and.not.empty;
    pm.expect(data, "name").to.have.property("name");
    pm.expect(data, "gender").to.have.property("gender");
    pm.expect(data, "birthday").to.have.property("birthday");
    pm.expect(data, "address").to.have.property("address");

    // Lý do khám / phác đồ / bệnh án
    pm.expect(data, "reason").to.have.property("reason");

    // Trạng thái
    const validStatuses = ["processing", "cancelled", "completed", "confirmed", "pending", "accepted", "rejected", "done"];
    pm.expect(validStatuses, "status value").to.include(data.status);

    // Mốc thời gian
    pm.expect(data, "create_at").to.have.property("create_at");
    pm.expect(data, "update_at").to.have.property("update_at");
});
