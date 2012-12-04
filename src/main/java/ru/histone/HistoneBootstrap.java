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
package ru.histone;

import com.fasterxml.jackson.core.JsonFactory;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.optimizer.AstImportResolver;
import ru.histone.optimizer.AstInlineOptimizer;
import ru.histone.optimizer.AstMarker;
import ru.histone.optimizer.AstOptimizer;
import ru.histone.parser.Parser;
import ru.histone.resourceloaders.ResourceLoader;

public class HistoneBootstrap {
    private Parser parser;
    private Evaluator evaluator;
    private NodeFactory nodeFactory;
    private AstOptimizer astAstOptimizer;
    private AstImportResolver astImportResolver;
    private AstMarker astMarker;
    private AstInlineOptimizer astInlineOptimizer;
    private ResourceLoader resourceLoader;

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public AstOptimizer getAstAstOptimizer() {
        return astAstOptimizer;
    }

    public void setAstAstOptimizer(AstOptimizer astAstOptimizer) {
        this.astAstOptimizer = astAstOptimizer;
    }

    public AstImportResolver getAstImportResolver() {
        return astImportResolver;
    }

    public void setAstImportResolver(AstImportResolver astImportResolver) {
        this.astImportResolver = astImportResolver;
    }

    public AstMarker getAstMarker() {
        return astMarker;
    }

    public void setAstMarker(AstMarker astMarker) {
        this.astMarker = astMarker;
    }

    public AstInlineOptimizer getAstInlineOptimizer() {
        return astInlineOptimizer;
    }

    public void setAstInlineOptimizer(AstInlineOptimizer astInlineOptimizer) {
        this.astInlineOptimizer = astInlineOptimizer;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
