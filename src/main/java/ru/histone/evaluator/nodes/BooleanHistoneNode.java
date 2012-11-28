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

/**
 * Class representing Boolean type in Histone
 */
public class BooleanHistoneNode extends Node {
    private Boolean value;

    protected BooleanHistoneNode(NodeFactory nodeFactory, boolean value) {
        super(nodeFactory);
        this.value = value;
    }

    /**
     * Returns boolean object value
     *
     * @return object value
     */
    public Boolean getValue() {
        return value;
    }

    @Override
    public Node oper_add(Node right) {
        if (right.isNumber()) {
            Histone.runtime_log_warn("Boolean: operation '+' is undefined for '{}' and '{}'", this, right);
            return getNodeFactory().UNDEFINED;
        } else {
            return this.getAsString().oper_add(right.getAsString());
        }
    }

    private Node commonMulDivSubMod(Node right) {
        Histone.runtime_log_warn("Boolean: operations '*/-%' is undefined for '{}' and '{}'", this, right);
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
        Histone.runtime_log_warn("Boolean: operations '- (negate)' is undefined for '{}'", this);
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_sub(Node right) {
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_not() {
        return value ? getNodeFactory().FALSE : getNodeFactory().TRUE;
    }

    @Override
    public Node oper_equal(Node right) {
        return (value.equals(right.getAsBoolean().getValue())) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
    }

    @Override
    public Node oper_greaterThan(Node right) {
        return (value == right.getAsBoolean().getValue()) ? getNodeFactory().FALSE : (value ? getNodeFactory().TRUE : getNodeFactory().FALSE);
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        return (value == right.getAsBoolean().getValue()) ? getNodeFactory().TRUE : (value ? getNodeFactory().TRUE : getNodeFactory().FALSE);
    }

    @Override
    public Node oper_lessThan(Node right) {
        return (value == right.getAsBoolean().getValue()) ? getNodeFactory().FALSE : (value ? getNodeFactory().FALSE : getNodeFactory().TRUE);
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        return (value == right.getAsBoolean().getValue()) ? getNodeFactory().TRUE : (value ? getNodeFactory().FALSE : getNodeFactory().TRUE);
    }

    @Override
    public BooleanHistoneNode getAsBoolean() {
        return this;
    }

    @Override
    public NumberHistoneNode getAsNumber() {
        return getNodeFactory().number(getAsBoolean().getValue() ? 1 : 0);
    }

    @Override
    public StringHistoneNode getAsString() {
        return getNodeFactory().string(getAsBoolean().getValue() ? "true" : "false");
    }

    @Override
    public ObjectHistoneNode getAsObject() {
        throw new RuntimeException("Can't cast " + getClass() + " to object");
    }

    @Override
    public JsonNode getAsJsonNode() {
        return getNodeFactory().jsonBoolean(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("boolean(");
        sb.append(value.toString());
        sb.append(")");
        return sb.toString();
    }

}
