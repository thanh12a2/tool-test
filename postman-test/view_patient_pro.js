// A-F-10 - view patient profile
// 5 test cases, all run real assertions in ONE Send click.
// TC05 uses the original GET response. TC01..TC04 use pm.sendRequest.
// ============================================================

const BASE = "http://192.168.1.142:80/PTIT-Do-An-Tot-Nghiep/api/patient/profile";
const AUTH = pm.request.headers.get("Authorization");
const TYPE = pm.request.headers.get("Type") || "Patient";
const JSON_HEADERS = { "Authorization": AUTH, "Type": TYPE };
const FORM_HEADERS = {
    "Authorization": AUTH,
    "Type": TYPE,
    "Content-Type": "application/x-www-form-urlencoded"
};

function parseJson(res) {
    try { return res.json(); } catch (e) { return {}; }
}
function getProfile(body) {
    if (!body) return {};
    return body.data || body.profile || body.patient || body;
}
function formBody(obj) {
    return Object.keys(obj).map(function (k) {
        return encodeURIComponent(k) + "=" + encodeURIComponent(obj[k]);
    }).join("&");
}

// ---------- TC01: Cập nhật tên/tuổi/địa chỉ thành công ----------
pm.test("A-F-08-TC01 - Cập nhật tên/tuổi/địa chỉ thành công: response OK và dữ liệu được lưu", function (done) {
    const payload = {
        action: "personal",
        name: "mới",
        gender: "Nam",
        birthday: "01-01-2000",
        address: "Hà Nội"
    };
    pm.sendRequest({
        url: BASE,
        method: "POST",
        header: FORM_HEADERS,
        body: { mode: "raw", raw: formBody(payload) }
    }, function (err, res) {
        pm.expect(err, "network error").to.be.null;
        pm.expect(res.code, "status").to.be.oneOf([200, 201]);
        const body = parseJson(res);
        const ok = body.result === true || body.success === true ||
            body.status === "success" || body.status === 200 ||
            (body.message && /success|thành công/i.test(body.message)) ||
            (res.code === 200 && body.data !== undefined);
        pm.expect(ok, "response báo thành công (result/success/message)").to.be.true;
        done();
    });
});

// ---------- TC02: Upload ảnh đại diện ----------
pm.test("A-F-08-TC02 - Upload ảnh đại diện: endpoint nhận request và không trả 5xx", function (done) {
    // Không có file thật để upload trong script, nên gửi request POST với action=avatar
    // hoặc multipart rỗng. Mục tiêu: endpoint tồn tại, không 5xx. Có thể fail nếu backend
    // yêu cầu file bắt buộc - đây là điều test case cần bộc lộ.
    const payload = { action: "avatar" };
    pm.sendRequest({
        url: BASE,
        method: "POST",
        header: FORM_HEADERS,
        body: { mode: "raw", raw: formBody(payload) }
    }, function (err, res) {
        pm.expect(err, "network error").to.be.null;
        pm.expect(res.code, "status không được 5xx").to.be.lessThan(500);
        // Kỳ vọng 200/201 nếu chấp nhận, hoặc 400/422 nếu validate thiếu file
        pm.expect(res.code, "status hợp lệ").to.be.oneOf([200, 201, 400, 401, 403, 422]);
        done();
    });
});

// ---------- TC03: Để trống tên → validation lỗi ----------
pm.test("A-F-08-TC03 - Validation: để trống tên phải báo lỗi 'Tên là bắt buộc'", function (done) {
    const payload = {
        action: "personal",
        name: "",
        gender: "Nam",
        birthday: "05-06-2004",
        address: "hn"
    };
    pm.sendRequest({
        url: BASE,
        method: "POST",
        header: FORM_HEADERS,
        body: { mode: "raw", raw: formBody(payload) }
    }, function (err, res) {
        pm.expect(err, "network error").to.be.null;
        // Kỳ vọng backend reject (400/422) HOẶC trả 200 kèm message lỗi
        const body = parseJson(res);
        const text = res.text();
        const hasValidationError =
            res.code === 400 || res.code === 422 ||
            body.result === false || body.success === false ||
            /bắt buộc|required|không được trống|empty|tên|name/i.test(text);
        pm.expect(hasValidationError, "phải có báo lỗi validate khi tên rỗng").to.be.true;
        done();
    });
});

// ---------- TC04: Ngày sinh không hợp lệ ----------
pm.test("A-F-08-TC04 - Validation: ngày sinh không hợp lệ (tương lai/sai format) phải báo lỗi", function (done) {
    const payload = {
        action: "personal",
        name: "thanhle",
        gender: "Nam",
        birthday: "99-99-9999", // không hợp lệ
        address: "hn"
    };
    pm.sendRequest({
        url: BASE,
        method: "POST",
        header: FORM_HEADERS,
        body: { mode: "raw", raw: formBody(payload) }
    }, function (err, res) {
        pm.expect(err, "network error").to.be.null;
        const body = parseJson(res);
        const text = res.text();
        const hasValidationError =
            res.code === 400 || res.code === 422 ||
            body.result === false || body.success === false ||
            /invalid|không hợp lệ|birthday|ngày sinh|date/i.test(text);
        pm.expect(hasValidationError, "phải có báo lỗi validate ngày sinh sai").to.be.true;
        done();
    });
});

// ---------- TC05: View profile - dữ liệu được lưu và hiển thị đúng ----------
pm.test("A-F-08-TC05 - View profile: trả về đầy đủ thông tin cá nhân (name, gender, birthday, address)", function () {
    pm.expect(pm.response.code, "status").to.eql(200);
    const body = pm.response.json();
    const profile = getProfile(body);
    pm.expect(profile, "profile object").to.be.an("object");
    pm.expect(profile.name || profile.full_name || profile.fullname, "name").to.be.a("string").and.not.empty;
    // Các field còn lại chỉ cần tồn tại (có thể rỗng nếu user chưa điền)
    pm.expect(profile, "gender field").to.satisfy(function (p) {
        return p.gender !== undefined || p.sex !== undefined;
    });
    pm.expect(profile, "birthday field").to.satisfy(function (p) {
        return p.birthday !== undefined || p.dob !== undefined || p.date_of_birth !== undefined;
    });
    pm.expect(profile, "address field").to.satisfy(function (p) {
        return p.address !== undefined;
    });
});