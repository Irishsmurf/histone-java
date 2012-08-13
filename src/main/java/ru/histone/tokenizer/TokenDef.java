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
 * Token definition holder
 */
public class TokenDef {
    /**
     * Token type
     */
    private TokenType type;

    /**
     * Token regexp
     */
    private String regexp;

    /**
     * Context where token appears
     */
    private TokenContext context;

    /**
     * Token kind
     */
    private TokenKind kind;

    /**
     * Context to switch to if this token appears in input sequence
     */
    private TokenContext transition;

    public TokenDef(TokenType type, TokenKind kind, TokenContext context, String regexp) {
        this(type, kind, context, null, regexp);
    }

    public TokenDef(TokenType type, TokenKind kind, TokenContext context, TokenContext transition, String regexp) {
        this.type = type;
        this.kind = kind;
        this.context = context;
        this.regexp = regexp;
        this.transition = transition;
    }

    public TokenType getType() {
        return type;
    }

    public String getRegexp() {
        return regexp;
    }

    public TokenContext getContext() {
        return context;
    }

    public TokenKind getKind() {
        return kind;
    }

    public TokenContext getTransition() {
        return transition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenDef tokenDef = (TokenDef) o;

        if (context != tokenDef.context) return false;
        if (kind != tokenDef.kind) return false;
        if (!regexp.equals(tokenDef.regexp)) return false;
        if (transition != tokenDef.transition) return false;
        if (type != tokenDef.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + regexp.hashCode();
        result = 31 * result + context.hashCode();
        result = 31 * result + kind.hashCode();
        result = 31 * result + (transition != null ? transition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("TokenDef(");
        sb.append("type=" + type + ", ");
        sb.append("context=" + context + ", ");
        sb.append("transition=" + transition);
        sb.append(")");

        return sb.toString();
    }

}
