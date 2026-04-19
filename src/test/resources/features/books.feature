Feature: Books API coverage

  Background:
    Given the Online Bookstore API is available

  @books @smoke @regression @BOOKS-API-001
  Scenario: Retrieve all books returns a non-empty collection
    When User retrieves all books
    Then the response status should be 200
    And the response should contain a non-empty list of books

  @books @smoke @regression @BOOKS-API-002
  Scenario: Retrieve a book by valid id returns the requested record
    When User retrieves the book with id 1
    Then the response status should be 200
    And the response should contain the requested book with id 1

  @books @regression @BOOKS-API-003
  Scenario: Create a new book from a DataTable payload
    When User creates a book with the following data:
      | id          | AUTO_ID          |
      | title       | Clean Code       |
      | description | API test book    |
      | pageCount   | 250              |
      | excerpt     | Useful excerpt   |
      | publishDate | NOW              |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"

  @books @regression @BOOKS-API-004
  Scenario: Update an existing book from a DataTable payload
    When User updates the book with id 1 using the following data:
      | id          | 1                       |
      | title       | Refactoring             |
      | description | Updated API test book   |
      | pageCount   | 320                     |
      | excerpt     | Updated useful excerpt  |
      | publishDate | NOW                     |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"

  @books @regression @BOOKS-API-005
  Scenario: Delete an existing book returns success
    When User deletes the existing book with id 1
    Then the response status should be 200
    And the delete response should "complete successfully"

  @books @negative @BOOKS-API-006
  Scenario: Retrieve a non-existing book by id returns not found
    When User retrieves a non-existing book
    Then the response status should be 404

  @books @negative @BOOKS-API-007
  Scenario: Retrieve a book by zero id returns not found
    When User retrieves the book with id 0
    Then the response status should be 404

  @books @negative @BOOKS-API-008
  Scenario: Retrieve a book by negative id returns not found
    When User retrieves the book with id -1
    Then the response status should be 404

  @books @negative @BOOKS-API-009
  Scenario: Create a book with a null title is accepted by the demo API
    When User creates a book with the following data:
      | id          | AUTO_ID             |
      | title       | NULL                |
      | description | Invalid title case  |
      | pageCount   | 120                 |
      | excerpt     | Sample excerpt      |
      | publishDate | NOW                 |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    And the book response should satisfy "preserve the title from the request payload"

  @books @negative @BOOKS-API-010
  Scenario: Create a book with an empty title is accepted by the demo API
    When User creates a book with the following data:
      | id          | AUTO_ID             |
      | title       | EMPTY               |
      | description | Empty title case    |
      | pageCount   | 120                 |
      | excerpt     | Sample excerpt      |
      | publishDate | NOW                 |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    And the book response should satisfy "preserve the title from the request payload"

  @books @negative @BOOKS-API-011
  Scenario: Create a book with missing text fields is accepted by the demo API
    When User creates a book with the following data:
      | id          | AUTO_ID        |
      | pageCount   | 120            |
      | excerpt     | Sample excerpt |
      | publishDate | NOW            |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"

  @books @negative @BOOKS-API-012
  Scenario: Create a book with a negative page count is accepted by the demo API
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Negative pages         |
      | description | Negative page count    |
      | pageCount   | -5                     |
      | excerpt     | Sample excerpt         |
      | publishDate | NOW                    |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    And the book response should satisfy "preserve the page count from the request payload"

  @books @negative @BOOKS-API-013
  Scenario: Create a book with zero page count is accepted by the demo API
    When User creates a book with the following data:
      | id          | AUTO_ID             |
      | title       | Zero pages          |
      | description | Zero page count     |
      | pageCount   | 0                   |
      | excerpt     | Sample excerpt      |
      | publishDate | NOW                 |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    And the book response should satisfy "preserve the page count from the request payload"

  @books @negative @BOOKS-API-014
  Scenario: Create a book with a very large page count is accepted by the demo API
    When User creates a book with the following data:
      | id          | AUTO_ID             |
      | title       | Large pages         |
      | description | Large page count    |
      | pageCount   | 2147483647          |
      | excerpt     | Sample excerpt      |
      | publishDate | NOW                 |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    And the book response should satisfy "preserve the page count from the request payload"

  @books @negative @BOOKS-API-015
  Scenario: Create a book with a duplicate id is accepted by the demo API
    When User creates a book with the following data:
      | id          | 1                    |
      | title       | Duplicate id         |
      | description | Duplicate id payload |
      | pageCount   | 120                  |
      | excerpt     | Sample excerpt       |
      | publishDate | NOW                  |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"

  @books @negative @BOOKS-API-016
  Scenario: Create a book with a malformed publish date returns bad request
    When User creates a book with the following data:
      | id          | AUTO_ID             |
      | title       | Bad Date            |
      | description | Invalid payload     |
      | pageCount   | 120                 |
      | excerpt     | Sample excerpt      |
      | publishDate | not-a-date          |
    Then the response status should be 400

  @books @negative @BOOKS-API-017
  Scenario: Update a non-existing book echoes the submitted payload
    When User updates the book with id 9000001 using the following data:
      | id          | 9000001                  |
      | title       | Non-existing update      |
      | description | Update non-existing book |
      | pageCount   | 120                      |
      | excerpt     | Sample excerpt           |
      | publishDate | NOW                      |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"

  @books @negative @BOOKS-API-018
  Scenario: Update a book with an empty title is accepted by the demo API
    When User updates the book with id 1 using the following data:
      | id          | 1                   |
      | title       | EMPTY               |
      | description | Update empty title  |
      | pageCount   | 120                 |
      | excerpt     | Sample excerpt      |
      | publishDate | NOW                 |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    And the book response should satisfy "preserve the title from the request payload"

  @books @negative @BOOKS-API-019
  Scenario: Update a book with a negative page count is accepted by the demo API
    When User updates the book with id 1 using the following data:
      | id          | 1                          |
      | title       | Negative update            |
      | description | Update negative page count |
      | pageCount   | -5                         |
      | excerpt     | Sample excerpt             |
      | publishDate | NOW                        |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    And the book response should satisfy "preserve the page count from the request payload"

  @books @negative @BOOKS-API-020
  Scenario: Update a book with a malformed publish date returns bad request
    When User updates the book with id 1 using the following data:
      | id          | 1                |
      | title       | Bad Date         |
      | description | Invalid payload  |
      | pageCount   | 120              |
      | excerpt     | Sample excerpt   |
      | publishDate | not-a-date       |
    Then the response status should be 400

  @books @negative @BOOKS-API-021
  Scenario: Delete a non-existing book still returns success
    When User deletes a non-existing book
    Then the response status should be 200
    And the delete response should "still return success"

  @books @negative @BOOKS-API-022
  Scenario: Delete a book by zero id still returns success
    When User deletes the existing book with id 0
    Then the response status should be 200
    And the delete response should "successful deletion"

  @books @negative @BOOKS-API-DELETE-VALIDATION-001
  Scenario: Delete a book with an oversized id value
    When User deletes a book using raw id value "3213123123123"
    Then the response status should be 400
    And the API error response should be a validation failure with traceId

  @books @negative @BOOKS-API-DELETE-VALIDATION-002
  Scenario: Delete a book with a non-numeric id value
    When User deletes a book using raw id value "abc"
    Then the response status should be 400
    And the API error response should indicate "validation failure"
