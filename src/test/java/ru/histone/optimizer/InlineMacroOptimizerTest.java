package ru.histone.optimizer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;
import ru.histone.HistoneException;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class InlineMacroOptimizerTest extends AbstractOptimizersTest {
    @Test
    public void inlineMacro() throws HistoneException, IOException {
        String input = input("inline/inline_macro.tpl");
        ArrayNode expected = (ArrayNode) getJackson().readTree("[\"\\nA\",\"B\"]");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.INLINE_MACRO);

        assertEquals(expected.toString(), ast.toString());
    }

    @Test
    public void inlineCall() throws HistoneException, IOException {
        String input = input("inline/inline_call.tpl");
        ArrayNode expected = (ArrayNode) getJackson().readTree("[\"\\nA\",\"B\\n\",\"Hello test world\"]");
        ArrayNode ast = getHistone().parseTemplateToAST(new StringReader(input));
        ast = getHistone().optimizeAST(ast, OptimizationTypes.INLINE_MACRO);

        assertEquals(expected.toString(), ast.toString());
    }

}
