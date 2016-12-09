/*
 * Copyright Swiss Reinsurance Company, Mythenquai 50/60, CH 8022 Zurich. All rights reserved.
 */

package lv.ctco.cukescore.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lv.ctco.cukescore.CukesCorePlugin;
import lv.ctco.cukescore.internal.context.GlobalWorldFacade;

import java.util.Set;

@Singleton
public class CukesRestFacade {

    /* Ugly Hack proposed by Cucumber developers: https://github.com/cucumber/cucumber-jvm/pull/295 */
    private static boolean firstRun = true;

    @Inject
    GlobalWorldFacade world;

    @Inject
    Set<CukesCorePlugin> pluginSet;

    @Inject
    RequestSpecificationFacade requestSpecificationFacade;

    public boolean firstScenario() {
        return firstRun;
    }

    public void beforeAllTests() {
        firstRun = false;
        for (CukesCorePlugin cukesCorePlugin : pluginSet) {
            cukesCorePlugin.beforeAllTests();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                afterAllTests();
            }
        });
    }

    public void beforeScenario() {
        world.reconstruct();
        for (CukesCorePlugin cukesCorePlugin : pluginSet) {
            cukesCorePlugin.beforeScenario();
        }
    }

    public void afterScenario() {
        for (CukesCorePlugin cukesCorePlugin : pluginSet) {
            cukesCorePlugin.afterScenario();
        }
        requestSpecificationFacade.initNewSpecification();
    }

    public void afterAllTests() {
        for (CukesCorePlugin cukesCorePlugin : pluginSet) {
            cukesCorePlugin.afterAllTests();
        }
    }
}
