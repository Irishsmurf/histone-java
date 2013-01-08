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
import org.junit.Ignore;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.IOUtils;

import java.io.*;

import static org.junit.Assert.assertTrue;

/**
 * User: sazonovkirill@gmail.com
 * Date: 25.12.12
 */
public class ConstantFoldingTest {
    private Histone histone;

    @Before
    public void init() throws HistoneException {
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histone = histoneBuilder.build();
    }

    @Test
    public void simplest() throws IOException, HistoneException {
        String input = input("simplest.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(finalAst.get(0).get(0).asInt() == AstNodeType.INT);
        assertTrue(finalAst.get(0).get(1).asInt() == 7);
    }

    @Test
    public void advanced_expr1() throws IOException, HistoneException {
        String input = input("advanced_expr1.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(finalAst.get(0).get(0).asInt() == AstNodeType.DOUBLE);
        assertTrue(finalAst.get(0).get(1).asDouble() == 1.43d);
    }

    @Test
    public void advanced_expr2() throws IOException, HistoneException {
        String input = input("advanced_expr2.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(finalAst.get(0).get(0).asInt() == AstNodeType.FALSE);
    }

    @Test
    public void advanced_expr3() throws IOException, HistoneException {
        String input = input("advanced_expr3.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(finalAst.get(0).get(0).asInt() == AstNodeType.TRUE);
    }

    @Test
    public void template1() throws IOException, HistoneException {
        String input = input("template1.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(true);
    }

    @Test
    public void template2() throws IOException, HistoneException {
        String input = input("template2.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(true);
    }

    @Test
    public void template3() throws IOException, HistoneException {
        String input = input("template3.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(true);
    }

    @Ignore
    @Test
    public void performanceTest() throws IOException, HistoneException {
        File f = new File("D:\\Megafon\\workspace\\portal-repository\\repository\\templates\\main\\index.tpl");
        StringWriter sw = new StringWriter();
        InputStream is = new FileInputStream(f);
        IOUtils.copy(is, sw);
        String input = sw.toString();

        for (int i = 0; i < 10000; i++) {
            ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
            String result = histone.evaluateAST(ast);
        }
    }

    private String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/constant_folding/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }
}
