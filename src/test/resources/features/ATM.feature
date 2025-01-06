Feature: ATM Functional Requirements
  In order to serve customers' transactions
  As a bank ATM
  We want to process withdrawals, transfers, and handle invalid attempts correctly

  Background:
    Given the ATM is initialized with totalFund: 10000 dailyLimit: 2000 500 500 "bankdata.json"

  Scenario: [FR10] Retain card after 4 consecutive wrong passwords
    Given I insert a valid card
    When I enter wrong password 4 times for account 1234
    Then the ATM should retain the card
    And I should see "CARD_RETAINED" message

  Scenario: [FR11] Successful authorization and valid withdrawal
    Given I insert a valid card
    When I enter the correct password 1111 for account 1234
    And I attempt to withdraw 400
    Then I should see "TRANSACTION_SUCCESS" message

  Scenario: [FR12] Withdrawal exceeds per-transaction limit
    Given I insert a valid card
    When I enter the correct password 1111 for account 1234
    And I attempt to withdraw 1000
    Then the ATM should display error and eject card

  Scenario: [FR13] Bank receives a valid withdrawal request
    Given I insert a valid card
    When I enter the correct password 1111 for account 1234
    And I attempt to withdraw 300
    Then I should see "TRANSACTION_SUCCESS" message

  Scenario: [FR14] Transaction successful => money dispensed, card ejected
    Given I insert a valid card
    When I enter the correct password 1111 for account 1234
    And I attempt to withdraw 300
    Then I should see "TRANSACTION_SUCCESS" message

  Scenario: [FR15] Dispensed money is logged
    Given I insert a valid card
    When I enter the correct password 1111 for account 1234
    And I attempt to withdraw 300
    Then I should see "TRANSACTION_SUCCESS" message

  Scenario: [FR16] Invalid account triggers transaction failure
    Given I insert a valid card
    When I enter the correct password 1111 for account 9998
    Then the ATM should display error and eject card

  Scenario: [FR17] Transfer to another account
    Given I insert a valid card
    When I enter the correct password 1111 for account 1234
    And I transfer 200 to account 9999
    Then the transfer should be successful
    And I should see "TRANSFER_SUCCESS" message