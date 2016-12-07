package lv.ctco.cukesrest.internal;

import lv.ctco.cukescore.CukesRuntimeException;
import lv.ctco.cukescore.internal.GuiceModule;
import lv.ctco.cukescore.internal.context.CaptureContext;
import lv.ctco.cukescore.internal.context.CaptureContextInterceptor;
import lv.ctco.cukescore.internal.context.InflateContext;
import lv.ctco.cukescore.internal.context.InflateContextInterceptor;
import lv.ctco.cukesrest.internal.switches.SwitchedBy;
import lv.ctco.cukesrest.internal.switches.SwitchedByInterceptor;

import static lv.ctco.cukesrest.internal.AssertionFacade.ASSERTION_FACADE;

public class CukesRestGuiceModule extends GuiceModule {

    @Override
    protected void configure() {
        bindInterceptor(new InflateContextInterceptor(), InflateContext.class);
        bindInterceptor(new CaptureContextInterceptor(), CaptureContext.class);
        bindInterceptor(new SwitchedByInterceptor(), SwitchedBy.class);

        bindAssertionFacade();
        bindPlugins();
    }

    @SuppressWarnings("unchecked")
    private void bindAssertionFacade() {
        String facadeImplType = System.getProperty(ASSERTION_FACADE);
        Class<? extends AssertionFacade> assertionFacadeClass;
        if (facadeImplType == null || facadeImplType.isEmpty()) {
            assertionFacadeClass = AssertionFacadeImpl.class;
        } else {
            try {
                assertionFacadeClass = (Class<AssertionFacade>) Class.forName(facadeImplType);
            } catch (ClassNotFoundException e) {
                throw new CukesRuntimeException("Invalid " + ASSERTION_FACADE + " value", e);
            } catch (ClassCastException e) {
                throw new CukesRuntimeException("Invalid " + ASSERTION_FACADE + " value", e);
            }
        }
        bind(AssertionFacade.class).to(assertionFacadeClass);
    }
}
