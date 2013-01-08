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
package ru.histone.optimizer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * User: sazonovkirill@gmail.com
 * Date: 25.12.12
 */
public class ConstantPropagationTest {
    private Histone histone;

    @Before
    public void init() throws HistoneException {
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histone = histoneBuilder.build();
    }

    @Test
    public void simplest() throws IOException, HistoneException {
        String input = input("var.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);
        assertTrue(true);
    }

    private String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/constant_propagation/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }
}
