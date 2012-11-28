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

import java.math.BigDecimal;

/**
 * Class representing String type in Histone
 */
public class StringHistoneNode extends Node {

    private String value;

    protected StringHistoneNode(NodeFactory nodeFactory, String value) {
        super(nodeFactory);
        this.value = value;
    }

//    /**
//     * Create string type object with empty string value
//     *
//     * @return string type object
//     */
//    public static StringHistoneNode create() {
//        return create(StringUtils.EMPTY);
//    }
//
//    /**
//     * Create string type object with specified value
//     *
//     * @param source value
//     * @return string type object
//     */
//    public static StringHistoneNode create(String source) {
//        if (source == null) {
//            return create();
//        }
//        return new StringHistoneNode(source);
//    }
//
//    /**
//     * Create string type object with value readed from specified Reader
//     *
//     * @param source object to read string value from
//     * @return string type object
//     * @throws IOException thrown is any error occurs
//     */
//    public static StringHistoneNode create(Reader source) throws IOException {
//        try {
//            return new StringHistoneNode(IOUtils.toString(source));
//        } finally {
//            IOUtils.closeQuietly(source);
//        }
//    }
//
//    /**
//     * Create string type object with value readed from specified Stream
//     *
//     * @param source object to read string value from
//     * @return string type object
//     * @throws IOException thrown is any error occurs
//     */
//    public static StringHistoneNode create(InputStream source) throws IOException {
//        try {
//            return new StringHistoneNode(IOUtils.toString(source));
//        } finally {
//            IOUtils.closeQuietly(source);
//        }
//    }

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
        Node result = getNodeFactory().UNDEFINED;

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
            result = getNodeFactory().string(String.valueOf(value.charAt(propIdx)));
//        } else if ("length".equals(name)) {
//            return NumberHistonegetNodeFactory().create(value.length());
        } else {
            result = super.getProp(name);
        }

        return result;
    }

    @Override
    public Node oper_add(Node right) {
        return getNodeFactory().string(value + right.getAsString().value);
    }

    private Node commonMulDivSubMod(Node right) {
        if (right.isUndefined()) {
            return getNodeFactory().UNDEFINED;
        } else if (right.isNull()) {
            return getNodeFactory().UNDEFINED;
        } else if (right.isBoolean()) {
            return getNodeFactory().UNDEFINED;
        } else if (right.isObject()) {
            return getNodeFactory().UNDEFINED;
        } else {
            throw new RuntimeException("Unknown right node type: " + right.getClass());
        }
    }

    @Override
    public Node oper_mul(Node right) {
        if (right.isNumber()) {
            if (this.getAsNumber().isUndefined()) {
                return getNodeFactory().UNDEFINED;
            } else {
                return this.getAsNumber().oper_mul(right);
            }
        } else if (right.isString()) {
            if (this.getAsNumber().isUndefined()) {
                return getNodeFactory().UNDEFINED;
            } else {
                if (right.getAsNumber().isUndefined()) {
                    return getNodeFactory().UNDEFINED;
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
                return getNodeFactory().UNDEFINED;
            }
            return this.getAsNumber().oper_div(right);
        }
        if (right.isString()) {
            if (this.getAsNumber().isUndefined() || right.getAsNumber().isUndefined()) {
                return getNodeFactory().UNDEFINED;
            }
            return this.getAsNumber().oper_div(right.getAsNumber());
        }
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_mod(Node right) {
        if (right.isNumber()) {
            if (this.getAsNumber().isUndefined()) {
                return getNodeFactory().UNDEFINED;
            } else {
                return this.getAsNumber().oper_mod(right);
            }
        } else if (right.isString()) {
            if (this.getAsNumber().isUndefined()) {
                return getNodeFactory().UNDEFINED;
            } else {
                if (right.getAsNumber().isUndefined()) {
                    return getNodeFactory().UNDEFINED;
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
            return getNodeFactory().UNDEFINED;
        } else {
            return this.getAsNumber().oper_negate();
        }
    }

    @Override
    public Node oper_sub(Node right) {
        if (right.isNumber()) {
            if (this.getAsNumber().isUndefined()) {
                return getNodeFactory().UNDEFINED;
            } else {
                return this.getAsNumber().oper_sub(right);
            }
        } else if (right.isString()) {
            if (this.getAsNumber().isUndefined()) {
                return getNodeFactory().UNDEFINED;
            } else {
                if (right.getAsNumber().isUndefined()) {
                    return getNodeFactory().UNDEFINED;
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
        return (getAsBoolean().getValue()) ? getNodeFactory().FALSE : getNodeFactory().TRUE;
    }

    @Override
    public Node oper_equal(Node right) {
        if (right.isString()) {
            return value.equals(right.getAsString().getValue()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isNumber()) {
            NumberHistoneNode leftNumber = this.getAsNumber();
            if (leftNumber.isNumber()) {
                return (leftNumber.getValue().equals(right.getAsNumber().getValue())) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            } else {
                return getNodeFactory().FALSE;
            }
        } else {
            return (this.getAsBoolean().equals(right.getAsBoolean())) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        }
    }

    @Override
    public Node oper_greaterThan(Node right) {
        if (right.isString()) {
            return (value.length() > right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isNumber()) {
            NumberHistoneNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_greaterThan(right);
            } else {
                return (value.length() > right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            }
        } else {
            return this.getAsBoolean().oper_greaterThan(right.getAsBoolean());
        }
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        if (right.isString()) {
            return (value.length() >= right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isNumber()) {
            NumberHistoneNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_greaterOrEqual(right);
            } else {
                return (value.length() >= right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            }
        } else {
            return this.getAsBoolean().oper_greaterOrEqual(right.getAsBoolean());
        }
    }

    @Override
    public Node oper_lessThan(Node right) {
        if (right.isString()) {
            return (value.length() < right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isNumber()) {
            NumberHistoneNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_lessThan(right);
            } else {
                return (value.length() < right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            }
        } else {
            return this.getAsBoolean().oper_lessThan(right.getAsBoolean());
        }
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        if (right.isString()) {
            return (value.length() <= right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isNumber()) {
            NumberHistoneNode leftNum = this.getAsNumber();
            if (leftNum.isNumber()) {
                return leftNum.oper_lessOrEqual(right);
            } else {
                return (value.length() <= right.getAsString().getValue().length()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            }
        } else {
            return this.getAsBoolean().oper_lessOrEqual(right.getAsBoolean());
        }
    }

    @Override
    public BooleanHistoneNode getAsBoolean() {
        return (getAsString().getValue().length() > 0) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
    }

    @Override
    public NumberHistoneNode getAsNumber() {
        NumberHistoneNode result;
        try {
            BigDecimal v = new BigDecimal(getAsString().getValue());
            result = getNodeFactory().number(v);
        } catch (NumberFormatException nfe) {
            result = getNodeFactory().UNDEFINED_NUMBER;
        }
        return result;
    }

    @Override
    public StringHistoneNode getAsString() {
        return getNodeFactory().string(value);
    }

    @Override
    public ObjectHistoneNode getAsObject() {
        throw new RuntimeException("Can't cast " + getClass() + " to object");
    }

    @Override
    public JsonNode getAsJsonNode() {
        return getNodeFactory().jsonString(value);
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
