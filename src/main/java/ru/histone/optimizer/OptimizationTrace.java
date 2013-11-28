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
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains some debug information about optimization of histone template AST.
 */
public class OptimizationTrace {
    /**
     * Initial AST.
     */
    private JsonNode originalAst;

    /**
     * Deparsed {@link #originalAst}.
     */
    private String originalSource;

    /**
     * Optimized AST.
     */
    private JsonNode processedAst;

    /**
     * Deparsed {@link #processedAst}.
     */
    private String processedSource;

    /**
     * Snapshots of AST on each stage of optimization.
     */
    private List<Frame> frames = new ArrayList<Frame>();

    //<editor-fold desc="Trivial getters/setters">
    /**
     * Initial AST.
     */
    public JsonNode getOriginalAst() {
        return originalAst;
    }

    /**
     * Initial AST.
     */
    public void setOriginalAst(JsonNode originalAst) {
        this.originalAst = originalAst;
    }

    /**
     * Deparsed {@link #originalAst}.
     */
    public String getOriginalSource() {
        return originalSource;
    }

    /**
     * Deparsed {@link #originalAst}.
     */
    public void setOriginalSource(String originalSource) {
        this.originalSource = originalSource;
    }

    /**
     * Optimized AST.
     */
    public JsonNode getProcessedAst() {
        return processedAst;
    }

    /**
     * Optimized AST.
     */
    public void setProcessedAst(JsonNode processedAst) {
        this.processedAst = processedAst;
    }

    /**
     * Deparsed {@link #processedAst}.
     */
    public String getProcessedSource() {
        return processedSource;
    }

    /**
     * Deparsed {@link #processedAst}.
     */
    public void setProcessedSource(String processedSource) {
        this.processedSource = processedSource;
    }

    /**
     * Snapshots of AST on each stage of optimization.
     */
    public List<Frame> getFrames() {
        return frames;
    }

    /**
     * Snapshots of AST on each stage of optimization.
     */
    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }
    //</editor-fold>

    /**
     * Set original template data: AST and deparsed source.
     */
    public void setOriginalAstAndSource(ArrayNode ast, String source) {
        this.originalAst = ast;
        this.originalSource = source;
    }

    /**
     * Set processed template data: AST and deparsed source.
     */
    public void setProcessedAstAndSource(ArrayNode ast, String source) {
        this.processedAst = ast;
        this.processedSource = source;
    }

    /**
     * Frame is one step of optimization procedure. It allows to save state of AST at each step.
     */
    public static class Frame {
        /**
         * Name of current stage of optimization.
         */
        private String name;

        /**
         * AST after this stage.
         */
        private JsonNode processedAst;

        /**
         * Deparsed {@link #processedAst}
         */
        private String processedSource;

        /**
         * Returns true if template output after this stage differs from origin output.
         */
        private boolean didBrokeCompability = false;

        /**
         * Evaluation time of the AST after this stage.
         */
        private long evaluationTimeAfterThisStep = 0;

        /**
         * AST length after this stage.
         */
        private long astLengthAfterThisStep = 0;

        //<editor-fold desc="Trivial getters/setters">
        /**
         * AST after this stage.
         */
        public JsonNode getProcessedAst() {
            return processedAst;
        }

        /**
         * AST after this stage.
         */
        public void setProcessedAst(JsonNode processedAst) {
            this.processedAst = processedAst;
        }

        /**
         * Deparsed {@link #processedAst}
         */
        public String getProcessedSource() {
            return processedSource;
        }

        /**
         * Deparsed {@link #processedAst}
         */
        public void setProcessedSource(String processedSource) {
            this.processedSource = processedSource;
        }

        /**
         * Name of current stage of optimization.
         */
        public String getName() {
            return name;
        }

        /**
         * Name of current stage of optimization.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Returns true if template output after this stage differs from origin output.
         */
        public boolean isDidBrokeCompability() {
            return didBrokeCompability;
        }

        /**
         * Returns true if template output after this stage differs from origin output.
         */
        public void setDidBrokeCompability(boolean didBrokeCompability) {
            this.didBrokeCompability = didBrokeCompability;
        }

        /**
         * Evaluation time of the AST after this stage.
         */
        public long getEvaluationTimeAfterThisStep() {
            return evaluationTimeAfterThisStep;
        }

        /**
         * Evaluation time of the AST after this stage.
         */
        public void setEvaluationTimeAfterThisStep(long evaluationTimeAfterThisStep) {
            this.evaluationTimeAfterThisStep = evaluationTimeAfterThisStep;
        }

        /**
         * AST length after this stage.
         */
        public long getAstLengthAfterThisStep() {
            return astLengthAfterThisStep;
        }

        /**
         * AST length after this stage.
         */
        public void setAstLengthAfterThisStep(long astLengthAfterThisStep) {
            this.astLengthAfterThisStep = astLengthAfterThisStep;
        }
        //</editor-fold>
    }
}
