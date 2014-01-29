package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

public class ASTTreeElementsCounter {

    public int count(JsonNode node) {
        int count = 0;

        if (node != null) {
            if (node.isObject()) {
                count = count + 1 + countObject((ObjectNode) node);
            } else if (node.isArray()) {
                count = count + 1 + countArray((ArrayNode) node);
            } else {
                count++;
            }
        }

        return count;
    }

    private int countArray(ArrayNode node) {
        int count = 0;

        for (int i = 0; i < node.size(); i++) {
            count = count + count(node.get(i));
        }

        return count;
    }

    private int countObject(ObjectNode node) {
        int count = 0;

        Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            count = count + 1 + count(entry.getValue());
        }

        return count;
    }
}
