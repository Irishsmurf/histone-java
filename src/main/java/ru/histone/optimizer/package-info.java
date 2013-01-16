/**
 * This package contains out first attempt to implement AST optimization algorithms. They all are intended to reduce
 * amount size of AST tree (count of AST nodes) for consequent reduce of memory consumption and evaluation time.
 *
 * <p>The basic function for optimizing AST is {@link ru.histone.Histone#optimizeAST(com.fasterxml.jackson.databind.node.ArrayNode)}.
 *
 * <p>This implementation of AST optimizer is (rather) experimental, and not intended for use in production mode (thought all Hstone
 * acceptance tests are passed). Some of implemented algorithms are implemented in draft way (e.g. not so efficient or productive as could be).
 * We also assume that it doesn't necessary to reduce time of optimization execution (due to both parsing and optimization have to be
 * executed only once, at the first access to the template file).
 *
 * <p>Implemented algorithms are listed below:
 * <ul compact>
 * <li>Constant folding ({@link ConstantFolding})</li>
 * <li>Constant propagation ({@link ConstantPropagation})</li>
 * <li>Replacement of if cases with constant conditions ({@link ConstantIfCases})</li>
 * <li>Resolving imported templates ({@link AstImportMarker}, {@link AstImportResolver})</li>
 * <li>Pre-evaluation of constant branches of AST ({@link AstMarker},{@link AstOptimizer})</li>
 * </ul>
 *
 * Almost all optimization units, that are listed, are inherited from {@link BaseOptimization}.
 */
package ru.histone.optimizer;