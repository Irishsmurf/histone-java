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
 * Represents Object type in Histone.
 *
 * For understanding of the implementaion you should keep in mind, that this class behaves as Javascript map class.
 * I want to pay your attension on two bullets:
 * 1. Keys have no type, e.g. key is always a string.
 * 2. [1, '0':2] === [2] if you understand this, then you know how it works.
 *
 * In this implementation its a hash map, in javascript - keyvaluepairs list based map.
 *
 * The main feature of this map, that you can add values without specifying keys, as if it was an array.
 * In this case, 'index' keys would be generated automatically for you (sequentally).
 * So, [1, 2, 3] means {'0' -> 1, '1' -> 2, '2' -> 3}. If you then delete '1' value ans add '4' value you'll have:
 * [2, 3, 4] === {'1' -> 2, '2' -> 3, '3' -> 4}. If then delete '4' value and add '5' value you'll have:
 * [2, 3, 5] === {'1' -> 2, '2' -> 3, '3' -> 5}. So there is a counter, that is incremented on element add and is decremented on value del.
 *
 * Call asJsonNode method will return an ARRAY, if
 * a) it has only index keys. We say that key is an index key, if it can be parsed as integer. Very simple.
 * b) there is index key with value 0.
 * c) every next index key equals previous plus one.
 * Otherwise it will be returned as object.
 *
 * Here are some consequences:
 * a) If you have at least one non-index key (e.g. keym which string value cannot be parsed as integer), you will have Object
 * b) If some object was (first) added without a key, and then removed, you'll never have an Array as output (because you've missed zero index key).
 * Except the situation, that you'll add zero based key manually: [1, 2, 3] -> remove(1) -> [2, 3] -> add(0, 4) -> [2, 3, 4] === {'1' -> 2, '2' -> 3, '0' -> 4}.
 *
 * @author sazonovkirill@gmail.com
 */
public class ObjectHistoneNode extends Node {
    /**
     * Map was extended to count all not-index keys, that are exist in the map.
     * That allows to skip linear searcg in asJsonNode call (see consequence (a)).
     *
     * We need LinkedHashMap here due to the fact, that some tests rely on keys sequence order.
     */
    private final Map<NodeKey, Node> elements = new LinkedHashMap<NodeKey, Node>() {
        @Override
        public Node put(NodeKey key, Node value) {
            if (key != null && key.isString()) stringNodeKeysCount++;
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends NodeKey, ? extends Node> m) {
            for (NodeKey key : m.keySet())
                if (key != null && key.isString())
                    stringNodeKeysCount++;

            super.putAll(m);
        }

        @Override
        public Node remove(Object key) {
            Node deleted = super.remove(key);
            if (deleted != null) {
                if (new NodeKey(key).isString()) stringNodeKeysCount--;
            }
            return deleted;
        }

        @Override
        public void clear() {
            stringNodeKeysCount = 0;
        }
    };

    /**
     * Count of not-index keys in map.
     */
    private int stringNodeKeysCount = 0;

    /**
     * Stores 'sequence' for index-key generation
     */
    private int indexCounter = 0;

    /**
     * Defines, if this object can be interpreted as an array.
     *
     * Call asJsonNode method will return an ARRAY, if
     * a) it has only index keys. We say that key is an index key, if it can be parsed as integer. Very simple.
     * b) there is index key with value 0.
     * c) every next index key equals previous plus one.
     * Otherwise it will be returned as object.
     *
     * It has linear complexity in worst case and O(1) in best.
     */
    private boolean isArray() {
        if (stringNodeKeysCount > 0) return false;

        for (int i = 0; i < elements.size(); i++) {
            if (!elements.containsKey(new NodeKey(i))) {
                return false;
            }
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

    /**
     * Return value by the key
     * @param name key
     * @return value by the key
     */
    @Override
    public Node getProp(String name) {
        if (name == null) return null;

        return elements.get(new NodeKey(name));
    }

    /**
     * Returns true, if this map contains particular key
     * @param name key
     * @return true, if this map contains particular key
     */
    @Override
    public boolean hasProp(String name) {
        if (name == null) return false;

        return elements.containsKey(new NodeKey(name));
    }

    //<editor-fold desc="add">

    /**
     * Add specified key value pair to the map.
     */
    public void add(String key, Node value) {
        if (key == null) throw new IllegalArgumentException();
        if (value == null) throw new IllegalArgumentException();

        elements.put(new NodeKey(key), value);
    }

    /**
     * Add specified key value pair to the map.
     */
    public void add(Integer key, Node value) {
        if (key == null) throw new IllegalArgumentException();
        if (value == null) throw new IllegalArgumentException();

        elements.put(new NodeKey(key), value);
    }

    /**
     * Add specified key value pair to the map.
     * @deprecated This function stays here for backward compability, but you should use add(String, ...) or add(Integer, ...) instead.
     */
    @Deprecated
    public void add(Object key, Node value) {
        if (key == null) throw new IllegalArgumentException();

        elements.put(new NodeKey(key), value);
    }


    public void add(Node value) {
        if (value == null) throw new IllegalArgumentException();

        int i = this.indexCounter;
        this.add(i, value);
        indexCounter++;
    }

    //</editor-fold>

    //<editor-fold desc="set">

    /**
     * Sets specified kay-value pair to the map
     */
    public void set(String key, Node value) {
        if (key == null) throw new IllegalArgumentException();
        if (value == null) throw new IllegalArgumentException();

        NodeKey nodeKey = new NodeKey(key);

        if (!value.isUndefined()) {
            elements.put(nodeKey, value);
        } else {
            elements.remove(nodeKey);
        }
    }

    /**
     * Sets specified kay-value pair to the map
     */
    public void set(Integer key, Node value) {
        if (key == null) throw new IllegalArgumentException();
        if (value == null) throw new IllegalArgumentException();

        NodeKey nodeKey = new NodeKey(key);

        if (!value.isUndefined()) {
            elements.put(nodeKey, value);
        } else {
            elements.remove(nodeKey);
        }
    }

    //</editor-fold>

    /**
     * Removes value using specified key.
     */
    public void remove(String key) {
        if (key == null) throw new IllegalArgumentException();

        elements.remove(new NodeKey(key));
    }

    /**
     * Return object elements presented as Map(key, value).
     */
    public Map<Object, Node> getElements() {

        Map<Object, Node> result = new LinkedHashMap<Object, Node>();
        for (NodeKey key : elements.keySet()) {
            Node value = elements.get(key);
            if (key.isString()) result.put(key.stringValue(), value);
            if (key.isInteger()) result.put(key.integerValue(), value);
        }

        return result;
    }

    /**
     * Return number of entries current object stores (number of pairs key:value).
     */
    public int size() {
        return elements.size();
    }

    //<editor-fold desc="Operations">
    /**
     * Concatenation operation.
     */
    @Override
    public Node oper_add(Node right) {
        if (right.isObject()) {
            ObjectHistoneNode result = getNodeFactory().object();
            ObjectHistoneNode rightObj = right.getAsObject();

            for (Entry<NodeKey, Node> entry : this.elements.entrySet()) {
                if (entry.getKey().isString()) {
                    result.add(entry.getKey().stringValue(), entry.getValue());
                }
                if (entry.getKey().isInteger()) {
                    result.add(entry.getValue());
                }
            }

            for (Entry<NodeKey, Node> entry : rightObj.elements.entrySet()) {
                if (entry.getKey().isString()) {
                    result.add(entry.getKey().stringValue(), entry.getValue());
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
    //</editor-fold>

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

            for (Entry<NodeKey, Node> entry : elements.entrySet()) {
                json.add(entry.getValue().getAsJsonNode());
            }

            return json;
        } else {
            // Return as object
            ObjectNode json = getNodeFactory().jsonObject();

            for (Entry<NodeKey, Node> entry : elements.entrySet()) {
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
     * Defines a key type for the internal map, wrapped by ObjectHistoneNode. It's important to remmember,
     * that key is _always_ a string. It also can be convertiable to a number (if it's an 'index' key), but it's a string.
     *
     * So, equals(), hashCode() and toString() implementations use only String value (field sValue).
     *
     * From some point of view, you may think, that we can not to store parsed values of index keys. And you are right.
     * But in this case we would have to parse ALL keys in ObjectHistoneNode#getAsJsonNode method. So I preffer to parse keys in add() function,
     * but not to do it in getAsJsonNode method.
     *
     * @author sazonovkirill@gmail.com
     */
    class NodeKey {
        /**
         * String value of this key. It's always not null, because key is always string.
         */
        private String sValue;

        /**
         * Integer value of this kiy. It CAN be null, if this key is not an index key.
         */
        private Integer iValue;

        /**
         * In this constructuor, we assign string value. And we try to parse it as integer to find if we can use it as index key.
         */
        public NodeKey(String s) {
            if (s == null) throw new IllegalArgumentException();

            sValue = s;

            try {
                iValue = new Integer(s);
            } catch (NumberFormatException e) {
                // notihng
            }
        }

        /**
         * In this constructor we definitely know, that i parameter is an index key. So we hust assign it ti sValue and iValue both.
         */
        public NodeKey(Integer i) {
            if (i == null) throw new IllegalArgumentException();

            sValue = i.toString();
            iValue = i;
        }

        /**
         * In this contructor, we dont know the object type. So it's a 'concatenation' of other two constructors.
         * Anyway we assign string value. And we try to assign number value also, if it's an Integer or can be parsed as integer.
         */
        public NodeKey(Object o) {
            if (o == null) throw new IllegalArgumentException();
            sValue = o.toString();

            if (o instanceof Integer) {
                iValue = (Integer) o;
            } else {
                try {
                    iValue = new Integer(o.toString());
                } catch (NumberFormatException e) {
                    // notihng
                }
            }
        }

        /**
         * Returns true, if this key CANNOT be used as index key.
         */
        public boolean isString() {
            return iValue == null;
        }

        /**
         * Returns true, if this index CAN be used as index key.
         */
        public boolean isInteger() {
            return iValue != null;
        }

        /**
         * Returns string value of this key. It's always available, because key is always a string.
         */
        public String stringValue() {
            return sValue;
        }

        /**
         * Returns integer value of this index. It's available only if this key is an index key.
         * Otherwise returns null.
         */
        public Integer integerValue() {
            return iValue;
        }

        /**
         * Implementation, based only on String value (sValue)
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NodeKey that = (NodeKey) o;

            if (sValue != null ? !sValue.equals(that.sValue) : that.sValue != null) return false;

            return true;
        }

        /**
         * Implementation, based only on String value (sValue)
         */
        @Override
        public int hashCode() {
            return sValue != null ? sValue.hashCode() : 0;
        }

        /**
         * Implementation, based only on String value (sValue)
         */
        @Override
        public String toString() {
            return sValue;
        }
    }

}
