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
package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;

public class AdditionalDataForOptimizationDebug {
    private String templateLocation;
    private JsonNode evaluationContext;
    private String originalOutput;

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public JsonNode getEvaluationContext() {
        return evaluationContext;
    }

    public void setEvaluationContext(JsonNode evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    public String getOriginalOutput() {
        return originalOutput;
    }

    public void setOriginalOutput(String originalOutput) {
        this.originalOutput = originalOutput;
    }
}
