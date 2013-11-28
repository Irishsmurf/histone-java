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

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: sazonovkirill@gmail.com
 * Date: 25.12.12
 */
@Ignore("This test validates only one optimization - ConstantPropagation - and fails, due to now optimization includes much more modules")
public class ConstantPropagationTest {
    private Histone histone;

    @Before
    public void init() throws HistoneException {
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histone = histoneBuilder.build();
    }

    @Test
    @Ignore("Not implemented yet!")
    public void call() throws IOException, HistoneException {
        throw new RuntimeException("Not implemented yet");
    }

    @Test
    public void for_() throws IOException, HistoneException {
        /**
         * Warning: variable content declaration is not implemented completely,
         * so variables, declared in a such manner will not be propagated.
         */

        String input = input("for.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        assertTrue(finalAst.get(2).get(3).get(0).get(3).get(1).asText().equals("Hello"));

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void if_() throws IOException, HistoneException {
        /**
         * Warning: variable content declaration is not implemented completely,
         * so variables, declared in a such manner will not be propagated.
         */

        String input = input("if.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        assertTrue(finalAst.get(4).get(1).get(0).get(0).get(0).asInt() == AstNodeType.TRUE);
        assertTrue(finalAst.get(4).get(1).get(0).get(1).get(3).get(2).get(1).get(1).get(1).asInt() == 1);
        assertTrue(finalAst.get(4).get(1).get(0).get(1).get(3).get(2).get(1).get(2).get(1).asInt() == 1);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void macro() throws IOException, HistoneException {
        String input = input("macro.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        assertTrue(finalAst.get(2).get(3).get(3).get(1).asText().equals("Hello"));
        assertTrue(finalAst.get(2).get(3).get(5).get(1).asText().equals("world"));

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void map() throws IOException, HistoneException {
        String input = input("map.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        assertTrue(finalAst.get(2).get(2).get(1).get(0).get(1).get(1).asInt() == 3);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void var() throws IOException, HistoneException {
        String input = input("var.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        assertTrue(finalAst.get(0).get(2).get(1).asInt() == 5);
        assertTrue(finalAst.get(2).get(2).get(1).asInt() == 5);
        assertTrue(finalAst.get(4).get(1).asInt() == 5);
        assertTrue(finalAst.get(6).get(2).get(1).asInt() == 5);
        assertTrue(finalAst.get(8).get(1).asInt() == 5);
        assertTrue(finalAst.get(10).get(2).get(1).get(1).get(1).asInt() == 5);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    private String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/constant_propagation/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }
}
