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
 
  Scenario Outline: TC09 & 10 - Create object with valid json payload and create duplicates
    When user sends POST to /objects with name as "<name>" year as <year> price as <price> cpu model as "<CPU model>" and disk size as "<Hard disk size>"
    Then the status code should be 200
    And a unique object id should be generated

    Examples:
        | name | year | price | CPU model | Hard disk size |
        | Apple MacBook Pro 16 | 2019  | 1849.99  | Intel Core i9 | 1 TB |
        | Apple MacBook 16 | 2018  | 1469.99  | Intel Core i9 | 1 TB |
        | iphone Pro 16 | 2019  | 1849.99  | Intel Core i9 | 1 TB |
        | iphone 16 | 2018  | 1649.99  | Intel Core i9 | 1 TB |
        | Apple MacBook Pro 16 | 2019  | 1849.99  | Intel Core i9 | 1 TB |
        | Apple MacBook 16 | 2018  | 1469.99  | Intel Core i9 | 1 TB |
  
  Scenario: TC11 - Creating object with malformed payload 
    When user sends POST to /objects with name as "iphone Pro 16" 
    Then the status code should be 400
    And the error message should contain "malformed payload"
 
  Scenario: TC13 - Creating object with wrong header
    Given content-type is set to text in header
    When user sends POST to "/objects" with complete valid json body  
    Then the status code should be 415
    And the error message should contain "unsupported media type"

Rule: US04 Update a New Object

  Scenario: TC14 - Update an existing object
    When user sends PUT to /objects with valid object id
        | name | year | price | CPU model | Hard disk size |
        | Updated Apple MacBook Pro 16 | 2019  | 1849.99  | Intel Core i9 | 1 TB |
        | Updated Apple MacBook 16 | 2018  | 1469.99  | Intel Core i9 | 1 TB |
        | Updated iphone Pro 16 | 2019  | 1849.99  | Intel Core i9 | 1 TB |
    Then the status code should be 200
    And the values must be updated 
    
  Scenario: TC15 - Update an non-existent object 
    When user sends PUT to "/objects/nonexistent-xyz99" with valid json payload
    Then the status code should be 404
    And the error message should contain "not found"

  Scenario: TC16 - Update an existing object with missing field
    When user sends PUT to /objects with valid object id 
    Then the status code should be 200
<<<<<<< HEAD
    And the response body should reflect all the changes made 
    And the response should match the "ObjectResponse" schema

=======
    And the values must be updated 
    
>>>>>>> 8fe6bae (Updated feature and stepdefinition for TS-03/04/10)
