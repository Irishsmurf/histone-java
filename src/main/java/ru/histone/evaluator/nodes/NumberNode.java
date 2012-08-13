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

import java.math.BigDecimal;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ru.histone.Histone;

/**
 * Class representing Null type in Histone
 */
public class NumberNode extends Node implements Comparable<NumberNode> {
    private BigDecimal value;

    protected NumberNode() {
    }

    private NumberNode(BigDecimal value) {
        this.value = value;
    }

    /**
     * Create number type object using specified value
     *
     * @param value value
     * @return number type object
     */
    public static NumberNode create(BigDecimal value) {
        return new NumberNode(value);
    }

    /**
     * Create number type object using specified value
     *
     * @param value value
     * @return number type object
     */
    public static NumberNode create(int value) {
        return new NumberNode(BigDecimal.valueOf(value));
    }

    /**
     * Create number type object using specified value
     *
     * @param value value
     * @return number type object
     * @throws NumberFormatException if {@code value} is infinite or NaN.
     */
    public static NumberNode create(double value) {
        return new NumberNode(BigDecimal.valueOf(value));
    }

    /**
     * Number type object value
     * @return object value
     */
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public Node oper_add(Node right) {
        if (right.isNumber()) {
            return NumberNode.create(this.value.add(right.getAsNumber().value));
        } else if (right.isString()) {
            return this.getAsString().oper_add(right);
        } else {
            Histone.runtime_log_warn("Number: operation '+' is undefined for '{}' and '{}'", this, right);
            return Node.UNDEFINED;
        }
    }

    private Node commonMulDivSubMod(Node right) {
        Histone.runtime_log_warn("Number: operation '*/-%' is undefined for '{}' and '{}'", this, right);
        return Node.UNDEFINED;

    }

    @Override
    public Node oper_mul(Node right) {
        if (right.isNumber()) {
            return NumberNode.create(value.multiply(right.getAsNumber().getValue()));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '*' can't cast right '{}' to number", right);
                return Node.UNDEFINED;
            } else {
                return NumberNode.create(value.multiply(right.getAsNumber().getValue()));
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_div(Node right) {
        if (right.isNumber()) {
            return NumberNode.create(value.divide(right.getAsNumber().getValue()));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '/' can't cast right '{}' to number", right);
                return Node.UNDEFINED;
            } else {
                return NumberNode.create(value.divide(right.getAsNumber().getValue()));
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_mod(Node right) {
        if (right.isNumber()) {
            return NumberNode.create(value.remainder(right.getAsNumber().getValue()));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '%' can't cast right '{}' to number", right);
                return Node.UNDEFINED;
            } else {
                return NumberNode.create(value.remainder(right.getAsNumber().getValue()));
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_negate() {
        return NumberNode.create(value.negate());
    }

    @Override
    public Node oper_sub(Node right) {
        if (right.isNumber()) {
            return NumberNode.create(value.subtract(right.getAsNumber().getValue()));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '-' can't cast right '{}' to number", right);
                return Node.UNDEFINED;
            } else {
                return NumberNode.create(value.subtract(right.getAsNumber().getValue()));
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
        if (right.isNumber()) {
            return value.equals(right.getAsNumber().getValue()) ? Node.TRUE : Node.FALSE;
        } else if (right.isString()) {
            NumberNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.equals(rightNum.getValue()) ? Node.TRUE : Node.FALSE;
            } else {
                return Node.FALSE;
            }
        } else {
            return (this.getAsBoolean().equals(right.getAsBoolean())) ? Node.TRUE : Node.FALSE;
        }
    }

    @Override
    public Node oper_greaterThan(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) > 0 ? Node.TRUE : Node.FALSE;
        } else if (right.isString()) {
            NumberNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) > 0 ? Node.TRUE : Node.FALSE;
            } else {
                return this.getAsString().oper_greaterThan(right);
            }
        }
        return this.getAsBoolean().oper_greaterThan(right.getAsBoolean());
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) >= 0 ? Node.TRUE : Node.FALSE;
        } else if (right.isString()) {
            NumberNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) >= 0 ? Node.TRUE : Node.FALSE;
            } else {
                return this.getAsString().oper_greaterOrEqual(right);
            }
        }
        return this.getAsBoolean().oper_greaterOrEqual(right.getAsBoolean());
    }

    @Override
    public Node oper_lessThan(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) < 0 ? Node.TRUE : Node.FALSE;
        } else if (right.isString()) {
            NumberNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) < 0 ? Node.TRUE : Node.FALSE;
            } else {
                return this.getAsString().oper_lessThan(right);
            }
        }
        return this.getAsBoolean().oper_lessThan(right.getAsBoolean());
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) <= 0 ? Node.TRUE : Node.FALSE;
        } else if (right.isString()) {
            NumberNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) <= 0 ? Node.TRUE : Node.FALSE;
            } else {
                return this.getAsString().oper_lessOrEqual(right);
            }
        }
        return this.getAsBoolean().oper_lessOrEqual(right.getAsBoolean());
    }

    @Override
    public BooleanNode getAsBoolean() {
        return (getAsNumber().getValue().equals(new BigDecimal(0))) ? Node.FALSE : Node.TRUE;
    }

    @Override
    public NumberNode getAsNumber() {
        return NumberNode.create(value);
    }

    @Override
    public StringNode getAsString() {
        StringNode result;

        if (value.compareTo(new BigDecimal("0")) == 0) {
            result = StringNode.create("0");
        } else {
            result = StringNode.create(getAsNumber().getValue().stripTrailingZeros().toPlainString());
        }

        return result;
    }

    @Override
    public ObjectNode getAsObject() {
        throw new RuntimeException("Can't cast " + getClass() + " to object");
    }

    @Override
    public JsonElement getAsJsonElement() {
        if (value.equals(new BigDecimal("0"))) {
            return new JsonPrimitive(new BigDecimal("0"));
        } else {
            return new JsonPrimitive(value);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("number(");
        sb.append(value.toPlainString());
        sb.append(")");
        return sb.toString();
    }

    @Override
    public int compareTo(NumberNode o) {
        if (o == null) {
            return 1;
        }
        return getValue().compareTo(o.getValue());
    }
}
