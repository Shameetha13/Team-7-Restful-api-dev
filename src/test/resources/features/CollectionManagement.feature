Feature: Fetch single object from a collection

  Background:
    Given the API URL "https://api.restful-api.dev" is up and running

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


    # Author: Barath (TS-02/06/15)
  Rule: TS-15 Verify that an authenticated user can successfully remove an item from their private collection.

    Scenario Outline: TC58 - DELETE existing collection item returns 200
      When user deletes the collection item from Excel sheet "bdata" at row <rowNum>
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the collection response time should be within 5000 ms
      
      
  # Author: Kamala Kannan (TS-01/05/09)
  Rule: TS-09 Verify that an authenticated user can view a list of all their created private collections.

      Examples:
        | rowNum |
        | 0      |
        | 1      |
    Scenario Outline: TC-36 - Authenticated GET /collections returns 200
      When user sends authenticated GET to "<endpoint>"
      Then the response status code should be <statusCode>
      And the "Content-Type" header of response should be "application/json"
      And the response should contain field "<field>"
      And the collection response time should be within 5000 ms

    Scenario: TC59 - DELETE non-existent collection item returns 404 using DataTable
      When user sends authenticated DELETE requests to "/collections/product/objects/" with invalid IDs
        | invalidId   |
        | invalid-001 |
        | invalidv@&  |
        | nnn         |
        | mmm         |
      Then the response status code should be 404
      And the error message in response body should contain "not found"
      And the collection response time should be within 5000 ms
      Examples:
        | endpoint     | statusCode | contentType      | field          |
        | /collections | 200        | application/json | collectionName |

    Scenario: TC60 - DELETE from another user collection returns 404
      When user sends authenticated DELETE to "/collections/testcollection1/objects/acbcvd34dfs3hj2423bsdbfs"
      Then the response status code should be 404
      And the error message in response body should contain "not found"
      And the collection response time should be within 5000 ms

    Scenario Outline: TC-37 - Empty collections list returns 200
      When user sends authenticated GET to "<endpoint>" with no objects and key "625fc069-ebae-4b7f-843e-5acd1aefd91d"
      Then the response status code should be <statusCode>
      And the "Content-Type" header of response should be "application/json"
      And the response body should contain an empty list
      And the collection response time should be within 5000 ms

      Examples:
        | endpoint     | statusCode | contentType      |
        | /collections | 200        | application/json |

    Scenario: TC-38 - GET /collections with invalid API key returns 403
      When user sends GET request to collections endpoint with invalid API key from test data
      Then the response status code should be 403
      And the error message in response body should contain "Invalid API key"
      And the collection response time should be within 5000 ms

    Scenario Outline: TC-39 - GET /collections with no API key returns 403
      When user sends unauthenticated GET to "<endpoint>"
      Then the response status code should be <statusCode>
      And the error message in response body should contain "API key is missing"
      And the collection response time should be within 5000 ms

      Examples:
        | endpoint     | statusCode | contentType      |
        | /collections | 403        | application/json |
        

  # Author : Manish (TS-11/13/14)

  Rule: TS-11 Verify that anauthenticated user can filter and view all the items belonging to a specific collection

    Scenario Outline: TC-044 - GET objects for a collection returns appropriate list
      When I send a GET request to collection objects using row <rowNum>
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the response body should contain a list of all objects in the collection
      And the collection response time should be within 2000 ms

      Examples:
        | rowNum |
        | 0      |
        | 1      |

    Scenario: TC-045 - GET objects for a non-existing collection returns empty list
      When I send a GET request to "/collections/randomCollectionXYZ123/objects"
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the response body should contain an empty list

    Scenario: TC-046 - GET objects response time is within acceptable limit
      When I send a GET request to "/collections/products/objects" and measure response time
      Then the response status code should be 200
      And the collection response time should be within 2000 ms
      
      
  # Author : Manish (TS-11/13/14)

  Rule: TS-13 Verify that an authenticated user can perform a full update on an inventory record inside their collection.

    Scenario Outline: TC-051 & 54 PUT update object with valid data and response time is within acceptable limit
      When I send a PUT request to "/collections/<collectionName>/objects/<objectId>" with a valid full payload
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the response body should contain the fully updated object
      And the response body should reflect all updated values from the request
      And the collection response time should be within 2000 ms

      Examples:
        | collectionName | objectId                         |
        | product        | ff8081819d82fab6019dcd2c59f7519d |

    Scenario Outline: TC-052 PUT update with missing mandatory fields returns 400 Bad Request
      When I send a PUT request to "/collections/<collectionName>/objects/<objectId>" with missing required fields
      Then the response status code should be 400
      And the "Content-Type" header of response should be "application/json"
      And the error message in response body should contain "not found"

      Examples:
        | collectionName | objectId                         |
        | product        | ff8081819d82fab6019dcd2c59f7519d |
        | product        | ff8081819d82fab6019dcd2c59f7519d |

    Scenario Outline: TC-053 - PUT update on another user's collection name but current user's api key
      When I send a PUT request to collection "<collectionName>" with name "<name>", year <year>, price <price>, cpu "<cpu>", and disk "<disk>"
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the collection response time should be within 5000 ms

      Examples:
        | collectionName | name        | year | price | cpu | disk  |
        | products       | Test Update | 2023 | 1000  | i5  | 256GB |

  Rule: TS-14 - Verify that an authenticated user can quickly change specific private data points of objects inside their collection.

    Scenario: TC-055 & TC-057 - PATCH update single attribute returns 200 & Response time validation
      When I send a PATCH request to collection "products" with payload
        | name    |
        | Hellooo |
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the response body should show updated "name" as "Hellooo"
      And other attributes should remain unchanged
      And the collection response time should be within 2000 ms

    Scenario: TC-056 - PATCH update with invalid data type still returns 200 (as per current behavior)
      When I send a PATCH request to collection "products" with payload
        | name       |
        | Patch name |
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the response body should show updated "name" as "Patch name"
      And other attributes should remain unchanged
      And the collection response time should be within 2000 ms
