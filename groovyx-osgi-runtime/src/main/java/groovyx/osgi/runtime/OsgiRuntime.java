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

package groovyx.osgi.runtime;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public interface OsgiRuntime {
	/**
	 * Start OSGi framework.
	 * 
	 * @return root bundle context
	 */
	BundleContext start();

	/**
	 * Shutdown OSGi framework.
	 */
	void stop();
	
	/**
	 * Determine whether this runtime can be started programmatically.
	 * 
	 * @return <code>true</code>, if this runtime can be started 
	 * 			programmatically, <code>false</code> otherwise
	 */
	boolean canStart();
	
	/**
	 * Determine whether this runtime can be stopped programmatically.
	 * 
	 * @return <code>true</code>, if this runtime can be stopped 
	 * 			programmatically, <code>false</code> otherwise
	 */
	boolean canStop();
	
	/**
	 * Determine whether the framework is running
	 * 
	 * @return <code>true</code>, if the runtime is running, <code>false</code> otherwise
	 */
	boolean isRunning();
	
	
	
	/**
	 * Get bundle context of system bundle.
	 * 
	 * @return bundle context or <code>null</code>, if
	 * 			the framework is not started
	 */
	BundleContext getBundleContext();
	
	/**
	 * Install bundle.
	 * 
	 * @param bundleFile file containing the bundle
	 * @param autoStart true, to start bundle after installation, false otherwise
	 *  
	 * @return installed bundle
	 */
	Bundle install(File bundleFile, boolean autoStart);
	
	/**
	 * Install bundle.
	 * 
	 * @param uri URI/URL leading to the bundle location
	 * @param autoStart true, to start bundle after installation, false otherwise
	 *  
	 * @return installed bundle
	 */
	Bundle install(String uri, boolean autoStart);
	
	/**
	 * Install bundle.
	 * 
	 * @param url URL leading to the bundle location
	 * @param autoStart true, to start bundle after installation, false otherwise
	 *  
	 * @return installed bundle
	 */
	Bundle install(URL url, boolean autoStart);
	
	/**
	 * Install bundle.
	 * 
	 * @param stream stream of bundle data
	 * @param autoStart true, to start bundle after installation, false otherwise
	 *  
	 * @return installed bundle
	 */
	Bundle install(InputStream stream, boolean autoStart);

	/**
	 * Install bundled. Bundles are started after all have been installed.
	 * 
	 * @param bundleFiles list of bundle files or URIs/URLs
	 * @param autoStart true, to start bundles after installation, false otherwise
	 *  
	 * @return installed bundle
	 */
	List<Bundle> install(List<Object> bundleFiles, boolean autoStart);
	
	/**
	 * Start bundles
	 * 
	 * @param bundles list of bundle ids or bundle symbolic names
	 */
	@SuppressWarnings("rawtypes")
	void startBundles(List bundles);

	/**
	 * Start bundle.
	 * 
	 * @param bundle bundle to start
	 */
	void startBundle(Bundle bundle);
	
	/**
	 * Start bundle.
	 * 
	 * @param bundleId bundle id
	 */
	void startBundle(int bundleId);
	
	/**
	 * Start bundle.
	 * 
	 * @param symbolicName bundle symbolic name
	 */
	void startBundle(String symbolicName);
	
	/**
	 * Stop bundles
	 * 
	 * @param bundles list of bundle ids or bundle symbolic names
	 */
	@SuppressWarnings("rawtypes")
	void stopBundles(List bundles);

	/**
	 * Stop bundle.
	 * 
	 * @param bundle bundle to start
	 */
	void stopBundle(Bundle bundle);
	
	/**
	 * Stop bundle.
	 * 
	 * @param bundleId bundle id
	 */
	void stopBundle(int bundleId);
	
	/**
	 * Stop bundle.
	 * 
	 * @param symbolicName bundle symbolic name
	 */
	void stopBundle(String symbolicName);

	/**
	 * Determine whether a bundle is a fragment.
	 *
	 * @param bundle bundle to check
	 * 
	 * @return true, if the bundle is a fragment, false otherwise
	 */
	boolean isFragment(Bundle bundle);
}
