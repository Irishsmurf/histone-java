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
package ru.histone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Test correct work of Histone public methods
 */
public class HistoneInputOutputMethodsTest {
    private Histone histone;
    private static final String INPUT_STRING = "a {{true}} b {{1+2}} c {{'русский'}} d";
    private static final String CONTEXT_STRING = "";
    private static final String EXPECTED_AST = "[\"a \",[16],\" b \",[9,[101,1],[101,2]],\" c \",[103,\"русский\"],\" d\"]";
    private static final String EXPECTED_OUT = "a true b 3 c русский d";
    private static ArrayNode INPUT_AST;

    private StringReader INPUT_READER;
    private InputStream INPUT_STREAM;
    private JsonNode CONTEXT_JSON;

    @Before
    public void before() throws HistoneException, IOException {
        ObjectMapper jackson = new ObjectMapper();


        HistoneBuilder builder = new HistoneBuilder();
        histone = builder.build();

        INPUT_READER = new StringReader(INPUT_STRING);
        CONTEXT_JSON = jackson.readTree(CONTEXT_STRING);
        INPUT_STREAM = new ByteArrayInputStream(INPUT_STRING.getBytes("UTF-8"));
        INPUT_AST = (ArrayNode) jackson.readTree(EXPECTED_AST);
    }

//    @Test
//    public void testParseToStringString() throws HistoneException {
//        String result = histone.parseToString(INPUT_STRING);
//        assertEquals(EXPECTED_AST, result);
//    }
//
//    @Test
//    public void testParseToStringReader() throws HistoneException {
//        String result = histone.parseToString(INPUT_READER);
//        assertEquals(EXPECTED_AST, result);
//    }
//
//    @Test
//    public void testParseToStringInputStream() throws HistoneException, UnsupportedEncodingException {
//        String result = histone.parseToString(INPUT_STREAM);
//        assertEquals(EXPECTED_AST, result);
//    }
//
//    @Test
//    public void testParseToJsonString() throws HistoneException {
//        ArrayNode result = histone.parseToJson(INPUT_STRING);
//        assertEquals(EXPECTED_AST, result.toString());
//    }
//
//    @Test
//    public void testParseToJsonReader() throws HistoneException {
//        ArrayNode result = histone.parseToJson(INPUT_READER);
//        assertEquals(EXPECTED_AST, result.toString());
//    }
//
//    @Test
//    public void testParseToJsonInputStream() throws HistoneException, UnsupportedEncodingException {
//        ArrayNode result = histone.parseToJson(INPUT_STREAM);
//        assertEquals(EXPECTED_AST, result.toString());
//    }
//
//    @Test
//    public void testProcessString() throws HistoneException {
//        String result = histone.processToString(INPUT_STRING);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessReader() throws HistoneException {
//        String result = histone.processToString(INPUT_READER);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessInputStream() throws HistoneException {
//        String result = histone.process(INPUT_STREAM);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessStringString() throws HistoneException {
//        String result = histone.process(INPUT_STRING, CONTEXT_STRING);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessStringJsonNode() throws HistoneException {
//        String result = histone.process(INPUT_STRING, CONTEXT_JSON);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessReaderString() throws HistoneException {
//        String result = histone.process(INPUT_READER, CONTEXT_STRING);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessReaderJsonNode() throws HistoneException {
//        String result = histone.process(INPUT_READER, CONTEXT_JSON);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessInputStreamString() throws HistoneException, UnsupportedEncodingException {
//        String result = histone.process(INPUT_STREAM, CONTEXT_STRING);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessInputStreamJsonNode() throws HistoneException, UnsupportedEncodingException {
//        String result = histone.process(INPUT_STREAM, CONTEXT_JSON);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessStringStringWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.process(INPUT_STRING, CONTEXT_STRING, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }
//
//    @Test
//    public void testProcessStringJsonNodeWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.process(INPUT_STRING, CONTEXT_JSON, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }
//
//    @Test
//    public void testProcessReaderStringWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.process(INPUT_READER, CONTEXT_STRING, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }
//
//    @Test
//    public void testProcessReaderJsonNodeWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.process(INPUT_READER, CONTEXT_JSON, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }
//
//    @Test
//    public void testProcessInputStreamStringWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.process(INPUT_STREAM, CONTEXT_STRING, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }
//
//    @Test
//    public void testProcessInputStreamJsonNodeWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.process(INPUT_STREAM, CONTEXT_JSON, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }
//
//    @Test
//    public void testProcessStringStringOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.process(INPUT_STRING, CONTEXT_STRING, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessStringJsonNodeOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.process(INPUT_STRING, CONTEXT_JSON, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessReaderStringOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.process(INPUT_READER, CONTEXT_STRING, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessReaderJsonNodeOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.process(INPUT_READER, CONTEXT_JSON, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessInputStreamStringOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.process(INPUT_STREAM, CONTEXT_STRING, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessInputStreamJsonNodeOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.process(INPUT_STREAM, CONTEXT_JSON, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessASTString() throws HistoneException {
//        String result = histone.processAST(INPUT_AST);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessASTStringString() throws HistoneException {
//        String result = histone.processAST(INPUT_AST, CONTEXT_STRING);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//    @Test
//    public void testProcessASTStringJsonNode() throws HistoneException {
//        String result = histone.processAST(INPUT_AST, CONTEXT_JSON);
//        assertEquals(EXPECTED_OUT, result);
//    }
//
//
//    @Test
//    public void testProcessASTStringStringOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.processAST(INPUT_AST, CONTEXT_STRING, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessASTStringJsonNodeOutputStream() throws HistoneException, UnsupportedEncodingException {
//        ByteArrayOutputStream result = new ByteArrayOutputStream();
//        OutputStream output = result;
//        histone.processAST(INPUT_AST, CONTEXT_JSON, output);
//        assertEquals(EXPECTED_OUT, result.toString("UTF-8"));
//    }
//
//    @Test
//    public void testProcessASTStringStringWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.processAST(INPUT_AST, CONTEXT_STRING, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }
//
//    @Test
//    public void testProcessASTStringJsonNodeWriter() throws HistoneException, UnsupportedEncodingException {
//        StringWriter result = new StringWriter();
//        histone.processAST(INPUT_AST, CONTEXT_JSON, result);
//        assertEquals(EXPECTED_OUT, result.getBuffer().toString());
//    }

}
