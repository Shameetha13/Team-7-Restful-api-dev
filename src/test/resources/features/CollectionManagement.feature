
Feature: Fetch single object from a collection

  Background:
    Given the API URL "https://api.restful-api.dev" is up and running
# Author: Barath (TS-02/06/15)
  Rule: TS-15 Verify that an authenticated user can successfully remove an item from their private collection.

    Scenario Outline: TC58 - DELETE existing collection item returns 200
      When user deletes the collection item from Excel sheet "bdata" at row <rowNum>
      Then the response status code should be 200
      And the "Content-Type" header of response should be "application/json"
      And the collection response time should be within 5000 ms

      Examples:
        | rowNum |
        | 0      |
        | 1      |

    Scenario: TC59 - DELETE non-existent collection item returns 404 using DataTable
      When user sends authenticated DELETE requests to "/collections/product/objects/" with invalid IDs
        | invalidId   |
        | invalid-001 |
        | invalidv@&  |
        | nnn         |
        | mmm         |
      Then the response status code should be 404
      And the error message in response body should contain "not found"
      And the collection response time should be within 5000 ms

    Scenario: TC60 - DELETE from another user collection returns 404
      When user sends authenticated DELETE to "/collections/testcollection1/objects/acbcvd34dfs3hj2423bsdbfs"
      Then the response status code should be 404
      And the error message in response body should contain "not found"
      And the collection response time should be within 5000 ms
