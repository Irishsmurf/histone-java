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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ru.histone.utils.IOUtils;
import ru.histone.utils.StringUtils;

/**
 * Class representing String type in Histone
 */
public class StringNode extends Node {

    private String value;

    private StringNode(String value) {
        this.value = value;
    }

    /**
     * Create string type object with empty string value
     *
     * @return string type object
     */
    public static StringNode create() {
        return create(StringUtils.EMPTY);
    }

    /**
     * Create string type object with specified value
     *
     * @param source value
     * @return string type object
     */
    public static StringNode create(String source) {
        if (source == null) {
            return create();
        }
        return new StringNode(source);
    }

    /**
     * Create string type object with value readed from specified Reader
     *
     * @param source object to read string value from
     * @return string type object
     * @throws IOException thrown is any error occurs
     */
    public static StringNode create(Reader source) throws IOException {
        try {
            return new StringNode(IOUtils.toString(source));
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    /**
     * Create string type object with value readed from specified Stream
     *
     * @param source object to read string value from
     * @return string type object
     * @throws IOException thrown is any error occurs
     */
    public static StringNode create(InputStream source) throws IOException {
        try {
            return new StringNode(IOUtils.toString(source));
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    /**
     * Return string type object value
     * @return string value
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean hasProp(String name) {

        int propIdx = -1;
        try {
            propIdx = Integer.parseInt(name);
            if (propIdx < 0) {
                propIdx = value.length() + propIdx;
            }
        } catch (NumberFormatException e) {
            // do nothing, this prop is not a number
        }

//        String props = "length";
        if ((propIdx >= 0) && (propIdx < value.length())) {
            return true;
//        } else if (props.contains(name)) {
//            return true;
        } else {
            return super.hasProp(name);
        }
    }

    @Override
    public Node getProp(String name) {
        Node result = Node.UNDEFINED;

        int propIdx = -1;
        try {
            propIdx = Integer.parseInt(name);
            if (propIdx < 0) {
                propIdx = value.length() + propIdx;
            }
        } catch (NumberFormatException e) {
            // do nothing, this prop is not a number
        }

        if ((propIdx >= 0) && (propIdx < value.length())) {
            result = StringNode.create(String.valueOf(value.charAt(propIdx)));
//        } else if ("length".equals(name)) {
//            return NumberNode.create(value.length());
        } else {
            result = super.getProp(name);
        }

        return result;
    }

    @Override
    public Node oper_add(Node right) {
        return StringNode.create(value + right.getAsString().value);
    }

    private Node commonMulDivSubMod(Node right) {
        if (right.isUndefined()) {
            return Node.UNDEFINED;
        } else if (right.isNull()) {
            return Node.UNDEFINED;
        } else if (right.isBoolean()) {
            return Node.UNDEFINED;
        } else if (right.isObject()) {
            return Node.UNDEFINED;
        } else {
            throw new RuntimeException("Unknown right node type: " + right.getClass());
        }
    }

    @Override
    public Node oper_mul(Node right) {
        if (right.isNumber()) {
            if (this.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            } else {
                return this.getAsNumber().oper_mul(right);
            }
        } else if (right.isString()) {
            if (this.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            } else {
                if (right.getAsNumber().isUndefined()) {
                    return Node.UNDEFINED;
                } else {
                    return this.getAsNumber().oper_mul(right.getAsNumber());
                }
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_div(Node right) {
        if (right.isNumber()) {
            if (this.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            }
            return this.getAsNumber().oper_div(right);
        }
        if (right.isString()) {
            if (this.getAsNumber().isUndefined() || right.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            }
            return this.getAsNumber().oper_div(right.getAsNumber());
        }
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_mod(Node right) {
        if (right.isNumber()) {
            if (this.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            } else {
                return this.getAsNumber().oper_mod(right);
            }
        } else if (right.isString()) {
            if (this.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            } else {
                if (right.getAsNumber().isUndefined()) {
                    return Node.UNDEFINED;
                } else {
                    return this.getAsNumber().oper_mod(right.getAsNumber());
                }
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_negate() {
        if (this.getAsNumber().isUndefined()) {
            return Node.UNDEFINED;
        } else {
            return this.getAsNumber().oper_negate();
        }
    }

    @Override
    public Node oper_sub(Node right) {
        if (right.isNumber()) {
            if (this.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            } else {
                return this.getAsNumber().oper_sub(right);
            }
        } else if (right.isString()) {
            if (this.getAsNumber().isUndefined()) {
                return Node.UNDEFINED;
            } else {
                if (right.getAsNumber().isUndefined()) {
                    return Node.UNDEFINED;
                } else {
                    return this.getAsNumber().oper_sub(right.getAsNumber());
                }
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_not() {
        return (getAsBoolean().getValue()) ? Node.FALSE : Node.TRUE;
    }

    @Override
    public Node oper_equal(Node right) {
        if (right.isString()) {
            return value.equals(right.getAsString().getValue()) ? Node.TRUE : Node.FALSE;
        } else if (right.isNumber()) {
            NumberNode leftNumber = this.getAsNumber();
            if (leftNumber.isNumber()) {
                return (leftNumber.getValue().equals(right.getAsNumber().getValue())) ? Node.TRUE : Node.FALSE;
            } else {
                return Node.FALSE;
            }
        } else {
            return (this.getAsBoolean().equals(right.getAsBoolean())) ? Node.TRUE : Node.FALSE;
        }
    }

    @Override
    public Node oper_greaterThan(Node right) {
        if (right.isString()) {
            return (value.length() > right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
        } else if (right.isNumber()) {
            NumberNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_greaterThan(right);
            } else {
                return (value.length() > right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
            }
        } else {
            return this.getAsBoolean().oper_greaterThan(right.getAsBoolean());
        }
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        if (right.isString()) {
            return (value.length() >= right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
        } else if (right.isNumber()) {
            NumberNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_greaterOrEqual(right);
            } else {
                return (value.length() >= right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
            }
        } else {
            return this.getAsBoolean().oper_greaterOrEqual(right.getAsBoolean());
        }
    }

    @Override
    public Node oper_lessThan(Node right) {
        if (right.isString()) {
            return (value.length() < right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
        } else if (right.isNumber()) {
            NumberNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_lessThan(right);
            } else {
                return (value.length() < right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
            }
        } else {
            return this.getAsBoolean().oper_lessThan(right.getAsBoolean());
        }
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        if (right.isString()) {
            return (value.length() <= right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
        } else if (right.isNumber()) {
            NumberNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_lessOrEqual(right);
            } else {
                return (value.length() <= right.getAsString().getValue().length()) ? Node.TRUE : Node.FALSE;
            }
        } else {
            return this.getAsBoolean().oper_lessOrEqual(right.getAsBoolean());
        }
    }

    @Override
    public BooleanNode getAsBoolean() {
        return (getAsString().getValue().length() > 0) ? Node.TRUE : Node.FALSE;
    }

    @Override
    public NumberNode getAsNumber() {
        NumberNode result;
        try {
            BigDecimal v = new BigDecimal(getAsString().getValue());
            result = NumberNode.create(v);
        } catch (NumberFormatException nfe) {
            result = Node.UNDEFINED_NUMBER;
        }
        return result;
    }

    @Override
    public StringNode getAsString() {
        return StringNode.create(value);
    }

    @Override
    public ObjectNode getAsObject() {
        throw new RuntimeException("Can't cast " + getClass() + " to object");
    }

    @Override
    public JsonElement getAsJsonElement() {
        return new JsonPrimitive(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("string(");
        sb.append(value);
        sb.append(")");
        return sb.toString();
    }

}
