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
package ru.histone.parser;

/**
 * Constants representing nodes in AST tree
 */
public interface AstNodeType {
	int OR = 1;
	int AND = 2;
	int EQUAL = 3;
	int NOT_EQUAL = 4;
	int LESS_OR_EQUAL = 5;
	int LESS_THAN = 6;
	int GREATER_OR_EQUAL = 7;
	int GREATER_THAN = 8;
	int ADD = 9;
	int SUB = 10;
	int MUL = 11;
	int DIV = 12;
	int MOD = 13;
	int NEGATE = 14;
	int NOT = 15;
	int TRUE = 16;
	int FALSE = 17;
	int NULL = 100;
	int INT = 101;
	int DOUBLE = 102;
	int STRING = 103;
	int TERNARY = 104;
	int SELECTOR = 105;
	int CALL = 106;
    int MAP = 107;
    int STATEMENTS = 109;
    int IMPORT = 110;
    int IF = 1000;
    int VAR = 1001;
    int FOR = 1002;
    int MACRO = 1003;
}
