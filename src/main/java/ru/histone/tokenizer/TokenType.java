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
package ru.histone.tokenizer;

/**
 * List of available token types
 */
public enum TokenType {
    T_EOF, IGNORE, LITERAL, T_FRAGMENT, T_BLOCK_START, T_BLOCK_END, T_LITERAL_START, T_LITERAL_END, T_COMMENT_START, T_COMMENT_END, EXPR_IF, EXPR_ELSEIF, EXPR_ELSE, EXPR_FOR, EXPR_IN, EXPR_VAR, EXPR_MACRO, EXPR_TRUE, EXPR_FALSE, EXPR_NULL, EXPR_AND, EXPR_OR, EXPR_NOT, EXPR_MOD, EXPR_IS, EXPR_NOT_EQUAL, EXPR_LESS_OR_EQUAL, EXPR_GREATER_OR_EQUAL, EXPR_WHITESPACES, EXPR_IDENT, EXPR_INTEGER, EXPR_LESS_THAN, EXPR_GREATER_THAN, EXPR_LBRACKET, EXPR_RBRACKET, EXPR_LBRACE, EXPR_RBRACE, EXPR_LPAREN, EXPR_RPAREN, EXPR_ASSIGN, EXPR_COLON, EXPR_COMMA, EXPR_SQUOTE, EXPR_QUOTE, EXPR_DOTDOT, EXPR_DOT, EXPR_ADD, EXPR_SUB, EXPR_MUL, EXPR_DIV, EXPR_STRING, TPL_COMMENT_WO_OPENNING, TPL_BLOCK_WO_OPENNING, EXPR_IF_END, EXPR_QUERY, EXPR_DOUBLE, EXPR_ARRAY, EXPR_OBJECT, EXPR_CALL, EXPRT_IMPORT, EXPR_THIS, EXPR_SELF, EXPR_GLOBAL;

}
