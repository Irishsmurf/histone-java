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

public abstract class Node {

    public static final NullNode NULL = new NullNode();
	public static final BooleanNode TRUE = BooleanNode.create(true);
	public static final BooleanNode FALSE = BooleanNode.create(false);
	public static final UndefinedNode UNDEFINED = UndefinedNode.INSTANCE;
	public static final NumberNode UNDEFINED_NUMBER = UndefinedNumberNode.INSTANCE;

	// Methods for type checking
	public boolean isBoolean() {
        return this instanceof BooleanNode;
	}

	public boolean isNumber() {
        return !(this instanceof UndefinedNumberNode) && (this instanceof NumberNode);
	}

	public boolean isFloat() {
        return isNumber() && (this.getAsNumber().isFloat());
	}

	public boolean isInteger() {
        return isNumber() && (this.getAsNumber().getValue().stripTrailingZeros().scale() <= 0);
	}

	public boolean isString() {
        return this instanceof StringNode;
	}

	public boolean isNull() {
        return this instanceof NullNode;
	}

	public boolean isUndefined() {
		return (this instanceof  UndefinedNode) || (this instanceof  UndefinedNumberNode);
	}

	public boolean isObject() {
        return this instanceof ObjectNode;
	}

    public boolean isAst() {
        return this instanceof AstNode;
    }

    public abstract BooleanNode getAsBoolean();

	public abstract NumberNode getAsNumber();

	public abstract StringNode getAsString();

	public abstract ObjectNode getAsObject();

    public static Node jsonToNode(JsonElement json) {
        if(json == null || json.isJsonNull()) {
            return Node.NULL;
        }
        if(json.isJsonArray()) {
            return ObjectNode.create(json.getAsJsonArray());
        }
        if(json.isJsonObject()) {
            return ObjectNode.create(json.getAsJsonObject());
        }
        if(!json.isJsonPrimitive()) {
            throw new IllegalArgumentException(String.format("Unknown type of JsonElement = '%s'", json.toString()));
        }
        JsonPrimitive primitive = json.getAsJsonPrimitive();
        if(primitive.isBoolean()) {
            return primitive.getAsBoolean() ? Node.TRUE : Node.FALSE;
        }
        if(primitive.isNumber()) {
            return NumberNode.create(primitive.getAsBigDecimal());
        }
        if(primitive.isString()) {
            return StringNode.create(primitive.getAsString());
        }
        throw new IllegalArgumentException(String.format("Unknown type of JsonElement = '%s'", json.toString()));
	}

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
		return oper_equal(right).getAsBoolean().getValue() ? Node.FALSE : Node.TRUE;
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
		return Node.UNDEFINED;
	}

	public abstract JsonElement getAsJsonElement();
}
