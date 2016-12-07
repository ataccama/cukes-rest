package lv.ctco.cukesrest.internal;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;
import lv.ctco.cukescore.CukesCorePlugin;
import lv.ctco.cukescore.CukesOptions;
import lv.ctco.cukescore.internal.context.GlobalWorldFacade;
import lv.ctco.cukescore.internal.context.InflateContext;
import lv.ctco.cukesrest.internal.matchers.AwaitConditionMatcher;
import lv.ctco.cukesrest.internal.switches.ResponseWrapper;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.with;

@Singleton
@InflateContext
public class ResponseFacade {

    @Inject
    RequestSpecificationFacade specification;
    @Inject
    GlobalWorldFacade world;
    @Inject
    Set<CukesCorePlugin> pluginSet;

    private Response response;
    private boolean expectException;
    private RuntimeException exception;

    public void doRequest(String httpMethod, final String url) throws Exception {
        final HttpMethod method = HttpMethod.parse(httpMethod);

        // TODO: Should be refactored into CukesCorePlugin
        boolean filterEnabled = world.getBoolean(CukesOptions.LOADRUNNER_FILTER_BLOCKS_REQUESTS);
        AwaitCondition awaitCondition = specification.awaitCondition();
        try {
            if (awaitCondition != null && !filterEnabled) {
                int intervalTime = awaitCondition.getInterval().getValue();
                TimeUnit intervalUnit = awaitCondition.getInterval().getUnitDict().getTimeUnit();

                int waitTime = awaitCondition.getWaitTime().getValue();
                TimeUnit unit = awaitCondition.getWaitTime().getUnitDict().getTimeUnit();

                // TODO Fix
                with().pollInterval(intervalTime, intervalUnit).await().atMost(waitTime, unit).until(doRequest(url, method),
                    new AwaitConditionMatcher(awaitCondition));
            } else {
                doRequest(url, method).call();
            }
        } catch (RuntimeException e) {
            if (!expectException) {
                throw e;
            }
            exception = e;
        }

        specification.initNewSpecification();
    }

    private void authenticate() {
        Optional<String> $type = world.get(CukesOptions.AUTH_TYPE);
        if (!$type.isPresent()) return;

        if ($type.get().equalsIgnoreCase("BASIC")) {
            authBasic();
        }
    }

    private Callable<ResponseWrapper> doRequest(final String url, final HttpMethod method) {
        final boolean filterEnabled = world.getBoolean(CukesOptions.LOADRUNNER_FILTER_BLOCKS_REQUESTS);
        return new Callable<ResponseWrapper>() {
            @Override
            public ResponseWrapper call() throws Exception {
                authenticate();
                for (CukesCorePlugin CukesCorePlugin : pluginSet) {
                    CukesCorePlugin.beforeRequest();
                }
                response = method.doRequest(specification.value(), url);
                for (CukesCorePlugin CukesCorePlugin : pluginSet) {
                    CukesCorePlugin.afterRequest();
                }
                if (!filterEnabled) {
                    cacheHeaders(response);
                }
                return new ResponseWrapper(response);
            }
        };
    }

    public Response response() {
        return response;
    }

    private void authBasic() {
        Optional<String> $username = world.get(CukesOptions.USERNAME);
        Optional<String> $password = world.get(CukesOptions.PASSWORD);
        if ($username.isPresent() && $password.isPresent()) {
            specification.basicAuthentication($username.get(), $password.get());
        }
    }

    public void setExpectException(boolean expectException) {
        this.expectException = expectException;
    }

    public RuntimeException getException() {
        return exception;
    }

    public void setException(RuntimeException exception) {
        this.exception = exception;
    }

    private void cacheHeaders(Response response) {
        clearOldHeaders();
        Headers headers = response.getHeaders();
        for (Header header : headers) {
            String headerName = CukesOptions.HEADER_PREFIX + header.getName();
            world.put(headerName, header.getValue());
        }
    }

    private void clearOldHeaders() {
        Set<String> keys = world.getKeysStartingWith(CukesOptions.HEADER_PREFIX);
        for (String key : keys) {
            world.remove(key);
        }
    }
}
