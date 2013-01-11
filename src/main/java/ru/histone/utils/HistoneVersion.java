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

package ru.histone.utils;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author sazonovkirill@gmail.com
 */
public class HistoneVersion {
    private static final Logger log = LoggerFactory.getLogger(HistoneVersion.class);
    public static String VERSION;

    private static final String VERSION_FILE_NAME = "version.properties";
    private static final String VERSION_KEY = "histone.version";

    static {
        Properties props = new Properties();
        try {
            props.load(HistoneVersion.class.getClassLoader().getResourceAsStream(VERSION_FILE_NAME));
        } catch (IOException e) {
            log.error("File version.properties not found");
        }

        VERSION = props.getProperty(VERSION_KEY);
    }

    public static void main(String[] args) {
        System.out.println(HistoneVersion.VERSION);
    }
}
