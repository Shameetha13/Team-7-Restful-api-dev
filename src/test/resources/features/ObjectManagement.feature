#Author: Barath (TS-02/06)
Feature:  - Object Operations

  Background:
    Given the base API is configured
  Scenario Outline: TC06 - Get object with valid ID returns 200 and object fields
    When user sends GET to "/objects/<objectId>"
    Then the status code should be 200
    And Response status line contains "200 OK"
    And the response body should contain field "id"
    And the response body should contain field "name"

  Examples:
    | objectId |
    | 1        |
    | 2        |
    | 3        |
    | 5        |
    | 10       |


  Scenario: TC07 - Get object with invalid ID returns 404 using DataTable
    When user sends GET requests to "/objects" with invalid IDs
      | invalidId          |
      | invalid-id-xyz-000 |
      | abc@123            |
      | wrong-id           |
      | !!!                |
      | 999999999          |
    Then each response status code should be 404
    And each response status line contains "Not Found"
    And each response body should have an error message

  Scenario: TC08 - Get single object responds within 2000ms using DDT
    When user sends multiple GET requests to "/objects" for performance check
    Then each response status code should be 200
    And each response time less than 2000 msms  
    
  Scenario Outline: TC22 - DELETE existing object returns 200

    Given a temporary object is created for testing
    When user deletes the object "<objectId>"
    Then the status code should be 200
    And Response status line contains "200 OK"


  Examples:
    | objectId |
    | temp1    |
    | temp2    |
    | temp3    |

  Scenario: TC23 - DELETE already-deleted object returns 404 using DataTable
    Given a temporary object is created and deleted
    When user deletes already deleted objects
      | objectId |
      | temp1    |
      | temp2    |
      | temp3    |
    Then each response status code should be 404
    And each response status line contains "Not Found"
    And each response body should have an error message

  Scenario Outline: TC24 - DELETE with invalid ID returns 404
    When user sends DELETE to "/objects/<invalidId>"
    Then the status code should be 404
    And Response status line contains "Not Found"

  Examples:
    | invalidId          |
    | invalid-id-xyz000  |
    | wrong123           |
    | id-not-found       |
    | 000invalid         |

  Scenario: TC25 - DELETE reserved object (ID 7) returns 405 using DDT
    When user sends multiple DELETE requests to reserved objects
    Then each response status code should be 405
    And each response status line contains "Method Not Allowed"
   
  #Author: Kamala Kannan (TS-05/09)

  Scenario Outline: TC-001 - Retrieve all public objects returns 200 with JSON array
    Given the API key is "<api_key>"
    When user sends GET to "<base_url>/<endpoint>"
    Then the status code should be 200
    And the response body should be a JSON array
    And the response schema should validate "id" as string and "name" as string
    And the collection variable "TS-01-objectId" is set to the first object's id
    And the collection variable "TS-01-nonexistobjectId" is set to last object's id plus 1

    Examples:
      | base_url   | endpoint     | api_key   |
      | <BASE_URL> | <EndPoint_1> | <API_Key> |


Scenario: TC-002 - Filter objects by specific IDs returns only matching objects
  Given the API key is "<API_Key>"
  And the following IDs are provided:
    | id1 | id2 |
    | 3   | 5   |
    | 1   | 2   |
    | 7   | 10  |
  When user sends GET request with multiple id params
  Then the status code should be 200
  And the JSON array should only contain the provided ids
  And the response schema should validate "id" as string and "name" as string


  Scenario: TC-003 - Non-existent ID returns 200 with empty array
    Given the API key is "<API_Key>"
    When user sends GET to "<BASE_URL>/<EndPoint_1>" with param "id=<TS-01-nonexistobjectId>"
    Then the status code should be 200
    And the response body should be an empty JSON array
    And the response schema should validate "id" as string and "name" as string


  Scenario Outline: TC-004 - Malformed ID as query param returns 200 with empty array
    Given the API key is "<API_Key>"
    When user sends GET to "<BASE_URL>/<EndPoint_1>" with param "id=<id_param>"
    Then the status code should be 200
    And the response body should be an empty JSON array
    And the response schema should validate "id" as string and "name" as string

    Examples:
      | id_param  |
      | 9xyz@#    |
      | 9_xyz_@#  |
      | abc!$%    |


  Scenario: TC-005 - GET all objects responds within 2000ms without auth header
    When user sends GET to "<BASE_URL>/<EndPoint_1>" without auth header
    Then the status code should be 200
    And the response time should be below 2000 ms
    And the response schema should validate "id" as string and "name" as string


  Scenario: TC-018 - KNOWN BUG - Wrong data type accepted with 200 instead of 400
    Given a temporary object is created for testing
    When user sends PATCH to the test object with body:
      """
      {
        "data": {
          "price": "expensive"
        }
      }
      """
    Then the status code should be 200


  Scenario: TC-019 - KNOWN BUG - updatedAt field missing from PATCH response
    Given a temporary object is created for testing
    When user sends PATCH to the test object with body:
      """
      {
        "data": {
          "price": 1999.99
        }
      }
      """
    Then the status code should be 200
    And the response body should contain field "updatedAt"


  Scenario Outline: TC-017 - PATCH single attribute with valid data returns 200
    Given the base URL is "<base_url>" and endpoint is "<endpoint>"
    When user sends PATCH to "/<endpoint>/<object_id>" with body:
      """
      {
        "data": {
          "price": <price>
        }
      }
      """
    Then the status code should be 200
    And the response time should be below 5000 ms
    And the response body should match the PATCH response schema
    And the response body should contain field "updatedAt"

    Examples:
      | base_url   | endpoint     | object_id   | price   |
      | <BASE_URL> | <EndPoint_1> | <Object_ID> | <price> |


Scenario: TC-020 - PATCH with invalid or non-existent object ID returns 404
  Given the following invalid object IDs:
    | object_id         |
    | invalid-b37cac2   |
    | invalid-id-abc123 |
    | 00000000          |

  When user sends PATCH requests with invalid IDs
  Then the status code should be 404
  And the response body should have an error message
  And the response time should be below 5000 ms

  Scenario Outline: TC-021 - PATCH reserved object ID returns 405 Method Not Allowed
    When user sends PATCH to "/<EndPoint_1>/<object_id>" with body:
      """
      {
        "data": {
          "price": 1999.99
        }
      }
      """
    Then the status code should be 405
    And the response body should have an error message
    And the response time should be below 5000 ms

    Examples:
      | object_id |
      | 1         |
      | 6         |
      | 10        |
    
    
#Author Varshinee
    
Rule: US03 Create a New Object
 
  Background:
    Given the base URI is set to "https://api.restful-api.dev"

  Scenario: TC09 - Create object with valid json payload
    When user sends POST to "/objects" with complete valid json body 
    Then the status code should be 200
    And a unique object id should be generated
    And the response should match the "ObjectResponse" schema
 
  Scenario: TC10 - Duplicate object creation 
    When user sends POST to "/objects" with same values as already existing object 
    Then the status code should be 200
    And a unique object id should be generated
    And the response should match the "ObjectResponse" schema
  
  Scenario: TC11 - Creating object with malformed payload 
    When user sends POST to "/objects" with missing required field 
    Then the status code should be 400
    And the response should contain appropriate error message
 
  Scenario: TC13 - Creating object with wrong header
    Given content-type is set to text in header
    When user sends POST to "/objects" with complete valid json body  
    Then the status code should be 415
    And the response should contain appropriate error message
    
Rule: US04 Update a New Object

  Scenario: TC14 - Update an existing object
    When user sends PUT to "/objects" with valid object id
    And the json payload is valid with updated values 
    Then the status code should be 200
    And the response body should reflect all the changes made 
    And the response should match the "ObjectResponse" schema

  Scenario: TC15 - Update an non-existent object 
    When user sends PUT to "/objects/nonexistent-xyz99" with valid json payload
    Then the status code should be 404
    And the response should contain an appropriate error message

  Scenario: TC16 - Update an existing object with missing field
    When user sends PUT to "/objects" with valid object id
    And the json payload only has updated values 
    Then the status code should be 200
    And the response body should reflect all the changes made 
    And the response should match the "ObjectResponse" schema

