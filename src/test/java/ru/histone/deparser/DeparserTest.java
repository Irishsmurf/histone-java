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
package ru.histone.deparser;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.utils.IOUtils;

import java.io.*;

import static org.junit.Assert.assertTrue;

@Ignore("To be refactored later")
public class DeparserTest {
    private Histone histone;
    private IDeparser deparser;

    @Before
    public void init() throws HistoneException {
        histone = new HistoneBuilder().build();
        deparser = new Deparser();
    }

    @Test
    public void test1() throws HistoneException {
        ArrayNode ast = histone.parseTemplateToAST(input("form.tpl"));
        String source = deparser.deparse(ast);

        try {
            FileWriter fw = new FileWriter("/Users/ksazonov/Temp/result.txt");
            fw.write(source);
            fw.flush();
            fw.close();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    public void test2() throws HistoneException {
        ArrayNode ast = histone.parseTemplateToAST(input("main.tpl"));
        String source = deparser.deparse(ast);

        try {
            FileWriter fw = new FileWriter("/Users/ksazonov/Temp/result.txt");
            fw.write(source);
            fw.flush();
            fw.close();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    public void test3() throws HistoneException {
        ArrayNode ast = histone.parseTemplateToAST(input("search.tpl"));
        String source = deparser.deparse(ast);

        try {
            FileWriter fw = new FileWriter("/Users/ksazonov/Temp/result.txt");
            fw.write(source);
            fw.flush();
            fw.close();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    public void test4() throws HistoneException {
        ArrayNode ast = histone.parseTemplateToAST(input("login.tpl"));
        String source = deparser.deparse(ast);

        try {
            FileWriter fw = new FileWriter("/Users/ksazonov/Temp/result.txt");
            fw.write(source);
            fw.flush();
            fw.close();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private String input(String filename) {
        try {
            StringWriter sw = new StringWriter();
            InputStream is = getClass().getResourceAsStream(filename);
            IOUtils.copy(is, sw);
            return sw.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
