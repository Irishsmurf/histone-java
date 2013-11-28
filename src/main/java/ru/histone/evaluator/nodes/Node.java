/**
 *    Copyright 2013 MegaFon
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

public abstract class Node {

    private NodeFactory nodeFactory = null;

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

//    public static final NullHistoneNode NULL = new NullHistoneNode();
//	public static final BooleanHistoneNode TRUE = BooleanHistoneNode.create(true);
//	public static final BooleanHistoneNode FALSE = BooleanHistoneNode.create(false);
//	public static final UndefinedNode UNDEFINED = UndefinedNode.INSTANCE;
//	public static final NumberHistoneNode UNDEFINED_NUMBER = UndefinedNumberHistoneNode.INSTANCE;

    protected Node(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    // Methods for type checking
    public boolean isBoolean() {
        return this instanceof BooleanHistoneNode;
    }

    public boolean isNumber() {
        return !(this instanceof UndefinedNumberHistoneNode) && (this instanceof NumberHistoneNode);
    }

    public boolean isFloat() {
        return isNumber() && (this.getAsNumber().getValue().stripTrailingZeros().scale() > 0);
    }

    public boolean isInteger() {
        return isNumber() && (this.getAsNumber().getValue().stripTrailingZeros().scale() <= 0);
    }

    public boolean isString() {
        return this instanceof StringHistoneNode;
    }

    public boolean isNull() {
        return this instanceof NullHistoneNode;
    }

    public boolean isUndefined() {
        return (this instanceof UndefinedNode) || (this instanceof UndefinedNumberHistoneNode);
    }

    public boolean isObject() {
        return this instanceof ObjectHistoneNode;
    }

    public boolean isNamespace() {
        return this instanceof NameSpaceNode;
    }

    public boolean isAst() {
        return this instanceof AstNode;
    }

    public abstract BooleanHistoneNode getAsBoolean();

    public abstract NumberHistoneNode getAsNumber();

    public abstract StringHistoneNode getAsString();

    public abstract ObjectHistoneNode getAsObject();

    public abstract Node oper_add(Node right);

    public abstract Node oper_mul(Node right);

    public abstract Node oper_div(Node right);

    public abstract Node oper_mod(Node right);

    public abstract Node oper_negate();

    public abstract Node oper_sub(Node right);

    public abstract Node oper_not();

    public final Node oper_and(Node right) {
        if (!this.getAsBoolean().getValue()) {
            return this;
        } else {
            return right;
        }
    }

    public final Node oper_or(Node right) {
        if (this.getAsBoolean().getValue()) {
            return this;
        } else {
            return right;
        }
    }

    public final Node oper_notEqual(Node right) {
        return oper_equal(right).getAsBoolean().getValue() ? nodeFactory.FALSE : nodeFactory.TRUE;
    }

    public abstract Node oper_equal(Node right);

    public boolean equals() {
        throw new RuntimeException("!!!!");
    }

    public abstract Node oper_greaterThan(Node right);

    public abstract Node oper_greaterOrEqual(Node right);

    public abstract Node oper_lessThan(Node right);

    public abstract Node oper_lessOrEqual(Node right);

    public boolean hasProp(String name) {
        Histone.runtime_log_warn("Object '{}' doesn't have property '{}'", toString(), name);
        return false;
    }

    public Node getProp(String name) {
        Histone.runtime_log_warn("Object '{}' doesn't have property '{}', returning 'undefined()'", toString(), name);
        return nodeFactory.UNDEFINED;
    }

    public abstract JsonNode getAsJsonNode();
}
