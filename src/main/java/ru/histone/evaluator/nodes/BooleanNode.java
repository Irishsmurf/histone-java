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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ru.histone.Histone;

/**
 * Class representing Boolean type in Histone
 */
public class BooleanNode extends Node {

//	public static final BooleanNode TRUE_INSTANCE = new BooleanNode(true);
//	public static final BooleanNode FALSE_INSTANCE = new BooleanNode(false);

    private Boolean value;

    /**
     * Creates boolean object with spcified value
     *
     * @param value boolean object value
     * @return boolean type object
     */
    public static BooleanNode create(boolean value) {
        return new BooleanNode(value);
    }

    protected BooleanNode(boolean value) {
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
            return Node.UNDEFINED;
        } else {
            return this.getAsString().oper_add(right.getAsString());
        }
    }

    private Node commonMulDivSubMod(Node right) {
        Histone.runtime_log_warn("Boolean: operations '*/-%' is undefined for '{}' and '{}'", this, right);
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
        Histone.runtime_log_warn("Boolean: operations '- (negate)' is undefined for '{}'", this);
        return Node.UNDEFINED;
    }

    @Override
    public Node oper_sub(Node right) {
        return commonMulDivSubMod(right);
    }

    @Override
    public Node oper_not() {
        return value ? Node.FALSE : Node.TRUE;
    }

    @Override
    public Node oper_equal(Node right) {
        return (value.equals(right.getAsBoolean().getValue())) ? Node.TRUE : Node.FALSE;
    }

    @Override
    public Node oper_greaterThan(Node right) {
        return (value == right.getAsBoolean().getValue()) ? Node.FALSE : (value ? Node.TRUE : Node.FALSE);
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        return (value == right.getAsBoolean().getValue()) ? Node.TRUE : (value ? Node.TRUE : Node.FALSE);
    }

    @Override
    public Node oper_lessThan(Node right) {
        return (value == right.getAsBoolean().getValue()) ? Node.FALSE : (value ? Node.FALSE : Node.TRUE);
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        return (value == right.getAsBoolean().getValue()) ? Node.TRUE : (value ? Node.FALSE : Node.TRUE);
    }

    @Override
    public BooleanNode getAsBoolean() {
        return this;
    }

    @Override
    public NumberNode getAsNumber() {
        return NumberNode.create(getAsBoolean().getValue() ? 1 : 0);
    }

    @Override
    public StringNode getAsString() {
        return StringNode.create(getAsBoolean().getValue() ? "true" : "false");
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
        sb.append("boolean(");
        sb.append(value.toString());
        sb.append(")");
        return sb.toString();
    }

}
