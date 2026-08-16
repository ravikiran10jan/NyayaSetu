Feature: Consultation Booking

  Scenario: Successfully book an active advocate
    Given an active lawyer with id "lawyer-1" and email "adv@example.com"
    And the current time is well within booking window
    When a client with email "client@example.com" and userId "client-1" books lawyer "lawyer-1" for a chat
    Then the booking is created with status "requested"
    And the booking has clientEmail "client@example.com"
    And the booking has mode "chat"

  Scenario: Cannot book an inactive advocate
    Given a pending lawyer with id "lawyer-2"
    When a client tries to book lawyer "lawyer-2"
    Then an IllegalArgumentException is thrown with message containing "not active"

  Scenario: Cannot book outside the valid time window
    Given an active lawyer with id "lawyer-3" and email "adv3@example.com"
    When a client books lawyer "lawyer-3" with a start time only 5 minutes from now
    Then an IllegalArgumentException is thrown with message containing "15 minutes"

  Scenario: Cannot book a conflicting time slot
    Given an active lawyer with id "lawyer-4" and email "adv4@example.com"
    And lawyer "lawyer-4" already has a conflicting booking
    When a client tries to book lawyer "lawyer-4" at the same time
    Then an IllegalStateException is thrown with message containing "conflict"

  Scenario: Advocate accepts a requested booking
    Given a booking with id "booking-1" in status "requested" assigned to lawyer with email "adv@example.com" and id "lawyer-1"
    When the advocate with email "adv@example.com" accepts booking "booking-1"
    Then the booking status is "accepted"
    And the booking acceptedAt is set

  Scenario: Advocate cannot accept another advocate's booking
    Given a booking with id "booking-2" in status "requested" assigned to lawyer with email "other@example.com" and id "lawyer-5"
    When the advocate with email "wrong@example.com" tries to accept booking "booking-2"
    Then a SecurityException is thrown

  Scenario: Advocate completes an accepted booking with past start time
    Given a booking with id "booking-3" in status "accepted" with past scheduled start assigned to lawyer with email "adv@example.com" and id "lawyer-1"
    When the advocate with email "adv@example.com" completes booking "booking-3"
    Then the booking status is "completed"

  Scenario: Cannot complete a booking scheduled in the future
    Given a booking with id "booking-4" in status "accepted" with future scheduled start assigned to lawyer with email "adv@example.com" and id "lawyer-1"
    When the advocate with email "adv@example.com" tries to complete booking "booking-4"
    Then an IllegalStateException is thrown with message containing "before scheduled"
