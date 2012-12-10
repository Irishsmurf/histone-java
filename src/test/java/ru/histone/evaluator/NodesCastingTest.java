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
package ru.histone.evaluator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.StringHistoneNode;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodesCastingTest {
    private ObjectMapper jackson;
    private NodeFactory nodeFactory;

    @Before
    public void before() {
        jackson = new ObjectMapper();
        nodeFactory = new NodeFactory(jackson);
    }

    @Test
    public void testNull() {
        Node node;

        node = nodeFactory.NULL;

        assertFalse(node.isUndefined());
        assertTrue(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("null", node.getAsString().getValue());
        assertEquals(new BigDecimal("0"), node.getAsNumber().getValue());
        assertEquals(Boolean.FALSE, node.getAsBoolean().getValue());
    }

    @Test
    public void testBoolean() {
        Node node;

        node = nodeFactory.TRUE;

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertTrue(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("true", node.getAsString().getValue());
        assertEquals(new BigDecimal("1"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());

        node = nodeFactory.FALSE;

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertTrue(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("false", node.getAsString().getValue());
        assertEquals(new BigDecimal("0"), node.getAsNumber().getValue());
        assertEquals(Boolean.FALSE, node.getAsBoolean().getValue());
    }

    @Test
    public void testUndefined() {
        Node node;

        node = nodeFactory.UNDEFINED;

        assertTrue(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("", node.getAsString().getValue());
        assertTrue(node.getAsNumber().isUndefined());
        assertEquals(Boolean.FALSE, node.getAsBoolean().getValue());
    }

    @Test
    public void testString() {
        Node node;

        node = nodeFactory.string("");

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertTrue(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("", node.getAsString().getValue());
        assertTrue(node.getAsNumber().isUndefined());
        assertEquals(Boolean.FALSE, node.getAsBoolean().getValue());

        node = nodeFactory.string("10");

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertTrue(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("10", node.getAsString().getValue());
        assertEquals(new BigDecimal("10"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());

        node = nodeFactory.string("10.123");

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertTrue(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("10.123", node.getAsString().getValue());
        assertEquals(new BigDecimal("10.123"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());

        node = nodeFactory.string("1.23E+5");

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertTrue(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("1.23E+5", node.getAsString().getValue());
        assertEquals(new BigDecimal("1.23E+5"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());

        node = nodeFactory.string("zz");

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertTrue(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("zz", node.getAsString().getValue());
        assertTrue(node.getAsNumber().isUndefined());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());
    }

    @Test
    public void testNumber() {
        Node node;

        node = nodeFactory.number(new BigDecimal("0"));

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertTrue(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("0", node.getAsString().getValue());
        assertEquals(new BigDecimal("0"), node.getAsNumber().getValue());
        assertEquals(Boolean.FALSE, node.getAsBoolean().getValue());

        node = nodeFactory.number(new BigDecimal("1"));

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertTrue(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("1", node.getAsString().getValue());
        assertEquals(new BigDecimal("1"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());

        node = nodeFactory.number(new BigDecimal("-1"));

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertTrue(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("-1", node.getAsString().getValue());
        assertEquals(new BigDecimal("-1"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());

        node = nodeFactory.number(new BigDecimal("1.12"));

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertTrue(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("1.12", node.getAsString().getValue());
        assertEquals(new BigDecimal("1.12"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());

        node = nodeFactory.number(new BigDecimal("1.12E-6"));

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertTrue(node.isNumber());
        assertFalse(node.isObject());
        assertEquals("0.00000112", node.getAsString().getValue());
        assertEquals(new BigDecimal("1.12E-6"), node.getAsNumber().getValue());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());
    }

    @Test
    public void testObject() {
        Node node;

        node = nodeFactory.object();

        assertFalse(node.isUndefined());
        assertFalse(node.isNull());
        assertFalse(node.isString());
        assertFalse(node.isBoolean());
        assertFalse(node.isNumber());
        assertTrue(node.isObject());
        assertEquals("", node.getAsString().getValue());
        assertTrue(node.getAsNumber().isUndefined());
        assertEquals(Boolean.TRUE, node.getAsBoolean().getValue());
    }

    @Test
    public void test111() {
        StringHistoneNode node = nodeFactory.string();

        assertTrue(Node.class.isAssignableFrom(node.getClass()));

        assertTrue(Node.class.isAssignableFrom(node.getClass().getSuperclass()));
    }

}
