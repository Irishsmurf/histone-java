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

public enum OptimizationTypes {
    CONSTANTS_SUBSTITUTION,
    SAFE_CODE_EVALUATION,
    SAFE_CODE_MARKER,
    IMPORT_RESOLVING,
    USELESS_VARIABLES,
    MACRO_EXPANDING,
    FOR_LOOP_EXPANDING,
    INLINE_MACRO,


    FRAGMENT_CONCATENATION,
    ELIMINATE_SINGLE_NODE;
}
