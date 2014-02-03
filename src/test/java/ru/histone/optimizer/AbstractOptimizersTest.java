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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class AbstractOptimizersTest {
    private Histone histone;
    private NodeFactory nodeFactory;
    private ObjectMapper jackson;

    @Before
    public void _before() throws HistoneException {
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histone = histoneBuilder.build();
        nodeFactory = histoneBuilder.getNodeFactory();
        jackson = new ObjectMapper();
    }

    protected Histone getHistone() {
        return histone;
    }

    protected NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    protected ObjectMapper getJackson() {
        return jackson;
    }

    protected void assertSafe(ArrayNode ast) {
        assertTrue(ast.size() > 1);
        assertTrue(ast.get(1).size() > 0);
        if (!ast.get(1).get(0).isTextual() && ast.get(1).get(0).asInt() < 0) {
            fail("Expected safe:" + ast.get(1).get(0).toString());
        }
    }

    protected void assertUnSafe(ArrayNode ast) {
        assertTrue(ast.size() > 1);
        assertTrue(ast.get(1).size() > 0);
        if (ast.get(1).get(0).isTextual() || ast.get(1).get(0).asInt() > 0) {
            fail("Expected unsafe:" + ast.get(1).get(0).toString());
        }
    }

    protected String input(String filename) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("optimizer/" + filename);
        IOUtils.copy(is, sw);
        return sw.toString();
    }


}
