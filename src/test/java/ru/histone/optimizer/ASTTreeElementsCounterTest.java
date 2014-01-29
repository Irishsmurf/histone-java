package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ASTTreeElementsCounterTest {
    private ASTTreeElementsCounter counter = new ASTTreeElementsCounter();
    private ObjectMapper jackson = new ObjectMapper();

    @Test
    public void test() {
        assertEquals(1, counter.count(toJsonNode("\"test\"")));
        assertEquals(3, counter.count(toJsonNode("[\"test\",1]")));
        assertEquals(5, counter.count(toJsonNode("[[1,2],1]")));
        assertEquals(8, counter.count(toJsonNode("[[1,2],[1,2,3]]")));
        assertEquals(3, counter.count(toJsonNode("{\"a\":\"b\"}")));
        assertEquals(5, counter.count(toJsonNode("{\"a\":\"b\", \"aa\":\"bb\"}")));
        assertEquals(7, counter.count(toJsonNode("{\"a\":[\"b\",1], \"aa\":\"bb\"}")));
        assertEquals(9, counter.count(toJsonNode("{\"a\":[\"b\",{\"z\":123}], \"aa\":\"bb\"}")));
    }

    private JsonNode toJsonNode(String json) {
        try {
            return jackson.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Error", e);
        }
    }
}
