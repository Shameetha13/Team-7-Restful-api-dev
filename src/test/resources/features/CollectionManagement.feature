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