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

/**
 * @author P.Salnikov <p.salnikov@gmail.com>
 */
public class ResourceLoaderSubtype_B implements ResourceLoader {
//    private static final Resource resource = new Resource() {
//        private final URI uri = URI.create("dummyB://stubresource");
//
//        @Override
//        public InputStream getInputStream() throws IOException {
//            return new ByteArrayInputStream("resolver_b_result".getBytes());
//        }
//
//        @Override
//        public URI getURI() {
//            return uri;
//        }
//
//        @Override
//        public void close() throws IOException {
//            //do nothing
//        }
//    };
//
//    @Override
//    public String getScheme() {
//        return "dummyB";
//    }
//
//    @Override
//    public Boolean isCacheable() {
//        return true;
//    }
//
//    @Override
//    public Resource load(URI location, Node... args) throws ResourceLoadException {
//        return resource;
//    }

    @Override
    public Resource load(String href, String baseHref, String[] contentTypes, Node... args) throws ResourceLoadException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String resolveFullPath(String href, String baseHref) throws ResourceLoadException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

