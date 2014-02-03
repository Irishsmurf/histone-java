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
import org.junit.Ignore;
import org.junit.Test;
import ru.histone.HistoneException;
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * User: sazonovkirill@gmail.com
 * Date: 24.12.12
 */
public class SafeASTNodesMarkerTest extends AbstractOptimizersTest {
    @Test
    public void safeIf() throws HistoneException, IOException {
        String input = input("safe_if.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertSafe(ast);
    }

    @Test
    public void unsafeIf() throws HistoneException, IOException {
        String input = input("unsafe_if.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertUnSafe(ast);
    }

    @Ignore("TODO")
    @Test
    public void safeFor() throws HistoneException, IOException {
        String input = input("safe_for.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertSafe(ast);
    }

    @Ignore("TODO")
    @Test
    public void unsafeFor() throws HistoneException, IOException {
        String input = input("unsafe_for.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertUnSafe(ast);
    }

    @Test
    public void safeVar() throws HistoneException, IOException {
        String input = input("safe_var.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertSafe(ast);
    }

    @Test
    public void unsafeVar() throws HistoneException, IOException {
        String input = input("unsafe_var.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertUnSafe(ast);
    }

    @Test
    public void safeStatements() throws HistoneException, IOException {
        String input = input("safe_statements.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertSafe(ast);
    }

    @Test
    public void unsafeStatements() throws HistoneException, IOException {
        String input = input("unsafe_statements.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertUnSafe(ast);
    }

    @Ignore("TODO")
    @Test
    public void safeNodeFunc() throws HistoneException, IOException {
        String input = input("safe_node_func.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertSafe(ast);
    }

    @Ignore("TODO")
    @Test
    public void unsafeNodeFunc() throws HistoneException, IOException {
        String input = input("unsafe_node_func.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertUnSafe(ast);
    }

    @Ignore("TODO")
    @Test
    public void safeMacro() throws HistoneException, IOException {
        String input = input("safe_macro.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertSafe(ast);
    }

    @Ignore("TODO")
    @Test
    public void unsafeMacro() throws HistoneException, IOException {
        String input = input("unsafe_macro.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertUnSafe(ast);
    }

    @Ignore("safe_template1.tpl has incorrect test code")
    @Test
    public void safeTemplate1() throws HistoneException, IOException {
        String input = input("safe_template1.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertSafe(ast);
    }

    @Test
    public void unsafeTemplate1() throws HistoneException, IOException {
        String input = input("unsafe_template1.tpl");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.SAFE_CODE_MARKER);

        assertUnSafe(ast);
    }

    private String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/marker/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }


}
