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
package ru.histone.eval;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class EvalTest {
    private Histone histone;
    private ObjectMapper om;
    private String baseUri;

    @Before
    public void init() throws HistoneException, URISyntaxException {
        histone = new HistoneBuilder().build();
        om = new ObjectMapper();
        baseUri = this.getClass().getClassLoader().getResource("ru/histone/eval").toURI().toString();
    }

    @Test
    public void test1() throws HistoneException, IOException, URISyntaxException {
        ObjectNode context = om.createObjectNode();

        ArrayNode ast = histone.parseTemplateToAST(new InputStreamReader(this.getClass().getResourceAsStream("test1.tpl")));
        String result = histone.evaluateAST(baseUri, ast, context);
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("test1.result.txt"));
        Assert.assertEquals(expected.trim(), result.trim());
    }

    @Test
    public void ruslan1() throws HistoneException, URISyntaxException, IOException {
        ObjectNode context = om.createObjectNode();

        context.put("this", "THIS");

        ArrayNode ast = histone.parseTemplateToAST(new InputStreamReader(this.getClass().getResourceAsStream("ruslan1.tpl")));
        String result = histone.evaluateAST(baseUri, ast, context);
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("ruslan1.result.txt"));

        Assert.assertEquals(expected.trim(), result.trim());
    }

    @Test
    @Ignore
    public void ruslan2() throws HistoneException, URISyntaxException, IOException {
        ObjectNode context = om.createObjectNode();

        context.put("this", "THIS");

        ArrayNode ast = histone.parseTemplateToAST(new InputStreamReader(this.getClass().getResourceAsStream("ruslan2.tpl")));
        String result = histone.evaluateAST(baseUri, ast, context);
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("ruslan2.result.txt"));

        Assert.assertEquals(expected.trim(), result.trim());
    }

    /**
     * Demostrates a seria of nested evals.
     */
    @Test
    public void nestedEvals() throws HistoneException, IOException {
        ObjectNode context = om.createObjectNode();

        ArrayNode ast = histone.parseTemplateToAST(new InputStreamReader(this.getClass().getResourceAsStream("nestedEvals.tpl")));
        String result = histone.evaluateAST(baseUri, ast, context);
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("nestedEvals.result.txt"));

        Assert.assertEquals(expected.trim(), result.trim());
    }

    /**
     * Demonstrates, that eval cannot change external context.
     */
    @Test
    public void saveState() throws HistoneException, IOException {
        ObjectNode context = om.createObjectNode();

        ArrayNode ast = histone.parseTemplateToAST(new InputStreamReader(this.getClass().getResourceAsStream("saveState.tpl")));
        String result = histone.evaluateAST(baseUri, ast, context);
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("saveState.result.txt"));

        Assert.assertEquals(expected.trim(), result.trim());
    }

    /**
     * Demonstrates, that eval cannot change external context.
     */
    @Test
    public void saveState2() throws HistoneException, IOException {
        ObjectNode context = om.createObjectNode();

        ArrayNode ast = histone.parseTemplateToAST(new InputStreamReader(this.getClass().getResourceAsStream("saveState2.tpl")));
        String result = histone.evaluateAST(baseUri, ast, context);
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("saveState2.result.txt"));

        Assert.assertEquals(expected.trim(), result.trim());
    }
}
