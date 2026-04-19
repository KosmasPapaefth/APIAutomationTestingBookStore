Feature: Authors API coverage

  Background:
    Given the Online Bookstore API is available

  @authors @smoke @regression @AUTHORS-API-001
  Scenario: Retrieve all authors returns a non-empty collection
    When User retrieves all authors
    Then the response status should be 200
    And the response should contain a non-empty list of authors

  @authors @smoke @regression @AUTHORS-API-002
  Scenario: Retrieve an author by valid id returns the requested record
    When User retrieves the author with id 1
    Then the response status should be 200
    And the response should contain the requested author with id 1
    And the author should be linked to book id 1

  @authors @regression @AUTHORS-API-003
  Scenario: Create a new author from a DataTable payload
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | AUTO_BOOK_ID |
      | firstName | John         |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @authors @regression @AUTHORS-API-004
  Scenario: Update an existing author from a DataTable payload
    When User updates the author with id 1 using the following data:
      | id        | 1            |
      | idBook    | 1            |
      | firstName | Jane         |
      | lastName  | Smith        |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @authors @regression @AUTHORS-API-005
  Scenario: Delete an existing author returns success
    When User deletes the author with id 1
    Then the response status should be 200
    And the delete response should "complete successfully"

  @authors @negative @AUTHORS-API-006
  Scenario: Retrieve a non-existing author by id returns not found
    When User retrieves a non-existing author
    Then the response status should be 404

  @authors @negative @AUTHORS-API-007
  Scenario: Retrieve an author by zero id returns not found
    When User retrieves the author with id 0
    Then the response status should be 404

  @authors @negative @AUTHORS-API-008
  Scenario: Retrieve an author by negative id returns not found
    When User retrieves the author with id -1
    Then the response status should be 404

  @authors @negative @AUTHORS-API-009
  Scenario: Create an author with null first name is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | AUTO_BOOK_ID |
      | firstName | NULL         |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the first name from the request payload"

  @authors @negative @AUTHORS-API-010
  Scenario: Create an author with empty first name is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | AUTO_BOOK_ID |
      | firstName | EMPTY        |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the first name from the request payload"

  @authors @negative @AUTHORS-API-011
  Scenario: Create an author with null last name is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | AUTO_BOOK_ID |
      | firstName | John         |
      | lastName  | NULL         |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the last name from the request payload"

  @authors @negative @AUTHORS-API-012
  Scenario: Create an author with empty last name is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | AUTO_BOOK_ID |
      | firstName | John         |
      | lastName  | EMPTY        |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the last name from the request payload"

  @authors @negative @AUTHORS-API-013
  Scenario: Create an author with missing first name is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | AUTO_BOOK_ID |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @authors @negative @AUTHORS-API-014
  Scenario: Create an author with missing last name is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | AUTO_BOOK_ID |
      | firstName | John         |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @authors @negative @AUTHORS-API-015
  Scenario: Create an author with missing linked book id defaults to zero
    When User creates an author with the following data:
      | id        | AUTO_ID |
      | firstName | John    |
      | lastName  | Doe     |
    Then the response status should be 200
    And the author response should satisfy "preserve the first name from the request payload"
    And the author response should satisfy "preserve the last name from the request payload"
    And the author response should have linked book id 0

  @authors @negative @AUTHORS-API-016
  Scenario: Create an author with negative linked book id is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | -1           |
      | firstName | John         |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the linked book id from the request payload"

  @authors @negative @AUTHORS-API-017
  Scenario: Create an author with zero linked book id is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | 0            |
      | firstName | John         |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the linked book id from the request payload"

  @authors @negative @AUTHORS-API-018
  Scenario: Create an author with a very large linked book id is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID      |
      | idBook    | 2147483647   |
      | firstName | John         |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the linked book id from the request payload"

  @authors @negative @AUTHORS-API-019
  Scenario: Create an author with a duplicate id is accepted by the demo API
    When User creates an author with the following data:
      | id        | 1            |
      | idBook    | 1            |
      | firstName | Duplicate    |
      | lastName  | Author       |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @authors @negative @AUTHORS-API-020
  Scenario: Update a non-existing author echoes the submitted payload
    When User updates the author with id 9000001 using the following data:
      | id        | 9000001      |
      | idBook    | 1            |
      | firstName | Ghost        |
      | lastName  | Writer       |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @authors @negative @AUTHORS-API-021
  Scenario: Update an author with empty first name is accepted by the demo API
    When User updates the author with id 1 using the following data:
      | id        | 1            |
      | idBook    | 1            |
      | firstName | EMPTY        |
      | lastName  | Writer       |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the first name from the request payload"

  @authors @negative @AUTHORS-API-022
  Scenario: Update an author with null last name is accepted by the demo API
    When User updates the author with id 1 using the following data:
      | id        | 1            |
      | idBook    | 1            |
      | firstName | John         |
      | lastName  | NULL         |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the last name from the request payload"

  @authors @negative @AUTHORS-API-023
  Scenario: Update an author with negative linked book id is accepted by the demo API
    When User updates the author with id 1 using the following data:
      | id        | 1            |
      | idBook    | -1           |
      | firstName | John         |
      | lastName  | Doe          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the linked book id from the request payload"

  @authors @negative @AUTHORS-API-024
  Scenario: Delete a non-existing author still returns success
    When User deletes a non-existing author
    Then the response status should be 200
    And the delete response should "still return success"

  @authors @negative @AUTHORS-API-025
  Scenario: Delete an author by zero id still returns success
    When User deletes the author with id 0
    Then the response status should be 200
    And the delete response should "successful deletion"

  @authors @negative @AUTHORS-API-026
  Scenario: Delete an author by negative id still returns success
    When User deletes the author with id -1
    Then the response status should be 200
    And the delete response should "non-existing delete still succeeds"
