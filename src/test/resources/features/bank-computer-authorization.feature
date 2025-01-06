Feature: Bank Computer Authorization (FR1–FR6)
  In order to authenticate ATM customers
  As a bank computer
  I want to verify bank code, password, and account status

  Background:
    Given the bank system is running with "bankdata.json"

  # FR1 & FR2: Check if bank code is valid or not
  Scenario Outline: Bank code validity
    Given the bank code is <bankCode>
    When the bank verifies only the bank code
    Then the result should be "<expected>"

    Examples:
      | bankCode | expected          |
      | 1001     | valid bank code   |
      | 5555     | bad bank code     |

  # FR3–FR6: Password check, account status check
  Scenario Outline: Verify password and account
    Given an account with number <accNum> has status "<accStatus>"
    And the password "<pwd>"
    And the bank code is <bankCode>
    When the bank verifies the card with password
    Then the bank result should be "<expected>"

    Examples:
      | accNum | accStatus | pwd  | bankCode | expected        |
      | 1234   | active    | 1111 | 1001     | account ok      |  # FR6
      | 1234   | active    | 9999 | 1001     | bad password    |  # FR4
      | 9999   | frozen    | 3333 | 1001     | bad account     |  # FR5
      | 4321   | active    | 1234 | 5555     | bad bank code   |  # FR2
