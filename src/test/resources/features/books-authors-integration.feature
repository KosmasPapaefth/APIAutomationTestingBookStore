@books @authors
Feature: Books and Authors integration coverage

  Background:
    Given the Online Bookstore API is available

  @integration @regression @BOOKS-AUTHORS-API-001
  Scenario: Create a book then create an author linked to it
    When User creates a book with the following data:
      | id          | AUTO_ID                  |
      | title       | Integration Patterns     |
      | description | Cross-entity test book   |
      | pageCount   | 280                      |
      | excerpt     | Book and author link     |
      | publishDate | NOW                      |
    Then the response status should be 200
    And the book response should satisfy "match the request payload"
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Martin          |
      | lastName  | Fowler          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @integration @negative @BOOKS-AUTHORS-API-002
  Scenario: Retrieve created book and author by id reflects demo API non-persistence
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Ephemeral Integration  |
      | description | Demo API persistence   |
      | pageCount   | 190                    |
      | excerpt     | Book retrieval check   |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Demo            |
      | lastName  | Author          |
    Then the response status should be 200
    When User retrieves the created book
    Then the response status should be 404
    When User retrieves the created author
    Then the response status should be 404

  @integration @regression @BOOKS-AUTHORS-API-003
  Scenario: Update an author linked to a created book
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Linked Updates         |
      | description | Update integration     |
      | pageCount   | 210                    |
      | excerpt     | Updated author link    |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Original        |
      | lastName  | Writer          |
    Then the response status should be 200
    When User updates the created author with the following data:
      | id        | CREATED_AUTHOR_ID |
      | idBook    | CREATED_BOOK_ID   |
      | firstName | Updated           |
      | lastName  | Writer            |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @integration @regression @BOOKS-AUTHORS-API-004
  Scenario: Delete an author linked to a created book
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Delete Author Flow     |
      | description | Delete integration     |
      | pageCount   | 230                    |
      | excerpt     | Linked delete          |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Delete          |
      | lastName  | Candidate       |
    Then the response status should be 200
    When User deletes the created author
    Then the response status should be 200
    And the delete response should "complete successfully"

  @integration @negative @BOOKS-AUTHORS-API-005
  Scenario: Create an author with a non-existing book id is accepted by the demo API
    When User creates an author with the following data:
      | id        | AUTO_ID |
      | idBook    | 9000001 |
      | firstName | Orphan  |
      | lastName  | Author  |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @integration @regression @BOOKS-AUTHORS-API-006
  Scenario: Create multiple authors for the same book
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Shared Authors         |
      | description | Multiple author link   |
      | pageCount   | 260                    |
      | excerpt     | Shared book            |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | First           |
      | lastName  | Contributor     |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Second          |
      | lastName  | Contributor     |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @integration @negative @BOOKS-AUTHORS-API-007
  Scenario: Delete a created book after linked author creation still succeeds
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Referential Demo       |
      | description | No enforced relation   |
      | pageCount   | 175                    |
      | excerpt     | Delete book flow       |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Linked          |
      | lastName  | Author          |
    Then the response status should be 200
    When User deletes the created book
    Then the response status should be 200
    And the delete response should "successful deletion"

  @integration @regression @BOOKS-AUTHORS-API-008
  Scenario: Relink an existing author from one created book to another created book
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Original Link          |
      | description | First integration book |
      | pageCount   | 220                    |
      | excerpt     | Initial relationship   |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Linked          |
      | lastName  | Author          |
    Then the response status should be 200
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Updated Link           |
      | description | Second integration book |
      | pageCount   | 240                    |
      | excerpt     | Updated relationship   |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User updates the created author with the following data:
      | id        | CREATED_AUTHOR_ID |
      | idBook    | CREATED_BOOK_ID   |
      | firstName | Relinked          |
      | lastName  | Author            |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
    And the author response should satisfy "preserve the linked book id from the request payload"

  @integration @negative @BOOKS-AUTHORS-API-009
  Scenario: Create another author using a deleted created book id still succeeds
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Deleted Parent         |
      | description | Referential demo       |
      | pageCount   | 180                    |
      | excerpt     | Deleted parent book    |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | First           |
      | lastName  | Author          |
    Then the response status should be 200
    When User deletes the created book
    Then the response status should be 200
    And the delete response should "successful deletion"
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Second          |
      | lastName  | Author          |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"

  @integration @negative @BOOKS-AUTHORS-API-010
  Scenario: Update a linked author after deleting the created book still succeeds
    When User creates a book with the following data:
      | id          | AUTO_ID                |
      | title       | Orphaned Relationship  |
      | description | Parent deletion flow   |
      | pageCount   | 205                    |
      | excerpt     | Orphaned author update |
      | publishDate | NOW                    |
    Then the response status should be 200
    When User creates an author with the following data:
      | id        | AUTO_ID         |
      | idBook    | CREATED_BOOK_ID |
      | firstName | Before          |
      | lastName  | Delete          |
    Then the response status should be 200
    When User deletes the created book
    Then the response status should be 200
    And the delete response should "successful deletion"
    When User updates the created author with the following data:
      | id        | CREATED_AUTHOR_ID |
      | idBook    | CREATED_BOOK_ID   |
      | firstName | After             |
      | lastName  | Delete            |
    Then the response status should be 200
    And the author response should satisfy "match the request payload"
