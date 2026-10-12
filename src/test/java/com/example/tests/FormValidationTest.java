package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.LoginPage;
import com.example.pages.ProjectSubmissionPage;
import com.example.pages.SubmissionsPage;
import com.example.utils.DataProviders;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;

public class FormValidationTest extends BaseTest {

    private ProjectSubmissionPage submissionPage;
    private SubmissionsPage submissionsPage;

    @BeforeMethod
    public void loginAsPodLead() {
        Map<String, String> data = formData("PL-04");
        new LoginPage(driver).login(data.get("User_Email"), data.get("User_Password"));
        submissionPage = new ProjectSubmissionPage(driver);
        submissionsPage = new SubmissionsPage(driver);
    }

    @Test(priority = 1, description = "PL-04: Open the new project submission page")
    public void testPL04OpenNewProjectSubmissionPage() {
        Map<String, String> data = formData("PL-04");

        openSubmissionPage();

        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_Status")));
        Assert.assertTrue(submissionPage.isCohortCriteriaDisplayed());
        Assert.assertEquals(submissionPage.getActiveCohortTitle(), data.get("Expected_Cohort_Title"));
        Assert.assertTrue(submissionPage.isMinimumAlignmentRequirementDisplayed());
        Assert.assertEquals(submissionPage.getMinimumAlignmentScore(), data.get("Expected_Min_Alignment"));
    }

    @Test(priority = 2, description = "PL-05: Validate required fields")
    public void testPL05RequiredFields() {
        Map<String, String> data = formData("PL-05");

        openSubmissionPage();
        submissionPage.clickSubmitForAnalysis();

        Assert.assertTrue(submissionPage.isValidationAlertDisplayed());
        Assert.assertTrue(submissionPage.getValidationAlertText().contains(data.get("Expected_Status")));
        Assert.assertFalse(submissionPage.getProjectTitleError().isEmpty());
        Assert.assertFalse(submissionPage.getProblemStatementError().isEmpty());
        Assert.assertFalse(submissionPage.getObjectivesError().isEmpty());
        Assert.assertFalse(submissionPage.getTechnologyStackError().isEmpty());
        Assert.assertTrue(submissionPage.getDocumentationLinkError().isEmpty());
        Assert.assertTrue(submissionPage.isSubmissionBlocked());
    }

    @Test(priority = 3, description = "Reject whitespace-only required fields")
    public void requiredFieldsShouldRejectWhitespaceOnlyValues() {
        openSubmissionPage();
        submissionPage.enterProjectTitle("   ");
        submissionPage.enterProblemStatement("   ");
        submissionPage.enterObjectives("   ");
        submissionPage.clickSubmitForAnalysis();

        Assert.assertFalse(submissionPage.getProjectTitleError().isEmpty());
        Assert.assertFalse(submissionPage.getProblemStatementError().isEmpty());
        Assert.assertFalse(submissionPage.getObjectivesError().isEmpty());
        Assert.assertTrue(submissionPage.isSubmissionBlocked());
    }

    @Test(priority = 4, description = "Reject invalid documentation URL")
    public void documentationLinkShouldRequireValidUrl() {
        openSubmissionPage();
        submissionPage.enterDocumentationLink("not-a-url");
        submissionPage.finishEditing();

        Assert.assertFalse(submissionPage.getDocumentationLinkError().isEmpty());
        Assert.assertFalse(submissionPage.isSubmitButtonEnabled());
    }

    @Test(priority = 5, description = "Enforce form character limits")
    public void fieldsShouldEnforceMaximumLengths() {
        openSubmissionPage();
        submissionPage.enterProjectTitle(repeat('T', 51));
        submissionPage.enterProblemStatement(repeat('P', 501));
        submissionPage.enterObjectives(repeat('O', 501));

        Assert.assertEquals(submissionPage.getProjectTitleLength(), 50);
        Assert.assertEquals(submissionPage.getProblemStatementLength(), 500);
        Assert.assertEquals(submissionPage.getObjectivesLength(), 500);
    }

    @Test(priority = 6, description = "PL-06: Submit a complete project idea")
    public void testPL06SubmitCompleteProjectIdea() {
        Map<String, String> data = formData("PL-06");
        String uniqueTitle = data.get("Project_Title") + " " + System.currentTimeMillis();

        openSubmissionPage();
        submissionPage.fillProjectForm(
                uniqueTitle,
                data.get("Problem_Statement"),
                data.get("Objectives"),
                data.get("Tech_Stack"),
                data.get("Doc_Link"));
        submissionPage.clickSubmitForAnalysis();

        Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed());
        Assert.assertTrue(submissionsPage.isProjectPresentInList(uniqueTitle));

        WebElement projectCard = submissionsPage.getSubmissionCardByTitle(uniqueTitle);
        Assert.assertNotNull(projectCard);
        Assert.assertFalse(submissionsPage.getCardStatus(projectCard).isEmpty());
    }

    @Test(priority = 7, description = "PL-07: Verify a project evaluated below 70% alignment")
    public void testPL07VerifyProjectEvaluatedBelow70Alignment() {
        Map<String, String> data = formData("PL-07");

        submissionsPage.navigateToSubmissions();
        WebElement projectCard = submissionsPage.findProjectEvaluatedBelowThreshold(70);

        Assert.assertNotNull(projectCard);
        Assert.assertTrue(submissionsPage.getCardStatus(projectCard).contains(data.get("Expected_Status")));

        int score = Integer.parseInt(submissionsPage.getCardScore(projectCard).replace("%", "").trim());
        Assert.assertTrue(score < 70);
        Assert.assertFalse(submissionsPage.getCardAnalysis(projectCard).isEmpty());
        Assert.assertFalse(submissionsPage.getCardMatchedCriteria(projectCard).isEmpty());
        Assert.assertFalse(submissionsPage.getCardMissingCriteria(projectCard).isEmpty());
    }

    private void openSubmissionPage() {
        submissionPage.navigateToSubmit();
        Assert.assertTrue(submissionPage.isSubmissionFormDisplayed());
    }

    private Map<String, String> formData(String testCaseId) {
        return DataProviders.getFormValidationTestCaseData(testCaseId);
    }

    private String repeat(char character, int count) {
        char[] characters = new char[count];
        Arrays.fill(characters, character);
        return new String(characters);
    }
}
