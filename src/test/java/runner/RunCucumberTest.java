package runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

// Bu sınıf testleri JUnit4 ile başlatır:
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",   // Feature dosyalarının konumu
    glue = {"steps"}, // Step definitions paketleri
    plugin = {"pretty", "html:target/cucumber-report.html", 
              "json:target/cucumber-report.json"}
)
public class RunCucumberTest {
}