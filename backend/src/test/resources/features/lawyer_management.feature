Feature: Lawyer Management

  Scenario: Submit a lawyer application
    When a lawyer application is submitted with name "Adv. Priya Sharma" email "priya@example.com" bar council "Bar Council of Delhi" enrolment "DL/2015/1234"
    Then a lawyer is created with status "pending"
    And the lawyer name is "Adv. Priya Sharma"

  Scenario: Admin activates a pending lawyer
    Given a lawyer with id "lawyer-10" in status "pending"
    When an admin updates lawyer "lawyer-10" status to "active" with note "Verified credentials"
    Then the lawyer status is "active"
    And the lawyer verifiedAt is set
    And the lawyer verificationNote is "Verified credentials"

  Scenario: Admin rejects a pending lawyer
    Given a lawyer with id "lawyer-11" in status "pending"
    When an admin updates lawyer "lawyer-11" status to "rejected" with note "Insufficient documents"
    Then the lawyer status is "rejected"
