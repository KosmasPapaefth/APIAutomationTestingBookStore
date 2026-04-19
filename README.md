# Online Bookstore API Automation Framework

API automation framework for the FakeRestAPI Online Bookstore service.

This project demonstrates a clean Java API test automation design using Cucumber BDD, Rest Assured, JUnit 5, Jackson, Maven reporting, GitHub Actions, and Xray-ready Cucumber JSON output.

## Tech Stack

- Java 17
- Maven
- Rest Assured
- Cucumber BDD
- JUnit 5
- Jackson
- PicoContainer for scenario-scoped dependency injection
- Cucumber HTML and JSON reporting
- Masterthought Cucumber Report
- GitHub Actions CI
- Xray-ready merged Cucumber JSON

## API Under Test

Base URL:

```text
https://fakerestapi.azurewebsites.net
```

Covered endpoints:

- `GET /api/v1/Books`
- `GET /api/v1/Books/{id}`
- `POST /api/v1/Books`
- `PUT /api/v1/Books/{id}`
- `DELETE /api/v1/Books/{id}`
- `GET /api/v1/Authors`
- `GET /api/v1/Authors/{id}`
- `POST /api/v1/Authors`
- `PUT /api/v1/Authors/{id}`
- `DELETE /api/v1/Authors/{id}`

## Framework Architecture

```text
src
|-- main
|   `-- java/com/bookstore
|       |-- client
|       |-- config
|       |-- models
|       `-- utils
`-- test
    |-- java/com/bookstore
    |   |-- assertions
    |   |-- context
    |   |-- hooks
    |   |-- runners
    |   |-- stepdefinitions
    |   `-- utils
    `-- resources
        |-- config.properties
        |-- cucumber.properties
        `-- features
.github/workflows
`-- api-tests.yml
```

## Engineering Design

The framework is intentionally practical and maintainable:

- API calls are centralized in dedicated client classes.
- Rest Assured defaults are configured once in `BaseApiClient`.
- Environment and endpoint configuration is centralized in `ConfigManager`.
- Step definitions are thin and only orchestrate client calls, context storage, and assertions.
- Assertions are centralized in `BookAssertions`, `AuthorAssertions`, and `ApiAssertions`.
- Request payloads are DataTable-driven instead of hardcoded in step definitions.
- Dynamic placeholders are resolved by reusable mapper/helper classes.
- Scenario state is stored in `TestContext`.
- Reporting and Xray preparation are part of the Maven `verify` lifecycle.

## Test Coverage

### Books API

Coverage includes:

- Retrieve all books
- Retrieve book by valid id
- Create a new book
- Update an existing book
- Delete an existing book
- Retrieve non-existing, zero-id, and negative-id books
- Create book with null, empty, or missing title/text fields
- Create or update book with negative, zero, and very large page count
- Create book with duplicate id
- Create or update book with malformed publish date
- Delete non-existing or zero-id book

### Authors API

Coverage includes:

- Retrieve all authors
- Retrieve author by valid id
- Create a new author
- Update an existing author
- Delete an existing author
- Retrieve non-existing, zero-id, and negative-id authors
- Create author with null, empty, or missing first/last name
- Create author with missing, negative, zero, or very large `idBook`
- Create author with duplicate id
- Update non-existing author
- Update author with empty/null values and negative linked book id
- Delete non-existing, zero-id, and negative-id author

### Books and Authors Integration

Cross-entity scenarios validate flows where `author.idBook` links to a book:

- Create a book and then create a linked author
- Retrieve created book and author and document demo API non-persistence
- Update an author linked to a created book
- Delete an author linked to a created book
- Create author with a non-existing book id
- Create multiple authors for the same book
- Delete a created book after linked author creation
- Relink an author from one created book to another
- Create another author using a deleted created book id
- Update a linked author after deleting the created book

## DataTable-Driven Payloads

Create and update scenarios use Cucumber DataTables for dynamic payload construction.

Example:

```gherkin
When User creates a book with the following data:
  | id          | AUTO_ID       |
  | title       | Clean Code    |
  | description | API test book |
  | pageCount   | 250           |
  | excerpt     | Useful text   |
  | publishDate | NOW           |
```

Supported placeholders:

- `AUTO_ID`
- `AUTO_BOOK_ID`
- `CREATED_BOOK_ID`
- `CREATED_AUTHOR_ID`
- `NOW`
- `EMPTY`
- `NULL`

## Configuration

Configuration is stored in:

```text
src/test/resources/config.properties
```

Supported configuration areas:

- Active environment selection
- Environment-specific base URLs
- Books and Authors endpoints
- Default `Content-Type` and `Accept` headers
- Logging toggle
- Timeout values
- Xray-ready result path

## Running Tests

Run the full suite:

```bash
mvn clean verify
```

Run tagged subsets:

```bash
mvn clean verify -Dcucumber.filter.tags="@smoke"
mvn clean verify -Dcucumber.filter.tags="@books"
mvn clean verify -Dcucumber.filter.tags="@authors"
mvn clean verify -Dcucumber.filter.tags="@integration"
mvn clean verify -Dcucumber.filter.tags="@negative"
```

## Reports

Generated reports:

- Cucumber HTML report: `target/cucumber-report.html`
- Raw Cucumber JSON: `target/cucumber/raw/cucumber-report.json`
- Merged Xray-ready JSON: `target/cucumber/cucumber.json`
- Masterthought report: `target/cucumber-report/`
- Surefire reports: `target/surefire-reports/`

The Maven `verify` phase generates the merged JSON file used for future Xray publishing.

## CI/CD

GitHub Actions workflow:

```text
.github/workflows/api-tests.yml
```

The workflow runs on:

- `push`
- `pull_request`
- `workflow_dispatch`

It performs:

- Java 17 setup using Temurin
- Maven dependency caching
- `mvn clean verify`
- Report artifact upload with `if: always()`

Uploaded artifacts:

- `target/surefire-reports`
- `target/cucumber/cucumber.json`
- `target/cucumber-report`
- `target/cucumber-report.html`

## Xray Readiness

The project is prepared for Xray integration but does not publish results automatically.

Implemented:

- Stable Xray-style scenario tags, such as `@BOOKS-API-001`, `@AUTHORS-API-001`, and `@BOOKS-AUTHORS-API-001`
- Merged upload-ready JSON at `target/cucumber/cucumber.json`
- Disabled GitHub Actions placeholder for future Xray upload

Not implemented:

- Real Xray API publishing
- Hardcoded credentials or secrets

Expected future CI secrets:

- `XRAY_CLIENT_ID`
- `XRAY_CLIENT_SECRET`
- `XRAY_API_URL`

## Demo API Behavior

FakeRestAPI is intentionally permissive and does not behave like a strict production API.

Observed behavior:

- Invalid lookups usually return `404`.
- Many invalid create/update payloads still return `200`.
- Duplicate ids can be accepted.
- Authors can reference non-existing books.
- Created entities are echoed in responses but are not reliably persisted.
- Delete operations often return `200`, even for non-existing ids.

The negative scenarios document actual demo API behavior rather than ideal business validation rules.

## Future Enhancements

The framework is intentionally single-threaded for assessment clarity.

Potential future improvements:

- Add parallel execution after validating scenario isolation and report merging.
- Enable real Xray publishing through CI secrets.
- Add contract/schema validation if the API contract becomes stable.
- Add retry handling for transient public demo API outages.

## Repository Hygiene

Generated files are excluded through `.gitignore`, including:

- `target/`
- `.idea/`
- compiled classes
- local logs

This keeps the repository focused on source code, tests, configuration, CI/CD, and documentation.
