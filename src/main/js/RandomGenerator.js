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
        "RandomGenerator",
        [],
        function() {
            var Ar = 16807;
            var pr = 2147483647;
            var ql = 127773;
            var rr = 2836;
            var conv = 1.0 / (pr - 1);
            var seed = new Date().getTime() % pr;
            return {
                random: function() {
                    var l = parseInt(seed / ql);
                    seed = Ar * (seed - ql * l) - rr * l;
                    if (seed < 0) {
                        seed += pr;
                    }
                    var r = conv * (seed - 1);
                    if (arguments.length === 2) {
                        r = arguments[0] + (arguments[1] - arguments[0]) * r;
                    }
                    return r;
                }
            };
        }
);
