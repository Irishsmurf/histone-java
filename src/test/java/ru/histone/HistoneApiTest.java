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
package ru.histone;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;
import ru.histone.resourceloaders.DefaultResourceLoader;
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

/**
 * User: sazonovkirill@gmail.com
 * Date: 04.12.12
 */
public class HistoneApiTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private HistoneBuilder builder;
    private String resourcesFolderPath;

    @Before
    public void init() {
        builder = new HistoneBuilder();
        builder.setResourceLoader(new DefaultResourceLoader());

        String relativePath = "relative_urls/template.tpl";
        URL resource = this.getClass().getClassLoader().getResource(relativePath);
        Assert.notNull(resource);

        String fullPath = resource.toString();
        resourcesFolderPath = fullPath.substring(0, fullPath.indexOf(relativePath));
    }

    @Test
    public void relativeUrls() throws HistoneException, IOException {
        Histone histone = builder.build();
        String result = histone.evaluateURI(resourcesFolderPath + "relative_urls/template.tpl", objectMapper.createObjectNode());
        String expected = readFileFromClasspath("relative_urls/expected.txt");

        assertEquals(expected.trim(), result.trim());
    }

    private String readFileFromClasspath(String uri) throws IOException {
        StringWriter sw = new StringWriter();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(uri);
        IOUtils.copy(is, sw);
        return sw.toString();
    }
}
