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
package ru.histone.evaluator.functions.global.stoptheworld_exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.HistoneStopTheWorldException;
import ru.histone.evaluator.functions.global.GlobalFunction;
import ru.histone.evaluator.functions.global.GlobalFunctionExecutionException;
import ru.histone.evaluator.functions.global.GlobalFunctionStopTheWorldException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StopTheWorldTest {
    private Histone histone;
    private ObjectMapper jackson;
    private String baseUri;
    private GlobalFunction functionWithStopTheWorldException;
    private NodeFactory nodeFactory;

    @Before
    public void init() throws HistoneException, URISyntaxException {
        jackson = new ObjectMapper();
        nodeFactory = new NodeFactory(jackson);

        functionWithStopTheWorldException = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "stop";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                CustomPayload payload = new CustomPayload("test123");
                throw new GlobalFunctionStopTheWorldException(payload, "Custom stop");
            }

        };


        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histoneBuilder.addGlobalFunction(functionWithStopTheWorldException);
        histone = histoneBuilder.build();
        baseUri = this.getClass().getClassLoader().getResource("ru/histone/evaluator/functions/global/stoptheworld_exceptions").toURI().toString();

    }

    @Test
    public void test() throws HistoneException, IOException, URISyntaxException {
        ObjectNode context = jackson.createObjectNode();

        ArrayNode ast = histone.parseTemplateToAST("A {{stop()}} B");

        try {
            histone.evaluateAST(baseUri, ast, context);
        } catch (HistoneStopTheWorldException e) {
            assertNotNull(e.getPayload());
            assertNotNull(e.getMessage());
            assertTrue(e.getPayload() instanceof CustomPayload);

            assertNotNull(e.getCause());
            assertNotNull(e.getCause().getMessage());
            assertTrue(e.getCause() instanceof GlobalFunctionStopTheWorldException);
            assertTrue(((GlobalFunctionStopTheWorldException) e.getCause()).getPayload() instanceof CustomPayload);
            assertEquals("Custom stop", e.getCause().getMessage());
            assertEquals("test123", ((CustomPayload) ((GlobalFunctionStopTheWorldException) e.getCause()).getPayload()).getText());

            assertEquals("StopTheWorld exception in global function stop() method", e.getMessage());
            assertEquals("test123", ((CustomPayload) e.getPayload()).getText());

            return;
        }

        fail("Expected HistoneStopTheWorldException exception");
    }

    public static class CustomPayload {
        private String text;

        public CustomPayload(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }


}
