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
package ru.histone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import ru.histone.evaluator.nodes.Node;
import ru.histone.resourceloaders.ContentType;
import ru.histone.resourceloaders.DefaultResourceLoader;
import ru.histone.resourceloaders.Resource;
import ru.histone.resourceloaders.ResourceLoadException;
import ru.histone.resourceloaders.StringResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpecialBugsTest {
    @Test
    public void bigDeciamlAsIs() throws HistoneException {
        ObjectMapper jackson = new ObjectMapper();
        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        jackson.setNodeFactory(jsonNodeFactory);

        HistoneBuilder builder = new HistoneBuilder();
        builder.setJackson(jackson);
        builder.setResourceLoader(new DefaultResourceLoader() {
            @Override
            public Resource load(String href, String baseHref, String[] contentTypes, Node... args) throws ResourceLoadException {
                Resource resource = new StringResource("\"test\"", baseHref, ContentType.TEXT);
                try {
                    assertNotNull(args);
                    assertEquals(1, args.length);
                    assertTrue(args[0].isObject());
                    JsonNode jsonNode = args[0].getAsJsonNode();
                    assertEquals("10", jsonNode.with("data").get("a").asText());
                    assertEquals("1", jsonNode.with("data").get("b").asText());
                } catch (Throwable throwable) {
                    TextNode json = new TextNode(throwable.getMessage());
                    resource = new StringResource(json.toString(), baseHref, ContentType.TEXT);
                }
                return resource;
            }
        });
        Histone histone = builder.build();

        String input = "{{loadJSON('/test',[data:[" +
                "a:10," +
                "b:1" +
                "]])}}";

        String output = histone.evaluate(input);
        assertEquals("test", output);
    }
}
