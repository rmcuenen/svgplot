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
  function ModuleFactory(dependencies, callback) {
    if (!(Object.prototype.toString.call(dependencies) === '[object Array]')) {
      throw "Invalid dependencies";
    }
    if (callback !== undefined &&
          !(Object.prototype.toString.call(callback) === '[object Function]')) {
      throw "Invalid callback";
    }
    this.dependencies = dependencies;
    this.callback = callback;
  }

  function Module(moduleId) {
    this.dependencies = [];
    this.moduleId = moduleId;
    this.file = moduleId + ".js";
    this.name = this.file.replace(/\.[^\.]+$/, "");
  }

  Module.prototype = {
    addDependency: function(module) {
      if (this.dependencies.indexOf(module) === -1) {
        this.dependencies.push(module);
      }
    },
    equals: function(obj) {
      return this.moduleId === obj.moduleId;
    },
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

  function ModuleLoader() {
    this.waiting = {};
    this.modules = {};
  }

  ModuleLoader.prototype = {
    defineModule: function(name, factory) {
      var module = this.waiting[name];
      if (module !== undefined) {
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
    requireModule: function(factory) {
      var module = this.getModule(uid());
      module.injected = "ARRIVED";
      module.factory = factory;
      for (var i = 0; i < factory.dependencies.length;  ++i) {
        module.addDependency(this.getModule(factory.dependencies[i]));
      }
      this.injectDependencies(module);
      this.checkModules();
    },
    injectModule: function(module) {
      if (module.executed !== undefined || module.injected !== undefined) {
        return;
      }
      module.injected = "REQUESTED";
      var moduleClass = document.createElementNS(SVGModule.SVG_NS, "script");
      moduleClass.setAttributeNS(SVGModule.XLINK_NS, "href", this.base + module.file);
      this.waiting[module.name] = module;
      var base = document.getElementsByTagName("script");
      document.documentElement.insertBefore(moduleClass, base[base.length - 1].nextSibling);
    },
    getModule: function(moduleId) {
      var module = this.modules[moduleId];
      if (module === undefined) {
        module = new Module(moduleId);
        this.modules[moduleId] = module;
      }
      return module;
    },
    injectDependencies: function(module) {
      for (var i = 0; i < module.dependencies.length; ++i) {
        this.injectModule(module.dependencies[i]);
      }
    },
    executeModule: function(module) {
      if (module.executed === "EXECUTING") {
        return;
      }
      if (module.executed === undefined) {
        if (module.factory === undefined) {
          return;
        }
        module.executed = "EXECUTING";
        var args = [];
        for (var i = 0; i < module.dependencies.length; ++i) {
          var def = this.executeModule(module.dependencies[i]);
          if (def === undefined) {
            delete module.executed;
            return;
          }
          args.push(def);
        }
        if (module.factory !== undefined) {
          module.exports = module.factory.callback.apply(window, args);
        }
        module.executed = "EXECUTED";
        if (REQUIRE_ID.test(module.moduleId)) {
          delete this.modules[module.moduleId];
        }
      }
      return module.exports;
    },
    checkModules: function() {
      for (var moduleId in this.modules) {
        if (this.modules.hasOwnProperty(moduleId)) {
          this.executeModule(this.modules[moduleId]);
        }
      }
    },
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
        } else if (".." === s ) {
          hIndex--;
          if (hIndex < 0) {
            break;
          }
          result = hierarchy[hIndex] + "/" + result;
        } else {
          if (result === undefined) {
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

  var REQUIRE_ID = /^require\\*/;
  var _uid = 1;
  function uid() {
    return "require*_" + _uid++;
  }
  var LOADER = new ModuleLoader();
  var SVGModule = {
    SVG_NS: "http://www.w3.org/2000/svg",
    XLINK_NS: "http://www.w3.org/1999/xlink",
    require: function(dependencies, callback) {
      LOADER.requireModule(new ModuleFactory(dependencies, callback));
    },
    define: function(name, dependencies, callback) {
      LOADER.defineModule(name, new ModuleFactory(dependencies, callback));
    }
  };

  if (node === undefined) {
    LOADER.base = "/";
  } else {
    var base = node.getAttribute("base");
    if (base === undefined) {
      base = node.getAttributeNS(SVGModule.XLINK_NS, "href");
      base = base.slice(0, base.lastIndexOf('/'));
    }
    if (base.charAt(base.length - 1) !== '/') {
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
