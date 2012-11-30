package ru.histone.acceptance.tests.functions.type;

import org.junit.runner.RunWith;
import ru.histone.acceptance.support.AcceptanceTest;
import ru.histone.acceptance.support.AcceptanceTestsRunner;

@RunWith(AcceptanceTestsRunner.class)
public class IsBooleanTest extends AcceptanceTest {
    @Override
    public String getFileName() {
        return "/functions/type.isBoolean.json";
    }
}
