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
package ru.histone.evaluator.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class representing Object type in Histone
 */
public class ObjectHistoneNode extends Node {
    private final Map<StringOrInteger, Node> elements = new LinkedHashMap<StringOrInteger, Node>() {
        @Override
        public Node put(StringOrInteger key, Node value) {
            if (key.isString()) stringKeysCount++;
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends StringOrInteger, ? extends Node> m) {
            for (StringOrInteger key : m.keySet())
                if (key.isString())
                    stringKeysCount++;

            super.putAll(m);
        }

        @Override
        public Node remove(Object key) {
            Node deleted = super.remove(key);
            if (deleted != null) {
                StringOrInteger soi = new StringOrInteger(key);
                if (soi.isString()) stringKeysCount--;
            }
            return deleted;
        }

        @Override
        public void clear() {
            stringKeysCount = 0;
        }
    };

    private int stringKeysCount = 0;



    /**
     * Stores current state of ObjectHistoneNode: array behaviour or object behaviour.
     */
    private boolean isArray() {
        if (stringKeysCount > 0) return false;

        int i1 = 0;
        for (StringOrInteger key : elements.keySet()) {
            int i2 = key.asInteger();
            if (i1 != i2) return false;

            i1++;
        }
        return true;
    }

    protected ObjectHistoneNode(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    /**
     * Check if this is special object named GlobalObject
     *
     * @return always false for this class
     * @see GlobalObjectNode
     */
    public boolean isGlobalObject() {
        return false;
    }

    // Refactored
    @Override
    public Node getProp(String name) {
        return elements.get(new StringOrInteger(name));
    }

    // Refactored
    @Override
    public boolean hasProp(String name) {
        return elements.containsKey(new StringOrInteger(name));
    }

    // Refactored
    public void add(Object key, Node value) {
        StringOrInteger soi = new StringOrInteger(key);
        elements.put(soi, value);
    }

    // Refactored
    public void set(String key, Node value) {
        StringOrInteger soi = new StringOrInteger(key);

        if (!value.isUndefined()) {
            elements.put(soi, value);
        } else {
            elements.remove(soi);
        }
    }

    // Refactored
    public void set(Integer key, Node value) {
        StringOrInteger soi = new StringOrInteger(key);

        if (!value.isUndefined()) {
            elements.put(soi, value);
        } else {
            elements.remove(soi);
        }
    }

    // Refactored
    public void add(Node value) {
        int size = this.elements.size() - stringKeysCount;
        this.add(size, value);
    }

    /**
     * Removes key:value using specified key key
     *
     * @param name
     */
    // Refactored
    public void remove(String name) {
        StringOrInteger key = new StringOrInteger(name);
        elements.remove(key);
    }

    /**
     * Return object elements presented as Map(key,value)
     *
     * @return map of object elements
     */
    // Refactored
    public Map<Object, Node> getElements() {
        // UNSAFE
        Map<Object, Node> result = new LinkedHashMap<Object, Node>();
        for (StringOrInteger key : elements.keySet()) {
            Node value = elements.get(key);
            if (key.isInteger()) result.put(key.asInteger(), value);
            if (key.isString()) result.put(key.asString(), value);
        }

        return result;
    }

    /**
     * Return number of entries current object stores (number of pairs key:value)
     *
     * @return number of object elements
     */
    // Refactored
    public int size() {
        return elements.size();
    }

    @Override
    public Node oper_add(Node right) {
        if (right.isObject()) {
            ObjectHistoneNode result = getNodeFactory().object();
            ObjectHistoneNode rightObj = right.getAsObject();

            for (Entry<StringOrInteger, Node> entry : this.elements.entrySet()) {
                if (entry.getKey().isString()) {
                    result.add(entry.getKey().asString(), entry.getValue());
                }
                if (entry.getKey().isInteger()) {
                    result.add(entry.getValue());
                }
            }

            for (Entry<StringOrInteger, Node> entry : rightObj.elements.entrySet()) {
                if (entry.getKey().isString()) {
                    result.add(entry.getKey().asString(), entry.getValue());
                }
                if (entry.getKey().isInteger()) {
                    result.add(entry.getValue());
                }
            }

            return result;
        } else if (right.isNumber()) {
            return getNodeFactory().UNDEFINED;
        } else {
            return this.getAsString().oper_add(right.getAsString());
        }
    }

    private Node commonMulDivSubMod(Node right) {
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_mul(Node right) {
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_div(Node right) {
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_mod(Node right) {
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_negate() {
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_sub(Node right) {
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_not() {
        return getNodeFactory().FALSE;
    }

    @Override
    public Node oper_equal(Node right) {
        return (right.getAsBoolean().getValue()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
    }

    @Override
    public Node oper_greaterThan(Node right) {
        return this.getAsBoolean().oper_greaterThan(right.getAsBoolean());
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        return this.getAsBoolean().oper_greaterOrEqual(right.getAsBoolean());
    }

    @Override
    public Node oper_lessThan(Node right) {
        return this.getAsBoolean().oper_lessThan(right.getAsBoolean());
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        return this.getAsBoolean().oper_lessOrEqual(right.getAsBoolean());
    }

    @Override
    public BooleanHistoneNode getAsBoolean() {
        return getNodeFactory().TRUE;
    }

    @Override
    public NumberHistoneNode getAsNumber() {
        return getNodeFactory().UNDEFINED_NUMBER;
    }


    @Override
    public StringHistoneNode getAsString() {
        StringBuilder sb = new StringBuilder();
        for (Node value : this.elements.values()) {
            if (!value.isUndefined()) {
                sb.append(value.getAsString().getValue()).append(" ");
            }
        }
        String sbStr = (sb.length() > 0) ? sb.substring(0, sb.length() - 1) : "";
        return getNodeFactory().string(sbStr);
    }

    @Override
    public ObjectHistoneNode getAsObject() {
        return this;
    }

    @Override
    public JsonNode getAsJsonNode() {
        if (size() == 0) return getNodeFactory().jsonArray();

        if (isArray()) {
            // Return as array
            ArrayNode json = getNodeFactory().jsonArray();

            for (Entry<StringOrInteger, Node> entry : elements.entrySet()) {
                json.add(entry.getValue().getAsJsonNode());
            }

            return json;
        } else {
            // Return as object
            ObjectNode json = getNodeFactory().jsonObject();

            for (Entry<StringOrInteger, Node> entry : elements.entrySet()) {
                if (!entry.getValue().isUndefined()) {
                    json.put(entry.getKey().toString(), entry.getValue().getAsJsonNode());
                } else {
                    json.put(entry.getKey().toString(), getNodeFactory().jsonObject());
                }
            }

            return json;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("object(");
        sb.append(elements.size());
        sb.append(")");
        return sb.toString();
    }

    /**
     * Represents a key for ObjectHistoneNode, that can be either string or integer.
     */
    public class StringOrInteger {
        private String sValue;
        private Integer iValue;

        public StringOrInteger(String s) {
            try {
                iValue = new Integer(s);
            } catch (NumberFormatException e) {
            }

            sValue = s;
        }

        public StringOrInteger(Integer i) {
            sValue = i.toString();
            iValue = i;
        }

        public StringOrInteger(Object o) {
            if (o instanceof Integer) {
                sValue = o.toString();
                iValue = (Integer) o;
            } else {
                sValue = o.toString();
                iValue = null;
            }
        }

        public boolean isString() {
            return iValue == null;
        }

        public boolean isInteger() {
            return iValue != null;
        }

        public String asString() {
            return sValue;
        }

        public Integer asInteger() {
            return iValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringOrInteger that = (StringOrInteger) o;

            if (sValue != null ? !sValue.equals(that.sValue) : that.sValue != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return sValue != null ? sValue.hashCode() : 0;
        }

        @Override
        public String toString() {
            return sValue;
        }
    }

}
