Feature: Public - Object Management

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

  # Author Barath
  Rule: TS-02 Verify that a guest user can view the full technical specifications of a specific object using the object ID.

    Scenario Outline: TC-06 & 08 Verify user is able to retrieve full details of a specific object using a valid ID and response time
      When user sends GET with valid object id <id>
      Then the status code should be 200
      And the response should have id <id>
      And the response header "Content-Type" should be "application/json"
      And the response time is below 3000 ms

      Examples:
        | id |
        | 1  |
        | 2  |

    Scenario Outline: TC-07 Verify appropriate error is returned when an invalid or non-existent object ID is provided.
      When user sends GET with invalid object id <id>
      Then the status code should be 404
      And the appropriate error message "not found" is present in response body

      Examples:
        | id     |
        | 13100  |
        | 209657 |

  # Author Varshinee
  Rule: TS-03 Verify that a guest user can successfully create a new global object

    Scenario Outline: TC-09 & 10 & 12 Successfully create unique and duplicate objects
      When user sends POST to endpoint with name as "<name>" year as <year> price as <price> cpu model as "<CPU model>" and disk size as "<Hard disk size>" and content type is "application/json"
      Then the status code should be 200
      And the response body has the field "id"
      And the value of "name" field in response should match the name in request
      And the response header "Content-Type" should be "application/json"
      And the response time is below 3000 ms

      Examples:
        | name                 | year | price   | CPU model     | Hard disk size |
        | Apple MacBook Pro 16 | 2019 | 1849.99 | Intel Core i9 | 1 TB           |
        | iphone Pro 16        | 2019 | 1849.99 | Intel Core i9 | 1 TB           |
        | Apple MacBook Pro 16 | 2019 | 1849.99 | Intel Core i9 | 1 TB           |
        | iphone Pro 16        | 2019 | 1849.99 | Intel Core i9 | 1 TB           |

    Scenario: TC-11 Creating object with malformed payload - DEFECT
      When user sends POST to endpoint with name as "iphone Pro 16" and price as "expensive" and content type is "application/json"
      Then the status code should be 400
      And the appropriate error message "Invalid request body" is present in response body

    Scenario: TC-13 - Creating object with wrong content type
      When user sends POST to endpoint with complete valid json body and content type is "text/plain"
      Then the status code should be 415
      And the appropriate error message "Unsupported Media Type" is present in response body

  Rule: TS-04 Verify that a guest user can update by replacing all existing details of a previously added device with new data

    Scenario: TC-14 - Update an existing object
      When user sends PUT to endpoint with valid object id and complete payload with updated values
        | name                         | year | price   | CPU model     | Hard disk size |
        | Updated Apple MacBook Pro 16 | 2019 | 1849.99 | Intel Core i9 | 1 TB           |
        | Updated Apple MacBook 16     | 2018 | 1469.99 | Intel Core i9 | 1 TB           |
        | Updated iphone Pro 16        | 2019 | 1849.99 | Intel Core i9 | 1 TB           |
      Then the status code should be 200
      And the value of "name" field in response should match the name in request
      And the response header "Content-Type" should be "application/json"
      And the response time is below 3000 ms

    Scenario: TC-15 - Update a non-existent object
      When user sends PUT to endpoint with invalid id "object-id-invalid-001"
      Then the status code should be 404
      And the appropriate error message "doesn't exist" is present in response body

    Scenario: TC-16 - Update an existing object with missing field
      When user sends PUT to endpoint with name as "iphone Pro 16" for object from config
      Then the status code should be 200
      And the value of "name" field in response should match the name in request
      And the response header "Content-Type" should be "application/json"

  # Author Kamala Kannan
  Rule: TS-05 Verify that a guest user can update a few fields without affecting other object fields(partial update).

    Scenario: TC-17 Verify user is able to patch a single specific attribute of an existing object.
      When I send a PATCH request to update the price for an object:
        | newPrice |
        | 1999.99  |
      Then the status code should be 200
      And the "data.price" in the response should match "1999.99"
      And the response time is below 3000 ms

    Scenario: TC-18 Verify user gets an appropriate error when updating an attribute with incorrect data type. - DEFECT
      When I send a PATCH request to update the price for an object:
        | newPrice   |
        | invalid001 |
      Then the status code should be 400
      And the appropriate error message "Invalid data type" is present in response body

    Scenario: TC-20 Verify appropriate error message is returned when incorrect or non-existent object ID is used in the request.
      When I send a PATCH request to endpoint with invalid object id "invalid-name-xyz" and price "1999.99"
      Then the status code should be 404
      And the appropriate error message "doesn't exist" is present in response body

    Scenario: TC-21 Verify appropriate error message is returned when ID of reserved object is used.
      When I send a PATCH request to endpoint with invalid object id "1" and price "1999.99"
      Then the status code should be 405
      And the appropriate error message "reserved" is present in response body

  # Author Barath
  Rule: TS-06 Verify that a guest user can successfully delete an inaccurate or outdated item from the global list.

    @DeleteObject
    Scenario: TC-22 Verify user is able to successfully delete an existing object from the global list.
      When DELETE is sent to object endpoint from config
      Then the status code should be 200
      And the appropriate message "has been deleted" is present in response body

    Scenario: TC-23 Verify appropriate error is returned when attempting to delete an already deleted object.
      When DELETE is sent to object endpoint from config
      Then the status code should be 404
      And the appropriate error message "doesn't exist" is present in response body

    Scenario: TC-24 Verify appropriate error is returned when attempting to delete an object with invalid ID.
      When DELETE is sent to objects endpoint with invalid IDs
        | invalidId   |
        | invalid-001 |
        | invalidv@&  |
      Then the status code should be 404
      And the appropriate error message "doesn't exist" is present in response body

    Scenario: TC-25 Verify appropriate error is returned when attempting to delete an object with reserved ID.
      When DELETE is sent to endpoint for reserved object
      Then the status code should be 405
      And the appropriate error message "reserved" is present in response body
