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
package ru.histone;

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import ru.histone.evaluator.functions.global.GlobalFunction;
import ru.histone.evaluator.functions.global.GlobalFunctionExecutionException;
import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.functions.node.NodeFunctionExecutionException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.StringNode;
import ru.histone.resourceloaders.Resource;
import ru.histone.resourceloaders.ResourceLoadException;
import ru.histone.resourceloaders.ResourceLoader;
import ru.histone.resourceloaders.StreamResource;

import static org.junit.Assert.*;

public class HistoneBuilderTest {
    private NodeFunction<StringNode> nodeFunction1;
    private NodeFunction<StringNode> nodeFunction2;
    private GlobalFunction globalFunction1;
    private GlobalFunction globalFunction2;
    private ResourceLoader resourceLoader1;
    private ResourceLoader resourceLoader2;

    @Before
    public void before() {


        nodeFunction1 = new NodeFunction<StringNode>() {
            @Override
            public String getName() {
                return "func1";
            }

            @Override
            public Node execute(StringNode target, Node... args) throws NodeFunctionExecutionException {
                return StringNode.create("111");
            }
        };

        nodeFunction2 = new NodeFunction<StringNode>() {
            @Override
            public String getName() {
                return "func2";
            }

            @Override
            public Node execute(StringNode target, Node... args) throws NodeFunctionExecutionException {
                return StringNode.create("222");
            }
        };

        globalFunction1 = new GlobalFunction() {
            @Override
            public String getName() {
                return "globalFunc1";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return StringNode.create("111");
            }
        };

        globalFunction2 = new GlobalFunction() {
            @Override
            public String getName() {
                return "globalFunc2";
            }

            @Override
            public Node execute(Node... args) throws GlobalFunctionExecutionException {
                return StringNode.create("222");
            }
        };
        resourceLoader1 = new ResourceLoader() {
            @Override
            public Resource load(String href, String baseHref, Node... args) throws ResourceLoadException {
                if (!href.equals("r1:/test.txt")) {
                    throw new ResourceLoadException("Resource not found");
                }

                try {
                    return new StreamResource(new ByteArrayInputStream("test".getBytes("UTF-8")), "r1:/test.txt");
                } catch (UnsupportedEncodingException e) {
                    throw new ResourceLoadException("Error", e);
                }
            }

            @Override
            public String resolveFullPath(String href, String baseHref) throws ResourceLoadException {
                return "r1:/test.txt";
            }
        };
        resourceLoader2 = new ResourceLoader() {
            @Override
            public Resource load(String href, String baseHref, Node... args) throws ResourceLoadException {
                if (!href.equals("r2:/test.txt")) {
                    throw new ResourceLoadException("Resource not found");
                }

                try {
                    return new StreamResource(new ByteArrayInputStream("TEST".getBytes("UTF-8")), "r2:/test.txt");
                } catch (UnsupportedEncodingException e) {
                    throw new ResourceLoadException("Error", e);
                }
            }

            @Override
            public String resolveFullPath(String href, String baseHref) throws ResourceLoadException {
                return "r2:/test.txt";
            }
        };
    }

    @Test
    public void commonTest() throws HistoneException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Set<GlobalFunction> globalFunctions = new HashSet<GlobalFunction>();
        globalFunctions.add(globalFunction1);
        globalFunctions.add(globalFunction2);

        Map<Class<? extends Node>, Set<NodeFunction<? extends Node>>> nodeFunctions = new HashMap<Class<? extends Node>, Set<NodeFunction<? extends Node>>>();
        nodeFunctions.put(StringNode.class, new HashSet<NodeFunction<? extends Node>>());
        nodeFunctions.get(StringNode.class).add(nodeFunction1);
        nodeFunctions.get(StringNode.class).add(nodeFunction2);

        HistoneBuilder builder = new HistoneBuilder();
        builder.setGson(gson);
        builder.setResourceLoader(resourceLoader1);
        builder.setGlobalFunctions(globalFunctions);
        builder.setNodeFunctions(nodeFunctions);

        Histone histone = builder.build();

        assertNotNull(histone);
    }

    @Test
    public void changeGsonAfterBuildExecuted() throws HistoneException {
        HistoneBuilder builder = new HistoneBuilder();
        Gson gson1 = new Gson();
        builder.setGson(gson1);

        Histone histone1 = builder.build();

        Gson gson2 = new Gson();
        builder.setGson(gson2);

        Histone histone2 = builder.build();

        assertNotSame(histone1, histone2);
        assertSame(gson1, histone1.getGson());
        assertSame(gson2, histone2.getGson());
    }

    @Test
    public void addResolversAfterBuildExecuted() throws HistoneException {
        HistoneBuilder builder = new HistoneBuilder();

        builder.setResourceLoader(resourceLoader1);
        Histone histone1 = builder.build();

        assertEquals("a test b", histone1.evaluate("a {{loadText('r1:/test.txt')}} b"));
        assertEquals("a  b", histone1.evaluate("a {{loadText('r2:/test.txt')}} b"));

        builder.setResourceLoader(resourceLoader2);
        Histone histone2 = builder.build();

        assertEquals("a test b", histone1.evaluate("a {{loadText('r1:/test.txt')}} b"));
        assertEquals("a  b", histone1.evaluate("a {{loadText('r2:/test.txt')}} b"));


        assertEquals("a  b", histone2.evaluate("a {{loadText('r1:/test.txt')}} b"));
        assertEquals("a TEST b", histone2.evaluate("a {{loadText('r2:/test.txt')}} b"));
    }

    @Test
    public void setNodeFunctionsAfterBuildExecuted() throws HistoneException {
        HistoneBuilder builder = new HistoneBuilder();

        Map<Class<? extends Node>, Set<NodeFunction<? extends Node>>> nodeFunctions1 = new HashMap<Class<? extends Node>, Set<NodeFunction<? extends Node>>>();
        nodeFunctions1.put(StringNode.class, new HashSet<NodeFunction<? extends Node>>());
        nodeFunctions1.get(StringNode.class).add(nodeFunction1);
        builder.setNodeFunctions(nodeFunctions1);

        Histone histone1 = builder.build();

        String result1 = histone1.evaluate("a {{'test'.func1()}} b");
        assertEquals("a 111 b", result1);

        Map<Class<? extends Node>, Set<NodeFunction<? extends Node>>> nodeFunctions2 = new HashMap<Class<? extends Node>, Set<NodeFunction<? extends Node>>>();
        nodeFunctions2.put(StringNode.class, new HashSet<NodeFunction<? extends Node>>());
        nodeFunctions2.get(StringNode.class).add(nodeFunction2);
        builder.setNodeFunctions(nodeFunctions2);

        Histone histone2 = builder.build();
        String result2 = histone2.evaluate("a {{'test'.func2()}} b");
        assertEquals("a 222 b", result2);
    }

    @Test
    public void addNodeFunctionsAfterBuildExecuted() throws HistoneException {
        HistoneBuilder builder = new HistoneBuilder();

        builder.addNodeFunction(StringNode.class, nodeFunction1);
        Histone histone1 = builder.build();

        assertEquals("a 111 b", histone1.evaluate("a {{'test'.func1()}} b"));

        builder.addNodeFunction(StringNode.class, nodeFunction2);
        Histone histone2 = builder.build();

        assertEquals("a 111 b", histone1.evaluate("a {{'test'.func1()}} b"));
        assertEquals("a  b", histone1.evaluate("a {{'test'.func2()}} b"));

        assertEquals("a 111 b", histone2.evaluate("a {{'test'.func1()}} b"));
        assertEquals("a 222 b", histone2.evaluate("a {{'test'.func2()}} b"));
    }

    @Test
    public void setGlobalFunctionsAfterBuildExecuted() throws HistoneException {
        HistoneBuilder builder = new HistoneBuilder();

        Set<GlobalFunction> globalFunctions1 = new HashSet<GlobalFunction>();
        globalFunctions1.add(globalFunction1);
        builder.setGlobalFunctions(globalFunctions1);

        Histone histone1 = builder.build();

        String result1 = histone1.evaluate("a {{globalFunc1()}} b");
        assertEquals("a 111 b", result1);

        Set<GlobalFunction> globalFunctions2 = new HashSet<GlobalFunction>();
        globalFunctions2.add(globalFunction2);
        builder.setGlobalFunctions(globalFunctions2);

        Histone histone2 = builder.build();
        String result2 = histone2.evaluate("a {{globalFunc2()}} b");
        assertEquals("a 222 b", result2);
    }

    @Test
    public void addGlobalFunctionsAfterBuildExecuted() throws HistoneException {
        HistoneBuilder builder = new HistoneBuilder();

        builder.addGlobalFunction(globalFunction1);

        Histone histone1 = builder.build();

        assertEquals("a 111 b", histone1.evaluate("a {{globalFunc1()}} b"));

        builder.addGlobalFunction(globalFunction2);

        Histone histone2 = builder.build();
        assertEquals("a 111 b", histone1.evaluate("a {{globalFunc1()}} b"));
        assertEquals("a  b", histone1.evaluate("a {{globalFunc2()}} b"));

        assertEquals("a 111 b", histone2.evaluate("a {{globalFunc1()}} b"));
        assertEquals("a 222 b", histone2.evaluate("a {{globalFunc2()}} b"));
    }

    @Test
    public void globalNamespace() throws URISyntaxException, HistoneException {
        HistoneBuilder builder = new HistoneBuilder();
        builder.setGlobalProperty(GlobalProperty.BASE_URI, new URI("dummy://some/folder/").toString());
        builder.setGlobalProperty(GlobalProperty.CLIENT_TYPE, "java");
        builder.setGlobalProperty(GlobalProperty.USER_AGENT, "MSIE");

        Histone histone = builder.build();

        String input = "a {{global.baseURI}} b {{global.clientType}} c {{global.userAgent}} d";
        String expected = "a dummy://some/folder/ b java c MSIE d";
        String output = histone.evaluate(input);

        assertEquals(expected, output);
    }

    @Test
    public void tttttt() throws HistoneException {
        HistoneBuilder builder = new HistoneBuilder();



    }
}
