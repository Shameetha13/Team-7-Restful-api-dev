Feature: TS15 - Authenticated Collections API

  Background:
    Given the base API is configured
    And the user is logged in

 Scenario: TC58 - DELETE existing collection item returns 200
    Given a collection item exists in "products"
    When user deletes the collection item from "products"
    Then the status code should be 200


  Scenario: TC59 - DELETE non-existent collection item returns 404
    When user sends authenticated DELETE to "/collections/products/objects/nonexistent-item-id"
    Then the status code should be 404
    And the response body should have an error message


  Scenario: TC60 - DELETE from another user collection returns 404
    When user sends authenticated DELETE to "/collections/other-user-coll/objects/someId"
    Then the status code should be 404

 #kamal
 
 #Author: Kamala Kannan US09

 
  Scenario: Authenticated GET /collections returns 200
    When user sends authenticated GET to "/collections"
    Then the status code should be 200

  Scenario: Empty collections list still returns 200
    When user sends authenticated GET to "/collections"
    Then the status code should be 200

  
  Scenario: GET /collections with invalid API key returns 403
    When user sends GET to "/collections" with invalid key "bad-key-xyz"
    Then the status code should be 403
    And the response body should have an error message

  
  Scenario: GET /collections with no API key returns 403
    When user sends unauthenticated GET to "/collections"
    Then the status code should be 403