Feature: Anonymous Chat

  Scenario: Start an anonymous chat with consent
    When a visitor starts an anonymous chat with alias "Visitor1" phoneMask "98***43210" phoneHash "abc123hash" and consent "true"
    Then an anonymous chat is created with status "open"
    And the chat expiresAt is 24 hours after creation

  Scenario: Cannot start anonymous chat without consent
    When a visitor tries to start a chat without consent
    Then an IllegalArgumentException is thrown with message containing "Consent"

  Scenario: Token hashing is deterministic
    When the token "my-secret-token-123" is hashed twice
    Then both hashes are identical

  Scenario: Send a message to an open chat
    Given an open anonymous chat with id "chat-1" and valid token hash
    When the anonymous sender sends message "I need legal help" to chat "chat-1"
    Then the message is stored with sender "anonymous"
    And the message body is "I need legal help"

  Scenario: Cannot send message to expired chat
    Given an expired anonymous chat with id "chat-2"
    When someone tries to send a message to chat "chat-2"
    Then an IllegalStateException is thrown with message containing "expired"

  Scenario: Token validation succeeds for correct token
    Given an open chat "chat-3" with tokenHash matching token "valid-token-xyz"
    When the token "valid-token-xyz" is validated against chat "chat-3"
    Then the validation result is "true"

  Scenario: Token validation fails for wrong token
    Given an open chat "chat-3" with tokenHash matching token "valid-token-xyz"
    When the token "wrong-token" is validated against chat "chat-3"
    Then the validation result is "false"
