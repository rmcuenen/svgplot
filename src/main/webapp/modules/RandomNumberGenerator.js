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
        "RandomNumberGenerator",
        [],
        function () {
            /**
             * The Euclidean algorithm to determine the greatest common divisor
             * (GCD) between two natural numbers.
             * 
             * @param {Number} a The first natural number.
             * @param {Number} b The second natural number.
             * @returns {Number} The greatest common divisor.
             */
            function gcd(a, b) {
                if (b === 0) {
                    return a;
                } else {
                    return gcd(b, a % b);
                }
            }

            /**
             * The multiplier (7^5) of the RNG's recurrence relation.
             * 
             * @constant
             * @type Number
             */
            var Ar = 16807;

            /**
             * The modulus (2^31 - 1) of the RNG's recurrence relation.
             * 
             * @constant
             * @type Number
             */
            var pr = 2147483647;

            /**
             * Define the integer part of the RNG's modulus divided by the RNG's 
             * multiplier to use the trick of Linus Schrage. 
             * 
             * @constant
             * @type Number
             */
            var ql = 127773;

            /**
             * Define the remainder of the RNG's modules divided by the RNG's
             * multiplier to use the trick of Linus Schrage.
             * 
             * @constant
             * @type Number
             */
            var rr = 2836;

            /**
             * Factor to convert the LCG's integers into pseudo-random numbers
             * between 0 and 1.
             * 
             * @constant
             * @type Number
             */
            var conv = 1.0 / (pr - 1);

            /**
             * The seed (based on the system time) of the RNG's recurrence relation.
             * 
             * @static
             * @type Number
             */
            var seed = new Date().getTime() % pr;

            while (gcd(seed, pr) > 1) {
                seed = (seed + 1) % pr;
            }

            /**
             * A linear congruential generator (LCG). More specifically a (Derrick
             * Henry "Dick") Lehmer random number generator (RNG) with particular
             * parameters suggested by Stephen K. Park and Keith W. Miller, now
             * known as MINSTD.
             * 
             * @namespace RamdomNumberGenerator
             */
            return {
                /**
                 * This function takes zero or two arguments. If there are zero
                 * arguments, a random number between 0 and 1 is generated. If
                 * there are two arguments, a random number between x and y is
                 * generated.
                 * 
                 * @returns {Number} The generated pseudo-random number.
                 */
                random: function () {
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
