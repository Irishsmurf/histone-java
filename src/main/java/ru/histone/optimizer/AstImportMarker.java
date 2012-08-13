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
package ru.histone.optimizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.parser.AstNodeType;

public class AstImportMarker {
    private static final Logger log = LoggerFactory.getLogger(AstImportMarker.class);

    public JsonArray mark(JsonArray ast) throws HistoneException {
        ImportResolverContext context = new ImportResolverContext();
        return markInternal(ast, context);
    }

    private JsonArray markInternal(JsonArray ast, ImportResolverContext context) throws HistoneException {
        JsonArray result = new JsonArray();

        for (JsonElement element : ast) {
            JsonElement node = markNode(element, context);
            result.add(node);
        }

        return result;
    }

    private JsonElement markNode(JsonElement element, ImportResolverContext context) throws HistoneException {
        if (isString(element)) {
            return element;
        }

        if (!element.isJsonArray()) {
            Histone.runtime_log_warn("Invalid JSON element! Neither 'string', nor 'array'. Element: '{}'", element.toString());
            return element;
        }

        JsonArray astArray = element.getAsJsonArray();

        int nodeType = getNodeType(astArray);
        switch (nodeType) {
            case AstNodeType.IMPORT:
                return markImport(astArray.get(1).getAsJsonPrimitive(), context);

            default:
                return astArray;
        }
    }

    private JsonElement markImport(JsonPrimitive name, ImportResolverContext context) throws HistoneException {
//        if (!isString(pathElement)) {
//            Histone.runtime_log_warn("Invalid path to imported template: '{}'", pathElement.toString());
//            return AstNodeFactory.createNode(AstNodeType.STRING, "");
//        }
//        String path = pathElement.getAsJsonPrimitive().getAsString();
//        Resource resource = null;
//        InputStream resourceStream = null;
//        try {
//            URI currentBaseURI = getContextBaseURI(context);
//            URI resourceFullPath = currentBaseURI == null ? resourceLoaderManager.getFullPath(path) : resourceLoaderManager.getFullPath(currentBaseURI, path);
//
//            if (context.hasImportedResource(resourceFullPath.toString())) {
//                Histone.runtime_log_info("Resource already imported.");
//                return AstNodeFactory.createNode(AstNodeType.STRING, "");
//            } else {
//                resource = currentBaseURI == null ? resourceLoaderManager.load(path, null) : resourceLoaderManager.load(currentBaseURI, path, null);
//
//            }
//        } finally {
//            IOUtils.closeQuietly(resourceStream, log);
//            IOUtils.closeQuietly(resource, log);
//        }
        throw new RuntimeException("UNIMPLEMENTED");
    }

    private boolean isString(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private int getNodeType(JsonArray astArray) {
        return astArray.get(0).getAsJsonPrimitive().getAsInt();
    }

}
