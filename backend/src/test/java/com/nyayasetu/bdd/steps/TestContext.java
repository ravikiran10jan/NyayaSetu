package com.nyayasetu.bdd.steps;

import io.cucumber.java.Before;

public class TestContext {
    private static Exception thrownException;

    @Before
    public void resetContext() {
        thrownException = null;
    }

    public static Exception getThrownException() {
        return thrownException;
    }

    public static void setThrownException(Exception e) {
        thrownException = e;
    }
}
