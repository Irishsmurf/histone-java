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
package ru.histone.evaluator.nodes;

import ru.histone.evaluator.EvaluatorContext;

/**
 * Special class for wrapping Histone context as object type object<br/>
 * This class is used for evaluating deep selectors (e.g. &#125;&#125;one.field.val&#125;&#125;)
 */
public class ContextWrapperNode extends ObjectHistoneNode {

    private EvaluatorContext context;

    private ContextWrapperNode(NodeFactory nodeFactory, EvaluatorContext context) {
        super(nodeFactory);
        this.context = context;
    }

//    /**
//     * Create context wrapper at evaluator context
//     *
//     * @param context evaluator context
//     * @return object type object with internal values copied from evaluator context
//     */
    public static ContextWrapperNode create(NodeFactory nodeFactory, EvaluatorContext context) {
        return new ContextWrapperNode(nodeFactory, context);
    }

    @Override
    public Node getProp(String name) {
        return context.getProp(name);
    }

    @Override
    public boolean hasProp(String name) {
        return context.hasProp(name);
    }

//    @Override
//    public void add(Object key, Node value) {
//        context.putProp(key, value);
//    }

//    @Override
//    public Set<Entry<String, Node>> entries() {
//        return context.entries();
//    }


    // Wrapper method stubs.
    @Override
    public final Node oper_add(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_mul(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_div(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_mod(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_negate() {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_sub(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_not() {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_equal(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_greaterThan(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_greaterOrEqual(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_lessThan(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

    @Override
    public final Node oper_lessOrEqual(Node right) {
        throw new RuntimeException("Not supported for this type of node");
    }

}
