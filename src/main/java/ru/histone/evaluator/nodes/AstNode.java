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

/**
 * Special class, used in AST optimizer module
 */
public class AstNode extends Node {
    private JsonElement value;

    public static AstNode create(JsonElement element) {
        return new AstNode(element);
    }

    private AstNode(JsonElement value) {
        this.value = value;
    }

    public JsonElement getValue() {
        return value;
    }

    @Override
    public BooleanNode getAsBoolean() {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public NumberNode getAsNumber() {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public StringNode getAsString() {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public ObjectNode getAsObject() {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public JsonElement getAsJsonElement() {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_add(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_mul(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_div(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_mod(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_negate() {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_sub(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_not() {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_equal(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_greaterThan(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_greaterOrEqual(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_lessThan(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }

    @Override
    public Node oper_lessOrEqual(Node right) {
        throw new UnsupportedOperationException("AstNode doesn't support this method");
    }
}
