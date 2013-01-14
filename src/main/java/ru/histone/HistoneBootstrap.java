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

import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.optimizer.*;
import ru.histone.parser.Parser;
import ru.histone.resourceloaders.ResourceLoader;

public class HistoneBootstrap {
    private Parser parser;
    private Evaluator evaluator;
    private NodeFactory nodeFactory;
    private AstOptimizer astOptimizer;
    private AstImportResolver astImportResolver;
    private AstMarker astMarker;
    private ResourceLoader resourceLoader;

    // Optimizers
    private ConstantFolding constantFolding;
    private ConstantPropagation constantPropagation;
    private ConstantIfCases constantIfCases;
    private UselessVariables uselessVariables;

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

    public AstOptimizer getAstOptimizer() {
        return astOptimizer;
    }

    public void setAstOptimizer(AstOptimizer astOptimizer) {
        this.astOptimizer = astOptimizer;
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

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ConstantFolding getConstantFolding() {
        return constantFolding;
    }

    public void setConstantFolding(ConstantFolding constantFolding) {
        this.constantFolding = constantFolding;
    }

    public ConstantPropagation getConstantPropagation() {
        return constantPropagation;
    }

    public void setConstantPropagation(ConstantPropagation constantPropagation) {
        this.constantPropagation = constantPropagation;
    }

    public ConstantIfCases getConstantIfCases() {
        return constantIfCases;
    }

    public void setConstantIfCases(ConstantIfCases constantIfCases) {
        this.constantIfCases = constantIfCases;
    }

    public UselessVariables getUselessVariables() {
        return uselessVariables;
    }

    public void setUselessVariables(UselessVariables uselessVariables) {
        this.uselessVariables = uselessVariables;
    }
}


