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
    Given a bank code <bankCode>
    And an account with number <accNum> has status "<accStatus>"
    And the password "<pwd>"
    When the bank verifies the card with password
    Then the bank result should be "<expected>"

    Examples:
      | bankCode | accNum | accStatus | pwd  | expected        |
      | 1001     | 1234   | active    | 1111 | account ok      |  # FR6
      | 1001     | 1234   | active    | 9999 | bad password    |  # FR4
      | 1001     | 4444   | frozen    | 3333 | bad account     |  # FR5
      | 5555     | 4321   | active    | 1234 | bad bank code   |  # FR2
