Feature: ATM Functional Requirements (FR1 - FR9)
  In order to ensure the ATM behaves correctly
  As a tester
  I want to verify FR1–FR9 functionalities using the updated bankdata.json

  Background:
    Given the Bank is started with "testdata.json"
    And the ATM is started with totalFund "10000", dailyLimit "2000", transactionLimit "500", minCashRequired "500"

  @FR2
  Scenario: [FR2] Show initial display when no card is inserted
    Then the ATM should show "Welcome to the Bank ATM! Insert your card..."

  @FR3
  Scenario: [FR3] ATM is out of cash => ATM_OUT_OF_CASH
    # Re-init the ATM with 200 totalFund < minCashRequired = 500
    Given the ATM is started with totalFund "200", dailyLimit "2000", transactionLimit "500", minCashRequired "500"
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    Then the system responds with code "ATM_OUT_OF_CASH"

  @FR1 @FR2
  Scenario: [FR1/FR2] Invalid bank code -> "bad bank code"
    # Bank code 9999 desteklenmiyor, bu nedenle 'BAD_BANK_CODE' mesajı dönmeli
    Given the ATM is started with totalFund "10000", dailyLimit "2000", transactionLimit "500", minCashRequired "500"
    When the user inserts a valid card with bankCode "9999" cardNumber "1111"
    And the system verifies the bankCode "9999"
    Then the system responds with code "BAD_BANK_CODE"

  @FR4
  Scenario: [FR4] Valid bank code but invalid password -> "bad password"
    # bankCode = 1001 geçerli, cardNumber = 1234 var, password yanlış
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "9999" for account "1234"
    Then the system responds with code "BAD_PASSWORD"

  @FR4
  Scenario: [FR4-3Times] 3 kez yanlış şifre -> kart retain edilmeli
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "9999" for account "1234"
    And the user enters PIN "8888" for account "1234"
    And the user enters PIN "7777" for account "1234"
    And the user enters PIN "7777" for account "1234"
    Then the system responds with code "CARD_RETAINED"

  @FR5
  Scenario: [FR5] Valid card & password but account is frozen -> "bad account"
    # accountNumber = 8888 (bankdata.json’da frozen)
    When the user inserts a valid card with bankCode "1001" cardNumber "8888"
    And the user enters PIN "3333" for account "8888"
    Then the system responds with code "BAD_ACCOUNT"

  @FR6
  Scenario: [FR6] Valid card & password & active account -> "account ok"
    # accountNumber = 9999 => active
    When the user inserts a valid card with bankCode "1001" cardNumber "9999"
    And the user enters PIN "2222" for account "9999"
    Then the system responds with code "ACCOUNT_OK"

  @FR7 @FR8 @FR9
  Scenario: [FR7/FR8] Successful withdrawal
    # accountNumber = 9999 (active), PIN = 2222
    When the user inserts a valid card with bankCode "1001" cardNumber "9999"
    And the user enters PIN "2222" for account "9999"
    And the user requests a withdrawal of "200"
    Then the system responds with code "TRANSACTION_SUCCESS"

  @FR7
  Scenario: [FR7] Transaction failure due to insufficient account balance
    # Hesapta 1000 var, 2000 çekmeye çalışınca "EXCEED_TRANSACTION_LIMIT"
    Given the ATM is started with totalFund "10000", dailyLimit "2000", transactionLimit "1000", minCashRequired "500"
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "1111" for account "1234"
    And the user requests a withdrawal of "2000"
    Then the system responds with code "EXCEED_TRANSACTION_LIMIT"

  @FR9
  Scenario: [FR9] Withdrawal exceeding daily limit
    When the user inserts a valid card with bankCode "1001" cardNumber "9999"
    And the user enters PIN "2222" for account "9999"
    And the user requests a withdrawal of "3000"
    Then the system responds with code "EXCEED_TRANSACTION_LIMIT"

  @FR9
  Scenario: [FR9] Withdrawal exceeding per-transaction limit
    # transactionLimit = 500, 600 çekmeye çalışınca "EXCEED_TRANSACTION_LIMIT"
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "1111" for account "1234"
    And the user requests a withdrawal of "600"
    Then the system responds with code "EXCEED_TRANSACTION_LIMIT"

  @FR8
  Scenario: [FR8] Check account update after a successful transaction
    # 1000 bakiye, 300 çekince geriye 700 kalacak
    Given the ATM is started with totalFund "10000", dailyLimit "2000", transactionLimit "500", minCashRequired "500"
    When the user inserts a valid card with bankCode "1001" cardNumber "1234"
    And the user enters PIN "1111" for account "1234"
    And the user requests a withdrawal of "300"
    Then the system responds with code "TRANSACTION_SUCCESS"
    And the user's account "1234" should be updated with a new balance "700"
