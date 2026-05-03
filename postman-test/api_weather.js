// ============================================================
// A-F-09-TC01 - Hiển thị nhiệt độ thời tiết trên màn Home
// Kiểm tra response từ OpenWeatherMap trả về đầy đủ thông tin
// nhiệt độ và tên thành phố để hiển thị trên Home.
// ============================================================

pm.test("A-F-09-TC01 - Màn Home hiển thị nhiệt độ thời tiết: response có name và temp hợp lệ", function () {
    pm.expect(pm.response.code, "Status code").to.eql(200);

    const body = pm.response.json();

    // Tên thành phố
    pm.expect(body, "response.name").to.have.property("name");
    pm.expect(body.name, "city name").to.be.a("string").and.not.empty;

    // Nhiệt độ
    pm.expect(body, "response.main").to.have.property("main");
    pm.expect(body.main, "main.temp").to.have.property("temp");
    pm.expect(body.main.temp, "temp value").to.be.a("number");
    // Biên hợp lý cho Hà Nội / đơn vị metric: -20 đến 50 độ C
    pm.expect(body.main.temp, "temp range (°C)").to.be.within(-20, 50);

    // Mô tả thời tiết (không bắt buộc nhưng thường có)
    pm.expect(body, "response.weather").to.have.property("weather");
    pm.expect(body.weather, "weather array").to.be.an("array").that.is.not.empty;
    pm.expect(body.weather[0], "weather[0].description").to.have.property("description");
});