package ru.histone.optimizer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;
import ru.histone.HistoneException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FragmentsConcatinationOptimizationTest extends AbstractOptimizersTest {

    @Test
    public void test_join() throws IOException, HistoneException {
        ArrayNode input = (ArrayNode) getJackson().readTree("[\"AAA\",\"BBB\"]");
        ArrayNode expeced = (ArrayNode) getJackson().readTree("[\"AAABBB\"]");

        ArrayNode ast = getHistone().optimizeAST(input);

        assertEquals(expeced.toString(), ast.toString());
    }

    @Test
    public void test_singleElement() throws IOException, HistoneException {
        ArrayNode input = (ArrayNode) getJackson().readTree("[[\"AAA\"]]");
        ArrayNode expected = (ArrayNode) getJackson().readTree("[\"AAA\"]");

        ArrayNode ast = getHistone().optimizeAST(input);

        assertEquals(expected.toString(), ast.toString());
    }

    @Test
    public void test_complex() throws IOException, HistoneException {
        ArrayNode input = (ArrayNode) getJackson().readTree("[[\"AAA\",\"BBB\",[\"C\",[\"C\",\"C\"]]]]");
        ArrayNode expected = (ArrayNode) getJackson().readTree("[\"AAABBBCCC\"]");

        ArrayNode ast = getHistone().optimizeAST(input);

        assertEquals(expected.toString(), ast.toString());
    }

}