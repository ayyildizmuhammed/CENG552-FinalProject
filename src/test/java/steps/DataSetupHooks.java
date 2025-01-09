package steps;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class handles creating and removing the test JSON file (bankdata.json)
 * before and after the Cucumber tests run.
 * 
 * Adheres to SOLID by separating file management concerns from the scenario steps.
 */
public class DataSetupHooks {

    private static final String JSON_FILE_PATH = "src/main/resources/testdata.json";

    // The sample JSON data that will be written to bankdata.json
    private static final String TEST_JSON_CONTENT = "{\n" +
        "    \"validBankCodes\": [\n" +
        "        1001\n" +
        "    ],\n" +
        "    \"accounts\": [\n" +
        "        {\n" +
        "            \"accountNumber\": 1234,\n" +
        "            \"password\": 1111,\n" +
        "            \"status\": \"active\",\n" +
        "            \"balance\": {\n" +
        "                \"availableBalance\": 1000,\n" +
        "                \"totalBalance\": 1000\n" +
        "            },\n" +
        "            \"transactionHistory\": [\n" +
        "                \"Deposit 1000\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\"\n" +
        "            ],\n" +
        "            \"dailyUsed\": 0,\n" +
        "            \"invalidPasswordCount\": 0\n" +
        "        },\n" +
        "        {\n" +
        "            \"accountNumber\": 9999,\n" +
        "            \"password\": 2222,\n" +
        "            \"status\": \"active\",\n" +
        "            \"balance\": {\n" +
        "                \"availableBalance\": 1000,\n" +
        "                \"totalBalance\": 1000\n" +
        "            },\n" +
        "            \"transactionHistory\": [\n" +
        "                \"Deposit 1000\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\"\n" +
        "            ],\n" +
        "            \"dailyUsed\": 0,\n" +
        "            \"invalidPasswordCount\": 0\n" +
        "        },\n" +
        "        {\n" +
        "            \"accountNumber\": 8888,\n" +
        "            \"password\": 3333,\n" +
        "            \"status\": \"frozen\",\n" +
        "            \"balance\": {\n" +
        "                \"availableBalance\": 1000,\n" +
        "                \"totalBalance\": 1000\n" +
        "            },\n" +
        "            \"transactionHistory\": [\n" +
        "                \"Deposit 1000\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\"\n" +
        "            ],\n" +
        "            \"dailyUsed\": 0,\n" +
        "            \"invalidPasswordCount\": 0\n" +
        "        },\n" +
        "        {\n" +
        "            \"accountNumber\": 4444,\n" +
        "            \"password\": 3333,\n" +
        "            \"status\": \"frozen\",\n" +
        "            \"balance\": {\n" +
        "                \"availableBalance\": 1000,\n" +
        "                \"totalBalance\": 1000\n" +
        "            },\n" +
        "            \"transactionHistory\": [\n" +
        "                \"Deposit 1000\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\",\n" +
        "                \"Withdraw 100\"\n" +
        "            ],\n" +
        "            \"dailyUsed\": 0,\n" +
        "            \"invalidPasswordCount\": 0\n" +
        "        }\n" +
        "    ]\n" +
        "}";

    /**
     * This hook runs once before any feature/scenario in the test suite.
     * It creates or overwrites the bankdata.json file with our test content.
     */
    @BeforeAll
    public static void beforeAll() {
        File file = new File(JSON_FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(TEST_JSON_CONTENT);
            System.out.println("[DataSetupHooks] Created test JSON file: " + JSON_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This hook runs once after all tests have finished.
     * It removes the bankdata.json file to clean up.
    //  */
    // @AfterAll
    // public static void afterAll() {
    //     try {
    //         Files.deleteIfExists(Paths.get(JSON_FILE_PATH));
    //         System.out.println("[DataSetupHooks] Deleted test JSON file: " + JSON_FILE_PATH);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}