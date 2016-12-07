package lv.ctco.cukesrest.loadrunner;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import lv.ctco.cukescore.CukesOptions;
import lv.ctco.cukescore.CukesRuntimeException;
import lv.ctco.cukescore.internal.context.GlobalWorldFacade;
import lv.ctco.cukesrest.loadrunner.function.WebCustomRequest;
import lv.ctco.cukesrest.loadrunner.mapper.WebCustomRequestMapper;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;

@Singleton
public class LoadRunnerFilter implements Filter {

    @Inject
    WebCustomRequestMapper mapper;

    @Inject
    GlobalWorldFacade globalWorldFacade;

    private LoadRunnerAction action;
    private LoadRunnerTransaction trx;

    @Before
    public void beforeScenario(Scenario scenario) {
        createLoadRunnerTransaction(scenario.getName());
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        WebCustomRequest request = mapper.map(requestSpec, ctx);
        trx.addFunction(request);
        boolean blockRequests = globalWorldFacade.getBoolean(CukesOptions.LOADRUNNER_FILTER_BLOCKS_REQUESTS);
        if (blockRequests) {
            return Mockito.mock(Response.class);
        }
        return ctx.next(requestSpec, responseSpec);
    }

    public void createLoadRunnerAction() {
        action = new LoadRunnerAction();
    }

    public LoadRunnerTransaction getTrx() {
        return trx;
    }

    public void createLoadRunnerTransaction(String name) {
        trx = new LoadRunnerTransaction();
        trx.setName(name);
        trx.setTrxFlag("transactionStatus");
        if (action != null) {
            action.addTransaction(trx);
        }
    }

    public void dump(OutputStream out) {
        try {
            if (action != null) out.write(action.format().getBytes());
        } catch (IOException e) {
            throw new CukesRuntimeException(e);
        }
    }
}
