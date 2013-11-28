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
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * User: sazonovkirill@gmail.com
 * Date: 24.12.12
 */
@Ignore
public class AstMarkerTest {
    private Histone histone;

    @Before
    public void init() throws HistoneException {
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histone = histoneBuilder.build();
    }

    @Test
    public void safeIf() throws HistoneException, IOException {
        String input = input("safe_if.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Test
    public void unsafeIf() throws HistoneException, IOException {
        String input = input("unsafe_if.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    @Test
    @Ignore("Maps safe check is not implemented")
    public void safeFor() throws HistoneException, IOException {
        String input = input("safe_for.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Test
    @Ignore("Maps safe check is not implemented")
    public void unsafeFor() throws HistoneException, IOException {
        String input = input("unsafe_for.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    @Test
    public void safeVar() throws HistoneException, IOException {
        String input = input("safe_var.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Test
    public void unsafeVar() throws HistoneException, IOException {
        String input = input("unsafe_var.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    @Test
    public void safeStatements() throws HistoneException, IOException {
        String input = input("safe_statements.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Test
    public void unsafeStatements() throws HistoneException, IOException {
        String input = input("unsafe_statements.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    @Test
    @Ignore("Some questions. Why it doesnt work?")
    public void safeSelector() throws HistoneException, IOException {
        String input = input("safe_selector.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Test
    public void unsafeSelector() throws HistoneException, IOException {
        String input = input("unsafe_selector.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    @Test
    public void safeCall() throws HistoneException, IOException {
        String input = input("safe_call.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Test
    public void unsafeCall() throws HistoneException, IOException {
        String input = input("unsafe_call.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    @Test
    public void safeMacro() throws HistoneException, IOException {
        String input = input("safe_macro.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Test
    public void unsafeMacro() throws HistoneException, IOException {
        String input = input("unsafe_macro.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    @Ignore
    @Test
    public void safeTemplate1() throws HistoneException, IOException {
        String input = input("safe_template1.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() > 0);
    }

    @Ignore
    @Test
    public void unsafeTemplate1() throws HistoneException, IOException {
        String input = input("unsafe_template1.tpl");
        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ast = histone.optimizeAST(ast);

        assertTrue(ast.get(1).get(0).asInt() < 0);
    }

    private String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/marker/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }


}
