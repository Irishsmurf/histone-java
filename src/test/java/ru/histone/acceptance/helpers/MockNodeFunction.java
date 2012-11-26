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
package ru.histone.acceptance.helpers;

import java.io.StringReader;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.functions.node.NodeFunctionExecutionException;
import ru.histone.evaluator.nodes.*;
import ru.histone.evaluator.nodes.BooleanHistoneNode;
import ru.histone.evaluator.nodes.StringHistoneNode;

/**
 *
 *
 */
public class MockNodeFunction extends NodeFunction {
    private String name;
    private String data;
    private String resultType;
    private boolean throwException;
    private Histone histone;


    public MockNodeFunction(NodeFactory nodeFactory, String name, String resultType, String data, boolean throwException) {
        super(nodeFactory);
        this.name = name;
        this.resultType = (resultType != null) ? resultType : "string";
        this.data = data;
        this.throwException = throwException;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node execute(Node target, Node... args) throws NodeFunctionExecutionException {
        if (throwException) {
            throw new RuntimeException("Function exception");
        }



//        {{var X = "test"}}
//        {{X.someFunc(123,'sdfdf',t)}}



        Node node = null;
        if ("string".equals(resultType.toLowerCase())) {

            ObjectNode context = getNodeFactory().jsonObject();
            ObjectNode targetObj = null;
            ArrayNode argsArray = null;
            context.put("target", targetObj);
            context.put("args", argsArray);

            String result = null;
            try {
                result = histone.evaluate(new StringReader(data), context);
            } catch (HistoneException e) {

            }

            node = getNodeFactory().string(result);
        } else if ("number".equals(resultType.toLowerCase())) {
            node = getNodeFactory().number(new BigDecimal(data));
        } else if ("boolean".equals(resultType.toLowerCase())) {
            node = "true".equalsIgnoreCase(data) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else {
            throw new RuntimeException();
        }

        return node;
    }


}
