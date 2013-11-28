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
package ru.histone.evaluator.functions.global;

import ru.histone.Histone;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.NumberHistoneNode;
import ru.histone.evaluator.nodes.ObjectHistoneNode;

/**
 * Generates array type object with numbers from diapason based on specified arguments<br/>
 */
public class Range extends GlobalFunction {
    public Range(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "range";
    }

    @Override
    public Node execute(Node... args) throws GlobalFunctionExecutionException {
        if (args.length < 2) {
            Histone.runtime_log_warn("Function range() needs to have two arguments, but you provided '{}' arguments", args.length);
            return getNodeFactory().UNDEFINED;
        }

        if (args.length > 2) {
            Histone.runtime_log_warn("Function range() has only two arguments, but you provided '{}' arguments", args.length);
        }

        NumberHistoneNode start = args[0].getAsNumber();
        NumberHistoneNode stop = args[1].getAsNumber();

        if (start.isUndefined() || !start.isInteger()) {
            Histone.runtime_log_warn("Can't cast first argument '{}' to integer Number for function range", start.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        if (stop.isUndefined() || !stop.isInteger()) {
            Histone.runtime_log_warn("Can't cast first argument '{}' to integer Number for function range", start.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        int startInt = start.getValue().intValue();
        int stopInt = stop.getValue().intValue();

        ObjectHistoneNode result = getNodeFactory().object();

        if (startInt <= stopInt) {
            for (int i = startInt; i <= stopInt; i++) {
                result.add(getNodeFactory().number(i));
            }
        } else {
            for (int i = startInt; i >= stopInt; i--) {
                result.add(getNodeFactory().number(i));
            }
        }

        return result;
    }
}
