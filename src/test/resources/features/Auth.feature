# Author Shameetha Ravikumar
Feature: User Authentication - Register & Login

  Background:
    Given the API URL is accessible with a valid API key

  Rule: TS-07 Verify that a user can successfully register for an account using a unique email and valid password

    Scenario Outline: TC-26 & 30 Register API validation and Response Time validation
      When I send a POST request with email "<email>", password "<password>" and name "<name>"
      Then validate the response status code <statusCode>
      And the status message should contain "<statusMsg>"
      And the response should contain "<email>"
      And the response name should contain "<name>"
      And the response Content-Type should contain "application/json"
      And the response time should be within 5000 ms

      Examples:
        | email    | password  | name     | statusCode | statusMsg |
        | <random> | Test@1234 | testuser | 200        | OK        |

    Scenario Outline: TC-27 Register API validation (Duplicate)
      When I send a POST request with email "<email>", password "<password>" and name "<name>"
      Then validate the response status code <statusCode>
      And the status message should contain "<statusMsg>"
      And the response time should be within 5000 ms

      Examples:
        | email       | password  | name     | statusCode | statusMsg |
        | <duplicate> | Test@1234 | testuser | 409        | Conflict  |

    Scenario Outline: TC-28 Register API validation (Simple Password- DEFECT)
      When I send a POST request with email "<email>", password "<password>" and name "<name>"
      Then validate the response status code <statusCode>
      And the status message should contain "<statusMsg>"
      And the response time should be within 5000 ms

      Examples:
        | email    | password | name     | statusCode | statusMsg   |
        | <random> | 1        | testuser | 400        | Bad Request |

    Scenario Outline: TC-29 Register API validation (Missing Field)
      When I send a POST request with email "<email>", password "<password>" and name "<name>"
      Then validate the response status code <statusCode>
      And the status message should contain "<statusMsg>"
      And the response time should be within 5000 ms

      Examples:
        | email    | password  | name | statusCode | statusMsg   |
        | <random> | Test@1234 |      | 400        | Bad Request |

  Rule: TS-08 Verify that a user can successfully login to their account

    Scenario: TC31 & TC35 - Login with valid credentials and Response time validation
      When I login with following details
        | email    | password  |
        | <random> | Test@1234 |
      Then validate the response status code 200
      And the response should contain JWT token
      And the response should contain user email
        | email         |
        | <verifyEmail> |
      And the response Content-Type should contain "application/json"
      And the response time should be within 5000 ms

    Scenario: TC32 - Login with wrong credentials
      When I login with following details
        | email              | password  |
        | wronguser@test.com | WrongPass |
      Then validate the response status code 401
      And the response time should be within 5000 ms

    Scenario: TC33 - Login without API key
      When I login with following details without API key
        | email              | password  |
        | newuser53@test.com | Test@1234 |
      Then validate the response status code 403
      And the response time should be within 5000 ms

    Scenario: TC34 - Login without email field
      When I login with following details
        | password  |
        | Test@1234 |
      Then validate the response status code 400
      And the response time should be within 5000 ms
