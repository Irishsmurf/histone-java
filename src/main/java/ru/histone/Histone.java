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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.deparser.Deparser;
import ru.histone.deparser.IDeparser;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.optimizer.*;
import ru.histone.parser.Parser;
import ru.histone.resourceloaders.ContentType;
import ru.histone.resourceloaders.Resource;
import ru.histone.resourceloaders.ResourceLoader;
import ru.histone.utils.IOUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Main Histone engine class. Histone template parsing/evaluation is done here.<br/>
 * Histone class is thread safe!<br/>
 * You shouldn't create this class by yourself, use {@link HistoneBuilder} instead.
 *
 * @see HistoneBuilder
 */
public class Histone {
    /**
     * General Histone logger. All debug information and general logging goes here
     */
    private static final Logger log = LoggerFactory.getLogger(Histone.class);

    /**
     * Special logger for histone template syntax errors
     */
    private static final Logger RUNTIME_LOG = LoggerFactory.getLogger(Histone.class.getName() + ".RUNTIME_LOG");

    /**
     * @deprecated (should be moved to GlobalProperties)
     */
    private static boolean devMode = false;

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
    private Simplifier simplifier;

    private final IDeparser deparser = new Deparser();

    public Histone(HistoneBootstrap bootstrap) {
        this.parser = bootstrap.getParser();
        this.evaluator = bootstrap.getEvaluator();
        this.nodeFactory = bootstrap.getNodeFactory();
        this.astImportResolver = bootstrap.getAstImportResolver();
        this.astMarker = bootstrap.getAstMarker();
        this.astOptimizer = bootstrap.getAstOptimizer();
        this.resourceLoader = bootstrap.getResourceLoader();
        this.constantFolding = bootstrap.getConstantFolding();
        this.constantPropagation = bootstrap.getConstantPropagation();
        this.constantIfCases = bootstrap.getConstantIfCases();
        this.uselessVariables = bootstrap.getUselessVariables();
        this.simplifier = bootstrap.getSimplifier();
    }

    public ArrayNode parseTemplateToAST(Reader templateReader) throws HistoneException {
        String inputString = null;
        try {
            inputString = IOUtils.toString(templateReader);
        } catch (IOException e) {
            log.error("Error reading input Reader", e);
            throw new HistoneException("Error reading input Reader", e);
        }
        return parser.parse(inputString);
    }

    public ArrayNode optimizeAST(ArrayNode templateAST) throws HistoneException {
        ArrayNode ast = astImportResolver.resolve(templateAST);
        long g1 = BaseOptimization.hash(ast);
        while (true) {
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = constantFolding.foldConstants(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                }
            }
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = constantPropagation.propagateConstants(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                }
            }
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = constantIfCases.replaceConstantIfs(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                }
            }
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = uselessVariables.removeUselessVariables(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                }
            }

            long g2 = BaseOptimization.hash(ast);
            if (g1 == g2) break;
        }

        ast = astMarker.mark(ast);
        ast = astOptimizer.optimize(ast);
        ast = simplifier.simplify(ast);
        return ast;
    }

    public ArrayNode optimizeASTWithTrace(ArrayNode templateAST, OptimizationTrace optimizationTrace) throws HistoneException {
        optimizationTrace.setOriginalAstAndSource(templateAST, deparser.deparse(templateAST));

        ArrayNode importsResolved = astImportResolver.resolve(templateAST);
        optimizationTrace.addFrame("ImportsResolving", importsResolved, deparser.deparse(importsResolved));

        ArrayNode ast = importsResolved;
        long g1 = BaseOptimization.hash(ast);
        while (true) {
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = constantFolding.foldConstants(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                    else {
                        optimizationTrace.addFrame("ConstantsFolding", ast, deparser.deparse(ast));
                    }
                }
            }
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = constantPropagation.propagateConstants(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                    else {
                        optimizationTrace.addFrame("ConstantsPropagation", ast, deparser.deparse(ast));
                    }
                }
            }
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = constantIfCases.replaceConstantIfs(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                    else {
                        optimizationTrace.addFrame("ConstantIfCases", ast, deparser.deparse(ast));
                    }
                }
            }
            {
                long h1 = BaseOptimization.hash(templateAST);
                while (true) {
                    ast = uselessVariables.removeUselessVariables(ast);
                    long h2 = BaseOptimization.hash(ast);
                    if (h1 == h2) break;
                    else {
                        optimizationTrace.addFrame("RemoveUselessVariables", ast, deparser.deparse(ast));
                    }
                }
            }

            long g2 = BaseOptimization.hash(ast);
            if (g1 == g2) break;
        }

        {
            long j1 = BaseOptimization.hash(ast);
            ast = astMarker.mark(ast);
            long j2 = BaseOptimization.hash(ast);
            if (j1 != j2) optimizationTrace.addFrame("AstMarker", ast, deparser.deparse(ast));
        }

        {
            long j1 = BaseOptimization.hash(ast);
            ast = astOptimizer.optimize(ast);
            long j2 = BaseOptimization.hash(ast);
            if (j1 != j2) optimizationTrace.addFrame("AstOptimizer", ast, deparser.deparse(ast));
        }

        {
            long j1 = BaseOptimization.hash(ast);
            ast = simplifier.simplify(ast);
            long j2 = BaseOptimization.hash(ast);
            if (j1 != j2) optimizationTrace.addFrame("AstSimplifier", ast, deparser.deparse(ast));
        }

        optimizationTrace.setProcessedAstAndSource(ast, deparser.deparse(ast));
        return ast;
    }

    public String evaluateAST(ArrayNode templateAST) throws HistoneException {
        return evaluateAST(null, templateAST, NullNode.instance);
    }

    public String evaluateAST(String baseURI, ArrayNode templateAST, JsonNode context) throws HistoneException {
        return evaluator.process(baseURI, templateAST, context);
    }

    public void evaluateAST(ArrayNode templateAST, Writer output) throws HistoneException {
        evaluateAST(null, templateAST, NullNode.instance, output);
    }

    public void evaluateAST(String baseURI, ArrayNode templateAST, Writer output) throws HistoneException {
        evaluateAST(baseURI, templateAST, NullNode.instance, output);
    }

    public void evaluateAST(ArrayNode templateAST, JsonNode context, Writer output) throws HistoneException {
        evaluateAST(null, templateAST, context, output);
    }

    public void evaluateAST(String baseURI, ArrayNode templateAST, JsonNode context, Writer output) throws HistoneException {
        String result = evaluateAST(baseURI, templateAST, context);
        try {
            output.write(result);
        } catch (IOException e) {
            throw new HistoneException("Error writing to output Writer", e);
        }
    }

    /**
     * Main function for Histone template evaluation.
     */
    public String evaluate(String baseURI, String templateContent, JsonNode context) throws HistoneException {
        ArrayNode ast = parser.parse(templateContent);
//        ArrayNode optimizedAst = optimizeAST(ast);
        return evaluator.process(baseURI, ast, context);
    }

    public ArrayNode evaluateAsAST(String baseURI, String templateContent, JsonNode context) throws HistoneException {
        return parser.parse(templateContent);
    }

    public String evaluateUri(String uri, JsonNode context) throws HistoneException {
        if (resourceLoader == null) throw new IllegalStateException("Resource loader is null for Histone instance");

        try {
            Resource resource = resourceLoader.load(uri, null, new String[]{ContentType.TEXT});
            String baseUri = resource.getBaseHref();

            InputStream is = resource.getInputStream();
            StringWriter sw = new StringWriter();
            IOUtils.copy(is, sw);
            String templateContent = sw.toString();
            return evaluate(baseUri, templateContent, context);
        } catch (IOException ioe) {
            throw new HistoneException(ioe);
        }
    }

    public String evaluate(String templateContent) throws HistoneException {
        return evaluate(null, templateContent, nodeFactory.jsonNull());
    }

    public String evaluate(String templateContent, JsonNode context) throws HistoneException {
        return evaluate(null, templateContent, context);
    }

    public String evaluate(Reader templateReader) throws HistoneException {
        return evaluate(null, templateReader, nodeFactory.jsonNull());
    }

    public String evaluate(String baseURI, Reader templateReader, JsonNode context) throws HistoneException {
        String templateContent = null;
        try {
            templateContent = IOUtils.toString(templateReader);
        } catch (IOException e) {
            throw new HistoneException("Error reading input Reader");
        }
        return evaluate(baseURI, templateContent, context);
    }

    public void evaluate(String baseURI, Reader templateReader, JsonNode context, Writer outputWriter) throws HistoneException {
        String result = evaluate(baseURI, templateReader, context);
        try {
            outputWriter.write(result);
        } catch (IOException e) {
            throw new HistoneException("Error writing to output Writer", e);
        }
    }

    public void setGlobalProperty(GlobalProperty property, String value) {
        evaluator.setGlobalProperty(property, value);
    }

    /**
     * Logs histone syntax error to special logger
     *
     * @param msg  message
     * @param e    exception
     * @param args arguments values that should be replaced in message
     */
    public static void runtime_log_error(String msg, Throwable e, Object... args) {
        RUNTIME_LOG.error(msg, args);

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // throw new HistoneException();

    }

    /**
     * Logs histone syntax info to special logger
     *
     * @param msg  message
     * @param args arguments values that should be replaced in message
     */
    public static void runtime_log_info(String msg, Object... args) {
        if (devMode) {
            runtime_log_error(msg, null, args);
        } else {
            RUNTIME_LOG.info(msg, args);
        }
    }

    /**
     * Logs histone syntax warning to special logger
     *
     * @param msg  message
     * @param args arguments values that should be replaced in message
     */

    public static void runtime_log_warn(String msg, Object... args) {
        if (devMode) {
            runtime_log_error(msg, null, args);
        } else {
            RUNTIME_LOG.warn(msg, args);
        }
    }

    /**
     * Logs histone syntax error to special logger
     *
     * @param msg  message
     * @param e    exception
     * @param args arguments values that should be replaced in message
     */

    public static void runtime_log_warn_e(String msg, Throwable e, Object... args) {
        if (devMode) {
            runtime_log_error(msg, e, args);
        } else {
            RUNTIME_LOG.warn(msg, e, args);
        }
    }
}
