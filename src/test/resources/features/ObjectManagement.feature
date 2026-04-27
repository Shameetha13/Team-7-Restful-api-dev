Feature:
# Author Kamala Kannan
  Rule: TS-01 Verify that a guest user can successfully retrieve a full list of all available public objects.

    Scenario: TC-01 & 05 Verify user is able to retrieve all the available objects.
      When user sends GET to endpoint
      Then the status code should be 200
      And each object contains the field "id"
      And the response header "Content-Type" should be "application/json"
      And the response time is below 3000 ms

    Scenario: TC-02 Verify filtering functionality using multiple IDs in query parameters.
      Given object with id 1 and 3 exists
      When user sends GET to endpoint with query params
      Then the status code should be 200
      And each object contains the field "id"
      And the response header "Content-Type" should be "application/json"

    Scenario: TC-03 Verify user is able to fetch details using non-existent object ID.
      Given object with id "21" doesn't exists
      When user sends GET to endpoint with query params
      Then the status code should be 200
      And the response header "Content-Type" should be "application/json"
      And the response body should contain an empty array

    Scenario: TC-04 Verify user is able to fetch using a malformed or invalid object ID.
      Given object with id "invalid-001" doesn't exists
      When user sends GET to endpoint with query params
      Then the status code should be 200
      And the response header "Content-Type" should be "application/json"
      And the response body should contain an empty array
# Author Kamala Kannan
  Rule: TS-05 Verify that a guest user can update a few fields without affecting other object fields(partial update).

    Scenario: TC-17 Verify user is able to patch a single specific attribute of an existing object.
      Given the "Content-Type" of the request body is set to "application/json"
      When I send a PATCH request to update the price for an object:
        | newPrice |
        | 1999.99  |
      Then the status code should be 200
      And the "data.price" in the response should match "1999.99"
      And the response time is below 3000 ms

    Scenario: TC-18 Verify user gets an appropriate error when updating an attribute with incorrect data type.
      Given the "Content-Type" of the request body is set to "application/json"
      When I send a PATCH request to update the price for an object:
        | newPrice   |
        | invalid001 |
      Then the status code should be 400
      And the appropriate error message "Invalid data type" is present in response body

    Scenario: TC-20 Verify appropriate error message is returned when incorrect or non-existent object ID is used in the request.
      Given the "Content-Type" of the request body is set to "application/json"
      When I send a PATCH request to endpoint with invalid object id "invalid-name-xyz" and price "1999.99"
      Then the status code should be 404
      And the appropriate error message "doesn't exist" is present in response body

    Scenario: TC-21 Verify appropriate error message is returned when ID of reserved object is used.
      Given the "Content-Type" of the request body is set to "application/json"
      When I send a PATCH request to endpoint with invalid object id "1" and price "1999.99"
      Then the status code should be 405
      And the appropriate error message "reserved" is present in response body