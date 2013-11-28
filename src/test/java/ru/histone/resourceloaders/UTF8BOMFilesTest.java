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
package ru.histone.resourceloaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class UTF8BOMFilesTest {
    private Histone histone;
    private ObjectMapper jackson;
    private String baseHref;

    @Before
    public void before() throws Exception {
        HistoneBuilder builder = new HistoneBuilder();

        URL url = getClass().getClassLoader().getResource("resourceloader/test.txt");
        baseHref = new File(url.toURI()).getParent() + "/";

        histone = builder.build();
        jackson = new ObjectMapper();
    }

    @Test
    public void test() throws Exception {
        String input = "A{{loadJSON('encoding-correct.json').toJSON()}}B{{loadJSON('encoding-wrong.json').toJSON()}}C";
        String expected = "A{\"text\":\"проверка кодировки\"}B{\"text\":\"проверка кодировки\"}C";

        String result = histone.evaluate("file:/" + baseHref, input, jackson.createObjectNode());

        assertEquals(expected, result);
    }
}
