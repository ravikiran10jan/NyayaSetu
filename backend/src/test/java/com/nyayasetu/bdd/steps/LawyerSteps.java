package com.nyayasetu.bdd.steps;

import com.nyayasetu.dto.LawyerApplicationRequest;
import com.nyayasetu.model.Lawyer;
import com.nyayasetu.repository.LawyerRepository;
import com.nyayasetu.service.LawyerService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class LawyerSteps {

    private LawyerService lawyerService;
    private LawyerRepository lawyerRepository;

    private Lawyer lawyer;

    @Before
    public void setUp() {
        lawyerRepository = Mockito.mock(LawyerRepository.class);
        lawyerService = new LawyerService(lawyerRepository);
    }

    @When("a lawyer application is submitted with name {string} email {string} bar council {string} enrolment {string}")
    public void aLawyerApplicationIsSubmittedWithNameEmailBarCouncilEnrolment(String name, String email, String barCouncil, String enrolmentNumber) {
        try {
            LawyerApplicationRequest request = new LawyerApplicationRequest();
            request.setName(name);
            request.setEmail(email);
            request.setBarCouncil(barCouncil);
            request.setEnrolmentNumber(enrolmentNumber);

            Mockito.when(lawyerRepository.save(any(Lawyer.class))).thenAnswer(invocation -> {
                Lawyer l = invocation.getArgument(0);
                l.setId("generated-lawyer-id");
                return l;
            });

            lawyer = lawyerService.submitApplication(request);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("a lawyer is created with status {string}")
    public void aLawyerIsCreatedWithStatus(String expectedStatus) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(lawyer);
        assertEquals(expectedStatus, lawyer.getStatus());
    }

    @Then("the lawyer name is {string}")
    public void theLawyerNameIs(String expectedName) {
        assertEquals(expectedName, lawyer.getName());
    }

    @Given("a lawyer with id {string} in status {string}")
    public void aLawyerWithIdInStatus(String lawyerId, String status) throws Exception {
        lawyer = new Lawyer();
        lawyer.setId(lawyerId);
        lawyer.setStatus(status);

        Mockito.when(lawyerRepository.findById(lawyerId)).thenReturn(lawyer);
        Mockito.when(lawyerRepository.update(any(Lawyer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("an admin updates lawyer {string} status to {string} with note {string}")
    public void anAdminUpdatesLawyerStatusToWithNote(String lawyerId, String status, String verificationNote) {
        try {
            lawyer = lawyerService.updateLawyerStatus(lawyerId, status, verificationNote, "admin@example.com");
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the lawyer status is {string}")
    public void theLawyerStatusIs(String expectedStatus) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(lawyer);
        assertEquals(expectedStatus, lawyer.getStatus());
    }

    @Then("the lawyer verifiedAt is set")
    public void theLawyerVerifiedAtIsSet() {
        assertNotNull(lawyer.getVerifiedAt());
    }

    @Then("the lawyer verificationNote is {string}")
    public void theLawyerVerificationNoteIs(String expectedNote) {
        assertEquals(expectedNote, lawyer.getVerificationNote());
    }
}
