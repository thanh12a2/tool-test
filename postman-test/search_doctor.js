// ============================================================
// Test script cho request: search doctor
// Kịch bản A-F-07 - phân bổ cho tìm kiếm bác sĩ:
//   TC01 - Tìm theo tên bác sĩ hợp lệ
//   TC03 - Tìm không có kết quả
//   TC04 - Tìm với chuỗi rỗng
//   TC05 - Tìm với một ký tự
//   TC06 - Tìm với ký tự đặc biệt / XSS

// ============================================================

let response = {};
try { response = pm.response.json(); } catch (e) { response = {}; }

const url = pm.request.url;
const searchParam = url.query.has("search") ? (url.query.get("search") || "") : "";
const list = Array.isArray(response && response.data) ? response.data : [];
const hasResults = list.length > 0;

// Phân loại kiểu từ khóa đang được gửi
const isEmpty = searchParam.length === 0;
const isOneChar = searchParam.length === 1;
const isSpecialXss = /[<>]|script|alert\(/i.test(searchParam);
const isNoMatch = /^zzz_/i.test(searchParam) || /khongton/i.test(searchParam);
const isNormalName = !isEmpty && !isOneChar && !isSpecialXss && !isNoMatch;

function skip(name, reason) {
    console.log(name + " skipped: " + reason);
    pm.expect(true, reason).to.be.true;
}

pm.test("A-F-07-TC01 - Tìm kiếm theo tên bác sĩ hợp lệ: hiển thị danh sách bác sĩ với tên, chuyên khoa, phòng", function () {
    if (!isNormalName) {
        skip("TC01", "query hiện tại='" + searchParam + "' không phải tên thường, TC này chỉ áp dụng khi search bằng tên bác sĩ.");
        return;
    }
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(response, "response").to.have.property("data");
    pm.expect(list, "data").to.be.an("array");
    if (!hasResults) {
        // Cho phép rỗng nếu keyword không khớp ai, nhưng vẫn báo log
        console.log("TC01: kết quả trống với keyword '" + searchParam + "'. Xem lại dữ liệu nếu cần.");
        return;
    }
    list.forEach(function (doc, idx) {
        const ctx = "doctor[" + idx + "]";
        // tên bác sĩ
        const nameValue = doc.name || doc.full_name || doc.fullname || (doc.user && (doc.user.name || doc.user.full_name));
        pm.expect(nameValue, ctx + ".name").to.be.a("string").and.not.empty;
        // chuyên khoa
        const specialityValue =
            (doc.speciality && (doc.speciality.name || doc.speciality)) ||
            (doc.specialty && (doc.specialty.name || doc.specialty)) ||
            doc.speciality_name || doc.specialty_name;
        pm.expect(specialityValue, ctx + ".speciality").to.not.be.undefined;
        // phòng khám
        const roomValue =
            (doc.room && (doc.room.name || doc.room)) ||
            doc.room_name || doc.room_number || doc.clinic || doc.office;
        pm.expect(roomValue, ctx + ".room").to.not.be.undefined;
    });
});

pm.test("A-F-07-TC02 - Tìm kiếm không có kết quả: trạng thái trống", function () {
    if (!isNoMatch) {
        skip("TC03", "query hiện tại='" + searchParam + "' không phải từ khóa 'không tồn tại', TC này chỉ áp dụng khi search 'zzz_khongton'.");
        return;
    }
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(list, "data phải rỗng").to.be.an("array").that.is.empty;
    if (response.quantity !== undefined) {
        pm.expect(response.quantity, "quantity").to.eql(0);
    }
});

pm.test("A-F-07-TC03 - Tìm kiếm với chuỗi rỗng: trả về danh sách (không có kết quả hoặc hiển thị tất cả)", function () {
    if (!isEmpty) {
        skip("TC04", "query hiện tại='" + searchParam + "' không phải chuỗi rỗng.");
        return;
    }
    pm.expect(pm.response.code, "Status code").to.eql(200);
    pm.expect(response, "response").to.have.property("data");
    pm.expect(list, "data phải là array").to.be.an("array");
});

pm.test("A-F-07-TC04 - Tìm kiếm với một ký tự: lọc theo ký tự hoặc báo cần nhập thêm", function () {
    if (!isOneChar) {
        skip("TC05", "query hiện tại='" + searchParam + "' không phải 1 ký tự.");
        return;
    }
    pm.expect(pm.response.code, "Status code").to.be.oneOf([200, 400, 422]);
    if (pm.response.code === 200) {
        pm.expect(response, "response").to.have.property("data");
        pm.expect(list, "data phải là array").to.be.an("array");
    }
});

pm.test("A-F-07-TC05 - Tìm kiếm ký tự đặc biệt / XSS: input được làm sạch, không có XSS, kết quả trống", function () {
    if (!isSpecialXss) {
        skip("TC06", "query hiện tại='" + searchParam + "' không phải XSS payload.");
        return;
    }
    pm.expect(pm.response.code, "Status code").to.be.oneOf([200, 400, 422]);
    const bodyText = pm.response.text();
    // Không được trả nguyên payload XSS chưa escape
    pm.expect(bodyText, "response không được echo thẻ <script> chưa escape")
        .to.not.match(/<script[^>]*>\s*alert\s*\(/i);
    if (pm.response.code === 200) {
        pm.expect(list, "kết quả nên rỗng với XSS payload").to.be.an("array").that.is.empty;
    }
});
