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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Returns day of week as a number for specified date<br/>
 * 1 - monday, 7 - sunday
 */
public class DayOfWeek extends GlobalFunction {
    public DayOfWeek(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "dayOfWeek";
    }

    @Override
    public Node execute(Node ... args) throws GlobalFunctionExecutionException {
        if (args.length < 3) {
            Histone.runtime_log_warn("Function dayOfWeek() needs to have three arguments, but you provided '{}' arguments", args.length);
            return getNodeFactory().UNDEFINED;
        }

        if (args.length > 3) {
            Histone.runtime_log_warn("Function dayOfWeek() has only three arguments, but you provided '{}' arguments", args.length);
        }

        NumberHistoneNode year = args[0].getAsNumber();
        NumberHistoneNode month = args[1].getAsNumber();
        NumberHistoneNode day = args[2].getAsNumber();

        if (year.isUndefined() || !year.isInteger()) {
            Histone.runtime_log_warn("Can't cast first argument '{}' to integer Number for function dayOfWeek", year.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        if (month.isUndefined() || !year.isInteger()) {
            Histone.runtime_log_warn("Can't cast second argument '{}' to integer Number for function dayOfWeek", month.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        if ((month.getValue().intValue() <= 0) || (month.getValue().intValue() > 12)) {
            Histone.runtime_log_warn("Second argument for function dayOfWeek should be between 1 and 12, you provided '{}' ", month.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        if (day.isUndefined() || !year.isInteger()) {
            Histone.runtime_log_warn("Can't cast third argument '{}' to integer Number for function dayOfWeek", day.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setLenient(false);
        calendar.set(Calendar.YEAR, year.getValue().intValue());
        calendar.set(Calendar.MONTH, month.getValue().intValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day.getValue().intValue());

        try {
            calendar.getTimeInMillis();
        } catch (IllegalArgumentException e) {
            Histone.runtime_log_warn_e("Arguments specified for dayOfWeek function is incorrect date (y:{},m:{},d:{})", e, year.getAsString(), month.getAsString(), day.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        if (!isLeapYear(year.getValue().intValue()) && (month.getValue().intValue() == 2) && (day.getValue().intValue() > 28)) {
            Histone.runtime_log_warn("Arguments specified for dayOfWeek function is incorrect date (y:{},m:{},d:{})", year.getAsString(), month.getAsString(), day.getAsString());
            return getNodeFactory().UNDEFINED;
        }

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }

        return getNodeFactory().number(dayOfWeek);
    }

    private static boolean isLeapYear(int year) {
        if (year < 0) {
            return false;
        }

        if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else if (year % 4 == 0) {
            return true;
        } else {
            return false;
        }
    }
}
