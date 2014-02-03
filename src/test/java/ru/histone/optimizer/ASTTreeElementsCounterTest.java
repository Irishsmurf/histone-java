/**
 *    Copyright 2013 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
