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

/**
 * Allows to enable/disable different steps of optimization.
 */
public class OptimizationProfile {
    private boolean useImportsResolving = false;
    private boolean useConstantsFolding = false;
    private boolean useConstantPropagation = false;
    private boolean removeConstantIfCases = false;
    private boolean removeUselessVariables = false;
    private boolean useAstOptimizer = false;
    private boolean useAstSimplifier = false;

    public boolean getUseOptimizationCycle() {
        return useConstantsFolding ||
                useConstantPropagation ||
                removeConstantIfCases ||
                removeUselessVariables;
    }

    //<editor-fold desc="Trivial getters/setters">
    public boolean isUseImportsResolving() {
        return useImportsResolving;
    }

    public void setUseImportsResolving(boolean useImportsResolving) {
        this.useImportsResolving = useImportsResolving;
    }

    public boolean isUseConstantsFolding() {
        return useConstantsFolding;
    }

    public void setUseConstantsFolding(boolean useConstantsFolding) {
        this.useConstantsFolding = useConstantsFolding;
    }

    public boolean isUseConstantPropagation() {
        return useConstantPropagation;
    }

    public void setUseConstantPropagation(boolean useConstantPropagation) {
        this.useConstantPropagation = useConstantPropagation;
    }

    public boolean isRemoveConstantIfCases() {
        return removeConstantIfCases;
    }

    public void setRemoveConstantIfCases(boolean removeConstantIfCases) {
        this.removeConstantIfCases = removeConstantIfCases;
    }

    public boolean isRemoveUselessVariables() {
        return removeUselessVariables;
    }

    public void setRemoveUselessVariables(boolean removeUselessVariables) {
        this.removeUselessVariables = removeUselessVariables;
    }

    public boolean isUseAstOptimizer() {
        return useAstOptimizer;
    }

    public void setUseAstOptimizer(boolean useAstOptimizer) {
        this.useAstOptimizer = useAstOptimizer;
    }

    public boolean isUseAstSimplifier() {
        return useAstSimplifier;
    }

    public void setUseAstSimplifier(boolean useAstSimplifier) {
        this.useAstSimplifier = useAstSimplifier;
    }
    //</editor-fold>
}
