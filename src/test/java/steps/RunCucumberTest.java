package steps;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

// Bu sınıf testleri JUnit4 ile başlatır:
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", plugin = { "pretty", "html:target/cucumber-report.html" })
public class RunCucumberTest {
}