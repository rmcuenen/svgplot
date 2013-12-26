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
        "SVGPlotHandler",
        ["SVGPlotAttributes", "ExpressionParser"],
        function(SVGPlotAttributes, ExpressionParser) {
            /**
             * @class The PathCreator is responsible for converting an SVGPlotElement
             *        into an SVGPathElement.
             *        The conversion is done by evaluating the given function and
             *        creating the appropriate path data with the resulting coordinates.
             * @name PathCreator
             * @property {Elememt} pathElement The SVGPathElement being constructed.
             * @property {SVGPlotAttribute} names Actually all the SVPlotAttributes
             *                                    as defined by {@link SVGPlotAttribute#names}.
             */
            function PathCreator() {
                this.pathElement = document.createElementNS(SVGModule.SVG_NS, "path");
                for (var attr in SVGPlotAttributes.names()) {
                    this[attr] = SVGPlotAttributes.create(attr);
                }
            }

            /**
             * @lends PathCreator
             */
            PathCreator.prototype = {
                /**
                 * Parses all the SVGPlotElement's attributes and children into
                 * the SVGPathElement.
                 * 
                 * @param {Element} plotElement The SVGPlotElement being handled.
                 */
                parseSVGPlotElement: function(plotElement) {
                    var attributes = plotElement.attributes;
                    for (var i = 0; i < attributes.length; ++i) {
                        var attribute = attributes[i];
                        if (typeof this[attribute.name] === 'undefined') {
                            this.pathElement.setAttribute(attribute.name, attribute.value);
                        } else {
                            this[attribute.name].parse(attribute.value);
                        }
                    }
                    while (plotElement.firstChild) {
                        this.pathElement.appendChild(plotElement.removeChild(plotElement.firstChild));
                    }
                },
                /**
                 * Creates the path data by evaluating the 'function' attribute.
                 * This method assumes that the 'function' attribute it set.
                 */
                createPath: function() {
                    var scale = 1;
                    var token = this["connected"].value === "smooth" ? 'S' : 'L';
                    var start = this["domain"].value[0];
                    var end = this["domain"].value[1];
                    var step = (end - start) / this["samples"].value;
                    if (typeof this["function"].value[0] === 'undefined') {
                        this["function"].value[0] = ExpressionParser.parse(this["variable"].value);
                    }
                    var x = this["function"].value[0];
                    var y = this["function"].value[1];
                    x.setVariable(this["variable"].value);
                    y.setVariable(this["variable"].value);
                    var value = start;
                    var point = [scale * x.visit(value), -scale * y.visit(value)];
                    var path = 'M' + point[0] + ',' + point[1];
                    while (value < end) {
                        value += step;
                        path += token;
                        point = [scale * x.visit(value), -scale * y.visit(value)];
                        if (token === 'S') {
                            var mid = [scale * x.visit(value - step / 2), -scale * y.visit(value - step / 2)];
                            path += mid[0] + ',' + mid[1] + ' ';
                        }
                        path += point[0] + ',' + point[1];
                    }
                    this.pathElement.setAttribute("d", path);
                }
            };

            /**
             * Convenience method to stringify a document node.
             * This results in a string showing the tagName and the attributes.
             * 
             * @param {Node} node The node to stringify.
             * @returns {String} The string representation of the node.
             */
            function toString(node) {
                var result = "<" + node.tagName;
                var attributes = node.attributes;
                for (var i = 0; i < attributes.length; ++i) {
                    result += " " + attributes[i].name + '="' + attributes[i].value + '"';
                }
                if (node.textContent) {
                    result += ">" + node.textContent + "</" + node.tagName + ">";
                } else {
                    result += " />";
                }
                return result;
            }

            /**
             * The SVGPlotHandler will handle SVGPlotElements by converting them
             * to SVGPathElements. If the SVGPlotElement is part of an SVGDocument
             * it is replaced by the corresponding SVGPathElement.
             * 
             * @namespace SVGPlotHandler
             */
            return {
                /**
                 * The SVGPlotHandler's handle method.
                 * 
                 * @param {Element} plotElement The SVGPlotElement being handled.
                 * @throws {NotFoundError} When the 'function' attribute is not set.
                 * @returns {Element} The converted SVGPathElement
                 */
                handle: function(plotElement) {
                    var creator = new PathCreator();
                    creator.parseSVGPlotElement(plotElement);
                    if (!creator["function"].set) {
                        var error = new Error("Function not set: " + toString(plotElement));
                        error.name = "NotFoundError";
                        throw error;
                    }
                    creator.createPath();
                    if (plotElement.parentNode) {
                        plotElement.parentNode.replaceChild(creator.pathElement, plotElement);
                    }
                    return creator.pathElement;
                }
            };
        }
);
