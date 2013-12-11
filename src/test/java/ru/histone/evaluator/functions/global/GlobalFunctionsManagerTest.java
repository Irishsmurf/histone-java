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
package ru.histone.evaluator.functions.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GlobalFunctionsManagerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private GlobalFunction emptyFunction1;
    private GlobalFunction emptyFunction2;
    private GlobalFunction emptyFunction1_DuplicatedName;
    private GlobalFunction emptyFunction1_DifferentCase;
    private GlobalFunction functionWithException;
    private GlobalFunction functionWithStopTheWorldException;
    private NodeFactory nodeFactory;

    @Before
    public void before() {
        nodeFactory = new NodeFactory(new ObjectMapper());

        emptyFunction1 = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "emptyFunction1";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        emptyFunction2 = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "emptyFunction2";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        emptyFunction1_DuplicatedName = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "emptyFunction1";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        emptyFunction1_DifferentCase = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "emptyfunction1";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return getNodeFactory().string(getName());
            }
        };

        functionWithException = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "functionWithException";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                throw new RuntimeException("Some exception");
            }
        };

        functionWithStopTheWorldException = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "functionWithException";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                CustomPayload payload = new CustomPayload("test123");
                throw new GlobalFunctionStopTheWorldException(payload, "Custom stop");
            }

        };
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


    @Test
    public void checkFunctionOnEmptyFunctionList() {
        GlobalFunctionsManager manager = new GlobalFunctionsManager();

        assertFalse(manager.hasFunction("aaa"));
    }

    @Test
    public void executeOnEmptyFunctionList() {
        thrown.expect(GlobalFunctionExecutionException.class);
        thrown.expectMessage("No GlobalFunction found by name");

        GlobalFunctionsManager manager = new GlobalFunctionsManager();

        manager.execute("aaa");
    }

    @Test
    public void checkFunctionRegistration() {
        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(emptyFunction1);

        assertFalse(manager.hasFunction("aaa"));
        assertTrue(manager.hasFunction("emptyFunction1"));
        assertFalse(manager.hasFunction("emptyfunction1"));
    }

    @Test
    public void checkSeveralFunctionRegistration() {
        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(emptyFunction1);
        manager.registerFunction(emptyFunction2);

        assertFalse(manager.hasFunction("aaa"));
        assertTrue(manager.hasFunction("emptyFunction1"));
        assertTrue(manager.hasFunction("emptyFunction2"));
    }

    @Test
    public void checkFunctionRegistrationDuplicatedName() {
        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(emptyFunction1);
        manager.registerFunction(emptyFunction1_DuplicatedName);
    }

    @Test
    public void checkFunctionRegistrationDifferentCaseInName() {
        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(emptyFunction1);
        manager.registerFunction(emptyFunction1_DifferentCase);

        Node result1 = manager.execute(emptyFunction1.getName());
        Node result2 = manager.execute(emptyFunction1_DifferentCase.getName());

        assertEquals("emptyFunction1", result1.getAsString().getValue());
        assertEquals("emptyfunction1", result2.getAsString().getValue());
    }

    @Test
    public void executeNonExistingFunction() {
        thrown.expect(GlobalFunctionExecutionException.class);
        thrown.expectMessage("No GlobalFunction found by name");

        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(emptyFunction1);

        manager.execute("aaa");
    }

    @Test
    public void executeFunctionWithDifferentCase() {
        thrown.expect(GlobalFunctionExecutionException.class);
        thrown.expectMessage("No GlobalFunction found by name");

        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(emptyFunction1);

        manager.execute("emptyfunction1");
    }


    @Test
    public void exceptionInGlobalFunction() {
        thrown.expect(GlobalFunctionExecutionException.class);
        thrown.expectMessage("GlobalFunction 'functionWithException' execution error");

        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(functionWithException);

        manager.execute("functionWithException");
    }

    @Test
    public void stopTheWorldExceptionInGlobalFunction() {
        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(functionWithStopTheWorldException);

        try {
            manager.execute("functionWithException");
        } catch (GlobalFunctionStopTheWorldException e) {
            assertNotNull(e.getPayload());
            assertNotNull(e.getMessage());
            assertTrue(e.getPayload() instanceof CustomPayload);
            assertEquals("Custom stop", e.getMessage());
            assertEquals("test123", ((CustomPayload) e.getPayload()).getText());

            return;
        }
        fail("Expected GlobalFunctionStopTheWorldException exception");
    }

    @Test
    public void functionResultWithArgs() {
        GlobalFunction globalFunction = new GlobalFunction(nodeFactory) {
            @Override
            public String getName() {
                return "myGlobalFunction";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                StringBuilder sb = new StringBuilder();
                sb.append(getName()).append(" = ").append("[");
                boolean addSeparator = false;
                for (Node node : args) {
                    if (addSeparator) {
                        sb.append("-");
                    }
                    sb.append(node.getAsString().getValue());
                    addSeparator = true;
                }
                sb.append("]");
                return getNodeFactory().string(sb.toString());
            }
        };


        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(globalFunction);


        Node node = manager.execute("myGlobalFunction", nodeFactory.string("A"), nodeFactory.string("B"), nodeFactory.string("C"));
        assertNotNull(node);
        assertTrue(node.isString());
        assertEquals(node.getAsString().getValue(), "myGlobalFunction = [A-B-C]");
    }
}
