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

package net.jetztgrad.groovy.osgi.runtime.support


import groovy.lang.Closure
import net.jetztgrad.groovy.osgi.runtime.support.BundleSpec

class OsgiBuilder {
	List<BundleSpec> bundles = []
	
	def bundles(Closure closure) {
		bundles = []
		def cl = closure.clone()
		cl.delegate = this
		cl()
	}
	
	def bundle(Map args) {
		bundle(args, null)
	}
	
	/**
	 * Bundle
	 * @param specs bundle specs in format 'group:name:version'
	 */
	def bundle(String specs) {
		bundle(specs, null)
	}
	
	/**
	* Bundle
	* @param specs bundle specs in format 'group:name:version'
	*/
	def bundle(String specs, Closure closure) {
		def parts = specs.split(':')
		if (parts?.size() < 3) {
			throw new IllegalArgumentException("invalid bundle spec! Required format: 'group:name:version'")
		}
		def (group, name, version) = parts as List 
		def args = [group: group, name: name, version: version ]
		bundle(args, closure)
	}
	
	def bundle(Map args, Closure closure) {
		if (args.name) {
			BundleSpec spec = new BundleSpec()
			spec.name = args.name
			
			if (args?.group) {
				spec.group = args.group
			}
			
			if (args?.version) {
				spec.version = args.version
			}
			
			if (closure) {
				def cl = closure.clone()
				cl.delegate = this
				cl(spec)
			}
			
			bundles << spec
		}
	}
}
