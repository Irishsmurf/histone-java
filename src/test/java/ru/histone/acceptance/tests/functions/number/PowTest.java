package ru.histone.acceptance.tests.functions.number;

import org.junit.runner.RunWith;
import ru.histone.acceptance.support.AcceptanceTest;
import ru.histone.acceptance.support.AcceptanceTestsRunner;

@RunWith(AcceptanceTestsRunner.class)
public class PowTest extends AcceptanceTest {
    @Override
    public String getFileName() {
        return "/functions/number.pow.json";
    }
}
