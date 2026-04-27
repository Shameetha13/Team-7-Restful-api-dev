# Author: Kamala Kannan (TS-01/05/09)
  Rule: TS-09 Verify that an authenticated user can view a list of all their created private collections.

    Scenario Outline: TC-36 - Authenticated GET /collections returns 200
      When user sends authenticated GET to "<endpoint>"
      Then the response status code should be <statusCode>
      And the "Content-Type" header of response should be "application/json"
      And the response should contain field "<field>"
      And the collection response time should be within 5000 ms

      Examples:
        | endpoint     | statusCode | contentType      | field          |
        | /collections | 200        | application/json | collectionName |

    Scenario Outline: TC-37 - Empty collections list returns 200
      When user sends authenticated GET to "<endpoint>" with no objects and key "625fc069-ebae-4b7f-843e-5acd1aefd91d"
      Then the response status code should be <statusCode>
      And the "Content-Type" header of response should be "application/json"
      And the response body should contain an empty list
      And the collection response time should be within 5000 ms

      Examples:
        | endpoint     | statusCode | contentType      |
        | /collections | 200        | application/json |

    Scenario: TC-38 - GET /collections with invalid API key returns 403
      When user sends GET request to collections endpoint with invalid API key from test data
      Then the response status code should be 403
      And the error message in response body should contain "Invalid API key"
      And the collection response time should be within 5000 ms

    Scenario Outline: TC-39 - GET /collections with no API key returns 403
      When user sends unauthenticated GET to "<endpoint>"
      Then the response status code should be <statusCode>
      And the error message in response body should contain "API key is missing"
      And the collection response time should be within 5000 ms

      Examples:
        | endpoint     | statusCode | contentType      |
        | /collections | 403        | application/json |