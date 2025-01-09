Feature: ATM Operation Flow
  In order to complete ATM transactions safely
  As a bank customer
  I want to follow the required steps at the ATM

  Background:
    Given for scenarios the bank system is running with "bankdata.json"

  @Scenario1
  Scenario: A user inserts a card and must proceed within 60 seconds
    Given the ATM is powered on
    And no card is currently inserted
    When the user inserts a valid cash card into the ATM
    And the user waits more than 60 seconds without further action
    Then the ATM should swallow (retain) the card

  @Scenario2
  Scenario: Entering password with Correction, Confirm, and Take Card options
    Given the user has just inserted a valid card
    When the ATM displays a screen to enter the PIN
    And the user enters an incorrect PIN "<pin>" and chooses "CORRECTION"
    Then the ATM should allow re-entering of the PIN
    When the user enters the PIN "<pin>" again and chooses "CONFIRM"
    Then the ATM should validate the PIN
    And if incorrect, display wrong PIN info and let the user try again
    And if the user enters the wrong PIN three times, the card will be frozen
    And if correct, the user is authenticated
    Examples:
      | pin   |
      | 9999  |
      | 1111  |

  @Scenario3
  Scenario: Showing main options after correct password
    Given the user is authenticated
    When the system displays the transaction menu
    Then the menu should include "Change Password", "Transfer Money", "Inquiry", "Withdrawal", "Deposit", and "Take the card"

  @Scenario4
  Scenario: Changing the password flow
    Given the user is authenticated
    And the user selects "Change Password"
    When the user enters the correct original password "<oldPin>"
    And the user provides a new password "<newPin>"
    Then the password should be changed successfully
    And the user should see a success message
    Examples:
      | oldPin | newPin |
      | 1111   | 2222   |

  @Scenario5
  Scenario: Transferring money
    Given the user is authenticated
    And the user selects "Transfer Money"
    When the user confirms the transfer prompts
    And enters the target account "<toAccount>"
    And enters the amount "<amount>"
    Then the ATM should confirm the transaction with the bank
    And show a successful message if completed
    Examples:
      | toAccount | amount |
      | 9876      | 200    |
      | 8888      | 350    |

  @Scenario6
  Scenario: Account inquiry
    Given the user is authenticated
    And the user selects "Inquiry"
    When the user chooses "Detailed Inquiry"
    Then the ATM should display the recent ten transactions
    When the user chooses "Balance Inquiry"
    Then the ATM should display both the account balance and the usable balance

  @Scenario7
  Scenario: Withdrawal flow
    Given the user is authenticated
    And the user selects "Withdrawal"
    When the user enters the amount "<withdrawAmount>"
    And the amount exceeds the current balance
    Then the ATM should show a warning message
    And let the user enter an appropriate amount
    When the user enters an acceptable amount "<newAmount>"
    Then the ATM dispenses the cash
    And displays a successful transaction message including the amount and fee
    Examples:
      | withdrawAmount | newAmount |
      | 10000          | 200       |
      | 1200           | 500       |

  @Scenario8
  Scenario: Deposit flow
    Given the user is authenticated
    And the user selects "Deposit"
    When the ATM shows a message to put the money into the machine
    And no deposit faults occur
    Then the ATM should confirm the deposit successfully

  @Scenario9
  Scenario: Taking the card and ending the transaction
    Given the user is authenticated
    And the user selects "Take the card"
    When the user retrieves his/her card
    Then the transaction should be ended
    And the session should be cleared