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
 * Special class representing Undefined values for Numbers.<br/>
 * This class is used for math expressions with undefined intermediate result
 */
public class UndefinedNumberNode extends NumberNode {

	public static final UndefinedNumberNode INSTANCE = new UndefinedNumberNode();

	@Override
	public Node oper_add(Node right) {
		if (right.isUndefined()) {
			return null;
		} else if (right.isNull()) {
			return null;
		} else if (right.isString()) {
			return null;
		} else if (right.isBoolean()) {
			return null;
		} else if (right.isNumber()) {
			return null;
		} else {
			throw new RuntimeException("Unknown right node type: " + right.getClass());
		}
	}

	@Override
	public BooleanNode getAsBoolean() {
		return Node.UNDEFINED.getAsBoolean();
	}

	@Override
	public NumberNode getAsNumber() {
		return Node.UNDEFINED.getAsNumber();
	}

	@Override
	public StringNode getAsString() {
		return Node.UNDEFINED.getAsString();
	}

	@Override
	public JsonElement getAsJsonElement() {
		return Node.UNDEFINED.getAsJsonElement();
	}

	@Override
	public String toString() {
		return "undefined()";
	}

}
