/**
 *    Copyright 2012 MegaFon
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
package ru.histone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

/**
 * Test correct work of Histone public methods
 */
public class HistonePlaygroundTest {
    private Histone histone;
    private ObjectMapper jackson;

    @Before
    public void before() throws HistoneException, UnsupportedEncodingException {
        HistoneBuilder builder = new HistoneBuilder();
        histone = builder.build();
        jackson = new ObjectMapper();
    }

    @Test
    public void test() throws Exception {
        String baseURI = getClass().getClassLoader().getResource("markerFileForBaseURI").toString();
        String input = "a {{1+3}} b";
        String context = "{}";
        String expectedAST = "[[\"HISTONE\",{\"version\":\"${histone.version}\"}],[\"a \",[9,[101,1],[101,3]],\" b\"]]";
        String expected = "a 4 b";

        ArrayNode resultAST = histone.parseTemplateToAST(input);
        String result = histone.evaluateAST(baseURI, resultAST, jackson.readTree(new StringReader(context)));

        assertEquals(expectedAST, resultAST.toString());
        assertEquals(expected, result);
    }
}
