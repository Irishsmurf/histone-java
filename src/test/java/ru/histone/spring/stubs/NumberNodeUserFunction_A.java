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
package ru.histone.spring.stubs;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.functions.node.NodeFunctionExecutionException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NumberNode;
import ru.histone.evaluator.nodes.StringNode;

/**
 * @author P.Salnikov <p.salnikov@gmail.com>
 */
public class NumberNodeUserFunction_A implements NodeFunction<NumberNode>{
    @Override
    public String getName() {
        return "numberFunctionA";
    }

    @Override
    public Node execute(NumberNode target, Node... args) throws NodeFunctionExecutionException {
        return StringNode.create("number_a_result");
    }
}
