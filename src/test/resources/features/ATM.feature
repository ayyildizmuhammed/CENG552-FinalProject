Feature: ATM Functional Requirements

  # FR10 - Card is kept after 3 wrong passwords
  Scenario: Enter wrong password 3 times in a row
    Given the ATM is initialized
    And I insert a valid card
    When I enter wrong password 4 times for account 1234
    Then the ATM should retain the card

  # FR11 & FR12 & FR13 & FR14 scenario - Successful withdrawal
  Scenario: Withdraw within transaction limit
    Given the ATM is initialized
    And I insert a valid card
    And I enter the correct password 1111 for account 1234
    When I attempt to withdraw 300
    Then I should see "TRANSACTION_SUCCESS" message

  # FR16 scenario - Transaction not successful
  Scenario: Withdraw above daily limit
    Given the ATM is initialized
    And I insert a valid card
    And I enter the correct password 1111 for account 1234
    When I attempt to withdraw 10000
    Then the ATM should display error and eject card

  # FR17 scenario - Transfer
  Scenario: Transfer some money
    Given the ATM is initialized
    And I insert a second valid card 99999
    And I enter the correct password 2222 for account 9999
    When I transfer 150 to account 1234
    Then the transfer should be successful