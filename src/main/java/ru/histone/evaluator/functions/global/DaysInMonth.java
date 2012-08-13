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
package ru.histone.evaluator.functions.global;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.histone.Histone;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NumberNode;

/**
 * Return number of days in specified month
 */
public class DaysInMonth implements GlobalFunction {
    @Override
    public String getName() {
        return "daysInMonth";
    }

    @Override
    public Node execute(Node... args) throws GlobalFunctionExecutionException {
        if (args.length < 2) {
            Histone.runtime_log_warn("Function range() needs to have two arguments, but you provided '{}' arguments", args.length);
            return Node.UNDEFINED;
        }

        if (args.length > 2) {
            Histone.runtime_log_warn("Function range() has only two arguments, but you provided '{}' arguments", args.length);
        }

        NumberNode year = args[0].getAsNumber();
        NumberNode month = args[1].getAsNumber();

        if (year.isUndefined() || !year.isInteger()) {
            Histone.runtime_log_warn("Can't cast first argument '{}' to integer Number for function dayOfWeek", year.getAsString());
            return Node.UNDEFINED;
        }

        if (month.isUndefined() || !year.isInteger()) {
            Histone.runtime_log_warn("Can't cast second argument '{}' to integer Number for function dayOfWeek", month.getAsString());
            return Node.UNDEFINED;
        }

        if ((month.getValue().intValue() <= 0) || (month.getValue().intValue() > 12)) {
            Histone.runtime_log_warn("Second argument for function dayOfWeek should be between 1 and 12, you provided '{}' ", month.getAsString());
            return Node.UNDEFINED;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setLenient(false);
        calendar.set(Calendar.YEAR, year.getValue().intValue());
        calendar.set(Calendar.MONTH, month.getValue().intValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        try {
            calendar.getTimeInMillis();
        } catch (IllegalArgumentException e) {
            Histone.runtime_log_warn_e("Arguments specified for dayOfWeek function is incorrect date (y:{},m:{},d:{})", e, year.getAsString(), month.getAsString());
            return Node.UNDEFINED;
        }

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return NumberNode.create(daysInMonth);
    }
}
