package lv.ctco.cukescore;

public interface CukesCorePlugin {

    void beforeAllTests();

    void afterAllTests();

    void beforeScenario();

    void afterScenario();

    void beforeRequest();

    void afterRequest();
}
