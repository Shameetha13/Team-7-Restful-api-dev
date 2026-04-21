Feature: TS0206 - Get Single Object by ID

  Background:
    Given the base API is configured
  Scenario: TC06 - Get object with valid ID returns 200 and object fields
    When user sends GET to "/objects/3"
    Then the status code should be 200
    And the response body should contain field "id"
    And the response body should contain field "name"

  Scenario: TC07 - Get object with invalid ID returns 404
    When user sends GET to "/objects/invalid-id-xyz-000"
    Then the status code should be 404
    And the response body should have an error message

  Scenario: TC08 - Get single object responds within 2000ms
    When user sends GET to "/objects/3"
    Then the status code should be 200
    And the response time should be below 2000 ms
    
    
  Scenario: TC22 - DELETE existing object returns 200
    Given a temporary object is created for testing
    When user deletes the test object
    Then the status code should be 200

    
  Scenario: TC23 - DELETE already-deleted object returns 404
    Given a temporary object is created and deleted
    When user deletes the already-deleted object
    Then the status code should be 404
    And the response body should have an error message
  
  Scenario: TC24 - DELETE with invalid ID returns 404
    When user sends DELETE to "/objects/invalid-id-xyz000"
    Then the status code should be 404

  Scenario: TC25 - DELETE reserved object (ID 7) returns 405
    When user sends DELETE to "/objects/7"
    Then the status code should be 405
