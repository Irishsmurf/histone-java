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

import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.GlobalProperty;
import ru.histone.evaluator.EvaluatorException;
import ru.histone.utils.CollectionUtils;
import ru.histone.utils.StringEscapeUtils;
import ru.histone.utils.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestCaseHolder {
    private String input;
    private boolean ignore;
    private TestSuiteHolder suite;
    private String expected;
    private ArrayNode expectedAST;
    private String context;
    private Set<MockFileDataHolder> mockFiles;
    private Set<MockGlobalFunctionHolder> mockGlobalFunctions;
    private Set<MockNodeFunctionHolder> mockNodeFunctions;
    private EvaluatorException exception;
    private Map<GlobalProperty, String> globalProperties;

    public TestCaseHolder(TestSuiteHolder suite) {
        this.suite = suite;
        this.mockFiles = new HashSet<MockFileDataHolder>();
        this.mockGlobalFunctions = new HashSet<MockGlobalFunctionHolder>();
        this.mockNodeFunctions = new HashSet<MockNodeFunctionHolder>();
        this.globalProperties = new HashMap<GlobalProperty, String>();
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public ArrayNode getExpectedAST() {
		return expectedAST;
	}

	public void setExpectedAST(ArrayNode expectedAST) {
		this.expectedAST = expectedAST;
	}

	public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public EvaluatorException getException() {
        return exception;
    }

    public void setException(EvaluatorException exception) {
        this.exception = exception;
    }

    public TestSuiteHolder getSuite() {
        return suite;
    }

    public Set<MockFileDataHolder> getMockFiles() {
        return mockFiles;
    }

    public Set<MockGlobalFunctionHolder> getMockGlobalFunctions() {
        return mockGlobalFunctions;
    }

    public Set<MockNodeFunctionHolder> getMockNodeFunctions() {
        return mockNodeFunctions;
    }

    public boolean addMockFile(MockFileDataHolder mockFile) {
        return this.mockFiles.add(mockFile);
    }

    public boolean addMockGlobalFunction(MockGlobalFunctionHolder mockFunction) {
        return this.mockGlobalFunctions.add(mockFunction);
    }

    public boolean addMockNodeFunction(MockNodeFunctionHolder mockFunction) {
        return this.mockNodeFunctions.add(mockFunction);
    }

    public void addGlobalProp(GlobalProperty property, String value) {
        globalProperties.put(property, value);
    }

    public void addGlobalProp(String name, String value) {
        GlobalProperty property = GlobalProperty.forName(name);
        if (property == null) {
            return;
        }
        globalProperties.put(property, value);
    }

    public Map<GlobalProperty, String> getGlobalProperties() {
        return globalProperties;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (suite != null) {
            result.append(suite.toString()).append("; ");
        } else {
            result.append("???????????").append("; ");
        }
        if (StringUtils.isNotEmpty(input)) {
            result.append("input: ").append("'").append(StringEscapeUtils.escapeJava(input)).append("'").append("; ");
        }
        if (StringUtils.isNotEmpty(expected)) {
            result.append("expected: ").append("'").append(expected).append("'").append("; ");
        }
        if (StringUtils.isNotEmpty(context)) {
            result.append("context: ").append("'").append(context).append("'").append("; ");
        }
        if (CollectionUtils.isNotEmpty(mockFiles)) {
            result.append("Mock files: {");
            for (MockFileDataHolder mockFile : mockFiles) {
                result.append(mockFile.toString()).append("; ");
            }
            result.append("}; ");
        }
        if (exception != null) {
            result.append("EvaluatorException {").append("lineNumber: ").append(exception.getLineNumber()).append("; ").append("expected: ").append(exception.getExpected()).append("; ").append("foud: ").append(exception.getFound()).append("}");
        }
        return result.toString();
    }
}
