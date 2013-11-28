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
package ru.histone.resourceloaders;

import ru.histone.evaluator.nodes.Node;

/**
 * Resource loader interface<br/>
 * Default Histone resource loader implements this interface and supports file: and http: protocols<br/>
 * If you want to implement your own custom resource loader, then implement this interface. If you want to extend default resource loaer, then you should override DefaultResourceLoader class and make calls to super methods.
 */
public interface ResourceLoader {
    /**
     * Returns if resource loader resources are cacheable (e.g. histone tpl files from file system)<br/>
     * This method is needed for AST optimizer only. It checks if we can make include/import inline, or we should keep include/import instructions untouchable.
     *
     * @return true if resource could be cached
     * @throws ResourceLoadException if errors occur
     */
    // TODO: We need to implement some algorithm to reload cache or check if cached resource should be loaded from resource loader again
//    public Boolean isCacheable(String href, String baseHref) throws ResourceLoadException;

    /**
     * Load resource using specified href, baseHref and arguments
     *
     * @param href         resource location
     * @param baseHref     base href for loading
     * @param contentTypes types of resources
     * @param args         additional custom arguments for resource loader
     * @return resource object
     * @throws ResourceLoadException if errors occur
     */
    public Resource load(String href, String baseHref, String[] contentTypes, Node... args) throws ResourceLoadException;

    /**
     * Return full path for specified resource href and base href
     *
     * @param href     resource href
     * @param baseHref base href
     * @return full path to resource
     * @throws ResourceLoadException in case of any errors
     */
    public String resolveFullPath(String href, String baseHref) throws ResourceLoadException;

}
