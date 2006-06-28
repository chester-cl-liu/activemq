/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.tool.spi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.ConnectionFactory;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class ClassLoaderSPIConnectionFactory implements SPIConnectionFactory {
    private static final Log log = LogFactory.getLog(ClassLoaderSPIConnectionFactory.class);

    public static final String KEY_EXT_DIR = "extDir";

    public final ConnectionFactory createConnectionFactory(Properties settings) throws Exception {

        // Load new context class loader
        ClassLoader newClassLoader = getContextClassLoader(settings);
        Thread.currentThread().setContextClassLoader(newClassLoader);

        return instantiateConnectionFactory(newClassLoader, settings);
    }

    protected ClassLoader getContextClassLoader(Properties settings) {
        String extDir = (String)settings.remove(KEY_EXT_DIR);
        if (extDir != null) {
            StringTokenizer tokens = new StringTokenizer(extDir, ";,");
            List urls = new ArrayList();
            while (tokens.hasMoreTokens()) {
                String dir = tokens.nextToken();
                try {
                    File f = new File(dir);
                    dir = f.getAbsolutePath();
                    System.out.println(dir);
                    urls.add(f.toURL());

                    File[] files = f.listFiles();
                    if( files!=null ) {
                        for (int j = 0; j < files.length; j++) {
                            if( files[j].getName().endsWith(".zip") || files[j].getName().endsWith(".jar") ) {
                                dir = files[j].getAbsolutePath();
                                urls.add(files[j].toURL());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to load ext dir: " + dir + ". Reason: " + e);
                }
            }

            URL u[] = new URL[urls.size()];
            urls.toArray(u);
            return new URLClassLoader(u, ClassLoaderSPIConnectionFactory.class.getClassLoader());
        }
        return ClassLoaderSPIConnectionFactory.class.getClassLoader();
    }

    protected abstract ConnectionFactory instantiateConnectionFactory(ClassLoader cl, Properties settings) throws Exception;
}
