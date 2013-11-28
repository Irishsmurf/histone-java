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
package ru.histone.acceptance.support;

import java.net.URI;

public class MockFileDataHolder {
    private URI location;
    private String data;

    public MockFileDataHolder(String location, String data) {
        this.location = URI.create(location);
        this.data = data;
    }

    public URI getLocation() {
        return location;
    }

    public String getScheme() {
        return location.getScheme();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("MockFile {").append("location: '").append(location).append("'; ").append("data: '").append(data).append("'}");
        return result.toString();
    }
}
