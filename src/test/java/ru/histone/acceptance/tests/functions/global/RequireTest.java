package ru.histone.acceptance.tests.functions.global;


import org.junit.runner.RunWith;
import ru.histone.acceptance.support.AcceptanceTest;
import ru.histone.acceptance.support.AcceptanceTestsRunner;


@RunWith(AcceptanceTestsRunner.class)
public class RequireTest  extends AcceptanceTest {
    @Override
    public String getFileName() {
        return "/functions/global.require.json";
    }
}
