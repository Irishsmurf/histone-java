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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AstOptimizerTest {
    private Histone histone;

    @Before
    public void init() throws HistoneException {
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histone = histoneBuilder.build();
    }

    //
    //  Templates from 'constant folding' folder
    //

    @Test
    public void constant_folding_simplest() throws IOException, HistoneException {
        String input = input("constant_folding/simplest.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        assertTrue(finalAst.get(0).asInt() == 7);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_folding_advanced_expr1() throws IOException, HistoneException {
        String input = input("constant_folding/advanced_expr1.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);
        assertTrue(finalAst.get(0).asText().equals("1.43"));

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_folding_advanced_expr2() throws IOException, HistoneException {
        String input = input("constant_folding/advanced_expr2.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);
        assertTrue(finalAst.get(0).asBoolean() == false);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_folding_advanced_expr3() throws IOException, HistoneException {
        String input = input("constant_folding/advanced_expr3.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);
        assertTrue(finalAst.get(0).asBoolean() == true);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_folding_template1() throws IOException, HistoneException {
        String input = input("constant_folding/template1.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_folding_template2() throws IOException, HistoneException {
        String input = input("constant_folding/template2.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_folding_template3() throws IOException, HistoneException {
        String input = input("constant_folding/template3.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }


    //
    // Templates from 'constant propagation' folder
    //

    @Test
    public void constant_propagation_call() throws IOException, HistoneException {
        String input = input("constant_propagation/call.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_propagation_for() throws IOException, HistoneException {
        String input = input("constant_propagation/for.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        String astS = histone.evaluateAST(ast);

        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_propagation_if() throws IOException, HistoneException {
        String input = input("constant_propagation/if.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_propagation_macro() throws IOException, HistoneException {
        String input = input("constant_propagation/macro.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_propagation_map() throws IOException, HistoneException {
        String input = input("constant_propagation/map.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void constant_propagation_var() throws IOException, HistoneException {
        String input = input("constant_propagation/var.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void test1_positive() throws IOException, HistoneException {
        String input = input("test1.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void test2_positive() throws IOException, HistoneException {
        String input = input("test2.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void test3_positive() throws IOException, HistoneException {
        String input = input("test3.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void test4_negative() throws IOException, HistoneException {
        String input = input("test4.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void test5() throws IOException, HistoneException {
        String input = input("test5.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void test6() throws IOException, HistoneException {
        String input = input("test6.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    @Test
    public void test7() throws IOException, HistoneException {
        String input = input("test7.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeAST(ast);

        // Assert that evaluation results are equal
        String astS = histone.evaluateAST(ast);
        String finalAstS = histone.evaluateAST(finalAst);
        assertEquals(astS, finalAstS);
    }

    private String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }
}
