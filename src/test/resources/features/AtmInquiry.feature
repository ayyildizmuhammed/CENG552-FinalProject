Feature: Account Inquiry
  In order to know the account details
  As a user
  I want to check my account balance and recent transactions

  Background:
    Given the ATM system is running with "testdata.json"
    And the user is authenticated for inquiry

  @InquiryBalance
  Scenario: Checking account balance
    When the user selects "balance" for inquiry
    Then the ATM should display the account balance

  @InquiryDetailed
  Scenario: Checking detailed account transactions
    When the user selects "detailed" for inquiry
    Then the ATM should display the last 10 transactions

  @InquiryInvalid
  Scenario: Invalid inquiry type
    When the user selects "invalid inquiry" for inquiry
    Then the ATM should display an error message for invalid inquiry type