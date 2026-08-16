Feature: Legal Matter Lifecycle

  Scenario: Client creates a legal matter
    When a client with email "client@example.com" userId "client-1" creates a matter titled "Property Dispute" in area "Property Law"
    Then a matter is created with status "intake"
    And the matter conflictStatus is "pending"
    And the matter ownerEmail is "client@example.com"

  Scenario: Matter creation defaults urgency to standard
    When a client creates a matter without specifying urgency
    Then the matter urgency is "standard"

  Scenario: Owner can update their own matter
    Given a matter with id "matter-1" owned by userId "client-1"
    When the owner "client-1" updates matter "matter-1" title to "Updated Title"
    Then the matter title is "Updated Title"

  Scenario: Non-owner cannot update a matter
    Given a matter with id "matter-2" owned by userId "client-1"
    When user "other-user" tries to update matter "matter-2"
    Then a SecurityException is thrown

  Scenario: Add a note record to a matter
    Given a matter with id "matter-3" owned by userId "client-1"
    When the author "client-1" adds a note record titled "Initial consultation" to matter "matter-3"
    Then the record is saved with matterId "matter-3"
    And the record author is "client-1"
