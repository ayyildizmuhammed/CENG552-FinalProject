Feature: Bank Computer Transaction (FR7–FR10)
  In order to handle ATM withdrawal requests
  As a bank computer
  I want to process transaction requests, update accounts, and enforce daily limit

  Background:
    Given the bank system is running with "bankdata.json" for transaction

  # FR7 & FR9: "WITHDRAW_REQUEST", check daily limit
  Scenario Outline: Bank processes withdraw request
    Given an account with number <accNum> has daily usage <usedSoFar>
    And daily limit is <k>
    And withdraw request is <amount>
    When the bank processes the transaction
    Then the bank reply should be "<expected>"

    Examples:
      | accNum | usedSoFar | k    | amount | expected              |
      | 1234   | 0         | 2000 | 300    | transaction succeeded |
      | 1234   | 1800      | 2000 | 300    | transaction failed    |

  # FR8: Update account after money dispensed
  Scenario: Account update after money dispensed
    Given an account with number 1234 has balance 1000
    When the bank receives "money dispensed" for amount 300
    Then the account balance should be 700

  # FR10: The bank only provides security for its own software
  Scenario: Unauthorized attempt to access bank computer
    Given an unauthorized system tries to access the bank
    When the bank checks the system identity
    Then the bank denies access
