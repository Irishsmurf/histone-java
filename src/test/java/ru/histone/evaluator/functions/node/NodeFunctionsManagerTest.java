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
package ru.histone.evaluator.functions.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.StringHistoneNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NodeFunctionsManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private NodeFunction emptyFunction1;
    private NodeFunction emptyFunction2;
    private NodeFunction emptyFunction1_DuplicatedName;
    private NodeFunction emptyFunction1_DifferentCase;
    private NodeFunction functionWithException;
    private NodeFactory nodeFactory;

    @Before
    public void before() {
        nodeFactory = new NodeFactory(new ObjectMapper());
        emptyFunction1 = new NodeFunction<StringHistoneNode>(nodeFactory) {
            @Override
            public String getName() {
                return "emptyFunction1";
            }

            @Override
            public Node execute(StringHistoneNode target, Node... args) throws NodeFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        emptyFunction2 = new NodeFunction<StringHistoneNode>(nodeFactory) {
            @Override
            public String getName() {
                return "emptyFunction2";
            }

            @Override
            public Node execute(StringHistoneNode target, Node... args) throws NodeFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        emptyFunction1_DuplicatedName = new NodeFunction<StringHistoneNode>(nodeFactory) {
            @Override
            public String getName() {
                return "emptyFunction1";
            }

            @Override
            public Node execute(StringHistoneNode target, Node... args) throws NodeFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        emptyFunction1_DifferentCase = new NodeFunction<StringHistoneNode>(nodeFactory) {
            @Override
            public String getName() {
                return "emptyfunction1";
            }

            @Override
            public Node execute(StringHistoneNode target, Node... args) throws NodeFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        functionWithException = new NodeFunction<StringHistoneNode>(nodeFactory) {
            @Override
            public String getName() {
                return "functionWithException";
            }

            @Override
            public Node execute(StringHistoneNode target, Node... args) throws NodeFunctionExecutionException {
                throw new RuntimeException("Some exception");
            }
        };
    }

    @Test
    public void checkFunctionOnEmptyFunctionList() {
        NodeFunctionsManager manager = new NodeFunctionsManager();

        assertFalse(manager.hasFunction(StringHistoneNode.class, "aaa"));
    }

    @Test
    public void executeOnEmptyFunctionList() {
        thrown.expect(NodeFunctionExecutionException.class);
        thrown.expectMessage("No NodeFunction 'aaa' found for node class 'StringHistoneNode'");

        NodeFunctionsManager manager = new NodeFunctionsManager();

        manager.execute(nodeFactory.string(), "aaa");
    }

    @Test
    public void checkFunctionRegistration() {
        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, emptyFunction1);

        assertFalse(manager.hasFunction(StringHistoneNode.class, "aaa"));
        assertTrue(manager.hasFunction(StringHistoneNode.class, "emptyFunction1"));
        assertFalse(manager.hasFunction(StringHistoneNode.class, "emptyfunction1"));
    }

    @Test
    public void checkSeveralFunctionRegistration() {
        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, emptyFunction1);
        manager.registerFunction(StringHistoneNode.class, emptyFunction2);

        assertFalse(manager.hasFunction(StringHistoneNode.class, "aaa"));
        assertTrue(manager.hasFunction(StringHistoneNode.class, "emptyFunction1"));
        assertTrue(manager.hasFunction(StringHistoneNode.class, "emptyFunction2"));
    }

    @Test
    public void checkFunctionRegistrationDuplicatedName() {
        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, emptyFunction1);
        manager.registerFunction(StringHistoneNode.class, emptyFunction1_DuplicatedName);
    }

    @Test
    public void checkFunctionRegistrationDifferentCaseInName() {
        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, emptyFunction1);
        manager.registerFunction(StringHistoneNode.class, emptyFunction1_DifferentCase);

        Node result1 = manager.execute(nodeFactory.string(), emptyFunction1.getName());
        Node result2 = manager.execute(nodeFactory.string(), emptyFunction1_DifferentCase.getName());

        assertEquals("emptyFunction1", result1.getAsString().getValue());
        assertEquals("emptyfunction1", result2.getAsString().getValue());
    }

    @Test
    public void executeNonExistingFunction() {
        thrown.expect(NodeFunctionExecutionException.class);
        thrown.expectMessage("No NodeFunction 'aaa' found for node class 'StringHistoneNode'");

        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, emptyFunction1);

        manager.execute(nodeFactory.string(), "aaa");
    }

    @Test
    public void executeFunctionWithDifferentCase() {
        thrown.expect(NodeFunctionExecutionException.class);
        thrown.expectMessage("No NodeFunction 'emptyfunction1' found for node class 'StringHistoneNode'");

        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, emptyFunction1);

        manager.execute(nodeFactory.string(), "emptyfunction1");
    }


    @Test
    public void exceptionInNodeFunction() {
        thrown.expect(NodeFunctionExecutionException.class);
        thrown.expectMessage("Error executing NodeFunction 'functionWithException' for node class 'StringHistoneNode'");

        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, functionWithException);

        manager.execute(nodeFactory.string(), "functionWithException");
    }

    @Test
    public void functionResultWithArgs() {
        NodeFunction NodeFunction = new NodeFunction<StringHistoneNode>(nodeFactory) {
            @Override
            public String getName() {
                return "myNodeFunction";
            }

            @Override
            public Node execute(StringHistoneNode target, Node... args) throws NodeFunctionExecutionException {
                StringBuilder sb = new StringBuilder();
                sb.append(getName()).append(" = ").append(target.getValue()).append("[");
                boolean addSeparator = false;
                for (Node node : args) {
                    if (addSeparator) {
                        sb.append("-");
                    }
                    sb.append(node.getAsString().getValue());
                    addSeparator = true;
                }
                sb.append("]");
                return nodeFactory.string(sb.toString());
            }
        };


        NodeFunctionsManager manager = new NodeFunctionsManager();
        manager.registerFunction(StringHistoneNode.class, NodeFunction);


        Node node = manager.execute(nodeFactory.string("+++"),"myNodeFunction", nodeFactory.string("A"), nodeFactory.string("B"), nodeFactory.string("C"));
        assertNotNull(node);
        assertTrue(node.isString());
        assertEquals(node.getAsString().getValue(), "myNodeFunction = +++[A-B-C]");
    }
}