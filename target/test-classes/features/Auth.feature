   #Author: Shameetha Ravikumar (TS-07/08/12)
   Feature: User Authentication

  Background:
    Given the authentication API is accessible with a valid API key

  Scenario: TC26 - POST /register with valid unique credentials returns 200
    When I register a new user with a unique email
    Then the register response status should be 200
    And the response Content-Type should contain "application/json"
    And the register response body should confirm account creation
    And the response time should be within 3000 ms
  
  Scenario: TC27 - POST /register with duplicate email returns 409 Conflict
    When I register the same email address twice
    Then the register response status should be 409 Conflict
    And the response Content-Type should contain "application/json"

   Scenario: TC28 - KNOWN BUG-001: Weak password accepted with 200 instead of 400
    When I register with a weak password "1"
    Then the register response is 200 as a known BUG-001
  
  Scenario: TC29 - POST /register without name field returns 400 Bad Request
    When I register without the name field
    Then the register response status should be 400 Bad Request
    And the response Content-Type should contain "application/json"

  Scenario: TC30 - POST /register responds within 3000ms
    When I register a new user and measure response time
    Then the register response time should be within 3000 ms

  Scenario: TC31 - POST /login with valid credentials returns 200 and JWT token
    Given the test user is registered
    When I login with valid credentials
    Then the login response should be 200 with a JWT token
    And the response Content-Type should contain "application/json"
    And the response time should be within 3000 ms

  Scenario: TC32 - POST /login with wrong credentials returns 401 Unauthorized
    When I login with wrong credentials
    Then the login response should be 401 Unauthorized
    And the response Content-Type should contain "application/json"

  Scenario: TC33 - POST /login without API key returns 403 Forbidden
    When I login without the API key header
    Then the login response should be 403 Forbidden
    And the response Content-Type should contain "application/json"
  
  Scenario: TC34 - POST /login without email field returns 400 Bad Request
    When I login without the email field
    Then the login response should be 400 Bad Request
    And the response Content-Type should contain "application/json"

  Scenario: TC35 - POST /login responds within 3000ms
    Given the test user is registered
    When I login and measure response time
    Then the login response time should be within 3000 ms
