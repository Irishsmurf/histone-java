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
package ru.histone.parser;

import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.tokenizer.Tokenizer;
import ru.histone.tokenizer.TokenizerFactory;

/**
 * Parser wrapper class - takes tokens from tokenizer and process them generating Abstract Syntax AstNodeFactory (AST) from input sequence
 */
public class Parser {
    private final static Logger log = LoggerFactory.getLogger(Parser.class);
    private TokenizerFactory tokenizerFactory;

    /**
     * Constructs parser with {@link TokenizerFactory} dependency
     *
     * @param tokenizerFactory tokenizer factory
     */
    public Parser(TokenizerFactory tokenizerFactory) {
        this.tokenizerFactory = tokenizerFactory;
    }

    /**
     * Parse input sequence into AST
     *
     * @param input input sequence
     * @return JSON representation of AST
     * @throws ParserException in case of parse error
     */
    public JsonArray parse(CharSequence input) throws ParserException {
        log.debug("parse(): input={}", new Object[]{input});

        Tokenizer tokenizer = tokenizerFactory.match(input);
        ParserImpl parser = new ParserImpl(tokenizer);
        JsonArray astTree = parser.parseTemplate();
        log.debug("parse(): astTree={}", astTree);

        return astTree;
    }
}
