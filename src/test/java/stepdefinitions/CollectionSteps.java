    // Author Shameetha
	@When("I add a collection item from Excel sheet {string} at row {int} into collection {string}")
	public void addCollectionItemFromExcel(String sheetName, int rowNum, String collection) {

		Map<String, String> rowData = ExcelUtility.getRowDataAsMap(sheetName, rowNum);

		ObjectAndCollection obj = new ObjectAndCollection();
		obj.setName(rowData.get("name"));

		ObjectAndCollection.Data data = new ObjectAndCollection.Data();
		data.setYear(Integer.parseInt(rowData.get("year")));
		data.setPrice(Double.parseDouble(rowData.get("price")));
		data.setCpuModel(rowData.get("cpu"));
		data.setHardDisk(rowData.get("harddisk"));
		obj.setData(data);

		String endpoint = "/collections/" + collection + "/objects";
		response = RestUtility.post(endpoint, obj);
		objectId = response.jsonPath().getString("id");
	}

	@And("the response should have name from Excel row {int}")
	public void validateName(int rowNum) {

		Map<String, String> rowData = ExcelUtility.getRowDataAsMap("data", rowNum);
		String expectedName = rowData.get("name");

		String actualName = response.jsonPath().getString("name");

		if (actualName == null) {
			System.out.println("Name not present in response");
		} else {
			Assert.assertEquals(actualName, expectedName, "Name mismatch!");
		}
	}

	@And("the response should have {string} from Excel row {int}")
	public void validateFieldFromExcel(String fieldName, int rowNum) {

		Map<String, String> rowData = ExcelUtility.getRowDataAsMap("data", rowNum);
		String expectedValue = rowData.get(fieldName);

		String jsonPath = fieldName.equals("name") ? "name" : "data." + getJsonPathMapping(fieldName);

		if (fieldName.equalsIgnoreCase("price") || fieldName.equalsIgnoreCase("year")) {

			double expected = Double.parseDouble(expectedValue);
			double actual = response.jsonPath().getDouble(jsonPath);
			Assert.assertEquals(actual, expected, 0.001, fieldName + " mismatch!");
		} else {

			String actualValue = response.jsonPath().getString(jsonPath);
			Assert.assertEquals(actualValue, expectedValue, fieldName + " mismatch!");
		}
	}

	private String getJsonPathMapping(String field) {
		if (field.equalsIgnoreCase("year"))
			return "year";
		if (field.equalsIgnoreCase("price"))
			return "price";
		if (field.equalsIgnoreCase("cpu"))
			return "'CPU model'";
		if (field.equalsIgnoreCase("harddisk"))
			return "'Hard disk size'";
		return field;
	}