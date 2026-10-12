package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProjectSubmissionPage extends BasePage {

    private static final int TIMEOUT_SECONDS = 10;

    private final By submitNavTab = By.cssSelector("a[href*='submit']");
    private final By formHeader = By.xpath("//h2[normalize-space()='Submit a project idea']");
    private final By activeCohortTitle = By.xpath("//*[normalize-space()='AI-Assisted Engineering']");
    private final By evaluationCriteriaSection = By.xpath("//*[normalize-space()='EVALUATION CRITERIA']");
    private final By minimumAlignmentScore = By.xpath("//*[normalize-space()='70%']");
    private final By projectTitleInput = By.name("projectTitle");
    private final By problemStatementInput = By.name("problemStatement");
    private final By objectivesInput = By.name("objectives");
    private final By stackSelectDropdown = By.cssSelector(".stack-select");
    private final By stackOptions = By.cssSelector(".stack-option");
    private final By documentationLinkInput = By.name("documentationLink");
    private final By submitForAnalysisButton = By.cssSelector("form button[type='submit']");
    private final By validationAlert = By.cssSelector(".alert.error, .alert");
    private final By projectTitleError = errorFor("projectTitle");
    private final By problemStatementError = errorFor("problemStatement");
    private final By objectivesError = errorFor("objectives");
    private final By documentationLinkError = errorFor("documentationLink");
    private final By technologyStackError = By.xpath(
            "//*[contains(@class,'stack-select')]/ancestor::*[contains(@class,'field')][1]"
                    + "//*[contains(@class,'field-error')]");

    public ProjectSubmissionPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToSubmit() {
        clickElement(WaitUtils.waitForElementClickable(driver, submitNavTab, TIMEOUT_SECONDS));
        WaitUtils.waitForUrlContains(driver, "/submit", TIMEOUT_SECONDS);
    }

    public boolean isSubmissionFormDisplayed() {
        return isDisplayed(formHeader) && isDisplayed(projectTitleInput);
    }

    public boolean isCohortCriteriaDisplayed() {
        return isDisplayed(activeCohortTitle) && isDisplayed(evaluationCriteriaSection);
    }

    public boolean isMinimumAlignmentRequirementDisplayed() {
        return isDisplayed(minimumAlignmentScore);
    }

    public String getActiveCohortTitle() {
        return getText(activeCohortTitle);
    }

    public String getMinimumAlignmentScore() {
        return getText(minimumAlignmentScore);
    }

    public void enterProjectTitle(String title) {
        setFieldValue(projectTitleInput, title);
    }

    public void enterProblemStatement(String problemStatement) {
        setFieldValue(problemStatementInput, problemStatement);
    }

    public void enterObjectives(String objectives) {
        setFieldValue(objectivesInput, objectives);
    }

    public void enterDocumentationLink(String link) {
        setFieldValue(documentationLinkInput, link);
    }

    public void fillProjectForm(String title, String problemStatement, String objectives,
                                String technologies, String documentationLink) {
        enterProjectTitle(title);
        enterProblemStatement(problemStatement);
        enterObjectives(objectives);
        selectTechnologies(technologies);
        enterDocumentationLink(documentationLink);
    }

    public void selectTechnologies(String commaSeparatedTechnologies) {
        if (commaSeparatedTechnologies == null || commaSeparatedTechnologies.trim().isEmpty()) {
            return;
        }

        clickElement(WaitUtils.waitForElementClickable(driver, stackSelectDropdown, TIMEOUT_SECONDS));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(stackOptions));

        for (String technology : commaSeparatedTechnologies.split(",")) {
            selectTechnology(technology.trim());
        }

        clickElement(WaitUtils.waitForElementClickable(driver, formHeader, TIMEOUT_SECONDS));
    }

    public void finishEditing() {
        clickElement(WaitUtils.waitForElementClickable(driver, formHeader, TIMEOUT_SECONDS));
    }

    public void clickSubmitForAnalysis() {
        clickElement(WaitUtils.waitForElementClickable(driver, submitForAnalysisButton, TIMEOUT_SECONDS));
    }

    public boolean isSubmitButtonEnabled() {
        return driver.findElement(submitForAnalysisButton).isEnabled();
    }

    public boolean isValidationAlertDisplayed() {
        return isDisplayed(validationAlert);
    }

    public String getValidationAlertText() {
        return getOptionalText(validationAlert);
    }

    public String getProjectTitleError() {
        return getOptionalText(projectTitleError);
    }

    public String getProblemStatementError() {
        return getOptionalText(problemStatementError);
    }

    public String getObjectivesError() {
        return getOptionalText(objectivesError);
    }

    public String getTechnologyStackError() {
        return getOptionalText(technologyStackError);
    }

    public String getDocumentationLinkError() {
        return getOptionalText(documentationLinkError);
    }

    public int getProjectTitleLength() {
        return getValueLength(projectTitleInput);
    }

    public int getProblemStatementLength() {
        return getValueLength(problemStatementInput);
    }

    public int getObjectivesLength() {
        return getValueLength(objectivesInput);
    }

    public boolean isSubmissionBlocked() {
        return getCurrentUrl().contains("/submit") && isSubmissionFormDisplayed();
    }

    private static By errorFor(String fieldName) {
        return By.xpath("//*[@name='" + fieldName
                + "']/ancestor::*[contains(@class,'field')][1]//*[contains(@class,'field-error')]");
    }

    private void setFieldValue(By locator, String value) {
        WebElement field = WaitUtils.waitForElementVisible(driver, locator, TIMEOUT_SECONDS);
        field.clear();
        if (value != null) {
            field.sendKeys(value);
        }
    }

    private void selectTechnology(String technology) {
        if (technology.isEmpty()) {
            return;
        }

        for (WebElement option : driver.findElements(stackOptions)) {
            if (option.getText().trim().equalsIgnoreCase(technology)) {
                clickElement(option);
                return;
            }
        }

        throw new IllegalArgumentException("Technology option not found: " + technology);
    }

    private int getValueLength(By locator) {
        String value = driver.findElement(locator).getAttribute("value");
        return value == null ? 0 : value.length();
    }

    private String getOptionalText(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return elements.isEmpty() ? "" : elements.get(0).getText().trim();
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (RuntimeException exception) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}
