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
package ru.histone;

import ru.histone.tokenizer.*;

import java.util.*;

/**
 * This class stores all tokens that exists in Histone syntax<br/>
 * All tokens are defined using {@link TokenDef} class and consists of four parts: token name, kind, context, and regexp.
 * For more details see {@link TokenDef}
 *
 * @see TokenDef
 */
public final class HistoneTokensHolder {

    private static Collection<TokenContext> collection(TokenContext... contexts) {
        Collection<TokenContext> result = new HashSet<TokenContext>();

        for (TokenContext context : contexts) {
            result.add(context);
        }

        return result;
    }

    private static final List<TokenDef> tokens = Collections.unmodifiableList(new ArrayList<TokenDef>() {
        {
            add(new TokenDef(TokenType.T_COMMENT_START, TokenKind.TOKEN, TokenContext.TEMPLATE, TokenContext.COMMENT, "\\Q{{*\\E"));
            add(new TokenDef(TokenType.T_COMMENT_END, TokenKind.TOKEN, TokenContext.COMMENT, TokenContext.TEMPLATE, "\\Q*}}\\E"));

            add(new TokenDef(TokenType.T_LITERAL_START, TokenKind.TOKEN, TokenContext.TEMPLATE, TokenContext.LITERAL, "\\Q{{%\\E"));
            add(new TokenDef(TokenType.T_LITERAL_END, TokenKind.TOKEN, TokenContext.LITERAL, TokenContext.TEMPLATE, "\\Q%}}\\E"));

            add(new TokenDef(TokenType.T_BLOCK_START, TokenKind.TOKEN, collection(TokenContext.TEMPLATE, TokenContext.EXPRESSION), new TokenTransitionCallback() {
                @Override
                public TokenContext getTransition(NestedLevelHolder nestLevelHolder) {
                    TokenContext result = (nestLevelHolder.nestLevel % 2 == 0) ? TokenContext.EXPRESSION : TokenContext.TEMPLATE;
                    nestLevelHolder.nestLevel++;
                    return result;
                }
            }, "\\Q{{\\E"));

            add(new TokenDef(TokenType.T_BLOCK_END, TokenKind.TOKEN, collection(TokenContext.TEMPLATE, TokenContext.EXPRESSION), new TokenTransitionCallback() {
                @Override
                public TokenContext getTransition(NestedLevelHolder nestLevelHolder) {
                    if (nestLevelHolder.nestLevel == 0) return TokenContext.TEMPLATE;
                    TokenContext result = (nestLevelHolder.nestLevel % 2 == 0) ? TokenContext.EXPRESSION : TokenContext.TEMPLATE;
                    nestLevelHolder.nestLevel--;
                    return result;
                }
            }, "\\Q}}\\E"));

            add(new TokenDef(TokenType.EXPR_IF, TokenKind.LITERAL, TokenContext.EXPRESSION, "if\\b"));
            add(new TokenDef(TokenType.EXPR_ELSEIF, TokenKind.LITERAL, TokenContext.EXPRESSION, "elseif\\b"));
            add(new TokenDef(TokenType.EXPR_ELSE, TokenKind.LITERAL, TokenContext.EXPRESSION, "else\\b"));
            add(new TokenDef(TokenType.EXPR_FOR, TokenKind.LITERAL, TokenContext.EXPRESSION, "for\\b"));
            add(new TokenDef(TokenType.EXPR_IN, TokenKind.LITERAL, TokenContext.EXPRESSION, "in\\b"));
            add(new TokenDef(TokenType.EXPR_VAR, TokenKind.LITERAL, TokenContext.EXPRESSION, "var\\b"));
            add(new TokenDef(TokenType.EXPR_MACRO, TokenKind.LITERAL, TokenContext.EXPRESSION, "macro\\b"));
            add(new TokenDef(TokenType.EXPR_CALL, TokenKind.LITERAL, TokenContext.EXPRESSION, "call\\b"));
            add(new TokenDef(TokenType.EXPRT_IMPORT, TokenKind.LITERAL, TokenContext.EXPRESSION, "import\\b"));

            add(new TokenDef(TokenType.EXPR_THIS, TokenKind.LITERAL, TokenContext.EXPRESSION, "this\\b"));
            add(new TokenDef(TokenType.EXPR_SELF, TokenKind.LITERAL, TokenContext.EXPRESSION, "self\\b"));
            add(new TokenDef(TokenType.EXPR_GLOBAL, TokenKind.LITERAL, TokenContext.EXPRESSION, "global\\b"));

            add(new TokenDef(TokenType.EXPR_TRUE, TokenKind.LITERAL, TokenContext.EXPRESSION, "true\\b"));
            add(new TokenDef(TokenType.EXPR_FALSE, TokenKind.LITERAL, TokenContext.EXPRESSION, "false\\b"));
            add(new TokenDef(TokenType.EXPR_NULL, TokenKind.LITERAL, TokenContext.EXPRESSION, "null\\b"));

            add(new TokenDef(TokenType.EXPR_AND, TokenKind.LITERAL, TokenContext.EXPRESSION, "and\\b"));
            add(new TokenDef(TokenType.EXPR_OR, TokenKind.LITERAL, TokenContext.EXPRESSION, "or\\b"));
            add(new TokenDef(TokenType.EXPR_NOT, TokenKind.LITERAL, TokenContext.EXPRESSION, "not\\b"));
            add(new TokenDef(TokenType.EXPR_MOD, TokenKind.LITERAL, TokenContext.EXPRESSION, "mod\\b"));
            add(new TokenDef(TokenType.EXPR_IS, TokenKind.LITERAL, TokenContext.EXPRESSION, "is\\b"));
            add(new TokenDef(TokenType.EXPR_NOT_EQUAL, TokenKind.LITERAL, TokenContext.EXPRESSION, "isNot\\b|<>|!="));
            add(new TokenDef(TokenType.EXPR_LESS_OR_EQUAL, TokenKind.LITERAL, TokenContext.EXPRESSION, "<="));
            add(new TokenDef(TokenType.EXPR_GREATER_OR_EQUAL, TokenKind.LITERAL, TokenContext.EXPRESSION, ">="));
            add(new TokenDef(TokenType.EXPR_LESS_THAN, TokenKind.LITERAL, TokenContext.EXPRESSION, "<"));
            add(new TokenDef(TokenType.EXPR_GREATER_THAN, TokenKind.LITERAL, TokenContext.EXPRESSION, ">"));

            add(new TokenDef(TokenType.EXPR_WHITESPACES, TokenKind.IGNORE, TokenContext.EXPRESSION, "[\\s]+"));

            add(new TokenDef(TokenType.EXPR_DOUBLE, TokenKind.TOKEN, TokenContext.EXPRESSION, "(?:[0-9]*\\.)?[0-9]+[eE][\\+\\-]?[0-9]+"));
            add(new TokenDef(TokenType.EXPR_DOUBLE, TokenKind.TOKEN, TokenContext.EXPRESSION, "[0-9]*\\.[0-9]+"));
            add(new TokenDef(TokenType.EXPR_INTEGER, TokenKind.TOKEN, TokenContext.EXPRESSION, "[0-9]+"));

            add(new TokenDef(TokenType.EXPR_LBRACKET, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\["));
            add(new TokenDef(TokenType.EXPR_RBRACKET, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\]"));
            add(new TokenDef(TokenType.EXPR_LPAREN, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\("));
            add(new TokenDef(TokenType.EXPR_RPAREN, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\)"));
            add(new TokenDef(TokenType.EXPR_QUERY, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\?"));
            add(new TokenDef(TokenType.EXPR_ASSIGN, TokenKind.LITERAL, TokenContext.EXPRESSION, "="));
            add(new TokenDef(TokenType.EXPR_COLON, TokenKind.LITERAL, TokenContext.EXPRESSION, ":"));
            add(new TokenDef(TokenType.EXPR_COMMA, TokenKind.LITERAL, TokenContext.EXPRESSION, ","));

            add(new TokenDef(TokenType.EXPR_DOT, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\."));
            add(new TokenDef(TokenType.EXPR_ADD, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\+"));
            add(new TokenDef(TokenType.EXPR_SUB, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\-"));
            add(new TokenDef(TokenType.EXPR_MUL, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\*"));
            add(new TokenDef(TokenType.EXPR_DIV, TokenKind.LITERAL, TokenContext.EXPRESSION, "\\/"));

            add(new TokenDef(TokenType.EXPR_STRING, TokenKind.TOKEN, TokenContext.EXPRESSION, "\'(?:[^\'\\\\]|\\\\.)*\'"));
            add(new TokenDef(TokenType.EXPR_STRING, TokenKind.TOKEN, TokenContext.EXPRESSION, "\"(?:[^\"\\\\]|\\\\.)*\""));

            add(new TokenDef(TokenType.EXPR_IDENT, TokenKind.TOKEN, TokenContext.EXPRESSION, "[a-zA-Z_$][a-zA-Z0-9_$]*"));
        }

    });

    private HistoneTokensHolder() {
    }

    public static List<TokenDef> getTokens() {
        return tokens;
    }

}
