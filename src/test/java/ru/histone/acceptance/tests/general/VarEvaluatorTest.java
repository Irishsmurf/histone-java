package ru.histone.acceptance.tests.general;

import org.junit.runner.RunWith;
import ru.histone.acceptance.support.AcceptanceTest;
import ru.histone.acceptance.support.AcceptanceTestsRunner;

@RunWith(AcceptanceTestsRunner.class)

public class VarEvaluatorTest extends AcceptanceTest {

    @Override
    public String getFileName() {
        return "/general/var_evaluator.json";
    }
}
