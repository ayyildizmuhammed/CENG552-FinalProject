Feature: ATM Basic Initialization and Authorization (FR1–FR9)
  In order to ensure an ATM can initialize and authorize cardholders
  As a bank customer
  I want to see how the ATM handles param setup, card acceptance, and password verification

  Background:
    Given the Bank is started with "bankdata.json"
    And the ATM is started with totalFund "1000", dailyLimit "2000", transactionLimit "500", minCashRequired "200"

  @fr2
  Scenario: No card => initial display
    Then the ATM should show "Welcome to the Bank ATM! Insert your card..."

  @fr3
  Scenario: ATM out of service if totalFund < minCashRequired
    Given the ATM is started with totalFund "150", dailyLimit "2000", transactionLimit "500", minCashRequired "200"
    When the user inserts a card with bankCode "1001" cardNumber "2222"
    Then the system responds with code "ATM_OUT_OF_CASH" and message "ATM does not have enough funds, returning your card."

  @fr4 @fr5 @fr6
  Scenario: Insert a valid card => read bank code & serial => log
    When the user inserts a card with bankCode "1001" cardNumber "2222"
    Then the system responds with code "CARD_ACCEPTED" and message "Card accepted, please enter your PIN."

  @fr7 @fr8 @fr9
  Scenario: Password check => bad password => card ejected
    When the user inserts a card with bankCode "1001" cardNumber "2222"
    Then the system responds with code "CARD_ACCEPTED" and message "Card accepted, please enter your PIN."
    When the user enters PIN "1235" for account "1111"
    Then the system responds with code "BAD_PASSWORD" and message "Wrong password. Try again."
    When the user enters PIN "1235" for account "1111"
    Then the system responds with code "CARD_RETAINED" and message "Too many wrong attempts. Card is retained."
