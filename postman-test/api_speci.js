// ============================================================
// A-F-09-TC02 - Hiển thị danh mục chuyên khoa trên màn Home
// Kiểm tra response trả về danh sách chuyên khoa với tên và ảnh.
// ============================================================

pm.test("A-F-09-TC02 - Màn Home hiển thị danh mục chuyên khoa: danh sách có tên và ảnh", function () {
    pm.expect(pm.response.code, "Status code").to.eql(200);

    const body = pm.response.json();
    pm.expect(body, "response").to.have.property("data");
    pm.expect(body.data, "data").to.be.an("array").that.is.not.empty;

    body.data.forEach(function (spec, idx) {
        const ctx = "speciality[" + idx + "]";
        // tên chuyên khoa
        const nameValue = spec.name || spec.speciality_name || spec.title;
        pm.expect(nameValue, ctx + ".name").to.be.a("string").and.not.empty;
        // ảnh
        const imageValue = spec.image || spec.icon || spec.photo || spec.thumbnail;
        pm.expect(imageValue, ctx + ".image").to.be.a("string").and.not.empty;
    });
});