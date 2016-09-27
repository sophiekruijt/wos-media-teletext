package nl.wos.teletext.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber"},
        features = {"classpath:nl.wos.teletext"},
        glue = { "classpath:nl/wos/teletext/steps" })

public class RunAll {

}
