package lv.ctco.cukescore.run;

import cucumber.api.*;
import lv.ctco.cukescore.loadrunner.junit.*;
import org.junit.runner.*;

@RunWith(CucumberLoadRunner.class)
@CucumberOptions(
    format = {"pretty"},
    features = "classpath:features",
    glue = {"lv.ctco.cukescore.api", "lv.ctco.cukescore.loadrunner"},
    strict = true
)
public class RunLoadrunnerGenerator {}
