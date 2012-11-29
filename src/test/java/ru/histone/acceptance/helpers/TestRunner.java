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

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 *
 *
 */
//@RunWith(EvaluatorAcceptanceTest.class)
public class TestRunner extends Runner {
    private Description testSuiteDescription;

    public TestRunner(Class<?> testClass) {
        testSuiteDescription = Description.createSuiteDescription(this.getClass().getName());
    }

    @Override
    public Description getDescription() {
        return testSuiteDescription;
    }

    @Override
    public void run(RunNotifier notifier) {
//        notifier.

    }
}
