# ProjectLens Form Validation — Seven-Day Scrum Report

## User Story

As a Pod Lead, I want the project submission form to validate required fields, input formats, technology selections, and field lengths so that only complete and meaningful project ideas are submitted for analysis.

## Description

This work covers the ProjectLens form available at `/submit`. It uses Page Object Model to keep Selenium locators and browser actions separate from TestNG assertions. The implementation validates form visibility, cohort information, mandatory fields, whitespace-only input, documentation URLs, character limits, technology selection, successful submission, and low-alignment results.

## Files in Scope

| File | Purpose |
|---|---|
| `src/main/java/com/example/pages/ProjectSubmissionPage.java` | Page Object containing form locators, actions, validation accessors, and reusable helpers |
| `src/test/java/com/example/tests/FormValidationTest.java` | TestNG scenarios and assertions for PL-04 through PL-07 and additional boundary validation |

Line numbers below refer to the final code snapshot. Each daily submission should contain the described logical change, not an incomplete Java class copied in isolation.

---

## Day 1 — Submission Page Foundation

### User Story

As a Pod Lead, I want automation to open the submission page and identify its controls so that later validation scenarios use stable reusable elements.

### Description

Created the Page Object foundation, central timeout, form locators, validation locators, constructor, and navigation method. Locators were matched against the running ProjectLens page.

### Code to Paste and Submit

| File | Lines | Code responsibility |
|---|---:|---|
| `ProjectSubmissionPage.java` | 1–46 | Package, imports, class, timeout, form locators, validation locators, constructor, and `navigateToSubmit()` |

### Daily Explanation

- Created a dedicated Page Object for `/submit`.
- Kept locators outside test methods.
- Used `WaitUtils` before navigation clicks.
- Verified navigation by waiting for `/submit` in the URL.

### Acceptance Result

Automation can navigate from the dashboard to the project submission route and reuse the same page object in every scenario.

### Suggested Commit

`feat: add project submission page object foundation`

---

## Day 2 — Page and Cohort Verification

### User Story

As a Pod Lead, I want to see the active cohort criteria and minimum alignment score before submitting an idea.

### Description

Added readable page-state methods for form visibility, cohort criteria, active cohort title, and minimum alignment requirement. Added the PL-04 test using Excel test data.

### Code to Paste and Submit

| File | Lines | Code responsibility |
|---|---:|---|
| `ProjectSubmissionPage.java` | 48–66 | Form, cohort, title, and alignment visibility methods |
| `FormValidationTest.java` | 1–40 | Imports, test setup, login, page initialization, and PL-04 test |

### Daily Explanation

- Logged in with Pod Lead test data.
- Opened the form through the Page Object.
- Verified the expected route.
- Verified `AI-Assisted Engineering` and the `70%` threshold.

### Acceptance Result

PL-04 confirms that the correct user can reach the form and see the active cohort configuration.

### Suggested Commit

`test: verify submission page and active cohort details`

---

## Day 3 — Modular Form Entry

### User Story

As a Pod Lead, I want automation to enter project information through reusable methods so that tests remain short and understandable.

### Description

Added individual input methods and one high-level `fillProjectForm()` workflow. Centralized field clearing and value entry to remove repeated Selenium code.

### Code to Paste and Submit

| File | Lines | Code responsibility |
|---|---:|---|
| `ProjectSubmissionPage.java` | 68–91 | Individual field entry methods and `fillProjectForm()` |
| `ProjectSubmissionPage.java` | 170–176 | Shared `setFieldValue()` helper |

### Daily Explanation

- Added separate methods for title, problem statement, objectives, and documentation link.
- Added one method for filling a complete project form.
- Allowed fields to be cleared and populated consistently.
- Kept tests free from direct Selenium element handling.

### Acceptance Result

A complete project form can be populated with one readable Page Object call while individual fields remain independently testable.

### Suggested Commit

`refactor: add reusable project form input actions`

---

## Day 4 — Technology Selection and Stable Interaction

### User Story

As a Pod Lead, I want one or more technologies selected reliably so that the required technology stack is correctly included with my idea.

### Description

Implemented comma-separated technology selection with explicit waits. Missing technologies now raise a clear exception instead of being silently ignored. Added reusable click fallback for intercepted elements.

### Code to Paste and Submit

| File | Lines | Code responsibility |
|---|---:|---|
| `ProjectSubmissionPage.java` | 93–110 | Dropdown opening, explicit wait, multi-technology selection, and dropdown closing |
| `ProjectSubmissionPage.java` | 178–191 | Exact technology matching and missing-option failure |
| `ProjectSubmissionPage.java` | 203–209 | Reusable normal-click and JavaScript-click fallback |

### Daily Explanation

- Removed fixed sleeps from technology selection.
- Waited for visible technology options.
- Split Excel comma-separated technologies into individual selections.
- Re-read options for each selection to reduce stale-element risk.
- Failed clearly when requested data does not exist in the UI.

### Acceptance Result

Technology selection supports single and multiple values and reports incorrect Excel values immediately.

### Suggested Commit

`feat: make technology selection explicit and reliable`

---

## Day 5 — Required-Field Validation

### User Story

As a Pod Lead, I want clear errors for missing mandatory fields so that I know what must be completed before submission.

### Description

Added submission actions, alert accessors, field-specific validation accessors, and blocked-submission verification. Replaced generic error-list searching with direct methods for each field. Added PL-05 assertions.

### Code to Paste and Submit

| File | Lines | Code responsibility |
|---|---:|---|
| `ProjectSubmissionPage.java` | 109–147 | Finish editing, submit action, button state, alert text, and field-specific errors |
| `ProjectSubmissionPage.java` | 161–168 | Blocked-submission check and reusable field-error locator |
| `ProjectSubmissionPage.java` | 198–201 | Safe optional-error text retrieval |
| `FormValidationTest.java` | 42–57 | PL-05 required-field validation test |

### Daily Explanation

- Submitted the form with all required fields empty.
- Verified the global validation alert.
- Verified title, problem statement, objectives, and technology errors independently.
- Confirmed the optional documentation link has no required-field error.
- Confirmed the user remains on `/submit`.

### Acceptance Result

PL-05 reports exactly which required field failed instead of returning one ambiguous collection of messages.

### Suggested Commit

`test: add field-specific required validation coverage`

---

## Day 6 — Boundary and Negative Validation

### User Story

As a Pod Lead, I want meaningless whitespace, invalid URLs, and excessive input lengths rejected so that submitted information remains usable.

### Description

Added focused negative tests based on behavior observed on the live page. These tests expose current application gaps without creating a project submission.

### Code to Paste and Submit

| File | Lines | Code responsibility |
|---|---:|---|
| `ProjectSubmissionPage.java` | 117–119 | Submit-button enabled state |
| `ProjectSubmissionPage.java` | 149–159 | Field-value length methods |
| `ProjectSubmissionPage.java` | 193–196 | Shared value-length helper |
| `FormValidationTest.java` | 59–93 | Whitespace, invalid URL, and maximum-length tests |
| `FormValidationTest.java` | 143–147 | Reusable test-string generator |

### Daily Explanation

- Added a whitespace-only required-field test.
- Added invalid documentation URL validation.
- Verified the submit button cannot proceed with invalid input.
- Verified 50-character title and 500-character textarea limits.
- Avoided actual submission in all negative cases.

### Acceptance Result

Tests detect the live defects where whitespace-only values and malformed documentation URLs are currently accepted.

### Suggested Commit

`test: cover whitespace url and length validation boundaries`

---

## Day 7 — End-to-End Submission and Final Cleanup

### User Story

As a Pod Lead, I want a valid idea saved and its evaluation result visible so that I can track whether it qualifies for trainer review.

### Description

Completed PL-06 and PL-07, consolidated common test setup, removed duplicate data-provider execution, removed unnecessary comments, and verified that only Form Validation files changed.

### Code to Paste and Submit

| File | Lines | Code responsibility |
|---|---:|---|
| `FormValidationTest.java` | 95–115 | PL-06 complete-form submission and project-card verification |
| `FormValidationTest.java` | 117–132 | PL-07 below-70 score, status, analysis, matched criteria, and missing criteria |
| `FormValidationTest.java` | 134–147 | Shared navigation, Excel data, and boundary-data helpers |

### Daily Explanation

- Generated a unique project title to identify the new submission.
- Used `fillProjectForm()` instead of repeating input actions.
- Verified redirect to the submissions page.
- Verified the created project and its current status.
- Verified a below-70 project shows `NEEDS_REVISION`, score, analysis, matched criteria, and missing criteria.
- Removed the duplicate DDT test that repeated PL-04 through PL-07.

### Acceptance Result

The Form Validation module now has a clean Page Object API, focused validation tests, and end-to-end coverage from form opening through evaluation display.

### Suggested Commit

`test: complete project submission and evaluation workflow`

---

## Final Seven-Day Summary

| Day | Deliverable |
|---:|---|
| 1 | Page Object foundation and live locators |
| 2 | Page access and cohort verification |
| 3 | Modular field-entry workflow |
| 4 | Reliable multi-technology selection |
| 5 | Field-specific required validation |
| 6 | Whitespace, URL, and length validation |
| 7 | Successful submission, evaluation verification, and cleanup |

## Verification Notes

- Live page structure was inspected at `https://project-lens-beta.vercel.app/submit`.
- Field-error and technology-option locators were matched against the running DOM.
- No unrelated source files were edited.
- Full Maven execution requires Java 17; the current computer exposes Java 8 to Maven.
- PL-06 creates a real beta submission, so it should run only in an approved test-data window.
