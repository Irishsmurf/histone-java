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

import java.util.*;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ru.histone.utils.StringUtils;

/**
 * Class representing Object type in Histone
 */
public class ObjectNode extends Node {
//    private boolean globalObject = false;

    private int maxIdx = 0;

    private Map<Object, Node> elements = new LinkedHashMap<Object, Node>();

    protected ObjectNode() {
    }

    /**
     * Creates empty src
     *
     * @return new src
     */
    public static ObjectNode create() {
        return new ObjectNode();
    }

    /**
     * Create src type src and fill it with items from specified ObjectNode src
     *
     * @param src source src ot use for copying items from
     * @return created obejct
     */
    public static ObjectNode create(ObjectNode src) {
        ObjectNode node = src.isGlobalObject() ? new GlobalObjectNode() : new ObjectNode();
        for (Entry<Object, Node> entry : src.elements.entrySet()) {
            node.add(entry.getKey(), entry.getValue());
        }
//        node.elements = new LinkedHashMap<Object, Node>(src.elements);
        return node;
    }

    /**
     * Create array node object and fill it with nodes from specified arguments
     *
     * @param elements node elements to use for creating new array object
     * @return new array node object
     */
    public static ObjectNode create(Node... elements) {
        if (elements == null) {
            return create();
        }
        ObjectNode node = new ObjectNode();
        for (Node item : elements) {
            node.add(item);
        }
        return node;
    }

    /**
     * Create array node object and fill it with nodes from specified arguments
     *
     * @param elements node elements to use for creating new array object
     * @return new array node object
     */
    public static ObjectNode create(String... elements) {
        if (elements == null) {
            return create();
        }
        ObjectNode node = new ObjectNode();
        for (String item : elements) {
            node.add(StringNode.create(item));
        }
        return node;
    }

    public static Node create(Collection<Node> elements) {
        if (elements == null) {
            return create();
        }
        ObjectNode node = new ObjectNode();
        for (Node item : elements) {
            node.add(item);
        }
        return node;
    }

    public static Node create(JsonArray src) {
        if (src == null) {
            return create();
        }
        ObjectNode node = new ObjectNode();
        for (JsonElement entry : src) {
            node.add(jsonToNode(entry));
        }
        return node;
    }


    public static ObjectNode create(List<Node> src) {
        if (src == null) {
            return create();
        }
        ObjectNode node = new ObjectNode();
        for (Node entry : src) {
            node.add(entry);
        }
        return node;
    }

    /**
     * Create src type src and fill it with items from specified JsonObject src
     *
     * @param src source src ot use for copying items from
     * @return created obejct
     */
    public static ObjectNode create(JsonObject src) {
        if (src == null) {
            return create();
        }
        ObjectNode node = new ObjectNode();
        for (Entry<String, JsonElement> entry : src.entrySet()) {
            node.add(entry.getKey(), jsonToNode(entry.getValue()));
        }
        return node;
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
//    public void put(JsonElement key, Node value) {
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
            ObjectNode result = ObjectNode.create();
            ObjectNode rightObj = right.getAsObject();

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
            return Node.UNDEFINED;
        } else {
            return this.getAsString().oper_add(right.getAsString());
        }
    }

    private Node commonMulDivSubMod(Node right) {
        return Node.UNDEFINED;
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
        return Node.UNDEFINED;
    }

    @Override
    public Node oper_sub(Node right) {
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_not() {
        return Node.FALSE;
    }

    @Override
    public Node oper_equal(Node right) {
        return (right.getAsBoolean().getValue()) ? Node.TRUE : Node.FALSE;
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
    public BooleanNode getAsBoolean() {
        return Node.TRUE;
    }

    @Override
    public NumberNode getAsNumber() {
        return Node.UNDEFINED_NUMBER;
    }


    @Override
    public StringNode getAsString() {
        StringBuilder sb = new StringBuilder();
        for (Node value : this.elements.values()) {
            if (!value.isUndefined()) {
                sb.append(value.getAsString().getValue()).append(" ");
            }
        }
        String sbStr = (sb.length() > 0) ? sb.substring(0, sb.length() - 1) : "";
        return StringNode.create(sbStr);
    }

    @Override
    public ObjectNode getAsObject() {
        return this;
    }

    @Override
    public JsonElement getAsJsonElement() {
        //check if we have at least one element with string key
        if (maxIdx != elements.size()) {
            JsonObject json = new JsonObject();

            for (Entry<Object, Node> entry : elements.entrySet()) {
                if (!entry.getValue().isUndefined()) {
                    json.add(entry.getKey().toString(), entry.getValue().getAsJsonElement());
                } else {
                    json.add(entry.getKey().toString(), new JsonObject());
                }
            }
            return json;
        } else {
            JsonArray json = new JsonArray();

            for (Entry<Object, Node> entry : elements.entrySet()) {
                json.add(entry.getValue().getAsJsonElement());
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
