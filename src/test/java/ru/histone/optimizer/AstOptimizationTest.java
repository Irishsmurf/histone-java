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
import com.fasterxml.jackson.databind.node.NullNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.histone.GlobalProperty;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.utils.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

public class AstOptimizationTest {
    private Histone histone;

    @Before
    public void before() throws HistoneException {
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histone = histoneBuilder.build();

    }
    

    @Test
    public void testImports() throws IOException, HistoneException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource("optimizer/import.tpl");
        String baseUri = "file:/" + new File(url.toURI()).getParent() + "/";
        FileInputStream fis = new FileInputStream(url.getPath());
        String input = IOUtils.toString(fis);

        ArrayNode normalAst = histone.parseTemplateToAST(new StringReader(input));
        String normalResult = histone.evaluateAST(baseUri, normalAst, NullNode.instance);

        ArrayNode importsAst = histone.optimizeAST(baseUri, normalAst);
        String importResult = histone.evaluateAST(importsAst);

        assertEquals(normalResult, importResult);
    }
    

    @Test
    public void test() throws HistoneException, IOException {

      /*
        URL url = getClass().getClassLoader().getResource("optimizer/import.tpl");
        FileInputStream fis = new FileInputStream(url.getPath());
        String input = IOUtils.toString(fis);
        input="{{var extName = requestParams.ext}}";

        System.out.println("Input");
        System.out.println(input);

        ArrayNode normalAst = histone.parseTemplateToAST(new StringReader(input));
        System.out.println("\n\nNormal AST");
        System.out.println(normalAst.toString());

        String normalResult = histone.evaluateAST(normalAst);
        System.out.println("\nNormal Result");

        System.out.println(normalResult);
        ArrayNode importsAst = histone.optimizeAST(normalAst);
        System.out.println("\n\nImports AST");
        System.out.println(importsAst.toString());

//        String optimizedResult = histone.evaluateAST(importsAst);
//        System.out.println("\nNormal Result");
//        System.out.println(optimizedResult);


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

    */
    }
}
