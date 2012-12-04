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
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

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
//        histone.setGlobalProperty(GlobalProperty.BASE_URI, "dummy:///test");
        jackson = new ObjectMapper();
    }

    @Test
    public void test() throws Exception {
        /*

        "input": "a {{resolveURI('../testresources/tpl/test_for_include_relative.tpl', global.baseURI) is include('../testresources/tpl/test_for_include_relative.tpl').split(' ')[2]}} b",
		"expectedResult": "a true b"
	}, {
		"input": "a {{'ZA' + resolveURI('../testresources/tpl/subfolder/file_for_include.tpl', global.baseURI) + 'BX' is include('../testresources/tpl/test_for_include_subfolder.tpl')}} b",
		"expectedResult": "a true b"


         */

        String baseURI = getClass().getResource("/logback-test.xml").toString();
        String input = "a  b";
        String context = "{}";//"{\"baseURI\":\"test:///test\"}";
        String expected = "a  b";

        String result = histone.evaluate(baseURI, input, jackson.readTree(new StringReader(context)));
        assertEquals(expected, result);

//        String uri = "file:/D:/Megafon/workspace/histone-java/src/test/resources/relative_urls/template.tpl";
//        String result = histone.evaluateUri(uri, jackson.createObjectNode());
//        int l = 5;
    }

}
