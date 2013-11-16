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
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(true);
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
