Feature: Deposit Money
  In order to add money to my account
  As an authenticated user
  I want to deposit cash into the ATM

  Background:
    Given the ATM system is running with "testdata.json" for deposit
    And the user is authenticated for deposit

  @DepositSuccess
  Scenario: Successful deposit
    When the user inserts cash amount 500
    Then the ATM should confirm the deposit as successful
