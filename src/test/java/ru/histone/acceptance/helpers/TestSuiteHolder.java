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

import ru.histone.utils.StringUtils;

public class TestSuiteHolder {
    private String fileName;
    private String suiteName;

    public TestSuiteHolder(String fileName, String suiteName) {
        this.fileName = fileName;
        this.suiteName = suiteName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSuiteName() {
        return suiteName;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{" + fileName);
        if (StringUtils.isNotEmpty(suiteName)) {
            result.append(": ").append(suiteName);
        }
        result.append("}");
        return result.toString();
    }
}
