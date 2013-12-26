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

SVGModule.define(
        "SVGPlotAttributes",
        ["ExpressionParser"],
        function(ExpressionParser) {
            /**
             * @class This object represents a SVGPlotAttribute.
             * @name SVGPlotAttribute
             * @property {String} name The name of the attribute.
             * @property {Object} value The (parsed) value of the attribute.
             * @property {Boolean} set Indicates whether or not the value is
             *                         already set (parsed).
             * @param {String} name The name of the attribute.
             */
            function SVGPlotAttribute(name) {
                this.name = name;
                this.value = DEFAULTS[name];
                this.set = false;
            }

            /**
             * @lends SVGPlotAttribute
             */
            SVGPlotAttribute.prototype = {
                /**
                 * Parses the given string by means of the parse function.
                 * The parse function is determined by name from the {@link PARSERS}
                 * repository.
                 * 
                 * @param {String} attr The string to be parsed in a value.
                 * @throws {ParseError} When the value is already set (parsed) before.
                 */
                parse: function(attr) {
                    if (this.set) {
                        var error = new Error("The attribute '" + this.name + "' is already defined");
                        error.name = "ParseError";
                        throw error;
                    }
                    this.value = PARSERS[this.name](attr);
                    this.set = true;
                }
            };

            /**
             * Repository of parse functions.
             * This is basically a mapping of attribute names to corresponding
             * parse functions.
             * 
             * @name PARSERS
             * @type Object
             */
            var PARSERS = {
                /**
                 * Parses a 'domain' attribute value.
                 * 
                 * @param {String} attr The sting value to be parsed.
                 * @throws {Exception} When the lower bound range value is higher
                 *                     then the upper bound range value.
                 * @throws {ParseError} When the string has not the 'domain' format.
                 */
                domain: function(attr) {
                    var target = attr.split(':');
                    if (target.length === 2) {
                        var pTree0 = ExpressionParser.parse(target[0]).visit();
                        var pTree1 = ExpressionParser.parse(target[1]).visit();
                        if (pTree0 > pTree1) {
                            var error = new Error("Invalid domain: " + pTree0 + " > " + pTree1);
                            error.name = "ParseError";
                            throw error;
                        }
                        return [pTree0, pTree1];
                    } else {
                        var error = new Error("Unknown domain format: " + attr);
                        error.name = "ParseError";
                        throw error;
                    }
                },
                /**
                 * Parses a 'samples' attribute value.
                 * 
                 * @param {String} attr The sting value to be parsed.
                 * @throws {ParseError} When the parsed value is not a positive integer.
                 */
                samples: function(attr) {
                    var target = 0 | Number(attr);
                    if (target > 0) {
                        return target;
                    }
                    var error = new Error("Invalid samples: " + attr);
                    error.name = "ParseError";
                    throw error;
                },
                /**
                 * Parses a 'variable' attribute value.
                 * 
                 * @param {String} attr The sting value to be parsed.
                 * @throws {ParseError} When the string has not the 'variable' format.
                 */
                variable: function(attr) {
                    var match = /#?[a-zA-Z][a-zA-Z0-9_]*/.exec(attr);
                    if (match !== null && match[0] === attr) {
                        return attr.charAt(0) === '#' ? attr : ('#' + attr);
                    }
                    var error = new Error("Invalid variable: " + attr);
                    error.name = "ParseError";
                    throw error;
                },
                /**
                 * Parses a 'connected' attribute value.
                 * 
                 * @param {String} attr The sting value to be parsed.
                 * @throws {ParseError} When the string is not one of 'sharp' or 'smooth'.
                 */
                connected: function(attr) {
                    if (/(sharp|smooth)/.test(attr)) {
                        return attr;
                    }
                    var error = new Error("Invalid connection type: " + attr);
                    error.name = "ParseError";
                    throw error;
                },
                /**
                 * Parses a 'function' attribute value.
                 * 
                 * @param {String} attr The sting value to be parsed.
                 * @throws {ParseError} When the string has not the 'function' format.
                 */
                function: function(attr) {
                    var pTree = [];
                    var target = attr.split(',');
                    var index = 0;
                    switch (target.length) {
                        case 2:
                            pTree[0] = ExpressionParser.parse(target[index++]);
                        case 1:
                            pTree[1] = ExpressionParser.parse(target[index]);
                            break;
                        default:
                            var error = new Error("Invalid function: " + attr);
                            error.name = "ParseError";
                            throw error;
                    }
                    return pTree;
                }
            };

            /**
             * This object holds the default values for the attributes.
             * The default value is set at attribute creation and is looked-up
             * in this map.
             * 
             * @name DEAFULTS
             * @type Object
             * @property {Number[]} domain The default domain range.
             * @property {Integer} samples The default number of samples.
             * @property {String} variable The default variable.
             * @property {String} connected The default connection type.
             * @property {Visitor} function The deafult function value object.
             */
            var DEFAULTS = {
                domain: [-5, 5],
                samples: 25,
                variable: "#x",
                connected: "sharp",
                function: null
            };

            /**
             * The SVGPlotAttribute interface is used to create attribute objects
             * that can parse a string value into an object value of the correct
             * type.
             * The implementation for the attributes of the SVG plot-element are
             * already prodived. Others can be set too.
             * 
             * @namespace SVGPlotAttribute
             */
            return {
                /**
                 * Creates a new SVGPlotAttribute implementation for the given attribute.
                 * 
                 * @param {type} name The attribute name.
                 * @throws {NotFoundError} When the requested attribute has no parse function.
                 * @returns {SVGPlotAttribute} The SVGPlotAttribute implementation.
                 */
                create: function(name) {
                    if (typeof PARSERS[name] === 'undefined') {
                        var error = new Error("Unknown attribute: " + name);
                        error.name = "NotFoundError";
                        throw error;
                    }
                    return new SVGPlotAttribute(name);
                },
                /**
                 * Returns the array of attribute names currently in the repository.
                 * 
                 * @returns {String[]} The attribute names in the repository.
                 */
                names: function() {
                    var result = [];
                    for (var name in PARSERS) {
                        if (PARSERS.hasOwnProperty(name)) {
                            result.push(name);
                        }
                    }
                    return result;
                },
                /**
                 * Sets the given mapping in the SVGPlotAttribute repositories.
                 * 
                 * @param {String} name The attribute name.
                 * @param {Object} defaultValue The default value (can be null).
                 * @param {Function} parseFunction The parse function.
                 * @throws {TypeError} When the parse function is invalid.
                 */
                setAttribute: function(name, defaultValue, parseFunction) {
                    if (!(Object.prototype.toString.call(parseFunction) === '[object Function]')) {
                        var error = new Error("Invalid parse function");
                        error.name = "TypeError";
                        throw error;
                    }
                    PARSERS[name] = parseFunction;
                    DEFAULTS[name] = defaultValue;
                }
            };
        }
);
