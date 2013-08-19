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
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class OptimizationTrace {
    private JsonNode originalAst;
    private String originalSource;

    private JsonNode processedAst;
    private String processedSource;

    private List<Frame> frames = new ArrayList<Frame>();

    public JsonNode getOriginalAst() {
        return originalAst;
    }

    public void setOriginalAst(JsonNode originalAst) {
        this.originalAst = originalAst;
    }

    public String getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(String originalSource) {
        this.originalSource = originalSource;
    }

    public JsonNode getProcessedAst() {
        return processedAst;
    }

    public void setProcessedAst(JsonNode processedAst) {
        this.processedAst = processedAst;
    }

    public String getProcessedSource() {
        return processedSource;
    }

    public void setProcessedSource(String processedSource) {
        this.processedSource = processedSource;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }

    public void setOriginalAstAndSource(ArrayNode ast, String source) {
        this.originalAst = ast;
        this.originalSource = source;
    }

    public void setProcessedAstAndSource(ArrayNode ast, String source) {
        this.processedAst = ast;
        this.processedSource = source;
    }

    public Frame addFrame(String frameName, ArrayNode ast, String source) {
        Frame frame = new Frame();
        frame.setName(frameName);
        frame.setProcessedAst(ast);
        frame.setProcessedSource(source);
        frames.add(frame);
        return frame;
    }

    public static class Frame {
        private String name;

        private JsonNode processedAst;
        private String processedSource;

        private boolean didBrokeCompability = false;
        private long evaluationTimeAfterThisStep = 0;

        public JsonNode getProcessedAst() {
            return processedAst;
        }

        public void setProcessedAst(JsonNode processedAst) {
            this.processedAst = processedAst;
        }

        public String getProcessedSource() {
            return processedSource;
        }

        public void setProcessedSource(String processedSource) {
            this.processedSource = processedSource;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isDidBrokeCompability() {
            return didBrokeCompability;
        }

        public void setDidBrokeCompability(boolean didBrokeCompability) {
            this.didBrokeCompability = didBrokeCompability;
        }

        public long getEvaluationTimeAfterThisStep() {
            return evaluationTimeAfterThisStep;
        }

        public void setEvaluationTimeAfterThisStep(long evaluationTimeAfterThisStep) {
            this.evaluationTimeAfterThisStep = evaluationTimeAfterThisStep;
        }
    }
}
