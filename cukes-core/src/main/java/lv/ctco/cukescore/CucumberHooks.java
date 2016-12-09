package lv.ctco.cukescore;

import com.google.inject.Inject;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import lv.ctco.cukescore.internal.CukesRestFacade;

public class CucumberHooks {

    @Inject
    CukesRestFacade cucumberFacade;

    @Before(order = CukesOptions.CUKES_BEFORE_HOOK_STARTUP_ORDER)
    public void beforeScenario() {
        if (cucumberFacade.firstScenario()) {
            cucumberFacade.beforeAllTests();
        }
        cucumberFacade.beforeScenario();
    }

    @After
    public void afterScenario() {
        cucumberFacade.afterScenario();
    }
}
