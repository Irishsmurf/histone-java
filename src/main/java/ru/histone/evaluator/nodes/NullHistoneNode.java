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

/**
 * Class representing Null type in Histone
 */
public class NullHistoneNode extends Node {

//	public static final NullHistoneNode INSTANCE = new NullHistoneNode();

	protected NullHistoneNode(NodeFactory nodeFactory) {
        super(nodeFactory);
	}

	@Override
	public Node oper_add(Node right) {
		if (right.isNumber()) {
			Histone.runtime_log_warn("Null: operation '+' is undefined for '{}' and '{}'", this, right);
			return getNodeFactory().UNDEFINED;
		} else {
			return this.getAsString().oper_add(right.getAsString());
		}
	}

	@Override
	public Node oper_mul(Node right) {
		Histone.runtime_log_warn("Null: operation '*' is undefined for '{}' and '{}'", this, right);
		return getNodeFactory().UNDEFINED;
	}

	@Override
	public Node oper_div(Node right) {
		Histone.runtime_log_warn("Null: operation '/' is undefined for '{}' and '{}'", this, right);
		return getNodeFactory().UNDEFINED;
	}

	@Override
	public Node oper_mod(Node right) {
		Histone.runtime_log_warn("Null: operation '%' is undefined for '{}' and '{}'", this, right);
		return getNodeFactory().UNDEFINED;
	}

	@Override
	public Node oper_negate() {
		Histone.runtime_log_warn("Null: operation '- (negate)' is undefined for '{}'", this);
		return getNodeFactory().UNDEFINED;
	}

	@Override
	public Node oper_sub(Node right) {
		Histone.runtime_log_warn("Null: operation '-' is undefined for '{}' and '{}'", this, right);
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
		return  getNodeFactory().number(0);
	}

	@Override
	public StringHistoneNode getAsString() {
		return getNodeFactory().string("null");
	}

	@Override
	public ObjectHistoneNode getAsObject() {
		return getNodeFactory().object();
	}

	@Override
	public JsonNode getAsJsonNode() {
		return getNodeFactory().jsonNull();
	}

	@Override
	public String toString() {
		return "null()";
	}

}
