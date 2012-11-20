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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class representing Object type in Histone
 */
public class ObjectHistoneNode extends Node {
//    private boolean globalObject = false;

    private int maxIdx = 0;

    private Map<Object, Node> elements = new LinkedHashMap<Object, Node>();

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

    @Override
    public Node getProp(String name) {
        //If we have string key, first of all try to parse it to number
        //if parsing was succesfull, then treat this key as number
        try {
            Integer keyInt = Integer.parseInt((String) name);
            return elements.get(keyInt);
        } catch (NumberFormatException e) {
            //do nothing
        }

        return elements.get(name);
    }

    @Override
    public boolean hasProp(String name) {
        //If we have string key, first of all try to parse it to number
        //if parsing was succesfull, then treat this key as number
        try {
            Integer keyInt = Integer.parseInt((String) name);
            return elements.containsKey(keyInt);
        } catch (NumberFormatException e) {
            //do nothing
        }

        return elements.containsKey(name);
    }

//    /**
//     * Adds new key:value pair to this object
//     *
//     * @param key   key key
//     * @param value value
//     */
//    public void put(JsonNode key, Node value) {
//        if (key.isJsonNull()) {
//            key = new JsonPrimitive("" + (elements.size()));
//        }
////        this.put();
//    }

    public void add(Object key, Node value) {
        //If we have string key, first of all try to parse it to number
        //if parsing was succesfull, then treat this key as number
        if (key instanceof String) {
            try {
                Integer keyInt = Integer.parseInt((String) key);
                key = keyInt;
            } catch (NumberFormatException e) {
                //do nothing
            }
        }

        if (key instanceof String) {
            elements.put(key, value);
        } else if (key instanceof Number) {
            if ((Integer) key > maxIdx) {
                maxIdx = (Integer) key;
            }
            elements.put(maxIdx++, value);
        } else {
            throw new RuntimeException(String.format("Wrong key type: (%s)%s", key.getClass(), key.toString()));
            //TODO: log wrong key type usage
        }
    }

    public void add(Node value) {
        this.add(0, value);
    }


    /**
     * Removes key:value using specified key key
     *
     * @param name
     */
    public void remove(String name) {
        elements.remove(name);
    }

    /**
     * Return object elements presented as Set(Entry(key,value))
     *
     * @return set with object entries
     */
//    public Set<Entry<Object, Node>> entries() {
//        return elements.entrySet();
//    }


    /**
     * Return object elements presented as Map(key,value)
     *
     * @return map of object elements
     */
    public Map<Object, Node> getElements() {
        return elements;
    }

    /**
     * Return number of entries current object stores (number of pairs key:value)
     *
     * @return number of object elements
     */
    public int size() {
        return elements.size();
    }

    @Override
    public Node oper_add(Node right) {
        if (right.isObject()) {
            ObjectHistoneNode result = getNodeFactory().object();
            ObjectHistoneNode rightObj = right.getAsObject();

            //put all elements from left object
            for (Object key : this.elements.keySet()) {
                result.add(key, this.elements.get(key));
            }

            //put al elements from right object
            for (Object key : rightObj.elements.keySet()) {
                result.add(key, rightObj.elements.get(key));
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
        //check if we have at least one element with string key
        if (maxIdx != elements.size()) {
            ObjectNode json = getNodeFactory().jsonObject();

            for (Entry<Object, Node> entry : elements.entrySet()) {
                if (!entry.getValue().isUndefined()) {
                    json.put(entry.getKey().toString(), entry.getValue().getAsJsonNode());
                } else {
                    json.put(entry.getKey().toString(), getNodeFactory().jsonObject());
                }
            }
            return json;
        } else {
            ArrayNode json = getNodeFactory().jsonArray();

            for (Entry<Object, Node> entry : elements.entrySet()) {
                json.add(entry.getValue().getAsJsonNode());
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

}
