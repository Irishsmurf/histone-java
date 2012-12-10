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
package ru.histone.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import ru.histone.acceptance.support.AcceptanceTest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.fail;

public class AllAcceptanceTestFilesCoveredCheckTest {
    private ObjectMapper jackson;
    private Set<String> testFilesInJson;

    @Before
    public void before() throws IllegalAccessException, InstantiationException, IOException {
        jackson = new ObjectMapper();
        testFilesInJson = new HashSet<String>();

        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/acceptance-test-cases.json"));
        JsonNode testCasesList = jackson.readTree(reader);
        Iterator<JsonNode> iter = testCasesList.iterator();
        while (iter.hasNext()) {
            String testFileName = iter.next().asText();
            testFilesInJson.add(testFileName);
        }
    }

    @Test
    public void testAllTestFilesCovered() throws IOException, IllegalAccessException, InstantiationException {
        Set<Class<?>> wrongTestClasses = new HashSet<Class<?>>();

        Set<Class<? extends AcceptanceTest>> testClasses = findAcceptanceTestClasses();
        for (Class<?> clazz : testClasses) {
            AcceptanceTest instance = (AcceptanceTest) clazz.newInstance();
            String fileName = instance.getFileName();

            if (!clazz.getName().endsWith("Test") || fileName == null || fileName.length() == 0) {
                wrongTestClasses.add(clazz);
            } else if (fileName.charAt(0) == '/') {
                fileName = fileName.substring(1);

                if (testFilesInJson.contains(fileName)) {
                    testFilesInJson.remove(fileName);
                } else {
                    wrongTestClasses.add(clazz);
                }
            }
        }

        if ((testFilesInJson.size() > 0) || (wrongTestClasses.size() > 0)) {
            //Check for absent files in test classes
            StringBuilder sbUncoveredJsonFiles = new StringBuilder("Following test files aren't covered by test classes: ");
            for (String fileName : testFilesInJson) {
                sbUncoveredJsonFiles.append(fileName).append(", ");
            }

            StringBuilder sbWrongTestClasses = new StringBuilder("Following test classes has wrong fileNames: ");
            for (Class<?> wrongClass : wrongTestClasses) {
                sbWrongTestClasses.append(wrongClass.getName()).append(", ");
            }

            StringBuilder sb = new StringBuilder("You have following errors: \n");
            if (testFilesInJson.size() > 0) {
                sb.append(sbUncoveredJsonFiles.toString().substring(0, sbUncoveredJsonFiles.length() - 2)).append('\n');
            }
            if (wrongTestClasses.size() > 0) {
                sb.append(sbWrongTestClasses.toString().substring(0, sbWrongTestClasses.length() - 2)).append('\n');
            }

            fail(sb.toString());
        }
    }

    private Set<Class<? extends AcceptanceTest>> findAcceptanceTestClasses() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forPackage(AcceptanceTest.class.getPackage().getName()))
                        .setScanners(new SubTypesScanner()));

        return reflections.getSubTypesOf(AcceptanceTest.class);
    }

}
