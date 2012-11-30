package ru.histone.acceptance.support;

import org.junit.runner.Describable;
import org.junit.runner.Description;

public abstract class AcceptanceTest implements Describable {
    private Description description;

    public abstract String getFileName();

    public AcceptanceTest() {
        description = Description.createSuiteDescription(getClass());
    }

    @Override
    public Description getDescription() {
        return description;
    }
}
