package lv.ctco.cukescore.run;

import cucumber.api.*;
import cucumber.api.junit.*;
import lv.ctco.cukescore.*;
import org.junit.*;
import org.junit.runner.*;

@RunWith(Cucumber.class)
@CucumberOptions(
    format = {"pretty", "json:target/cucumber.json", "lv.ctco.cukescore.formatter.CukesRestJsonFormatter:target/cucumber2.json"},
    features = "classpath:features",
    glue = "lv.ctco.cukescore.api",
    strict = true
)
public class RunCukesTest {

    @BeforeClass
    public static void setUp() throws Exception {
        new SampleApplication().run(new String[]{"server", "server.yml"});
    }
}
