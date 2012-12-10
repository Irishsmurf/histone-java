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
package ru.histone.acceptance.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;
import ru.histone.evaluator.functions.global.GlobalFunction;
import ru.histone.evaluator.functions.global.GlobalFunctionExecutionException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;

import java.math.BigDecimal;

/**
 *
 *
 */
public class MockGlobalFunction extends GlobalFunction {
    private String name;
    private String data;
    private String resultType;
    private boolean throwException;

    private Histone histone;

    public MockGlobalFunction(NodeFactory nodeFactory, String name, String resultType, String data, boolean throwException) {
        super(nodeFactory);
        this.name = name;
        this.resultType = resultType;
        this.data = data;
        this.throwException=throwException;
        HistoneBuilder histoneBuilder = new HistoneBuilder();
        histoneBuilder.setJackson(new ObjectMapper());
        try {
            histone = histoneBuilder.build();
        } catch (HistoneException e) {
            throw new RuntimeException("Error initializing inner Histone",e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node execute(Node... args) throws GlobalFunctionExecutionException {
        if (throwException) {
            throw new RuntimeException("Function exception");
        }

        Node node = null;
        if ("string".equals(resultType.toLowerCase())) {
            ObjectNode context = getNodeFactory().jsonObject();
            ArrayNode argsArray = getNodeFactory().jsonArray();
            for(Node arg : args){
                argsArray.add(arg.getAsJsonNode());
            }
            context.put("args", argsArray);

            String result = null;
            try {
                result = histone.evaluate(data, context);
            } catch (HistoneException e) {

            }

            node = getNodeFactory().string(result);
        } else if ("number".equals(resultType.toLowerCase())) {
            node = getNodeFactory().number(new BigDecimal(data));
        } else if ("boolean".equals(resultType.toLowerCase())) {
            node = "true".equalsIgnoreCase(data) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else {
            throw new RuntimeException(String.format("ResultType '%s' not supported by tests", resultType));
        }

        return node;
    }
}
