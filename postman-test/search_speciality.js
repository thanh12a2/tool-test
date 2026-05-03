    // ============================================================
    // A-F-09 - search speciality
    // ============================================================

    const BASE = "http://192.168.1.142:80/PTIT-Do-An-Tot-Nghiep/api/specialities";
    const DOCTORS_BASE = "http://192.168.1.142:80/PTIT-Do-An-Tot-Nghiep/api/doctors";
    const AUTH = pm.request.headers.get("Authorization");
    const TYPE = pm.request.headers.get("Type") || "Patient";
    const HEADERS = { "Authorization": AUTH, "Type": TYPE };

    function parseJson(res) {
        try { return res.json(); } catch (e) { return {}; }
    }
    function getList(body) {
        return Array.isArray(body && body.data) ? body.data : [];
    }



    // ---------- TC01: tìm theo chuyên khoa (dùng response gốc) ----------
    pm.test("A-F-09-TC01 - Tìm kiếm theo chuyên khoa: hiển thị chuyên khoa", function () {
        pm.expect(pm.response.code, "Status code").to.eql(200);
        const body = pm.response.json();
        pm.expect(body, "response").to.have.property("data");
        const list = getList(body);
        pm.expect(list, "data").to.be.an("array");
        if (list.length === 0) {
            console.log("TC02: danh sách rỗng với keyword gốc. URL gốc nên dùng keyword chuyên khoa thường như 'tim'.");
            return;
        }
        list.forEach(function (spec, idx) {
            const ctx = "speciality[" + idx + "]";
            const nameValue = spec.name || spec.speciality_name || spec.title;
            pm.expect(nameValue, ctx + ".name").to.be.a("string").and.not.empty;
        });
    });

    function subGet(search, cb) {
        const url = BASE + "?search=" + encodeURIComponent(search) + "&length=10";
        pm.sendRequest({ url: url, method: "GET", header: HEADERS }, function (err, res) {
            cb(err, res);
        });
    }

    // ---------- TC02: không có kết quả ----------
    pm.test("A-F-09-TC02 - Tìm kiếm không có kết quả: trạng thái trống", function (done) {
        subGet("zzz_khongton_" + Date.now(), function (err, res) {
            pm.expect(err, "network error").to.be.null;
            pm.expect(res.code, "status").to.eql(200);
            const body = parseJson(res);
            const list = getList(body);
            pm.expect(list, "data phải rỗng").to.be.an("array").that.is.empty;
            if (body.quantity !== undefined) {
                pm.expect(body.quantity, "quantity").to.eql(0);
            }
            done();
        });
    });

    // ---------- TC03: chuỗi rỗng ----------
    pm.test("A-F-09-TC03 - Tìm kiếm với chuỗi rỗng: trả về danh sách", function (done) {
        subGet("", function (err, res) {
            pm.expect(err, "network error").to.be.null;
            pm.expect(res.code, "status").to.eql(200);
            const body = parseJson(res);
            pm.expect(body, "response").to.have.property("data");
            pm.expect(getList(body), "data phải là array").to.be.an("array");
            done();
        });
    });

    // ---------- TC04: một ký tự ----------
    pm.test("A-F-09-TC04 - Tìm kiếm với một ký tự: lọc theo ký tự hoặc báo cần nhập thêm", function (done) {
        subGet("N", function (err, res) {
            pm.expect(err, "network error").to.be.null;
            pm.expect(res.code, "status").to.be.oneOf([200, 400, 422]);
            if (res.code === 200) {
                const body = parseJson(res);
                pm.expect(body, "response").to.have.property("data");
                pm.expect(getList(body), "data phải là array").to.be.an("array");
            }
            done();
        });
    });

    // ---------- TC05: XSS payload ----------
    pm.test("A-F-09-TC05 - Tìm kiếm ký tự đặc biệt/XSS: input được làm sạch, không phản hồi XSS", function (done) {
        subGet("<script>alert(1)</script>", function (err, res) {
            pm.expect(err, "network error").to.be.null;
            pm.expect(res.code, "status").to.be.oneOf([200, 400, 422]);
            const text = res.text();
            pm.expect(text, "không được echo nguyên <script>alert(")
                .to.not.match(/<script[^>]*>\s*alert\s*\(/i);
            if (res.code === 200) {
                const body = parseJson(res);
                pm.expect(getList(body), "kết quả nên rỗng với XSS").to.be.an("array").that.is.empty;
            }
            done();
        });
    });
