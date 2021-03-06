/*
 * Copyright 2009-2010 Wolfgang Schell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jetztgrad.groovy.osgi.runtime.external

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The external runtime does not provide a {@link BundleContext} and can
 * not be started or stopped. Installing bundles works via the dropins folder.
 * 
 * @author Wolfgang Schell
 */
class ExternalRuntime extends AbstractOsgiRuntime {
	ExternalRuntime(Map runtimeProperties) {
		super(runtimeProperties)
	}

	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#doStart()
	 */
	@Override
	public BundleContext doStart() {
		throw new RuntimeException("the runtime can not be started")
	}
	
	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#canStart()
	 */
	@Override
	public boolean canStart() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#doStop()
	 */
	@Override
	public void doStop() {
		throw new RuntimeException("the runtime can not be stopped")
	}
	
	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#canStop()
	 */
	@Override
	public boolean canStop() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#isRunning()
	 */
	@Override
	public boolean isRunning() {
		// assume the runtime is always running
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#install(java.io.File, boolean)
	 */
	@Override
	public Bundle install(File bundleFile, boolean autoStart) {
		if (!bundleFile.exists()
			|| !bundleFile.isFile()) {
			throw new IllegalArgumentException("invalid bundle file: " + bundleFile)
		}
		
		File dropinsDir = getDropinsDir()
		if ((dropinsDir == null)
			|| !dropinsDir.exists()
			|| !dropinsDir.isDirectory()) {
			throw new IllegalArgumentException("invalid dropins directory: " + dropinsDir)
		}
		// copy file into dropins folder
		File targetFile = new File(dropinsDir, bundleFile.name)
		targetFile << bundleFile.newInputStream()
		
		// we have no bundle instance
		return null
	}
	
	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#install(java.io.InputStream, boolean)
	 */
	@Override
	public Bundle install(InputStream stream, boolean autoStart) {
		// TODO not yet supported
		if (stream) {
			// close quietly
			try {
				stream.close()
			}
			catch (Throwable t) {
				// ignore
			}
		}
		
		return null
	}
	
	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#install(java.lang.String, boolean)
	 */
	@Override
	public Bundle install(String bundleFile, boolean autoStart) {
		// remove file prefix
		if (bundleFile.startsWith("file:")) {
			bundleFile -= "file:"
		}
		File file = new File(bundleFile)
		return install(file, autoStart);
	}
	
	/* (non-Javadoc)
	 * @see net.jetztgrad.groovy.osgi.runtime.AbstractOsgiRuntime#install(java.net.URL, boolean)
	 */
	@Override
	public Bundle install(URL url, boolean autoStart) {
		if (url.getProtocol() == "file") {
			File file = new File(url.getFile())
			return install(file, autoStart)
		}
		else {
			return install(url.openStream(), autoStart)
		}
	}
}

