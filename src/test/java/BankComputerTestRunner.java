import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",   // Feature dosyalarının konumu
    glue = {""}, // Step definitions paketleri
    plugin = {"pretty", "html:target/cucumber-report.html", 
              "json:target/cucumber-report.json"},
    monochrome = true
)
public class BankComputerTestRunner {
    // Boş sınıf
}
