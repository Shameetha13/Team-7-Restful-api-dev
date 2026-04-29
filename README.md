# Team 7 — RESTful API Test Automation Framework

**BDD-driven API test automation framework** built with Java, Rest Assured, and Cucumber to validate the [restful-api.dev](https://api.restful-api.dev) platform. Developed as part of our sprint evaluation, this project covers end-to-end testing of Public Object Management, User Authentication, and Authenticated Collection Management.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Test Coverage](#test-coverage)
- [Prerequisites](#prerequisites)
- [Setup & Configuration](#setup--configuration)
- [Running the Tests](#running-the-tests)
- [Test Reports](#test-reports)
- [Test Data](#test-data)
- [Team Members](#team-members)

---

## Project Overview

This framework automates API testing for `https://api.restful-api.dev` — a free, hosted REST API used to practice and learn API automation. Our suite validates all major API flows through **60 test cases** spanning three feature areas:

| Feature Area | Scope | Test Cases |
|---|---|---|
| Public Object Management | CRUD on global objects (no auth) | TC-01 to TC-25 |
| User Authentication | Register & Login flows | TC-26 to TC-35 |
| Authenticated Collection Management | CRUD on private collections | TC-36 to TC-60 |

The framework follows **Behaviour Driven Development (BDD)** principles using Gherkin syntax, making test scenarios readable by both technical and non-technical stakeholders.

---

## Tech Stack

| Tool / Library | Version | Purpose |
|---|---|---|
| Java | 11+ | Core programming language |
| Maven | 3.x | Build tool & dependency management |
| Rest Assured | 4.5.1 | HTTP request building & response validation |
| Cucumber | 7.33.0 | BDD framework (Gherkin feature files) |
| TestNG | Latest | Test runner & execution lifecycle |
| Jackson Databind | 2.17.2 | JSON serialization / deserialization |
| Apache POI | 5.2.3 | Reading Excel-based test data (DDT) |
| ExtentReports | 1.14.0 | HTML test execution reports |
| Hamcrest | 3.0 | Fluent assertion library |
| Commons IO | 2.15.1 | File I/O utilities |

---

## Project Structure

```
Team-7-Restful-api-dev/
│
├── src/
│   ├── main/java/
│   │   ├── pojoclass/
│   │   │   ├── AuthRequest.java          # POJO for auth request payload
│   │   │   └── ObjectAndCollection.java  # POJO for object/collection payload
│   │   └── utils/
│   │       ├── RestUtility.java          # Reusable REST methods (GET/POST/PUT/PATCH/DELETE)
│   │       ├── ExcelUtility.java         # Apache POI helper for DDT from Excel
│   │       ├── FileUtility.java          # config.properties reader
│   │       └── JavaUtility.java          # General Java helper methods
│   │
│   └── test/
│       ├── java/
│       │   ├── hooks/
│       │   │   └── Hooks.java            # Cucumber Before/After lifecycle hooks
│       │   ├── runner/
│       │   │   └── TestRunner.java       # Cucumber + TestNG test runner
│       │   └── stepdefinitions/
│       │       ├── AuthSteps.java        # Step defs for Auth feature
│       │       ├── CollectionSteps.java  # Step defs for Collection Management
│       │       └── ObjectSteps.java      # Step defs for Object Management
│       │
│       └── resources/
│           ├── features/
│           │   ├── Auth.feature
│           │   ├── CollectionManagement.feature
│           │   └── ObjectManagement.feature
│           ├── testdata/
│           │   └── Group 7 DDT.xlsx      # Excel-based Data Driven Test data
│           ├── config.properties         # Base URL, API keys, endpoints
│           ├── extent-config.xml         # ExtentReports configuration
│           └── extent.properties         # ExtentReports adapter settings
│
├── target/
│   ├── ExtentReports/extent-report.html  # ExtentReports HTML output
│   └── report.html                       # Cucumber HTML report
│
└── pom.xml                               # Maven project configuration
```

---

## Test Coverage

### Feature 1: Public Object Management (`ObjectManagement.feature`)

| Rule | Test Cases | Description |
|---|---|---|
| TS-01 | TC-01 to TC-05 | Retrieve all public objects & filter by ID |
| TS-02 | TC-06 to TC-08 | Retrieve a single object by valid/invalid ID |
| TS-03 | TC-09 to TC-13 | Create new objects (valid, duplicate, malformed, wrong content-type) |
| TS-04 | TC-14 to TC-16 | Full update (PUT) on existing/non-existent objects |
| TS-05 | TC-17 to TC-21 | Partial update (PATCH) — valid & invalid scenarios |
| TS-06 | TC-22 to TC-25 | Delete objects — existing, deleted, invalid, and reserved IDs |

### Feature 2: User Authentication (`Auth.feature`)

| Rule | Test Cases | Description |
|---|---|---|
| TS-07 | TC-26 to TC-30 | Register: valid, duplicate, simple password (defect), missing field |
| TS-08 | TC-31 to TC-35 | Login: valid credentials, wrong credentials, no API key, missing field |

### Feature 3: Authenticated Collection Management (`CollectionManagement.feature`)

| Rule | Test Cases | Description |
|---|---|---|
| TS-09 | TC-36 to TC-39 | GET all collections — authenticated, empty, invalid/missing API key |
| TS-10 | TC-40 to TC-43 | GET single object in a collection — valid, invalid key, invalid ID/collection |
| TS-11 | TC-44 to TC-46 | Filter/list objects in a collection — valid, non-existing, response time |
| TS-12 | TC-47 to TC-50 | POST — add item to collection (valid, missing field defect, malformed) |
| TS-13 | TC-51 to TC-54 | PUT — full update on collection object |
| TS-14 | TC-55 to TC-57 | PATCH — partial update on collection object |
| TS-15 | TC-58 to TC-60 | DELETE — existing item, non-existent, another user's collection |

---

## Prerequisites

Ensure the following are installed on your machine:

- **Java JDK 11 or higher** — [Download](https://adoptium.net/)
- **Apache Maven 3.x** — [Download](https://maven.apache.org/download.cgi)
- **Git** — [Download](https://git-scm.com/)
- An IDE such as **Eclipse** or **IntelliJ IDEA** (Eclipse project files included)

Verify your installation:
```bash
java -version
mvn -version
```

---

## Setup & Configuration

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Team-7-Restful-api-dev
```

### 2. Configure Test Parameters

All configuration is managed through `src/test/resources/config.properties`:

```properties
# Base URL of the API under test
base.url=https://api.restful-api.dev

# API key for authenticated requests
api.key=<your-api-key>

# Alternate API key (used for cross-user validation scenarios)
collection.alt.api.key=<alternate-api-key>

# Endpoints
endpoint.register=/register
endpoint.login=/login
endpoint.objects=/objects
endpoint.collections=/collections
```

> ⚠️ **Note:** The `api.key` is tied to a user account on restful-api.dev. You can register for a free API key at [https://api.restful-api.dev](https://api.restful-api.dev). Replace the existing key with your own before running the tests.

### 3. Install Dependencies

```bash
mvn clean install -DskipTests
```

---

## Running the Tests

### Run All Tests

```bash
mvn test
```

### Run a Specific Feature

Use the `-Dcucumber.filter.tags` flag to run tests by tag:

```bash
# Run only the delete object test
mvn test -Dcucumber.filter.tags="@DeleteObject"
```

### Run via IDE

1. Open the project in Eclipse or IntelliJ IDEA.
2. Navigate to `src/test/java/runner/TestRunner.java`.
3. Right-click → **Run As → TestNG Test**.

---

## Test Reports

After test execution, reports are generated in the following locations:

| Report | Location | Description |
|---|---|---|
| **ExtentReport** | `target/ExtentReports/extent-report.html` | Rich HTML report with pass/fail stats, scenario details, and timeline |
| **Cucumber HTML** | `target/report.html` | Standard Cucumber HTML report |
| **TestNG Report** | `test-output/index.html` | TestNG suite-level execution summary |
| **JUnit XML** | `test-output/junitreports/` | Machine-readable XML for CI/CD integration |

Open any `.html` file in a browser to view the full test execution results.

---

## Test Data

Data Driven Testing (DDT) is powered by an Excel workbook:

**`src/test/resources/testdata/Group 7 DDT.xlsx`**

The workbook contains multiple sheets corresponding to different test scenarios. The `ExcelUtility` class handles all reads via Apache POI, supporting:

- Row-based data retrieval by sheet name and row index
- Full row-to-map conversion (header → value)
- Dynamic row count detection

---

## Team Members

| Name | Contribution |
|---|---|
| **Shameetha Ravikumar** | Authentication regitster & login (TS-07, TS-08), Collection add item (TS-12) |
| **Kamala Kannan** | Object filter/list (TS-01), PATCH update (TS-05), Collection GET (TS-09) |
| **Varshinee** | Object creation (TS-03), PUT update (TS-04), Collection single object GET (TS-10) |
| **Manish** | Collection filter/list (TS-11), PUT update (TS-13), PATCH update (TS-14) |
| **Barath** | Object retrieve (TS-02), Object delete (TS-06), Collection DELETE (TS-15) |

---

## Known Defects

The following test cases document confirmed API defects identified during the sprint:

- **TC-11** — Creating an object with a malformed payload (e.g., string price) returns `200 OK` instead of `400 Bad Request`.
- **TC-18** — PATCH with an incorrect data type returns `200 OK` instead of `400 Bad Request`.
- **TC-28** — Register with a simple/weak password returns `200 OK` instead of `400 Bad Request`.
- **TC-49** — POST Creating an object with a missing field in the payload returns `200 OK` instead of `400 Bad Request`.
- **TC-52** — PUT update with missing mandatory fields does not return the expected `400 Bad Request`.

---

*Sprint Evaluation Project — Team 7*
