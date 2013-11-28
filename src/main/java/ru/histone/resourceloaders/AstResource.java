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
package ru.histone.resourceloaders;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class AstResource implements Resource<JsonNode> {
    private final String baseHref;
    private final JsonNode ast;

    public AstResource(JsonNode ast, String baseHref) {
        this.baseHref = baseHref;
        this.ast = ast;
    }

    @Override
    public JsonNode getContent() throws IOException {
        return ast;
    }

    @Override
    public String getBaseHref() {
        return baseHref;
    }

    @Override
    public String getContentType() {
        return ContentType.AST;
    }

    @Override
    public void close() throws IOException {
    }
}
