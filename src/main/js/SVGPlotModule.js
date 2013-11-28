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
     * The ModuleFactory is responsible for creating a {@link Module}.
     * Only one {@link Module} can be created by any one factory.
     * However, the factory can specify which modules need to be resolved before
     * the target {@link Module} can be created by means of dependencies.
     * These depending modules are passed into the factory's creation method to
     * provide access to those modules.
     * 
     * @constructor
     * @param {String[]} dependencies The module identifiers the target module depends on.
     * @param {function} callback     Callback method. This method is invoked when the
     *                                requested modules (and their dependencies) are resolved.
     */
    function ModuleFactory(dependencies, callback) {
        if (!(Object.prototype.toString.call(dependencies) === '[object Array]')) {
            throw "Invalid dependencies";
        }
        if (typeof callback !== 'undefined' &&
                !(Object.prototype.toString.call(callback) === '[object Function]')) {
            throw "Invalid callback";
        }
        /**
         * Module identifiers the target module depends on.
         * @type String[]
         */
        this.dependencies = dependencies;
        /**
         * Creation method. This method is invoked when the module dependencies
         * (and their dependencies) are resolved, at which point the factory is
         * supposed to create and return the module.
         * @type function
         */
        this.callback = callback;
    }

    /**
     * Status enumeration that indicates the module is requested (by another module),
     * or its resources are resolved.
     * @typedef {String} Status
     * @readonly
     * @enum {String}
     * @property {String} REQUESTED The REQUESTED status indicates that the module is requested but not yet resolved.
     * @property {String} ARRIVED   The ARRIVED status indicates that the module is resolved.
     */

    /**
     * Process enumeration that indicates the (resolved) module is in the process
     * of being defined, or its definition function has successfully executed.
     * @typedef {String} Process
     * @readonly
     * @enum {String}
     * @property {String} EXECUTING The EXECUTING status indicates that the module is in the process of being defined.
     * @property {String} EXECUTED  The EXECUTED status indicates that the module's definition function has executed successfully.
     */

    /**
     * Creates the Module instance with the given identifier. The module name and
     * JavaScript file are determined from this identifier.
     * 
     * @constructor
     * @param {String} moduleId The module identifier.
     */
    function Module(moduleId) {
        /**
         * List of dependencies.
         * @type Module[]
         */
        this.dependencies = [];

        /**
         * The module identifier.
         * @type String
         */
        this.moduleId = moduleId;

        /**
         * The JavaScript file of this module.
         * @type String
         */
        this.file = moduleId + ".js";

        /**
         * The module name.
         * @type String
         */
        this.name = this.file.replace(/\.[^\.]+$/, "");

        /**
         * The resolve status.
         * @type Status
         * @name injected
         */

        /**
         * The process status.
         * @type Process
         * @name executed
         */

        /**
         * The module itself, made available by the execution of the definition function.
         * @type Object
         * @name exports
         */
    }

    /**
     * @classdesc The Module class holds all the information about a module.
     * This class is used during the loading process. It is not the module itself,
     * but keeps track of the module status (while being loaded),its definition
     * function and its resources.
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
     * @constructor
     * @private
     */
    function ModuleLoader() {
        /**
         * Map containing modules for which the module resource is being loaded.
         * @type Object.<String, Module>
         */
        this.waiting = {};

        /**
         * Map containing the created modules.
         * @type Object.<String, Module>
         */
        this.modules = {};
    }

    /**
     * @classdesc The ModuleLoader is the loader that will load the module's resources.
     * Since this class implements the singleton pattern there is only one ModuleLoader
     * at one time. Therefore the module store can be used globally so no module
     * needs to be loaded twice.
     */
    ModuleLoader.prototype = {
        /**
         * Start the definition process of the Module identified by the given class name.
         * 
         * @param {String}        name    The name of the defining {@link Module}.
         * @param {ModuleFactory} factory The {@link ModuleFactory} providing the module's dependencies and definition function.
         */
        defineModule: function(name, factory) {
            var module = this.waiting[name];
            if (typeof module !== undefined) {
                delete this.waiting[name];
                module.factory = factory;
                if (module.injected !== "ARRIVED") {
                    for (var i = 0; i < factory.dependencies.length; ++i) {
                        var moduleId = factory.dependencies[i];
                        if (moduleId.charAt(0) === '.') {
                            moduleId = this.resolveRelative(module, moduleId);
                        }
                        module.addDependency(this.getModule(moduleId));
                    }
                    module.injected = "ARRIVED";
                }
                this.injectDependencies(module);
                this.checkModules();
            }
        },
        /**
         * Start the request procedure.
         * 
         * @param {ModuleFactory} factory The {@link ModuleFactory} providing the requested module identifiers.
         */
        requireModule: function(factory) {
            var module = this.getModule(uid());
            module.injected = "ARRIVED";
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
            module.injected = "REQUESTED";
            var moduleClass = document.createElementNS(SVGModule.SVG_NS, "script");
            moduleClass.setAttributeNS(SVGModule.XLINK_NS, "href", this.base + module.file);
            this.waiting[module.name] = module;
            var base = document.getElementsByTagName("script");
            document.documentElement.insertBefore(moduleClass, base[base.length - 1].nextSibling);
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
         * @private
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
         * @private
         * @param {Module} module The module to be defined.
         */
        executeModule: function(module) {
            if (module.executed === "EXECUTING") {
                return;
            }
            if (typeof module.executed === 'undefined') {
                if (typeof module.factory === 'undefined') {
                    return;
                }
                module.executed = "EXECUTING";
                var args = [];
                for (var i = 0; i < module.dependencies.length; ++i) {
                    var def = this.executeModule(module.dependencies[i]);
                    if (typeof def === 'undefined') {
                        delete module.executed;
                        return;
                    }
                    args.push(def);
                }
                if (typeof module.factory !== 'undefined') {
                    module.exports = module.factory.callback.apply(window, args);
                }
                module.executed = "EXECUTED";
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
         * @private
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
     * @private
     * @constant {Regex}
     */
    var REQUIRE_ID = /^require\\*/;

    /**
     * Counter for dummy module identifiers.
     * @private
     * @static
     * @type Number
     */
    var _uid = 1;

    /**
     * Generate a module identifier. This identifier is used in the request process.
     * 
     * @function
     * @private
     * @static
     * @returns {String} An unique, internal use, module identifier.
     */
    function uid() {
        return "require*_" + _uid++;
    }

    /**
     * Reference to the singleton instance.
     * 
     * @private
     * @static
     * @type ModuleLoader
     */
    var LOADER = new ModuleLoader();

    /**
     *  Utility object that provides the means to request and define modules.
     *  
     * @global
     * @type Object
     */
    var SVGModule = {
        /**
         * SVG namespace identifier.
         * @type String
         */
        SVG_NS: "http://www.w3.org/2000/svg",
        /**
         * XLINK namespace identifier.
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
         * @static
         * @param {String[]} dependencies The module identifiers being requested.
         * @param {function} callback     Callback method.
         */
        require: function(dependencies, callback) {
            LOADER.requireModule(new ModuleFactory(dependencies, callback));
        },
        /**
         * This method should be invoked from within a initialization block of the
         * defining {@link Module}.
         * 
         * @static
         * @param {String}   name
         * @param {String[]} dependencies
         * @param {function} callback
         */
        define: function(name, dependencies, callback) {
            LOADER.defineModule(name, new ModuleFactory(dependencies, callback));
        }
    };

    if (node === null) {
        LOADER.base = "";
    } else {
        var base = node.getAttribute("base");
        if (base === null) {
            base = node.getAttributeNS(SVGModule.XLINK_NS, "href");
            base = base.slice(0, base.lastIndexOf('/') + 1);
        }
        if (base.length > 0 && base.charAt(base.length - 1) !== '/') {
            base += '/';
        }
        LOADER.base = base;
    }

    window.SVGModule = SVGModule;
    window.onerror = function(msg, url, line) {
        alert(msg + "\n\n" + url + " (" + line + ")");
        return false;
    };
})(document.getElementById('svgplot-loader'));
