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
        ["SVGPlotter"],
        function(SVGPlotter) {
            /**
             * Callback method for when the whole SVGDocument is loaded.
             */
            function DOMParser() {
                handle(document.documentElement);
                /* Listen for document changes. */
                document.addEventListener("DOMSubtreeModified", function(event) {
                    handle(event.target);
                }, false);
            }

            /**
             * This method inspects the given element. If it is an SVGPlotElement
             * it is delegated to the SVGPlotter, otherwise its children are
             * inspected recursively.
             * 
             * @param {Node} element The element being inspected.
             */
            function handle(element) {
                if (element.nodeName.toLowerCase() === "plot") {
                    SVGPlotter.handle(element);
                } else {
                    for (var i = 0; i < element.childElementCount; i++) {
                        handle(element.children[i]);
                    }
                }
            }

            if (["interactive", "complete"].indexOf(document.readyState) !== -1) {
                /* Document is already loaded; start parsing. */
                DOMParser();
            } else {
                /* Register the callback method. */
                document.addEventListener("DOMContentLoaded", DOMParser, false);
            }
        }
);
