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

public class MockNodeFunctionHolder {
    private String name;
    private String nodeType;
    private String returnType;
    private String data;
    private boolean exception;


    public MockNodeFunctionHolder(String name, String nodeType, String returnType, String data, boolean exception) {
        this.name = name;
        this.nodeType = nodeType;
        this.returnType = returnType;
        this.data = data;
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public boolean isException() {
        return exception;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
