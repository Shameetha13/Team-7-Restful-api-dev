
Feature:
# Author Barath
  Rule: TS-02 Verify that a guest user can view the full technical specifications of a specific object using the object ID.

    Scenario Outline: TC-06 & 08 Verify user is able to retrieve full details of a specific object using a valid ID and response time
      Given object with id <id> exists
      When user sends GET to endpoint for single object
      Then the status code should be 200
      And the response should have id <id>
      And the response header "Content-Type" should be "application/json"
      And the response time is below 3000 ms

      Examples:
        | id |
        | 1  |
        | 2  |

    Scenario Outline: TC-07 Verify appropriate error is returned when an invalid or non-existent object ID is provided.
      Given object with id <id> doesn't exists
      When user sends GET to endpoint for single object
      Then the status code should be 404
      And the appropriate error message "not found" is present in response body

      Examples:
        | id     |
        | 13100  |
        | 209657 |
        
# Author Barath
  Rule: TS-06 Verify that a guest user can successfully delete an inaccurate or outdated item from the global list.

    Scenario: TC-22 Verify user is able to successfully delete an existing object from the global list.
      When DELETE is sent to object endpoint from config
      Then the status code should be 200
      And the appropriate message "has been deleted" is present in response body

    Scenario: TC-23 Verify appropriate error is returned when attempting to delete an already deleted object.
      When DELETE is sent to object endpoint from config
      Then the status code should be 404
      And the appropriate error message "doesn't exist" is present in response body

    Scenario: TC-24 Verify appropriate error is returned when attempting to delete an object with invalid ID.
      When DELETE is sent to objects endpoint with invalid IDs
        | invalidId   |
        | invalid-001 |
        | invalidv@&  |
      Then the status code should be 404
      And the appropriate error message "doesn't exist" is present in response body

    Scenario: TC-25 Verify appropriate error is returned when attempting to delete an object with reserved ID.
      When DELETE is sent to endpoint for reserved object
      Then the status code should be 405
      And the appropriate error message "reserved" is present in response body
