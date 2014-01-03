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
        "DocumentScaler",
        [],
        function() {
            /**
             * Find the SVGDocument the element belongs to.
             * 
             * @param {Element} el The target element.
             * @returns {SVGDocument} The SVGDocument.
             */
            function findDocument(el) {
                var doc = el;
                while (doc.parentElement &&
                        doc.nodeName.toLowerCase() !== "svg") {
                    doc = doc.parentElement;
                }
                return doc;
            }

            /**
             * This interface can be used to scale the entire SVGDocument
             * to the dimensions of an SVGElement.
             * This is done by creating a viewport width the desired dimensions
             * and using a stroke width scaled with the current client dimensions.
             * 
             * @namespace DocumentScaler
             */
            return {
                /**
                 * This method will scale the SVGDocument to the dimensions of
                 * the given SVGElement.
                 * 
                 * @param {SVGElement} element The reference element used to scale
                 *                          the entire document.
                 */
                scaleTo: function(element) {
                    var doc = findDocument(element);
                    var box = element.getBBox();
                    var viewport = doc.createSVGRect();
                    viewport.x = Math.floor(box.x);
                    viewport.y = Math.floor(box.y);
                    viewport.width = box.width + 2 * (box.x - viewport.x);
                    viewport.height = box.height + 2 * (box.y - viewport.y);
                    doc.setAttribute("viewBox", [viewport.x, viewport.y,
                        viewport.width, viewport.height].join(' '));
                    var clientRect = doc.getBoundingClientRect();
                    doc.setAttribute("stroke-width",
                            2 * Math.max(viewport.width / clientRect.width,
                                    viewport.height / clientRect.height));
                }
            };
        }
);