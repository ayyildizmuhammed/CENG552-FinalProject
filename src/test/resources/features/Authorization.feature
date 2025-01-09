Feature: ATM Functional Requirements (FR1 - FR9)
  In order to ensure the ATM behaves correctly
  As a tester
  I want to verify FR1–FR9 functionalities using the updated bankdata.json

  Background:
    Given the Bank is started with "bankdata.json"
    And the ATM is started with totalFund "10000", dailyLimit "2000", transactionLimit "500", minCashRequired "500"

  @FR2
  Scenario: [FR2] Show initial display when no card is inserted
    Then the ATM should show "Welcome to the Bank ATM! Insert your card..."

  @FR3
  Scenario: [FR3] Reject card if ATM has less than minCashRequired
    # Re-init the ATM with only 200 totalFund
    Given the ATM is started with totalFund "200", dailyLimit "2000", transactionLimit "500", minCashRequired "500"
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    Then the system responds with code "ATM_OUT_OF_CASH" and message "ATM does not have enough funds, returning your card."

  @FR4
  Scenario: [FR4] Insert an invalid or expired card
    # We'll assume bankCode is valid, but the cardNumber doesn't exist in JSON
    Given the ATM is started with totalFund "10000", dailyLimit "2000", transactionLimit "500", minCashRequired "500"
    When the user inserts a valid card with bankCode "1001" cardNumber "999999"
    Then the system responds with code "INVALID_CARD" and message "Your card is invalid or expired. Ejecting..."

  @FR5
  Scenario: [FR5] Invalid bank code
    When the user inserts a invalid card with bankCode "9999" cardNumber "1234"
    And the user enters PIN "1111" for account "1234"
    Then the system responds with code "BAD_BANK_CODE" and message "Unsupported bank code. Card ejected."

  @FR6
  Scenario: [FR6] Wrong password once
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "9999" for account "1234"
    Then the system responds with code "BAD_PASSWORD" and message "Wrong password. Try again."

  @FR6
  Scenario: [FR6-3times] Card retained after 3 consecutive wrong passwords
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "9999" for account "1234"
    And the user enters PIN "8888" for account "1234"
    And the user enters PIN "7777" for account "1234"
    Then the system responds with code "CARD_RETAINED" and message "Too many wrong attempts. Card is retained."

  @FR6
  Scenario: [FR6-frozen] Attempt to log in with a frozen account
    # accountNumber 8888 => status = frozen
    When the user inserts a valid card with bankCode "1001" cardNumber "8888"
    And the user enters PIN "3333" for account "8888"
    Then the system responds with code "BAD_ACCOUNT" and message "There is a problem with this account. Card ejected."

  @FR7 @FR8
  Scenario: [FR7/FR8] Successful withdrawal
    When the user inserts a valid card with bankCode "1001" cardNumber "9999"
    And the user enters PIN "2222" for account "9999"
    And the user requests a withdrawal of "200"
    Then the system responds with code "TRANSACTION_SUCCESS" and message "Withdrawal done."

  @FR9
  Scenario: [FR9] Withdrawal exceeding daily limit (2000)
    # Try 3000 => should fail
    When the user inserts a valid card with bankCode "1001" cardNumber "9999"
    And the user enters PIN "2222" for account "9999"
    And the user requests a withdrawal of "3000"
    Then the system responds with code "EXCEED_DAILY_LIMIT" and message "Requested amount exceeds ATM daily limit."

  @FR9
  Scenario: [FR9] Withdrawal exceeding per-transaction limit (500)
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "1111" for account "1234"
    And the user requests a withdrawal of "600"
    Then the system responds with code "EXCEED_TRANSACTION_LIMIT" and message "Requested amount exceeds the transaction limit."
