#Author: Shameetha Ravikumar (TS-07/08/12)
Feature: User Authentication - Register

  Background:
    Given the authentication API is accessible with a valid API key

  # ================= Register =================

  Scenario Outline: Register API validation
    When I send a POST request to "/register" with email "<email>", password "<password>" and name "<name>"
    Then the register response status should be <statusCode>
    And the response Content-Type should contain "<contentType>"
    And the response time should be within 3000 ms

    Examples:
      | email             | password  | name     | statusCode | contentType      |
      | newuser@test.com  | Test@1234 | testuser | 200        | application/json |
      | newuser@test.com  | Test@1234 | testuser | 409        | application/json |
      | weakuser@test.com | 1         | testuser | 200        | application/json |
      | noname@test.com   | Test@1234 |          | 400        | application/json |

  # ================= Login =================


  Scenario: TC31 - Login with valid credentials
    Given the test user is registered
    When I login with following details
      | email                 | password  |
      | existinguser@test.com | Test@1234 |
    Then the login response status should be 200
    And the response should contain JWT token
    And the response Content-Type should contain "application/json"
    And the response time should be within 3000 ms

  Scenario: TC32 - Login with wrong credentials
    When I login with following details
      | email              | password  |
      | wronguser@test.com | WrongPass |
    Then the login response status should be 401
    And the response Content-Type should contain "application/json"


  Scenario: TC33 - Login without API key
    When I login with following details without API key
      | email                 | password  |
      | existinguser@test.com | Test@1234 |
    Then the login response status should be 403
    And the response Content-Type should contain "application/json"


  Scenario: TC34 - Login without email field
    When I login with following details
      | password  |
      | Test@1234 |
    Then the login response status should be 400
    And the response Content-Type should contain "application/json"


  Scenario: TC35 - Login response time validation
    Given the test user is registered
    When I login with following details
      | email                 | password  |
      | existinguser@test.com | Test@1234 |
    Then the response time should be within 3000 ms