package org.codehaus.groovy.osgi.runtime.equinox

import java.io.File;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker

import org.codehaus.groovy.osgi.runtime.AbstractOsgiRuntime

import org.eclipse.core.runtime.adaptor.EclipseStarter


/**
 * Equinox OSGi runtime.
 * 
 * <p>
 * Preferable use Equinox 3.6.1 or higher because of shutdown bugs
 * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=312917">312917</a> 
 * and <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=314343">314343</a>.
 * </p> 
 */
class EquinoxRuntime extends AbstractOsgiRuntime {
	Properties frameworkProperties = new Properties()
	
	EquinoxRuntime(Map runtimeProperties) {
		if (runtimeProperties) {
			frameworkProperties.putAll(runtimeProperties)
		}
	}
	
	BundleContext doStart() {
		// initialize framework
		frameworkProperties.setProperty("eclipse.ignoreApp", "true")
		frameworkProperties.setProperty("eclipse.application.noDefault", "true")

		EclipseStarter.setInitialProperties(frameworkProperties)

		// start framework
		def args = [ "-clean", "-consoleLog", "-console" ]
		
		if (argsMap?.containsKey("debug")) {
			args << "-debug"
			String path = argsMap?.debug?.toString()
			if (path) {
				args << path
			}
		}
		
		// configure (remote) console 
		def consoleEnabled = config?.osgi?.console?.enabled ?: false
		def defaultConsolePort = config?.osgi?.console?.port ?: 8023
		def consolePort = 0
		if (argsMap?.consolePort) {
			if (argsMap.consolePort instanceof Boolean) {
				consolePort = defaultConsolePort
			}
			else {
				consolePort = argsMap.consolePort
			}
			consoleEnabled = true
		}
		else if (argsMap?.remoteConsole
		&& !consolePort) {
			consolePort = defaultConsolePort
			consoleEnabled = true
		}
		
		if (consoleEnabled) {
			if (!consolePort) {
				consolePort = defaultConsolePort
			}
			args << consolePort.toString()
			println "running Equinox console on port $consolePort"
		}
		
		this.bundleContext = EclipseStarter.startup( args as String[], null );
		
		//configureLogging()
		
		return this.bundleContext
	}
	
	/* (non-Javadoc)
	 * @see groovyx.osgi.OsgiRuntime#configureLogging()
	 */
	void configureLogging() {
		if (!this.bundleContext) {
			return
		}
		
		// create log configuration
		//def dir = new File(osgiRuntimePath)
		File logConfig = new File(dropinsDir, 'org.ops4j.pax.logging.properties')
		if (!logConfig.exists()) {
			println "preparing log configuration in file ${logConfig}"
			logConfig.withWriter { writer ->
				writer << """# log configuration
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
log4j.rootLogger=INFO, stdout

log4j.logger.org.ops4j.pax.web=DEBUG
log4j.logger.org.springframework.osgi.extender.internal.blueprint=WARN
log4j.logger.org.springframework.osgi.extender.internal.activator=WARN

# show some spring message
log4j.logger.org.springframework.core.io.support=DEBUG
"""
			}
		}
		
		/*
		 File logConfigBundle = new File(logConfig.parent, 'Log4JConfigurationFragment-1.0.0.jar')
		 if (!logConfigBundle.exists()
		 || (logConfigBundle.lastModified() < logConfig.lastModified())) {
		 println "creating log configuration bundle in file ${logConfigBundle}"
		 // create log4j fragment bundle containing logging configuration
		 // see http://lists.ops4j.org/pipermail/general/2009q4/003297.html
		 // for TinyBundles usage notes
		 InputStream bundleStream = TinyBundles.newBundle()
		 .add('log4j.properties', new FileInputStream(logConfig))
		 .set(org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION, '2')
		 .set(org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME, 'Log4JConfigurationFragment')
		 .set(org.osgi.framework.Constants.BUNDLE_NAME, 'Log4JConfigurationFragment')
		 .set(org.osgi.framework.Constants.BUNDLE_VERSION, '1.0.0')
		 .set(org.osgi.framework.Constants.BUNDLE_CLASSPATH, '.')
		 .set(org.osgi.framework.Constants.FRAGMENT_HOST, 'com.springsource.org.apache.log4j')
		 .build(TinyBundles.with()) //withBnd())
		 if (bundleStream) {
		 // save bundle to file
		 logConfigBundle.withOutputStream { out ->
		 out << bundleStream
		 }
		 }
		 }
		 if (logConfigBundle.exists()) {
		 // install log configuration fragment
		 //this.bundleContext.installBundle("file:${logConfigBundle.canonicalPath}")
		 }
		 */
		
		// set log configuration as Dictionary using ConfigAdmin to pid 'org.ops4j.pax.logging'
		// see http://wiki.ops4j.org/display/paxlogging/Configuration
		
		Properties log4jProperties = new Properties();
		try {
			log4jProperties.load(new FileInputStream(logConfig));
			println "Loaded log4j.properties"
		}
		catch (IOException e) {
			println "Failed to load log4j.properties: " + e.message
		}
		
		// Use a Servicetracker to wait for ConfigurationAdmin service
		ServiceTracker tracker = new ServiceTracker(this.bundleContext, 'org.osgi.service.cm.ConfigurationAdmin', null);
		tracker.open();
		def service = null;
		try {
			service = tracker.waitForService(0);
		}
		catch (InterruptedException e) {
			println "Failed to get ConfigurationAdmin service:" + e.message
		}
		
		// Update the Pax-Logging configuration by setting the
		// log4j.properties contents via the ConfigurationAdmin service
		def configuration = null;
		try {
			configuration = service.getConfiguration("org.ops4j.pax.logging", null);
		}
		catch (IOException e) {
			println "Failed to get configuration: " + e.message
		}
		
		try {
			configuration.update(log4jProperties);
		}
		catch (IOException e) {
			println "Failed to update configuration properties:" + e.message
		}
		tracker.close()
		println "Log configuration updated!"
	}
	
	/* (non-Javadoc)
	 * @see groovyx.osgi.runtime.OsgiRuntime#stop()
	 */
	void doStop() {
		EclipseStarter.shutdown();
		this.bundleContext = null
	}
}
