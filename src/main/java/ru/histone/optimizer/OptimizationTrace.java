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

    public void addFrame(String frameName, ArrayNode ast, String source) {
        Frame frame = new Frame();
        frame.setName(frameName);
        frame.setProcessedAst(ast);
        frame.setProcessedSource(source);
        frames.add(frame);
    }

    public static class Frame {
        private String name;

        private JsonNode processedAst;
        private String processedSource;

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
    }
}
