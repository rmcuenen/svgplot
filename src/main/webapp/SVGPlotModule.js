/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * The contents of this file are subject to the Common Development and Distribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is SVG Plot Module Extension.
 *
 * The Initial Developer of the Original Code is R. M. Cuenen
 * Portions created by the Initial Developer are Copyright (C) 2013
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Raymond Cuenen <Raymond.Cuenen@gmail.com>
 *
 * If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 */

(function(node) {
    /**
     * @class The ModuleFactory is responsible for creating a {@link Module}.
     *        Only one {@link Module} can be created by any one factory.
     *        However, the factory can specify which modules need to be resolved
     *        before the target {@link Module} can be created by means of dependencies.
     *        These depending modules are passed into the factory's creation method
     *        to provide access to those modules.
     * @name ModuleFactory
     * @param {String[]} dependencies The module identifiers the target module depends on.
     * @param {function(...[Object])} callback Callback method. This method is invoked
     *        when therequested modules (and their dependencies) are resolved.
     * @throws {TypeError} When the given dependencies is not an array.
     * @throws {TypeError} When an ivalid callback function is given.
     * @property {String[]} dependencies Module identifiers the target module depends on.
     * @property {function(...[Object])} callback Creation method. This method is
     *           invoked when the module dependencies (and their dependencies) are
     *           resolved, at which point the factory is supposed to create and
     *           return the module.
     */
    function ModuleFactory(dependencies, callback) {
        if (!(Object.prototype.toString.call(dependencies) === '[object Array]')) {
            var error = new Error("Invalid dependencies");
            error.name = "TypeError";
            throw error;
        }
        if (typeof callback !== 'undefined' &&
                !(Object.prototype.toString.call(callback) === '[object Function]')) {
            var error = new Error("Invalid callback");
            error.name = "TypeError";
            throw error;
        }
        this.dependencies = dependencies;
        this.callback = callback ? callback : function() {
        };
    }

    /**
     * Status enumeration that indicates the module is requested (by another module),
     * or its resources are resolved.
     * @namespace Module~Status
     * @readonly
     * @enum {String}
     */
    var Status = {
        /**
         * The REQUESTED status indicates that the module is requested but not
         * yet resolved.
         */
        REQUESTED: "REQUESTED",
        /**
         * The ARRIVED status indicates that the module is resolved.
         */
        ARRIVED: "ARRIVED"
    };

    /**
     * Process enumeration that indicates the (resolved) module is in the process
     * of being defined, or its definition function has successfully executed.
     * @namespace Module~Process
     * @readonly
     * @enum {String} 
     */
    var Process = {
        /**
         * The EXECUTING status indicates that the module is in the process of
         * being defined.
         */
        EXECUTING: "EXECUTING",
        /**
         * The EXECUTED status indicates that the module's definition function
         * has executed successfully.
         */
        EXECUTED: "EXECUTED"
    };

    /**
     * Creates the Module instance with the given identifier. The module name and
     * JavaScript file are determined from this identifier.
     * 
     * @class The Module class holds all the information about a module.
     *        This class is used during the loading process. It is not the module
     *        itself, but keeps track of the module status (while being loaded),
     *        its definition function and its resources.
     * @name Module
     * @param {String} moduleId The module identifier.
     * @property {Module[]} dependencies List of dependencies.
     * @property {String} moduleId The module identifier.
     * @property {String} file The JavaScript file of this module.
     * @property {String} name The module name.
     * @property {Status} injected The resolve status.
     * @property {Process} executed The process status.
     * @property {Object} exports The module itself, made available by the execution
     *           of the definition function.
     * @property {ModuleFactory} factory The definition function.
     */
    function Module(moduleId) {
        this.dependencies = [];
        this.moduleId = moduleId;
        this.file = moduleId + ".js";
        this.name = this.moduleId.replace(/^.*\//, "");
    }

    /**
     * @lends Module#
     */
    Module.prototype = {
        /**
         * Add the given Module as a dependency to this module. When the given
         * module is already a dependency of this module it is not added again.
         * 
         * @param {Module} module The dependency.
         */
        addDependency: function(module) {
            if (this.dependencies.indexOf(module) === -1) {
                this.dependencies.push(module);
            }
        },
        /**
         * Modules are equal by their identifier.
         * 
         * @param {Object} obj The object to compare to this module.
         * @returns {boolean}
         */
        equals: function(obj) {
            return this.moduleId === obj.moduleId;
        },
        /**
         * Returns a string representation of the module.
         * 
         * @returns {String}
         */
        toString: function() {
            return JSON.stringify({
                mid: this.moduleId,
                file: this.file,
                executed: this.executed,
                dep: '[' + this.dependencies.join(", ") + ']',
                def: '{' + (this.factory ? ' ' + this.factory + ' ' : '') + '}',
                result: '{' + (this.exports ? ' ' + this.exports + ' ' : '') + '}',
                injected: this.injected
            });
        }
    };

    /**
     * Private constructor (singleton pattern).
     * 
     * @class The ModuleLoader is the loader that will load the module's resources.
     *        Since this class implements the singleton pattern there is only one
     *        ModuleLoader at one time. Therefore the module store can be used
     *        globally so no module needs to be loaded twice.
     * @name ModuleLoader
     * @property {String} moduleBase The base URL for loading modules.
     * @property {Object.<String, Module>} waiting Map containing modules for which
     *           the module resource is being loaded.
     * @property {Object.<String, Module>} modules Map containing the created modules.
     */
    function ModuleLoader() {
        this.moduleBase = "";
        this.waiting = {};
        this.modules = {};
    }

    /**
     * @lends ModuleLoader#
     */
    ModuleLoader.prototype = {
        /**
         * Start the definition process of the Module identified by the given class name.
         * 
         * @param {String} name The name of the defining {@link Module}.
         * @param {ModuleFactory} factory The {@link ModuleFactory} providing the
         *        module's dependencies and definition function.
         */
        defineModule: function(name, factory) {
            var module = this.waiting[name];
            if (typeof module !== 'undefined') {
                delete this.waiting[name];
                module.factory = factory;
                if (module.injected !== Status.ARRIVED) {
                    for (var i = 0; i < factory.dependencies.length; ++i) {
                        var moduleId = factory.dependencies[i];
                        if (moduleId.charAt(0) === '.') {
                            moduleId = this.resolveRelative(module, moduleId);
                        }
                        module.addDependency(this.getModule(moduleId));
                    }
                    module.injected = Status.ARRIVED;
                }
                this.injectDependencies(module);
                this.checkModules();
            }
        },
        /**
         * Start the request procedure.
         * 
         * @param {ModuleFactory} factory The {@link ModuleFactory} providing the
         *        requested module identifiers.
         */
        requireModule: function(factory) {
            var module = this.getModule(uid());
            module.injected = Status.ARRIVED;
            module.factory = factory;
            for (var i = 0; i < factory.dependencies.length; ++i) {
                module.addDependency(this.getModule(factory.dependencies[i]));
            }
            this.injectDependencies(module);
            this.checkModules();
        },
        /**
         * Inject the resources of the given {@link Module}. This means the module's
         * JavaScript file is added to the document to start the definition process.
         * 
         * @param {Module} module The {@link Module} which resources is to be loaded.
         */
        injectModule: function(module) {
            if (module.executed || module.injected) {
                return;
            }
            module.injected = Status.REQUESTED;
            var moduleClass = document.createElementNS(SVGModule.SVG_NS, "script");
            moduleClass.setAttributeNS(SVGModule.XLINK_NS, "href", this.moduleBase + module.file);
            moduleClass.onerror = function() {
                var error = new Error("Error while loading " + module.file);
                error.name = "ModuleError";
                throw error;
            };
            this.waiting[module.name] = module;
            var scripts = document.getElementsByTagName("script");
            document.documentElement.insertBefore(moduleClass, scripts[scripts.length - 1].nextSibling);
        },
        /**
         * Get a {@link Module} from the store, or create one if it does not exists.
         * 
         * @param {String} moduleId The module identifier.
         * @returns {Module} The {@link Module} instance representing the requested module.
         */
        getModule: function(moduleId) {
            var module = this.modules[moduleId];
            if (typeof module === 'undefined') {
                module = new Module(moduleId);
                this.modules[moduleId] = module;
            }
            return module;
        },
        /**
         * Inject the resources of the dependencies of the given module.
         * 
         * @see ModuleLoader#injectModule
         * @param {Module} module The module to inject the dependencies for.
         */
        injectDependencies: function(module) {
            for (var i = 0; i < module.dependencies.length; ++i) {
                this.injectModule(module.dependencies[i]);
            }
        },
        /**
         * Execute the definition of the given module. First the definition of
         * the dependencies is executed before the given module.
         * 
         * @param {Module} module The module to be defined.
         */
        executeModule: function(module) {
            if (module.executed === Process.EXECUTING) {
                return null;
            }
            if (typeof module.executed === 'undefined') {
                if (typeof module.factory === 'undefined') {
                    return null;
                }
                module.executed = Process.EXECUTING;
                var args = [];
                for (var i = 0; i < module.dependencies.length; ++i) {
                    var def = this.executeModule(module.dependencies[i]);
                    if (def === null) {
                        delete module.executed;
                        return null;
                    }
                    args.push(def);
                }
                if (typeof module.factory !== 'undefined') {
                    module.exports = module.factory.callback.apply(window, args);
                }
                module.executed = Process.EXECUTED;
                if (REQUIRE_ID.test(module.moduleId)) {
                    delete this.modules[module.moduleId];
                }
            }
            return module.exports;
        },
        /**
         * Check if requested modules need and can be executed.
         */
        checkModules: function() {
            for (var moduleId in this.modules) {
                if (this.modules.hasOwnProperty(moduleId)) {
                    this.executeModule(this.modules[moduleId]);
                }
            }
        },
        /**
         * This method resolves a relative identifier to an absolute identifier.
         * Module identifiers in dependencies may be relative to the module that
         * depends on them.
         * 
         * @param {Module} module The module having the module identifier as a dependency.
         * @param {String} id The relative module identifier.
         * @returns {String} The absolute module identifier.
         */
        resolveRelative: function(module, id) {
            var hierarchy = module.moduleId.split("/");
            var hIndex = hierarchy.length - 2;
            if (hIndex < 0) {
                return id;
            }
            var path = id.split("/");
            var result = null;
            for (var i = path.length; i > 0; i--) {
                var s = path[i - 1];
                if ("." === s) {
                    result = hierarchy[hIndex] + "/" + result;
                } else if (".." === s) {
                    hIndex--;
                    if (hIndex < 0) {
                        break;
                    }
                    result = hierarchy[hIndex] + "/" + result;
                } else {
                    if (result === null) {
                        result = s;
                    } else {
                        result = s + "/" + result;
                    }
                }
            }
            for (var i = hIndex; i > 0; i--) {
                result = hierarchy[hIndex - 1] + "/" + result;
            }
            return result;
        }
    };

    /**
     * Pattern to identify dummy modules.
     * 
     * @constant
     * @type Regex
     */
    var REQUIRE_ID = /^require\\*/;

    /**
     * Counter for dummy module identifiers.
     * 
     * @static
     * @type Number
     */
    var _uid = 1;

    /**
     * Generate a module identifier. This identifier is used in the request process.
     * 
     * @static
     * @returns {String} An unique, internal use, module identifier.
     */
    function uid() {
        return "require*_" + _uid++;
    }
    ;

    /**
     * Reference to the singleton instance.
     * 
     * @static
     * @type ModuleLoader
     */
    var LOADER = new ModuleLoader();

    /**
     * Utility object that provides the means to request and define modules.
     * 
     * @namespace
     */
    SVGModule = {
        /**
         * SVG namespace identifier.
         * 
         * @type String
         */
        SVG_NS: "http://www.w3.org/2000/svg",
        /**
         * XLINK namespace identifier.
         * 
         * @type String
         */
        XLINK_NS: "http://www.w3.org/1999/xlink",
        /**
         * This method is invoked to request one or more modules. This is usually
         * done by the application during startup, but it is also possible to request
         * modules within a module execution context. However, it is more common to 
         * require a module as dependency rather then to request it upon execution.
         * The request is asynchronous. That means that this method returns as soon
         * as possible after it is invoked. When the requested module (and its dependencies)
         * are resolved the callback method is invoked.
         * 
         * @param {String[]} dependencies The module identifiers being requested.
         * @param {function(...[Object])} callback Callback method.
         */
        require: function(dependencies, callback) {
            LOADER.requireModule(new ModuleFactory(dependencies, callback));
        },
        /**
         * This method should be invoked from within a initialization block of the
         * defining {@link Module}.
         * 
         * @param {String} name
         * @param {String[]} dependencies
         * @param {function(...[Object])} callback
         */
        define: function(name, dependencies, callback) {
            LOADER.defineModule(name, new ModuleFactory(dependencies, callback));
        }
    };

    if (node !== null && node.nodeName.toLowerCase() === "script") {
        var scriptBase = node.getAttributeNS(SVGModule.XLINK_NS, "href");
        if (scriptBase.indexOf("SVGPlotModule.js") > -1) {
            var moduleBase = node.getAttribute("moduleBase");
            if (moduleBase === null) {
                moduleBase = scriptBase.slice(0, scriptBase.lastIndexOf('/') + 1);
            }
            if (moduleBase.length > 0 && moduleBase.charAt(moduleBase.length - 1) !== '/') {
                moduleBase += '/';
            }
            LOADER.moduleBase = moduleBase;
        }
    }

    window.onerror = function(msg, url, line) {
        alert(msg.replace("Uncaught ", '') + "\n\n" + url + " (" + line + ")");
        return false;
    };
})(document.getElementById('svgplot-loader'));
