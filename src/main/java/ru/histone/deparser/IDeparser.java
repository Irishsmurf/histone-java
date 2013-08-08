package ru.histone.deparser;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface IDeparser {
    String deparse(ArrayNode ast);
}
