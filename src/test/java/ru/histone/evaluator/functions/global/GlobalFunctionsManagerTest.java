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
package ru.histone.evaluator.functions.global;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.StringNode;

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

    @Before
    public void before() {
        emptyFunction1 = new GlobalFunction() {
            @Override
            public String getName() {
                return "emptyFunction1";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return StringNode.create(getName());
            }
        };

        emptyFunction2 = new GlobalFunction() {
            @Override
            public String getName() {
                return "emptyFunction2";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return StringNode.create(getName());
            }
        };

        emptyFunction1_DuplicatedName = new GlobalFunction() {
            @Override
            public String getName() {
                return "emptyFunction1";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return StringNode.create(getName());
            }
        };

        emptyFunction1_DifferentCase = new GlobalFunction() {
            @Override
            public String getName() {
                return "emptyfunction1";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return StringNode.create(getName());
            }
        };

        functionWithException = new GlobalFunction() {
            @Override
            public String getName() {
                return "functionWithException";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                throw new RuntimeException("Some exception");
            }
        };
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

        assertEquals("emptyFunction1",result1.getAsString().getValue());
        assertEquals("emptyfunction1",result2.getAsString().getValue());
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
    public void functionResultWithArgs() {
        GlobalFunction globalFunction = new GlobalFunction() {
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
                return StringNode.create(sb.toString());
            }
        };


        GlobalFunctionsManager manager = new GlobalFunctionsManager();
        manager.registerFunction(globalFunction);


        Node node = manager.execute("myGlobalFunction", StringNode.create("A"), StringNode.create("B"), StringNode.create("C"));
        assertNotNull(node);
        assertTrue(node.isString());
        assertEquals(node.getAsString().getValue(), "myGlobalFunction = [A-B-C]");
    }
}
