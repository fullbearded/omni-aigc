client.test("request executed successful", function() {
    client.assert(response.status == 200, "Response status is not 200")
});

client.test("response body status successful", function() {
    client.assert(response.body.status == 200, "Response body status is not 200")
});
