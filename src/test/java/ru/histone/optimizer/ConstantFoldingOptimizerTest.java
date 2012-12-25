package ru.histone.optimizer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
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

import static org.junit.Assert.assertTrue;

/**
 * User: sazonovkirill@gmail.com
 * Date: 25.12.12
 */
public class ConstantFoldingOptimizerTest {
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

        assertTrue(false);
    }

    @Test
    public void template2() throws IOException, HistoneException {
        String input = input("template2.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(false);
    }

    @Test
    public void template3() throws IOException, HistoneException {
        String input = input("template3.tpl");

        ArrayNode ast = histone.parseTemplateToAST(new StringReader(input));
        ArrayNode finalAst = histone.optimizeConstantFolding(ast);

        assertTrue(false);
    }

    private String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/constant_folding/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }
}
