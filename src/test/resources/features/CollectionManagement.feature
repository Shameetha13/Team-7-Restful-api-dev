#Author: Barath (TS-02/06/15)

Feature: Authenticated Collections API

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
 
 
#Author: Kamala Kannan (TS-09)

   Scenario Outline: TC-036 - Authenticated GET collections with existing collections returns 200
    Given the API key is "<api_key>"
    When user sends GET to "<base_url>/<endpoint>"
    Then the status code should be 200
    And the response header "Content-Type" should be present
    And the response time should be below 5000 ms
    And the response body should match the collections schema with "collectionName" and "objectCount"

    Examples:
      | base_url   | endpoint     | api_key   |
      | <BASE_URL> | <EndPoint_2> | <API_KEY> |


  Scenario Outline: TC-037 - Authenticated GET collections with no collections returns 200 with empty array
    Given the API key is "<api_key>"
    When user sends GET to "<base_url>/<endpoint>"
    Then the status code should be 200
    And the response header "Content-Type" should be present
    And the response time should be below 5000 ms
    And the response body should be an empty JSON array

    Examples:
      | base_url   | endpoint     | api_key      |
      | <BASE_URL> | <EndPoint_2> | <no_col_API> |
  Scenario: TC-038 - GET collections with invalid API key returns 403
    Given the API key is "<invalid_API>"
    When user sends GET to "<BASE_URL>/<EndPoint_2>"
    Then the status code should be 403
    And the response status text should be "Forbidden"
    And the response time should be below 5000 ms
    And the response body should have an error message


  Scenario: TC-039 - GET collections with no API key returns 403
    Given no API key header is sent
    When user sends GET to "<BASE_URL>/<EndPoint_2>"
    Then the status code should be 403
    And the response status text should be "Forbidden"
    And the response time should be below 5000 ms
  
    
#Author : Manish (TS-11/13/14)

Scenario Outline: TC-044 - GET objects for a collection returns appropriate list
    When I send a GET request to "/collections/<collectionName>/objects"
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should contain a list of all objects in the collection
    And the response time should be within 2000 ms

    Examples:
      | collectionName  |
      | products        |
      | test            |

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

Scenario Outline: TC-051 - PUT update object with valid data returns 200
    When I send a PUT request to "/collections/<collectionName>/objects/<objectId>" with a valid full payload
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should contain the fully updated object
    And the response body should reflect all updated values from the request
    And the response time should be within 2000 ms

    Examples:
      |  collectionName  |  objectId  |
      | <collectionName> | <objectId> |

Scenario Outline: TC-052 - PUT update with missing mandatory fields returns 400 Bad Request
    When I send a PUT request to "/collections/<collectionName>/objects/<objectId>" with missing required fields
    Then the response status should be 400 Bad Request
    And the response Content-Type should contain "application/json"
    And the response body should indicate missing or null required fields

    Examples:
      | collectionName | objectId |
      | products       | 12345    |
      | products       | 67890    |

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

Scenario Outline: TC-055 - PATCH update single attribute returns 200
    When I send a PATCH request to "/collections/<collectionName>/objects/<objectId>" with a single attribute update
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should show the updated attribute value
    And other attributes should remain unchanged
    And the response time should be within 2000 ms

    Examples:
      | collectionName | objectId |
      | products       | 101      |
      | products       | 202      |

Scenario: TC-056 - PATCH update with invalid data type still returns 200 (as per current behavior)
    When I send a PATCH request to "/collections/products/objects/{objectId}" with an invalid data type
    Then the response status should be 200 OK
    And the response Content-Type should contain "application/json"
    And the response body should reflect the updated attribute even with incorrect data type
    And other attributes should remain unchanged

Scenario Outline: TC-057 - PATCH response time is within acceptable limit
    When I send a PATCH request to "/collections/<collectionName>/objects/<objectId>" with partial data and measure response time
    Then the response status should be 200 OK
    And the response time should be within 2000 ms

    Examples:
      | collectionName | objectId |
      | <collectionName> | <objectId> |
    
    
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
 
 
#Author Varshinee

  Scenario: Get an existing object from a collection
    Given The user is already registered and their API key is valid
    And The collection item should exist in the collection
    When GET request is sent for collection "product" and object ID "ff8081819d82fab6019d953b72a91709"
    Then the response status code should be 200
    And the value of "id" field in response should match with that in request
    And the response should match the "ObjectResponse" schema

  Scenario: Get an existing object from a collection with invalid API key
    Given The API key is invalid
    And The collection item should exist in the collection
    When GET request is sent for collection "product" and object ID "ff8081819d82fab6019d953b72a91709"
    Then the response status code should be 403
    And the response body should contain appropriate error message

  Scenario: Get an non existent object from a collection
    Given The API key is valid
    And The collection item should not exist in the collection
    When GET request is sent for collection "product" and object ID "invalid-001"
    Then the response status code should be 404
    And the response body should contain appropriate error message

  Scenario: Get an object from a non existing collection
    Given The API key is valid
    And The collection should not exist
    When GET request is sent for collection "invalid-xyz" and object ID "ff8081819d82fab6019d953b72a91709"
    Then the response status code should be 404
    And the response body should contain appropriate error message
