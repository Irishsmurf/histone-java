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

import ru.histone.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Histone has predefined list of global properties whose values could be changed.<br/>
 * For more details see Histone Manual
 */
public enum GlobalProperty {
    /**
     * Base URI for histone resource loaders
     */
    BASE_URI("baseURI"),

    /**
     * Cline type for current context: client (javascript), server(java,php)
     */
    CLIENT_TYPE("clientType"),

    /**
     * User agent (if any) for current context (subject ot change, currently HTTP User-Agent header is used as it is)
     */
    USER_AGENT("userAgent");

    private String name;

    private GlobalProperty(String name) {
        this.name = name;
        GlobalProperties.register(this);
    }

    /**
     * Returns string representation for current global property
     *
     * @return string representation for current global property
     */
    public String getName() {
        return name;
    }

    /**
     * Finds enum representation for global property by it's string name
     *
     * @param name
     * @return enum representation of global property
     */
    public static GlobalProperty forName(String name) {
        return GlobalProperties.forName(name);
    }

    private static class GlobalProperties {
        private static final Map<String, GlobalProperty> CACHE = new HashMap<String, GlobalProperty>();

        private GlobalProperties() {
        }

        static void register(GlobalProperty property) {
            if (property == null || StringUtils.isEmpty(property.getName())) {
                return;
            }
            CACHE.put(property.getName(), property);
        }

        static GlobalProperty forName(String name) {
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            return CACHE.get(name);
        }
    }
}
