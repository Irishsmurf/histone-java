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
import ru.histone.Histone;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class representing Null type in Histone
 */
public class NumberHistoneNode extends Node implements Comparable<NumberHistoneNode> {
    private BigDecimal value;

    protected NumberHistoneNode(NodeFactory nodeFactory, BigDecimal value) {
        super(nodeFactory);
        this.value = value;
    }

    /**
     * Number type object value
     *
     * @return object value
     */
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public Node oper_add(Node right) {
        if (right.isNumber()) {
            return getNodeFactory().number(this.value.add(right.getAsNumber().value));
        } else if (right.isString()) {
            return this.getAsString().oper_add(right);
        } else {
            Histone.runtime_log_warn("Number: operation '+' is undefined for '{}' and '{}'", this, right);
            return getNodeFactory().UNDEFINED;
        }
    }

    private Node commonMulDivSubMod(Node right) {
        Histone.runtime_log_warn("Number: operation '*/-%' is undefined for '{}' and '{}'", this, right);
        return getNodeFactory().UNDEFINED;

    }

    @Override
    public Node oper_mul(Node right) {
        if (right.isNumber()) {
            return getNodeFactory().number(value.multiply(right.getAsNumber().getValue()));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '*' can't cast right '{}' to number", right);
                return getNodeFactory().UNDEFINED;
            } else {
                return getNodeFactory().number(value.multiply(right.getAsNumber().getValue()));
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_div(Node right) {
        if (right.isNumber()) {
            return getNodeFactory().number(value.divide(right.getAsNumber().getValue(), 2, RoundingMode.HALF_UP));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '/' can't cast right '{}' to number", right);
                return getNodeFactory().UNDEFINED;
            } else {
                return getNodeFactory().number(value.divide(right.getAsNumber().getValue(), 2, RoundingMode.HALF_UP));
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_mod(Node right) {
        if (right.isNumber()) {
            return getNodeFactory().number(value.remainder(right.getAsNumber().getValue()));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '%' can't cast right '{}' to number", right);
                return getNodeFactory().UNDEFINED;
            } else {
                return getNodeFactory().number(value.remainder(right.getAsNumber().getValue()));
            }
        } else {
            return commonMulDivSubMod(right);
        }
    }

    @Override
    public Node oper_negate() {
        return getNodeFactory().number(value.negate());
    }

    @Override
    public Node oper_sub(Node right) {
        if (right.isNumber()) {
            return getNodeFactory().number(value.subtract(right.getAsNumber().getValue()));
        } else if (right.isString()) {
            if (right.getAsNumber().isUndefined()) {
                Histone.runtime_log_warn("Number: for operation '-' can't cast right '{}' to number", right);
                return getNodeFactory().UNDEFINED;
            } else {
                return getNodeFactory().number(value.subtract(right.getAsNumber().getValue()));
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
        if (right.isNumber()) {
            return value.equals(right.getAsNumber().getValue()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isString()) {
            NumberHistoneNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.equals(rightNum.getValue()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            } else {
                return getNodeFactory().FALSE;
            }
        } else {
            return (this.getAsBoolean().equals(right.getAsBoolean())) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        }
    }

    @Override
    public Node oper_greaterThan(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) > 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isString()) {
            NumberHistoneNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) > 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            } else {
                return this.getAsString().oper_greaterThan(right);
            }
        }
        return this.getAsBoolean().oper_greaterThan(right.getAsBoolean());
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) >= 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isString()) {
            NumberHistoneNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) >= 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            } else {
                return this.getAsString().oper_greaterOrEqual(right);
            }
        }
        return this.getAsBoolean().oper_greaterOrEqual(right.getAsBoolean());
    }

    @Override
    public Node oper_lessThan(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) < 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isString()) {
            NumberHistoneNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) < 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            } else {
                return this.getAsString().oper_lessThan(right);
            }
        }
        return this.getAsBoolean().oper_lessThan(right.getAsBoolean());
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        if (right.isNumber()) {
            return value.compareTo(right.getAsNumber().getValue()) <= 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else if (right.isString()) {
            NumberHistoneNode rightNum = right.getAsNumber();
            if (rightNum.isNumber()) {
                return value.compareTo(rightNum.getValue()) <= 0 ? getNodeFactory().TRUE : getNodeFactory().FALSE;
            } else {
                return this.getAsString().oper_lessOrEqual(right);
            }
        }
        return this.getAsBoolean().oper_lessOrEqual(right.getAsBoolean());
    }

    @Override
    public BooleanHistoneNode getAsBoolean() {
        return (getAsNumber().getValue().equals(new BigDecimal(0))) ? getNodeFactory().FALSE : getNodeFactory().TRUE;
    }

    @Override
    public NumberHistoneNode getAsNumber() {
        return getNodeFactory().number(value);
    }

    @Override
    public StringHistoneNode getAsString() {
        StringHistoneNode result;

        if (value.compareTo(new BigDecimal("0")) == 0) {
            result = getNodeFactory().string("0");
        } else {
            result = getNodeFactory().string(getAsNumber().getValue().stripTrailingZeros().toPlainString());
        }

        return result;
    }

    @Override
    public ObjectHistoneNode getAsObject() {
        throw new RuntimeException("Can't cast " + getClass() + " to object");
    }

    @Override
    public JsonNode getAsJsonNode() {
        if (value.equals(new BigDecimal("0"))) {
            return getNodeFactory().jsonNumber(new BigDecimal("0"));
        } else {
            return getNodeFactory().jsonNumber(value);
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
    public int compareTo(NumberHistoneNode o) {
        if (o == null) {
            return 1;
        }
        return getValue().compareTo(o.getValue());
    }
}
