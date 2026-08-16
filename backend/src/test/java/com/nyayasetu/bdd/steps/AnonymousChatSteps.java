package com.nyayasetu.bdd.steps;

import com.nyayasetu.dto.AnonymousChatRequest;
import com.nyayasetu.model.AnonymousChat;
import com.nyayasetu.model.AnonymousMessage;
import com.nyayasetu.repository.AnonymousChatRepository;
import com.nyayasetu.repository.AnonymousMessageRepository;
import com.nyayasetu.service.AnonymousChatService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class AnonymousChatSteps {

    private AnonymousChatService chatService;
    private AnonymousChatRepository chatRepository;
    private AnonymousMessageRepository messageRepository;

    private AnonymousChat chat;
    private AnonymousMessage message;
    private String hash1;
    private String hash2;
    private boolean validationResult;

    @Before
    public void setUp() {
        chatRepository = Mockito.mock(AnonymousChatRepository.class);
        messageRepository = Mockito.mock(AnonymousMessageRepository.class);
        chatService = new AnonymousChatService(chatRepository, messageRepository);
    }

    @When("a visitor starts an anonymous chat with alias {string} phoneMask {string} phoneHash {string} and consent {string}")
    public void aVisitorStartsAnAnonymousChatWithAliasPhoneMaskPhoneHashAndConsent(String alias, String phoneMask, String phoneHash, String consentStr) {
        try {
            AnonymousChatRequest request = new AnonymousChatRequest();
            request.setAlias(alias);
            request.setPhoneMask(phoneMask);
            request.setPhoneHash(phoneHash);
            request.setConsent(Boolean.parseBoolean(consentStr));

            Mockito.when(chatRepository.save(any(AnonymousChat.class))).thenAnswer(invocation -> {
                AnonymousChat c = invocation.getArgument(0);
                c.setId("generated-chat-id");
                return c;
            });

            chat = chatService.createChat(request);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("an anonymous chat is created with status {string}")
    public void anAnonymousChatIsCreatedWithStatus(String expectedStatus) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(chat);
        assertEquals(expectedStatus, chat.getStatus());
    }

    @Then("the chat expiresAt is 24 hours after creation")
    public void theChatExpiresAtIs24HoursAfterCreation() {
        assertNotNull(chat.getExpiresAt());
        assertNotNull(chat.getCreatedAt());
        long diff = chat.getExpiresAt() - chat.getCreatedAt();
        long expectedDiff = 24 * 60 * 60 * 1000;
        assertEquals(expectedDiff, diff, 1000); // Allow 1 second tolerance
    }

    @When("a visitor tries to start a chat without consent")
    public void aVisitorTriesToStartAChatWithoutConsent() {
        try {
            AnonymousChatRequest request = new AnonymousChatRequest();
            request.setAlias("Visitor1");
            request.setPhoneMask("98***43210");
            request.setPhoneHash("abc123hash");
            request.setConsent(false);

            chat = chatService.createChat(request);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @When("the token {string} is hashed twice")
    public void theTokenIsHashedTwice(String token) {
        hash1 = chatService.hashToken(token);
        hash2 = chatService.hashToken(token);
    }

    @Then("both hashes are identical")
    public void bothHashesAreIdentical() {
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(hash1, hash2);
    }

    @Given("an open anonymous chat with id {string} and valid token hash")
    public void anOpenAnonymousChatWithIdAndValidTokenHash(String chatId) throws Exception {
        chat = new AnonymousChat();
        chat.setId(chatId);
        chat.setStatus("open");
        chat.setExpiresAt(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        Mockito.when(chatRepository.findById(chatId)).thenReturn(chat);
        Mockito.when(messageRepository.save(any(AnonymousMessage.class))).thenAnswer(invocation -> {
            AnonymousMessage m = invocation.getArgument(0);
            m.setId("generated-message-id");
            return m;
        });
        Mockito.when(chatRepository.update(any(AnonymousChat.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("the anonymous sender sends message {string} to chat {string}")
    public void theAnonymousSenderSendsMessageToChat(String body, String chatId) {
        try {
            message = chatService.sendMessage(chatId, "anonymous", body);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the message is stored with sender {string}")
    public void theMessageIsStoredWithSender(String expectedSender) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(message);
        assertEquals(expectedSender, message.getSender());
    }

    @Then("the message body is {string}")
    public void theMessageBodyIs(String expectedBody) {
        assertEquals(expectedBody, message.getBody());
    }

    @Given("an expired anonymous chat with id {string}")
    public void anExpiredAnonymousChatWithId(String chatId) throws Exception {
        chat = new AnonymousChat();
        chat.setId(chatId);
        chat.setStatus("open");
        chat.setExpiresAt(System.currentTimeMillis() - 60 * 60 * 1000); // Expired 1 hour ago

        Mockito.when(chatRepository.findById(chatId)).thenReturn(chat);
    }

    @When("someone tries to send a message to chat {string}")
    public void someoneTriesToSendAMessageToChat(String chatId) {
        try {
            message = chatService.sendMessage(chatId, "anonymous", "test message");
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Given("an open chat {string} with tokenHash matching token {string}")
    public void anOpenChatWithTokenHashMatchingToken(String chatId, String token) throws Exception {
        chat = new AnonymousChat();
        chat.setId(chatId);
        chat.setStatus("open");
        chat.setExpiresAt(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        chat.setTokenHash(chatService.hashToken(token));

        Mockito.when(chatRepository.findById(chatId)).thenReturn(chat);
    }

    @When("the token {string} is validated against chat {string}")
    public void theTokenIsValidatedAgainstChat(String token, String chatId) {
        try {
            validationResult = chatService.validateToken(chatId, token);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the validation result is {string}")
    public void theValidationResultIs(String expectedResult) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        boolean expected = Boolean.parseBoolean(expectedResult);
        assertEquals(expected, validationResult);
    }
}
