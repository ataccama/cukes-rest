package lv.ctco.cukesrest.internal;

import com.google.inject.Injector;

public class IntegrationTestBase {

    private Injector injector = new GuiceInjectorSource().getInjector();

    public Injector getInjector() {
        return injector;
    }
}
