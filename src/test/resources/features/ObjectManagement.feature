#Author: Barath (TS-02/06/15)
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
   
  #Author: Kamala Kannan (TS-01/05/09)
  
   Given the base API is configured

  Scenario: Retrieve all public objects returns 200 with JSON array
    When user sends GET to "/objects"
    Then the status code should be 200
    And the response body should be a JSON array
  
  Scenario: Filter objects by multiple IDs returns only those objects
    When user sends GET to "/objects" with param "id=3&id=5"
    Then the status code should be 200
    And the JSON array should only have ids "3" and "5"
  
  Scenario: Non-existent ID returns 200 with empty array
    When user sends GET to "/objects" with param "id=99999"
    Then the status code should be 200
    And the response body should be an empty JSON array
 
  Scenario: Malformed ID returns 200 with empty array
    When user sends GET to "/objects" with param "id=9xyz@#"
    Then the status code should be 200
    And the response body should be an empty JSON array
  
  Scenario: GET all objects responds within 2000ms
    When user sends GET to "/objects"
    Then the status code should be 200
    And the response time should be below 2000 ms
  
  Scenario: PATCH single attribute returns 200
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
  
  Scenario: KNOWN BUG - Wrong data type accepted with 200 instead of 400
    Given a temporary object is created for testing
    When user sends PATCH to the test object with body:
      """
      {
        "data": {
          "price": "very expensive"
        }
      }
      """
    Then the status code should be 200
  
  Scenario: KNOWN BUG - updatedAt field missing from PATCH response
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
  
  Scenario: PATCH with invalid ID returns 404
    Given a temporary object is created for testing
    When user sends PATCH to "/objects/invalid-id-abc123" with body:
      """
      {
        "data": {
          "price": 1999.99
        }
      }
      """
    Then the status code should be 404
    And the response body should have an error message

  Scenario: PATCH reserved object (ID 1) returns 405
    Given a temporary object is created for testing
    When user sends PATCH to "/objects/1" with body:
      """
      {
        "data": {
          "price": 100
        }
      }
      """
    Then the status code should be 405
    
    
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