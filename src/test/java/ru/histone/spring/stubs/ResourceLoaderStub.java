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
package ru.histone.spring.stubs;

import ru.histone.evaluator.nodes.Node;
import ru.histone.resourceloaders.Resource;
import ru.histone.resourceloaders.ResourceLoadException;
import ru.histone.resourceloaders.ResourceLoader;
import ru.histone.resourceloaders.StreamResource;

import java.io.ByteArrayInputStream;

public class ResourceLoaderStub implements ResourceLoader {
    @Override
    public Resource load(String href, String baseHref, Node... args) throws ResourceLoadException {
        if (href.equals("dummyA:/text.txt")) {
            return new StreamResource(new ByteArrayInputStream("resolver_a_result".getBytes()), "dummyA:/text.txt");
        } else if (href.equals("dummyB:/text.txt")) {
            return new StreamResource(new ByteArrayInputStream("resolver_b_result".getBytes()), "dummyB:/text.txt");
        } else {
            throw new ResourceLoadException("Resource nto found");
        }
    }

    @Override
    public String resolveFullPath(String href, String baseHref) throws ResourceLoadException {
        return href;
    }
}
