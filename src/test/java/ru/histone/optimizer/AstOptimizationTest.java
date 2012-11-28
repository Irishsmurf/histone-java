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
package ru.histone.optimizer;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.utils.IOUtils;

import java.io.*;

import static org.junit.Assert.assertEquals;

@Ignore
public class AstOptimizationTest {
    private Histone histone;
//    private Gson gson;

    @Before
    public void before() throws HistoneException {
//        GsonBuilder gb = new GsonBuilder();
//        gb.serializeNulls();
//
//        final Resource res1 = new Resource() {
//            private final URI uri = URI.create("tt:/test.tpl");
//
//            @Override
//            public InputStream getInputStream() throws IOException {
//                return new ByteArrayInputStream("{{macro test1()}}test1{{/macro}}{{import 'a/test.tpl'}}".getBytes());
//            }
//
//            @Override
//            public URI getURI() {
//                return uri;
//            }
//
//            @Override
//            public void close() throws IOException {
//                //do nothing
//            }
//        };
//
//        final Resource res2 = new Resource() {
//            private final URI uri = URI.create("tt:/a/test.tpl");
//
//            @Override
//            public InputStream getInputStream() throws IOException {
//                return new ByteArrayInputStream("{{macro test2()}}test2{{/macro}}{{import 'b/test.tpl'}}".getBytes());
//            }
//
//            @Override
//            public URI getURI() {
//                return uri;
//            }
//
//            @Override
//            public void close() throws IOException {
//                //do nothing
//            }
//        };
//
//        final Resource res3 = new Resource() {
//            private final URI uri = URI.create("tt:/a/b/test.tpl");
//
//            @Override
//            public InputStream getInputStream() throws IOException {
//                return new ByteArrayInputStream("{{macro test3()}}test3{{/macro}}".getBytes());
//            }
//
//            @Override
//            public URI getURI() {
//                return uri;
//            }
//
//            @Override
//            public void close() throws IOException {
//                //do nothing
//            }
//        };
//
//        HistoneBuilder sb = new HistoneBuilder();
//        sb.setGlobalProperty(GlobalProperty.BASE_URI, "tt:/");
//        ResourceLoader resolver = new ResourceLoader() {
//            @Override
//            public String getScheme() {
//                return "tt";
//            }
//
//            @Override
//            public Boolean isCacheable() {
//                return true;
//            }
//
//            @Override
//            public Resource load(URI location, Node... args) throws ResourceLoadException {
//                if (location.toString().equals("tt:/test.tpl")) {
//                    return res1;
//                } else if (location.toString().equals("tt:/a/test.tpl")) {
//                    return res2;
//                } else if (location.toString().equals("tt:/a/b/test.tpl")) {
//                    return res3;
//                } else {
//                    throw new ResourceLoadException("Error: unknown resource");
//                }
//            }
//        };
//
//        sb.setResourceLoader(resolver);
//        ResourceLoader fileResolver = new ResourceLoader() {
//            @Override
//            public String getScheme() {
//                return "file";
//            }
//
//            @Override
//            public Boolean isCacheable() {
//                return true;
//            }
//
//            @Override
//            public Resource load(URI location, Node... args) throws ResourceLoadException {
//                InputStream stream = null;
//                try {
//                    stream = new FileInputStream(location.getPath());
//                } catch (FileNotFoundException e) {
//                    throw new ResourceLoadException("Error while loading resource", e);
//                }
//                return new ContentResource(stream, location);
//            }
//        };
//        sb.setResourceLoader(fileResolver);
//        gson = gb.create();
//        sb.setGson(gson);
//        histone = sb.build();
    }

//    private static class ContentResource implements Resource {
//        private InputStream stream;
//        private URI uri;
//
//        public ContentResource(InputStream stream, URI uri) {
//            this.stream = stream;
//            this.uri = uri;
//        }
//
//        @Override
//        public InputStream getInputStream() throws IOException {
//            return stream;
//        }
//
//        @Override
//        public URI getURI() {
//            return uri;
//        }
//
//        @Override
//        public void close() throws IOException {
//            stream.close();
//        }
//    }

    @Test
    public void test() throws HistoneException, IOException {
        String input = "{{for v in array(1,2,3)}} {{if v is 1}}te{{vv}}st{{/if}} {{/for}}";
        input = "A{{import 'file:///Users/psalnikov/Work/Admin/src/main/webapp/index.html'}}B" +
//                "{{macro test2()}} ZZ{{self.arguments}}XX{{global.ttt}}YY {{/macro}}" +
//                "{{macro test(a,b)}} AA{{self.arguments}}BB{{a}}CC{{test2(0)}}DD {{/macro}}" +

                "";

//        input = "{{object('arguments':array(1,2,'ttt'))}} {{test(1,2,'ttt')}}";
//        input = "{{x}} {{global.x}} {{this.x}} {{global.uniqueId()}} {{uniqueId()}} {{'test'.toUpperCase()}} {{(1+2-4).abs()}}";
//        input  = "{{for x in object('arguments':array(1,2,3)).arguments}} {{x}} {{/for}}";
//        input = "{{call test(1,2)}}ABC{{x}}ZXY{{/call}}";
        FileInputStream fis = new FileInputStream("/Users/psalnikov/Work/Admin/src/main/webapp/index.html");
        input = IOUtils.toString(fis);
//        input="{{var extName = requestParams.ext}}";

        System.out.println("Input");
        System.out.println(input);

        ArrayNode normalAst = histone.parseTemplateToAST(new StringReader(input));
        System.out.println("\n\nNormal AST");
//        System.out.println(gson.toJson(normalAst));

        String normalResult = histone.evaluateAST(normalAst);
        System.out.println("\nNormal Result");
        System.out.println(normalResult);

//        ArrayNode importsAst = histone.resolveImports(normalAst);
//        System.out.println("\n\nImports AST");
//        System.out.println(gson.toJson(importsAst));
//
//        ArrayNode markedAst = histone.markAst(importsAst);
//        System.out.println("\n\nMarked AST");
//        System.out.println(gson.toJson(markedAst));
//
//        ArrayNode inlinedAst = histone.inlineAst(markedAst);
//        System.out.println("\nInlined AST");
//        System.out.println(gson.toJson(inlinedAst));
//
//        ArrayNode markedAst2 = histone.markAst(inlinedAst);
//        System.out.println("\n\nMarked AST2");
//        System.out.println(gson.toJson(markedAst2));
//
//        ArrayNode inlinedAst2 = histone.inlineAst(markedAst2);
//        System.out.println("\nInlined AST2");
//        System.out.println(gson.toJson(inlinedAst2));
//
//        ArrayNode optimizedAst = histone.optimizeAst(inlinedAst2);
//        System.out.println("\n\nOptimized AST");
//        System.out.println(gson.toJson(makeASTShort(optimizedAst)));
////        System.out.println(gson.toJson(optimizedAst));
//
//        String optimizedResult = histone.processAST(optimizedAst);
//        System.out.println("\n\nOptimized Result");
//        System.out.println(optimizedResult);

//        assertEquals(normalResult, optimizedResult);
    }

    private ArrayNode makeASTShort(ArrayNode ast) {
//        ArrayNode result = new ArrayNode();
//        for (JsonNode elem : ast) {
//            if (elem.isJsonPrimitive()) {
//                result.add(new JsonPrimitive(shortenString(elem.getAsJsonPrimitive().getAsString())));
//            } else {
//                result.add(elem);
//            }
//        }
//        return result;
        return null;
    }

    private String shortenString(String string) {
        int SHORT_LEN = 12;
        if (string.trim().length() > SHORT_LEN * 3) {
            return string.substring(0, SHORT_LEN).trim() + " . . . " + string.substring(string.length() - SHORT_LEN).trim();
        } else {
            return string;
        }
    }
}
