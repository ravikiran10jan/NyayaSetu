package com.nyayasetu.bdd.steps;

import com.nyayasetu.dto.MatterRequest;
import com.nyayasetu.model.LegalMatter;
import com.nyayasetu.model.MatterRecord;
import com.nyayasetu.repository.MatterRecordRepository;
import com.nyayasetu.repository.MatterRepository;
import com.nyayasetu.service.MatterService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class MatterSteps {

    private MatterService matterService;
    private MatterRepository matterRepository;
    private MatterRecordRepository matterRecordRepository;

    private LegalMatter matter;
    private MatterRecord record;

    @Before
    public void setUp() {
        matterRepository = Mockito.mock(MatterRepository.class);
        matterRecordRepository = Mockito.mock(MatterRecordRepository.class);
        matterService = new MatterService(matterRepository, matterRecordRepository);
    }

    @When("a client with email {string} userId {string} creates a matter titled {string} in area {string}")
    public void aClientWithEmailUserIdCreatesAMatterTitledInArea(String ownerEmail, String ownerUserId, String title, String practiceArea) {
        try {
            MatterRequest request = new MatterRequest();
            request.setTitle(title);
            request.setPracticeArea(practiceArea);

            Mockito.when(matterRepository.save(any(LegalMatter.class))).thenAnswer(invocation -> {
                LegalMatter m = invocation.getArgument(0);
                m.setId("generated-matter-id");
                return m;
            });

            matter = matterService.createMatter(request, ownerEmail, ownerUserId);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("a matter is created with status {string}")
    public void aMatterIsCreatedWithStatus(String expectedStatus) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(matter);
        assertEquals(expectedStatus, matter.getStatus());
    }

    @Then("the matter conflictStatus is {string}")
    public void theMatterConflictStatusIs(String expectedConflictStatus) {
        assertEquals(expectedConflictStatus, matter.getConflictStatus());
    }

    @Then("the matter ownerEmail is {string}")
    public void theMatterOwnerEmailIs(String expectedOwnerEmail) {
        assertEquals(expectedOwnerEmail, matter.getOwnerEmail());
    }

    @When("a client creates a matter without specifying urgency")
    public void aClientCreatesAMatterWithoutSpecifyingUrgency() {
        try {
            MatterRequest request = new MatterRequest();
            request.setTitle("Test Matter");
            request.setPracticeArea("Test Area");
            // urgency is null

            Mockito.when(matterRepository.save(any(LegalMatter.class))).thenAnswer(invocation -> {
                LegalMatter m = invocation.getArgument(0);
                m.setId("generated-matter-id");
                return m;
            });

            matter = matterService.createMatter(request, "client@example.com", "client-1");
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the matter urgency is {string}")
    public void theMatterUrgencyIs(String expectedUrgency) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(matter);
        assertEquals(expectedUrgency, matter.getUrgency());
    }

    @Given("a matter with id {string} owned by userId {string}")
    public void aMatterWithIdOwnedByUserId(String matterId, String ownerUserId) throws Exception {
        matter = new LegalMatter();
        matter.setId(matterId);
        matter.setOwnerUserId(ownerUserId);
        matter.setTitle("Original Title");

        Mockito.when(matterRepository.findById(matterId)).thenReturn(matter);
        Mockito.when(matterRepository.update(any(LegalMatter.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("the owner {string} updates matter {string} title to {string}")
    public void theOwnerUpdatesMatterTitleTo(String ownerUserId, String matterId, String newTitle) {
        try {
            MatterRequest request = new MatterRequest();
            request.setTitle(newTitle);

            matter = matterService.updateMatter(matterId, request, ownerUserId);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the matter title is {string}")
    public void theMatterTitleIs(String expectedTitle) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(matter);
        assertEquals(expectedTitle, matter.getTitle());
    }

    @When("user {string} tries to update matter {string}")
    public void userTriesToUpdateMatter(String userId, String matterId) {
        try {
            MatterRequest request = new MatterRequest();
            request.setTitle("Hacked Title");

            matter = matterService.updateMatter(matterId, request, userId);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @When("the author {string} adds a note record titled {string} to matter {string}")
    public void theAuthorAddsANoteRecordTitledToMatter(String author, String title, String matterId) {
        try {
            MatterRecord newRecord = new MatterRecord();
            newRecord.setKind("note");
            newRecord.setTitle(title);

            Mockito.when(matterRecordRepository.save(any(MatterRecord.class))).thenAnswer(invocation -> {
                MatterRecord r = invocation.getArgument(0);
                r.setId("generated-record-id");
                return r;
            });

            record = matterService.addMatterRecord(matterId, newRecord, author);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the record is saved with matterId {string}")
    public void theRecordIsSavedWithMatterId(String expectedMatterId) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(record);
        assertEquals(expectedMatterId, record.getMatterId());
    }

    @Then("the record author is {string}")
    public void theRecordAuthorIs(String expectedAuthor) {
        assertEquals(expectedAuthor, record.getAuthor());
    }
}
