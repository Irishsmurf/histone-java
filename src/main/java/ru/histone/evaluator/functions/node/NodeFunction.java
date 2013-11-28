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
package ru.histone.evaluator.functions.node;

import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;

/**
 * Use this interface when you need to implement your own function for Histone type
 */
public abstract class NodeFunction<T extends Node> {

    private NodeFactory nodeFactory;

    public NodeFunction(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    /**
     * Returns NodeFacotry instance
     *
     * @return NodeFactory instance
     */
    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    /**
     * Return function name
     *
     * @return function name
     */
    public abstract String getName();


    /**
     * This method will be run when Histone evaluate function<br/>
     *
     * @param args arguments from Histone template
     * @return result as one of Histone types
     * @throws ru.histone.evaluator.functions.global.GlobalFunctionExecutionException
     *          if your function stops with error and you need to put details into log, then you should use this exception
     */
    public abstract Node execute(T target, Node... args) throws NodeFunctionExecutionException;
}
