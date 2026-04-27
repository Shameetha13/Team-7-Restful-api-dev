  # Author: Shameetha Ravikumar (TS-07/08/12)

  Rule: TS-12 Verify that an authenticated user can add a new item directly to a specific private collection.

    Scenario Outline: TC47 to TC50 (Add valid collection item, Add collection item with missing field (Defect), Add collection item with malformed data) - Process Row <rowNum>
      When I add a collection item from Excel sheet "data" at row <rowNum> into collection "products"
      Then the response status code should be <expectedStatus>
      And the "Content-Type" header of response should be "application/json"
      And the collection response time should be within 8000 ms
      And the response should have name from Excel row <rowNum>
      And the response should have "year" from Excel row <rowNum>
      And the response should have "price" from Excel row <rowNum>
      And the response should have "cpu" from Excel row <rowNum>
      And the response should have "harddisk" from Excel row <rowNum>

      Examples:
        | rowNum | expectedStatus |
        | 0      | 200            |
        | 1      | 400            |
        | 2      | 200            |