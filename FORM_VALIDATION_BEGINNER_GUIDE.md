# ProjectLens Form Validation — Beginner-Friendly Working Guide

## 1. What Is ProjectLens?

ProjectLens is a web application where a team member can submit a software-project idea. The application compares the idea with predefined cohort requirements and calculates an alignment score.

The application then decides what happens next:

- A strong idea can move to trainer review.
- A weak idea can be marked as needing revision.
- A trainer can approve, reject, or request changes.
- The user can track results and notifications.

The automation project behaves like a person using this website. It opens a browser, enters information, clicks buttons, and checks whether the expected result appears.

---

## 2. What Is My Part?

My responsibility is the **Project Submission Form and Form Validation module**.

The page is:

`https://project-lens-beta.vercel.app/submit`

This module checks whether:

- A Pod Lead can open the submission page.
- Active cohort information is visible.
- Required fields cannot be left empty.
- Whitespace-only values are rejected.
- Input length limits work correctly.
- Documentation links have a valid URL format.
- Technology selections work correctly.
- A complete project idea can be submitted.
- The submitted project appears in the submissions list.
- A project below 70% shows the expected revision status and analysis.

In simple words, my job is to make sure users cannot submit bad or incomplete information and that valid information moves through the correct workflow.

---

## 3. Important Technologies

| Technology | Simple meaning |
|---|---|
| Java | Programming language used to write the automation |
| Selenium WebDriver | Controls the browser like a user |
| TestNG | Runs tests and reports whether they pass or fail |
| Maven | Downloads libraries and starts the test suite |
| Page Object Model | Keeps page controls separate from test assertions |
| Apache POI | Reads test data from Excel |
| Extent Reports | Creates the HTML execution report |

---

## 4. Project Folder Structure

```text
ProjectLens_Test
│
├── pom.xml
├── testng.xml
├── testng-form-validation.xml
│
└── src
    ├── main/java/com/example
    │   ├── pages
    │   │   ├── BasePage.java
    │   │   ├── LoginPage.java
    │   │   ├── ProjectSubmissionPage.java
    │   │   └── SubmissionsPage.java
    │   │
    │   └── utils
    │       ├── ConfigReader.java
    │       ├── DriverManager.java
    │       ├── WaitUtils.java
    │       ├── DataProviders.java
    │       └── ExcelReader.java
    │
    └── test
        ├── java/com/example
        │   ├── base/BaseTest.java
        │   ├── listeners/TestListener.java
        │   └── tests/FormValidationTest.java
        │
        └── resources
            ├── config/config.properties
            └── testdata/TestData.xlsx
```

---

## 5. Complete Working Flow

```text
Maven command
    ↓
TestNG reads testng-form-validation.xml
    ↓
BaseTest starts browser
    ↓
Configuration opens ProjectLens login page
    ↓
Excel provides Pod Lead credentials and test data
    ↓
LoginPage logs in
    ↓
ProjectSubmissionPage opens /submit
    ↓
FormValidationTest performs test scenario
    ↓
ProjectSubmissionPage enters data or reads validation errors
    ↓
TestNG compares actual and expected results
    ↓
TestListener records Pass or Fail
    ↓
Browser closes
```

Every test receives a fresh browser because `BaseTest` starts the browser before each test and closes it afterward.

---

## 6. What Happens When Tests Start?

### Step 1: Maven receives the command

The Form Validation suite can be selected with:

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-form-validation.xml"
```

Maven reads `pom.xml`, loads Selenium, TestNG, Apache POI, and reporting dependencies, and then starts TestNG.

### Step 2: TestNG reads the suite

`testng-form-validation.xml` tells TestNG to execute:

```text
com.example.tests.FormValidationTest
```

### Step 3: BaseTest opens the browser

`BaseTest.java` calls `DriverManager.initDriver()`.

The browser type comes from:

```text
src/test/resources/config/config.properties
```

The browser opens the configured ProjectLens login URL.

### Step 4: The user is logged in

Before every Form Validation test, `loginAsPodLead()` runs.

It performs these actions:

1. Reads Pod Lead credentials from Excel.
2. Creates a `LoginPage` object.
3. Enters the email and password.
4. Clicks Sign in.
5. Creates the page objects needed by the test.

### Step 5: The test opens the submission page

The test calls:

```java
openSubmissionPage();
```

That helper calls:

```java
submissionPage.navigateToSubmit();
```

Selenium clicks the Submit Idea navigation link and waits until the URL contains `/submit`.

### Step 6: The test performs actions

Depending on the scenario, the automation may:

- read cohort information;
- leave fields empty;
- enter whitespace;
- enter an invalid URL;
- enter long values;
- fill the complete form;
- select technologies;
- submit the project.

### Step 7: Assertions check the result

An assertion means: “compare what happened with what should have happened.”

Example:

```java
Assert.assertFalse(submissionPage.getProjectTitleError().isEmpty());
```

Simple meaning:

> The Project Title error must not be empty. If no error appears, fail the test.

### Step 8: Result is reported

`TestListener.java` receives the result.

- Passed test: records Pass.
- Failed test: records failure reason and screenshot.
- Skipped test: records why it was skipped.

The HTML report is created inside `test-output`.

### Step 9: Browser closes

`BaseTest.tearDown()` runs after every test and closes WebDriver.

---

## 7. What Is Page Object Model?

Without Page Object Model, a test may look like this:

```java
driver.findElement(By.name("projectTitle")).sendKeys("My Project");
driver.findElement(By.cssSelector("button[type='submit']")).click();
```

This mixes technical browser code with the test scenario.

With Page Object Model, the test looks like this:

```java
submissionPage.enterProjectTitle("My Project");
submissionPage.clickSubmitForAnalysis();
```

The Page Object knows **how** to interact with the page. The test only describes **what** the user wants to do.

This makes the code:

- easier to read;
- easier to explain;
- easier to reuse;
- easier to update when the UI changes;
- less repetitive.

---

## 8. My Main Page Object

The main Page Object is:

`src/main/java/com/example/pages/ProjectSubmissionPage.java`

It has four main responsibilities.

### A. Store page locators

Locators identify elements on the page.

Examples:

```java
private final By projectTitleInput = By.name("projectTitle");
private final By problemStatementInput = By.name("problemStatement");
private final By submitForAnalysisButton = By.cssSelector("form button[type='submit']");
```

Simple meaning:

- Find the Project Title field by its name.
- Find the Problem Statement field by its name.
- Find the submit button inside the form.

### B. Perform user actions

Examples:

```java
enterProjectTitle(title);
enterProblemStatement(problemStatement);
selectTechnologies(technologies);
clickSubmitForAnalysis();
```

These methods behave like a user typing and clicking.

### C. Read page state

Examples:

```java
isSubmissionFormDisplayed();
getProjectTitleError();
isSubmitButtonEnabled();
isSubmissionBlocked();
```

These methods return information that tests can verify.

### D. Hide technical complexity

`fillProjectForm()` combines multiple actions:

```java
submissionPage.fillProjectForm(
        title,
        problemStatement,
        objectives,
        technologies,
        documentationLink);
```

The test does not need to know how each Selenium operation works.

---

## 9. My Test Class

The main test file is:

`src/test/java/com/example/tests/FormValidationTest.java`

It contains seven focused tests.

### Test 1: PL-04 — Open Submission Page

Checks:

- User reaches `/submit`.
- Submission form appears.
- Active cohort criteria appear.
- Cohort name is correct.
- Minimum score is 70%.

### Test 2: PL-05 — Required Fields

Checks:

- Empty form cannot be submitted.
- Global validation alert appears.
- Project Title error appears.
- Problem Statement error appears.
- Objectives error appears.
- Technology Stack error appears.
- Documentation Link remains optional.
- User stays on `/submit`.

### Test 3: Whitespace Validation

Enters spaces into required fields.

Expected behavior:

- Spaces should be treated like empty input.
- Required-field errors should appear.
- Submission should remain blocked.

This test currently exposes a real application problem: whitespace-only values are treated as valid.

### Test 4: Documentation URL Validation

Enters:

```text
not-a-url
```

Expected behavior:

- URL error should appear.
- Submit button should remain disabled.

This test currently exposes another application problem: the field accepts invalid URL text.

### Test 5: Character Limits

Checks:

- Title accepts a maximum of 50 characters.
- Problem Statement accepts a maximum of 500 characters.
- Objectives accepts a maximum of 500 characters.

The helper creates long test strings instead of manually typing hundreds of characters.

### Test 6: PL-06 — Complete Submission

Checks:

- A complete valid form can be submitted.
- User is redirected to the submissions page.
- New project appears in the list.
- Project displays a current status.

A timestamp is added to the project title so the automation can identify the exact project it created.

### Test 7: PL-07 — Below-70 Evaluation

Checks:

- A project below 70% exists.
- Its status contains `NEEDS_REVISION`.
- Its score is below 70.
- Analysis is present.
- Matched criteria are present.
- Missing criteria are present.

---

## 10. How Excel Test Data Works

Test data is stored in:

`src/test/resources/testdata/TestData.xlsx`

The relevant worksheet is:

```text
FormValidationWorkflow
```

The flow is:

```text
FormValidationTest
    ↓ asks for PL-04, PL-05, PL-06, or PL-07
DataProviders
    ↓
ExcelReader
    ↓ reads TestData.xlsx
Returns one row as Map<String, String>
```

Example:

```java
Map<String, String> data = formData("PL-06");
String title = data.get("Project_Title");
```

Simple meaning:

1. Find the Excel row with `TC_ID = PL-06`.
2. Read its Project Title column.
3. Use that value in the test.

This separates test data from Java logic.

---

## 11. How Technology Selection Works

Excel stores technologies as comma-separated text:

```text
Java, Spring Boot, Angular, REST API
```

The Page Object:

1. Opens the Technology Stack dropdown.
2. Waits for the options to appear.
3. Splits the text at commas.
4. Trims spaces from every value.
5. Finds each option by exact visible text.
6. Clicks each matching option.
7. Fails clearly if a requested option does not exist.
8. Closes the dropdown.

This is better than silently ignoring invalid Excel data.

---

## 12. How Validation Errors Are Read

Each field has its own error method:

```java
getProjectTitleError();
getProblemStatementError();
getObjectivesError();
getTechnologyStackError();
getDocumentationLinkError();
```

The Page Object finds the field container and reads the error belonging to that field.

This is better than reading every error into one list because a failed assertion tells us exactly which field has a problem.

Example failure:

```text
Project Title error was expected but was empty.
```

That is easier to debug than:

```text
Validation errors were incorrect.
```

---

## 13. Difference Between Validation Test and Successful Submission

### Negative validation test

Purpose: prove bad data is rejected.

Examples:

- empty form;
- whitespace-only values;
- invalid URL;
- excessive length.

These tests should not create a project.

### Positive submission test

Purpose: prove valid data is accepted.

PL-06 fills all required fields, selects technologies, submits the form, and checks the created project.

This test changes beta application data, so it should not run repeatedly without coordination.

---

## 14. Why Explicit Waits Are Important

Web pages do not always display elements immediately. Data may load after navigation.

A fixed sleep says:

> Wait exactly this long, whether the page is ready or not.

An explicit wait says:

> Continue as soon as the required element becomes visible or clickable.

This project uses explicit waits for navigation and technology options. That makes tests faster and more reliable than fixed sleeps.

---

## 15. Pass and Fail Examples

### Passing example

```java
Assert.assertEquals(submissionPage.getProjectTitleLength(), 50);
```

Actual length is 50. Expected length is 50. Test passes.

### Failing example

```java
Assert.assertFalse(submissionPage.getDocumentationLinkError().isEmpty());
```

If the application accepts `not-a-url`, no error appears. The returned error is empty. Test fails and exposes the defect.

A failed test does not always mean automation is broken. It can mean automation correctly found an application bug.

---

## 16. Known Live Application Findings

During live inspection of `/submit`, these behaviors were observed:

1. Required fields accept whitespace-only values.
2. Documentation Link accepts text that is not a URL.
3. Submit button remains enabled while the form is invalid.
4. Over-limit input can show a misleading “required” message.
5. Error fields do not expose strong accessibility relationships.
6. Technology selector needs better combobox accessibility metadata.

The new negative tests describe the expected correct behavior. They may fail until the application team fixes these defects.

---

## 17. How to Explain My Work in a Scrum Call

Use this short explanation:

> I worked on the Project Submission Form Validation module. I refactored the submission Page Object so form actions and field errors are reusable and tests remain clean. I added coverage for required fields, whitespace-only values, invalid documentation URLs, character limits, technology selection, successful submission, and below-70 evaluation results. I also removed duplicate test execution and replaced fixed waiting with explicit Selenium waits. Live page analysis identified application gaps in whitespace and URL validation.

---

## 18. How to Explain My Work to an Interviewer

> I used Page Object Model to separate browser interaction from test assertions. The Page Object owns locators and reusable actions such as filling the form, selecting multiple technologies, submitting, and reading field-specific errors. The TestNG class describes business scenarios using Excel data. I added negative boundary tests and an end-to-end positive test. I validated locators against the live DOM and designed negative tests so they do not create unwanted submissions.

---

## 19. Commands I Need to Know

### Check Maven

```powershell
mvn -version
```

### Run only Form Validation

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-form-validation.xml"
```

### Check changed files

```powershell
git status --short
```

### View changes

```powershell
git diff
```

### Important environment note

The project is configured for Java 17. Maven currently detects Java 8 on this computer. Java 17 must be installed and selected before normal compilation and execution.

---

## 20. Final Simple Summary

```text
My test logs in as Pod Lead
    ↓
Opens Submit Idea page
    ↓
Checks form and cohort information
    ↓
Tries invalid values
    ↓
Checks correct validation errors
    ↓
Fills valid information
    ↓
Submits project
    ↓
Checks project in submissions list
    ↓
Checks score, status, and analysis
```

My Page Object controls the page. My test class checks the business rules. Excel supplies the data. TestNG decides Pass or Fail. Extent Reports records the result.
