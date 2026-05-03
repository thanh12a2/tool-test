// ============================================================
// A-F-09-TC03 - Hiển thị danh sách bác sĩ nổi bật trên màn Home
// Kiểm tra mỗi bác sĩ có tên, chuyên khoa và ảnh để hiển thị.
// ============================================================

pm.test("A-F-09-TC03 - Màn Home hiển thị bác sĩ nổi bật: mỗi bác sĩ có tên, chuyên khoa, ảnh", function () {
    pm.expect(pm.response.code, "Status code").to.eql(200);

    const body = pm.response.json();
    pm.expect(body, "response").to.have.property("data");
    pm.expect(body.data, "data").to.be.an("array").that.is.not.empty;

    body.data.forEach(function (doc, idx) {
        const ctx = "doctor[" + idx + "]";

        // tên bác sĩ
        const nameValue = doc.name || doc.full_name || doc.fullname ||
                          (doc.user && (doc.user.name || doc.user.full_name));
        pm.expect(nameValue, ctx + ".name").to.be.a("string").and.not.empty;

        // chuyên khoa
        const specialityValue =
            (doc.speciality && (doc.speciality.name || doc.speciality)) ||
            (doc.specialty && (doc.specialty.name || doc.specialty)) ||
            doc.speciality_name || doc.specialty_name;
        pm.expect(specialityValue, ctx + ".speciality").to.not.be.undefined;

        // ảnh
        const imageValue = doc.image || doc.avatar || doc.photo || doc.thumbnail ||
                           (doc.user && (doc.user.image || doc.user.avatar));
        pm.expect(imageValue, ctx + ".image").to.be.a("string").and.not.empty;
    });
});