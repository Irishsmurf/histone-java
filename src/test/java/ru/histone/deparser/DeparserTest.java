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
            FileWriter fw = new FileWriter("C:\\Temp\\result.txt");
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
            FileWriter fw = new FileWriter("C:\\Temp\\result.txt");
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
            FileWriter fw = new FileWriter("C:\\Temp\\result.txt");
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
