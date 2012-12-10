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

import ru.histone.resourceloaders.Resource;

import java.io.IOException;
import java.io.InputStream;

public class MockResource implements Resource {
//    private MockFileDataHolder dataHolder;
//
//    public MockResource(MockFileDataHolder dataHolder) {
//        this.dataHolder = dataHolder;
//    }
//
//    @Override
//    public InputStream getInputStream() throws IOException {
//        return new ByteArrayInputStream(dataHolder.getData().getBytes());
//    }
//
//    @Override
//    public URI getURI() {
//        return dataHolder.getLocation();
//    }
//
//    @Override
//    public void close() throws IOException {
//        //do nothing
//    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getBaseHref() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
