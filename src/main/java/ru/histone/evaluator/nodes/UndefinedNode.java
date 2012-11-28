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

/**
 * Class representing undefined type in Histone
 */
public class UndefinedNode extends Node {

    protected UndefinedNode(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public Node oper_add(Node right) {
        if (right.isNumber() || right.isUndefined()) {
            return getNodeFactory().UNDEFINED;
        } else {
            return this.getAsString().oper_add(right.getAsString());
        }
    }

    @Override
    public Node oper_mul(Node right) {
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_div(Node right) {
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_mod(Node right) {
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_negate() {
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_sub(Node right) {
        return getNodeFactory().UNDEFINED;
    }

    @Override
    public Node oper_not() {
        return getNodeFactory().TRUE;
    }

    @Override
    public Node oper_equal(Node right) {
        return (this.getAsBoolean() == right.getAsBoolean()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
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
        return getNodeFactory().FALSE;
    }

    @Override
    public NumberHistoneNode getAsNumber() {
        return getNodeFactory().UNDEFINED_NUMBER;
    }

    @Override
    public StringHistoneNode getAsString() {
        return getNodeFactory().string("");
    }

    @Override
    public ObjectHistoneNode getAsObject() {
        throw new RuntimeException("Can't cast " + getClass() + " to object");
    }

    @Override
    public JsonNode getAsJsonNode() {
        return getNodeFactory().NULL.getAsJsonNode();
    }

    @Override
    public String toString() {
        return "undefined()";
    }

}
