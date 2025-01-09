Feature: ATM Transactions for FR10–FR17
  In order to handle repeated wrong passwords, withdrawals, and transfers
  As an ATM
  I want to fulfill the requirements from FR10 to FR17 without overlapping FR1–FR9 tests

  Background:
    # Burada ATM ve Bank'ı başlatıyoruz (örnek parametreler):
    Given the Bank is up with "testdata.json"
    And the ATM is up with totalFund "10000", dailyLimit "400", transactionLimit "700", minCashRequired "500"
    # Bu adımlar, FR1–FR2 (ATM init, initial display) vb. testlerden ziyade “ortam hazırlığı” içindir.

  # FR10: "If a card was entered more than three times in a row with wrong password => the card is kept by the ATM."
  Scenario: [FR10] Retain card after 4 consecutive wrong passwords
    Given I insert a valid card
    When I enter wrong password 4 times for account 1234
    Then the ATM should retain the card
    And I should see "CARD_RETAINED" message

  # FR11–FR16: Withdrawal flow
  # - FR11: The ATM offers withdrawal
  # - FR12: If the withdrawal amount > transactionLimit => "EXCEED_TRANSACTION_LIMIT"
  # - FR13: If initial withdrawal sequence successful => Bank processes
  # - FR14: If transaction succeeded => money is dispensed
  # - FR15: If money dispensed => log the amount with card serial
  # - FR16: If transaction not successful => error + card ejected

  # A single Scenario Outline can cover multiple outcomes (successful or failing):
  Scenario Outline: [FR11–FR16] Withdrawal Scenarios
    Given I insert a valid card
    And I enter the correct password "<pin>" for account "<accNum>"
    When I attempt to withdraw <amount>
    Then I should see "<expectedCode>" message

    Examples:
      | pin   | accNum | amount | expectedCode                   | Explanation                                                             |
      | 1111  | 1234   | 300    | TRANSACTION_SUCCESS            | Normal successful withdrawal => FR14 => money dispensed, FR15 => log    |
      | 1111  | 1234   | 800    | EXCEED_TRANSACTION_LIMIT       | FR12: over per-txn limit (m=500)                                        |
      | 1111  | 1234   | 600    | EXCEED_DAILY_LIMIT             | FR9 extension, daily limit is 2000 => eğer 2000’ı da aşıyorsa -> fail   |
      | 1111  | 3366   | 100    | BAD_ACCOUNT                    | FR16: invalid account => transaction fails => error + eject card        |

  # FR17: Transfer to another account
  Scenario: [FR17] Transfer money
    Given I insert a valid card
    And I enter the correct password "1111" for account "1234"
    When I transfer 150 to account 9999
    Then the transfer should be successful
    And I should see "TRANSFER_SUCCESS" message
