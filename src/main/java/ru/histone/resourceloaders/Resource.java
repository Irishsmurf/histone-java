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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * All resources loaded via resource loaders should be accessible via this interface
 */
public interface Resource extends Closeable {
    /**
     * <p>It is expected that each call creates a <i>fresh</i> stream.
     * <p>This requirement is particularly important when you consider an API such
     * as JavaMail, which needs to be able to read the stream multiple times when
     * creating mail attachments. For such a use case, it is <i>required</i>
     * that each <code>getInputStream()</code> call returns a fresh stream.
     *
     * @return {@link java.io.InputStream}.
     * @throws java.io.IOException if the stream could not be opened
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Return a base href for this resource.
     *
     * @return base href value
     */
    public String getBaseHref();
}
