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
package ru.histone.acceptance.support;

import ru.histone.evaluator.nodes.Node;
import ru.histone.resourceloaders.Resource;
import ru.histone.resourceloaders.ResourceLoadException;
import ru.histone.resourceloaders.ResourceLoader;

public class MockResourceLoader implements ResourceLoader {
//    private String name;
//    private Map<URI, MockFileDataHolder> mockData;
//
//    public MockResourceLoader(String name, Map<URI, MockFileDataHolder> mockData) {
//        this.name = name;
//        this.mockData = mockData;
//    }
//
//    @Override
//    public String getScheme() {
//        return name;
//    }
//
//    @Override
//    public Boolean isCacheable() {
//        return true;
//    }
//
//
//    @Override
//    public Resource load(URI location, Node... args) throws ResourceLoadException {
//        MockFileDataHolder dataHolder = mockData.get(location);
//
//        if (dataHolder == null) {
//            throw new ResourceLoadException(String.format("No resource found by location = '%s'", location));
//        }
//
//        if (dataHolder.getData().contains(":exception:")) {
//            throw new RuntimeException("Loader exception");
//        }
//
//        if (dataHolder.getData().contains("args")) {
//            StringBuilder sb = new StringBuilder();
//            sb.append('[');
//            boolean addSeparator = false;
//            for (Node arg : args) {
//                if (addSeparator) {
//                    sb.append('-');
//                }
//                sb.append(arg.getAsString());
//                addSeparator = true;
//            }
//            sb.append(']');
//
//            dataHolder.setData(dataHolder.getData().replace(":args:", sb.toString()));
//        }
//
//        return new MockResource(dataHolder);
//    }

    @Override
    public Resource load(String href, String baseHref, Node... args) throws ResourceLoadException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String resolveFullPath(String href, String baseHref) throws ResourceLoadException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
