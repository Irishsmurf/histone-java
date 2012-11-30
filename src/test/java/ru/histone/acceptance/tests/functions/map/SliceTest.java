package ru.histone.acceptance.tests.functions.map;

import org.junit.runner.RunWith;
import ru.histone.acceptance.support.AcceptanceTest;
import ru.histone.acceptance.support.AcceptanceTestsRunner;

@RunWith(AcceptanceTestsRunner.class)
public class SliceTest extends AcceptanceTest {
    @Override
    public String getFileName() {
        return "/functions/map.slice.json";
    }
}
