package com.nyayasetu.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class SharedContext {
    private static Exception thrownException;
    private static Scenario currentScenario;

    @Before
    public void beforeScenario(Scenario scenario) {
        currentScenario = scenario;
        thrownException = null;
    }

    public static Exception getThrownException() {
        return thrownException;
    }

    public static void setThrownException(Exception e) {
        thrownException = e;
    }

    public static void clearThrownException() {
        thrownException = null;
    }
}
