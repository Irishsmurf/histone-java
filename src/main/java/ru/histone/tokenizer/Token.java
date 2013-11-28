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
package ru.histone.tokenizer;

/**
 * Token holder object<br/>
 * Stores all information about token: it's type, position in input sequence and it's content (if any)
 */
public class Token {
    public static final Token EOF_TOKEN = new Token(TokenType.T_EOF, -1, "EOF");

    /**
     * Token content. Not all tokens has their content.
     */
    private String content;

    /**
     * Token type
     */
    private TokenType type;

    /**
     * Token position in input sequence
     */
    private int pos;

    public Token(TokenType type, int pos, String content) {
        super();
        this.type = type;
        this.content = content;
        this.pos = pos;
    }

    public Token(Token original) {
        this(original.getType(), original.getPos(), original.getContent());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Token(");
        sb.append("type=" + type + ", ");
        sb.append("content=" + ((content != null) ? content.replaceAll("\n", "\\\\n") : content));
        sb.append(")");

        return sb.toString();
    }
}
