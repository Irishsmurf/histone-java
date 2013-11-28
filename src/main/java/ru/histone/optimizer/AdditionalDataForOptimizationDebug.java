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
package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Contains some data, required for tracing histone optimization
 */
public class AdditionalDataForOptimizationDebug {
    /**
     * Location of source template.
     */
    private String templateLocation;

    /**
     * Histone context for evaluation of the template.
     */
    private JsonNode evaluationContext;

    /**
     * Output, generated with NOT optimized AST.
     */
    private String originalOutput;

    //<editor-fold desc="Trivial getters/setters">
    /**
     * Output, generated with NOT optimized AST.
     */
    public String getTemplateLocation() {
        return templateLocation;
    }

    /**
     * Output, generated with NOT optimized AST.
     */
    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    /**
     * Histone context for evaluation of the template.
     */
    public JsonNode getEvaluationContext() {
        return evaluationContext;
    }

    /**
     * Histone context for evaluation of the template.
     */
    public void setEvaluationContext(JsonNode evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    /**
     * Output, generated with NOT optimized AST.
     */
    public String getOriginalOutput() {
        return originalOutput;
    }

    /**
     * Output, generated with NOT optimized AST.
     */
    public void setOriginalOutput(String originalOutput) {
        this.originalOutput = originalOutput;
    }
    //</editor-fold>
}
