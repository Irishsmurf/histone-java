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
package ru.histone.resourceloaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import ru.histone.GlobalProperty;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;

import static org.junit.Assert.assertEquals;

public class HistoneExternalResourcesTest {

    private HistoneBuilder builder;
    private String testResourceFullLocation;
    private String testResourceBaseHref;
    private String testResourceRelativePath;

    @Before
    public void before() throws HistoneException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        builder = new HistoneBuilder();
        builder.setGson(gson);

        testResourceRelativePath = "resourceloader/test.txt";
        testResourceFullLocation = this.getClass().getClassLoader().getResource(testResourceRelativePath).toString();

        testResourceBaseHref = testResourceFullLocation.substring(0, testResourceFullLocation.indexOf(testResourceRelativePath));
    }

    @Test
    public void testDefaultResourceLoaderSuccess() throws HistoneException {
        Histone histone = builder.build();
        String expected = "a test content b";
        String input = "a {{include('" + testResourceFullLocation + "')}} b";

        String result = histone.evaluate(input);
        assertEquals(expected, result);
    }

    @Test
    public void testDefaultResourceLoaderFailure() throws HistoneException {
        Histone histone = builder.build();
        String expected = "a  b";
        String input = "a {{include('unkownfolder/unknownfile.txt')}} b";

        String result = histone.evaluate(input);
        assertEquals(expected, result);
    }

    @Test
    public void testDefaultResourceLoaderRelativeLocationSuccess() throws HistoneException {
        builder.setGlobalProperty(GlobalProperty.BASE_URI, testResourceBaseHref);
        Histone histone = builder.build();
        String expected = "a test content b";
        String input = "a {{include('" + testResourceRelativePath + "')}} b";

        String result = histone.evaluate(input);
        assertEquals(expected, result);
    }

    @Test
    public void testDefaultResourceLoaderRelativeLocationFailure() throws HistoneException {
        builder.setGlobalProperty(GlobalProperty.BASE_URI, testResourceBaseHref);
        Histone histone = builder.build();
        String expected = "a  b";
        String input = "a {{include('unkownfolder/unknownfile.txt')}} b";

        String result = histone.evaluate(input);
        assertEquals(expected, result);
     }

    @Test
    public void testIncludeFromSubfolder() throws HistoneException {
        builder.setGlobalProperty(GlobalProperty.BASE_URI, testResourceBaseHref);
        Histone histone = builder.build();
        String expected = "a Z AAABBB X b";
        String input = "a {{include('resourceloader/template_for_include.tpl')}} b";

        String result = histone.evaluate(input);
        assertEquals(expected, result);
    }

    @Test
    public void testImportFromSubfolder() throws HistoneException {
        builder.setGlobalProperty(GlobalProperty.BASE_URI, testResourceBaseHref);
        Histone histone = builder.build();
        String expected = "a  b test macro result c";
        String input = "a {{import 'resourceloader/template_for_import.tpl'}} b {{test()}} c";

        String result = histone.evaluate(input);
        assertEquals(expected, result);
    }

}

