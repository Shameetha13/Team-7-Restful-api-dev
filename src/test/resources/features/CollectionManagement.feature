Feature: TS15 - Authenticated Collections API

  Background:
    Given the base API is configured
    And the user is logged in

 Scenario: TC58 - DELETE existing collection item returns 200
    Given a collection item exists in "products"
    When user deletes the collection item from "products"
    Then the status code should be 200


  Scenario: TC59 - DELETE non-existent collection item returns 404
    When user sends authenticated DELETE to "/collections/products/objects/nonexistent-item-id"
    Then the status code should be 404
    And the response body should have an error message


  Scenario: TC60 - DELETE from another user collection returns 404
    When user sends authenticated DELETE to "/collections/other-user-coll/objects/someId"
    Then the status code should be 404
 
 #Author: Kamala Kannan US09

 
  Scenario: Authenticated GET /collections returns 200
    When user sends authenticated GET to "/collections"
    Then the status code should be 200

  Scenario: Empty collections list still returns 200
    When user sends authenticated GET to "/collections"
    Then the status code should be 200

  
  Scenario: GET /collections with invalid API key returns 403
    When user sends GET to "/collections" with invalid key "bad-key-xyz"
    Then the status code should be 403
    And the response body should have an error message

  
  Scenario: GET /collections with no API key returns 403
    When user sends unauthenticated GET to "/collections"
    Then the status code should be 403
    
#Author : Manish (TS-11/13/14)

Scenario: TC-044 - GET objects for a valid collection returns 200 with list
    When I send a GET request to "/collections/products/objects"
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should contain a list of all objects in the collection
    And the response time should be within 2000 ms

Scenario: TC-045 - GET objects for a non-existing collection returns empty list
    When I send a GET request to "/collections/randomCollectionXYZ123/objects"
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should contain an empty list
    And the response body should indicate no items found for the collection

Scenario: TC-046 - GET objects response time is within acceptable limit
    When I send a GET request to "/collections/products/objects" and measure response time
    Then the response status should be 200 OK
    And the response time should be within 2000 ms

Scenario: TC-051 - PUT update object with valid data returns 200
    When I send a PUT request to "/collections/products/objects/{objectId}" with a valid full payload
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should contain the fully updated object
    And the response body should reflect all updated values from the request
    And the response time should be within 2000 ms

Scenario: TC-052 - PUT update with missing mandatory fields returns 400 Bad Request
    When I send a PUT request to "/collections/products/objects/{objectId}" with missing required fields
    Then the response status should be 400 Bad Request
    And the response Content-Type should contain "application/json"
    And the response body should indicate missing or null required fields

Scenario: TC-053 - PUT update on another user's collection should not allow access
    Given another user also has a collection
    When I send a PUT request to "/collections/products/objects/{objectId}" using another user's authorization
    Then the response status should be 200 OK
    And the response should not update the other user's collection
    And a new record should be created in the current user's collection instead
    And the response body should reflect the new record in current user's collection

Scenario: TC-054 - PUT update response time is within acceptable limit
    When I send a PUT request to "/collections/products/objects/{objectId}" with valid data and measure response time
    Then the response status should be 200 OK
    And the response time should be within 2000 ms

Scenario: TC-055 - PATCH update single attribute returns 200
    When I send a PATCH request to "/collections/products/objects/{objectId}" with a single attribute update
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should show the updated attribute value
    And other attributes should remain unchanged
    And the response time should be within 2000 ms

Scenario: TC-056 - PATCH update with invalid data type still returns 200 (as per current behavior)
    When I send a PATCH request to "/collections/products/objects/{objectId}" with an invalid data type
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should reflect the updated attribute even with incorrect data type
    And other attributes should remain unchanged

Scenario: TC-057 - PATCH response time is within acceptable limit
    When I send a PATCH request to "/collections/products/objects/{objectId}" with partial data and measure response time
    Then the response status should be 200 OK
    And the response time should be within 2000 ms
    
    
    #Author: Shameetha Ravikumar (TS-07/08/12)
    
  Scenario Outline: TC47 - POST /collections/{name}/objects with valid payload returns 200
    When I add a collection item using Excel row <rowIndex>
    Then the add-item response should be valid with all fields
    And the response Content-Type should contain "application/json"

    Examples:
      | rowIndex |
      | 0        |
      | 1        |
      | 2        |
      | 3        |
      | 4        |
      | 5        |

  
  Scenario: TC48 - POST to another user's collection creates item in current user's collection
    When I POST to another user's collection "other-user-collection"
    Then the response should be 200 and item created in current user's collection
    And the response Content-Type should contain "application/json"

  
  Scenario: TC49 - POST collection item with missing name field returns 200 (known defect)
    When I add a collection item with missing name field in collection "products"
    Then the response is 200 as a known defect for missing name
    And the response Content-Type should contain "application/json"

  
  Scenario: TC50 - POST collection item with malformed payload returns 200
    When I add a collection item with malformed payload in collection "products"
    Then the malformed payload response should be 200 with id present
    And the response Content-Type should contain "application/json"
 