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
        "MathematicalEngine",
        ["RandomNumberGenerator"],
        function(RandomNumberGenerator) {
            /**
             * This mathematical engine consits solely of functions, which either
             * make use of other internal functions, the JavaScript Math object
             * or the browser's mathematical engine.
             * 
             * @namespace MathematicalEngine
             */
            return {
                /**
                 * Evaluates the absolute value of x.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                abs: function(x) {
                    return Math.abs(x);
                },
                /**
                 * Arccosine of x in degrees.
                 * The result is in the range [0&deg;, 180&deg;].
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                acos: function(x) {
                    return this.deg(Math.acos(x));
                },
                /**
                 * Adds x and y.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                add: function(x, y) {
                    return x + y;
                },
                /**
                 * This returns 1 if x and y both evaluate to non-zero values.
                 * Otherwise 0 is returned.
                 * 
                 * @param {Object} x
                 * @param {Object} y
                 * @returns {0|1}
                 */
                and: function(x, y) {
                    return x != 0 && y != 0 ? 1 : 0;
                },
                /**
                 * Arcsine of x.
                 * The result is in degrees and in the range &plusmn;90&deg;.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                asin: function(x) {
                    return this.deg(Math.asin(x));
                },
                /**
                 * Arctangent of x in degrees.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                atan: function(x) {
                    return this.deg(Math.atan(x));
                },
                /**
                 * Arctangent of y &divide; x in degrees.
                 * This also takes into account the quadrants.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                atan2: function(x, y) {
                    return this.ifthenelse(this.not(y), this.ifthenelse(this.less(x, 0), 180, 0), this.multiply(2, this.atan(this.divide(y, this.add(this.veclen(x, y), x)))));
                },
                /**
                 * Rounds x up to the nearest integer.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                ceil: function(x) {
                    return Math.ceil(x);
                },
                /**
                 * Cosine of x. By employing the r operator, x can be in radians.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                cos: function(x) {
                    return Math.cos(this.rad(x));
                },
                /**
                 * Cosecant of x. By employing the r operator, x can be in radians.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                cosec: function(x) {
                    return this.divide(1, this.sin(x));
                },
                /**
                 * The hyperbolic cosine of x.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                cosh: function(x) {
                    return this.multiply(0.5, this.add(this.exp(x), this.exp(-x)));
                },
                /**
                 * Cotangent of x. By employing the r operator, x can be in radians.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                cot: function(x) {
                    return this.divide(1, this.tan(x));
                },
                /**
                 * Convert x to degrees. x is assumed to be in radians.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                deg: function(x) {
                    return this.multiply(x, this.divide(180, this.pi()));
                },
                /**
                 * Divide x by y and round to the nearest integer.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Integer}
                 */
                div: function(x, y) {
                    return this.int(this.round(this.divide(x, y)));
                },
                /**
                 * Divide x by y.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                divide: function(x, y) {
                    return x / y;
                },
                /**
                 * Returns Euler's number (e, the base of the natural logarithm).
                 * 
                 * @returns {Number}
                 */
                e: function() {
                    return Math.E;
                },
                /**
                 * This returns 1 if x === y and 0 otherwise.
                 * 
                 * @param {Object} x
                 * @param {Object} y
                 * @returns {0|1}
                 */
                equal: function(x, y) {
                    return x == y ? 1 : 0;
                },
                /**
                 * Returns the value of e^x, where e is Euler's number and x is
                 * the power.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                exp: function(x) {
                    return Math.exp(x);
                },
                /**
                 * Return x!.
                 * 
                 * @param {Integer} x
                 * @returns {Number}
                 */
                factorial: function(x) {
                    return this.int(x) === 0 ? 1 : this.multiply(this.int(x), this.factorial(this.subtract(x, 1)));
                },
                /**
                 * This evaluates to 0.
                 * 
                 * @returns {0}
                 */
                false: function() {
                    return 0;
                },
                /**
                 * Rounds x down to the nearest integer.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                floor: function(x) {
                    return Math.floor(x);
                },
                /**
                 * Returns the fractional part of x .
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                frac: function(x) {
                    return x % 1;
                },
                /**
                 * This returns 1 if x > y and 0 otherwise.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {0|1}
                 */
                greater: function(x, y) {
                    return x > y ? 1 : 0;
                },
                /**
                 * Returns the integer part of x .
                 * 
                 * @param {Number} x
                 * @returns {Integer}
                 */
                int: function(x) {
                    return 0 | x;
                },
                /**
                 * This returns y if x evaluates to some non-zero value,
                 * otherwise z is returned.
                 * 
                 * @param {Object} x
                 * @param {Object} y
                 * @param {Object} z
                 * @returns {Object}
                 */
                ifthenelse: function(x, y, z) {
                    return x != 0 ? y : z;
                },
                /**
                 * This returns 1 if x < y and 0 otherwise.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {0|1}
                 */
                less: function(x, y) {
                    return x < y ? 1 : 0;
                },
                /**
                 * Returns the natural logarithm of the given number.
                 * Returns the power to which the base e (Euler's number) must be
                 * raised to obtain the given number.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                ln: function(x) {
                    return Math.log(x);
                },
                /**
                 * Returns the logarithm in base 10 of the given number.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                log10: function(x) {
                    return this.divide(this.ln(x), this.ln(10));
                },
                /**
                 * Returns the logarithm in base 2 of the given number.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                log2: function(x) {
                    return this.divide(this.ln(x), this.ln(2));
                },
                /**
                 * Return the maximum value from x1...xn.
                 * 
                 * @returns {Number}
                 */
                max: function() {
                    var m = Number.NEGATIVE_INFINITY;
                    for (var i = 0; i < arguments.length; i++) {
                        m = Math.max(m, arguments[i]);
                    }
                    return m;
                },
                /**
                 * Return the minimum value from x1...xn.
                 * 
                 * @returns {Number}
                 */
                min: function() {
                    var m = Number.POSITIVE_INFINITY;
                    for (var i = 0; i < arguments.length; i++) {
                        m = Math.min(m, arguments[i]);
                    }
                    return m;
                },
                /**
                 * This evaluates x modulo y, using truncated division.
                 * The sign of the result is the same as the sign of x/y.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                mod: function(x, y) {
                    return this.subtract(x, this.multiply(y, this.int(this.divide(x, y))));
                },
                /**
                 * This evaluates x modulo y, using floored division.
                 * The sign of the result is never negative.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                Mod: function(x, y) {
                    return this.subtract(x, this.multiply(y, this.floor(this.divide(x, y))));
                },
                /**
                 * Multiply x by y.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                multiply: function(x, y) {
                    return x * y;
                },
                /**
                 * This returns −x.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                neg: function(x) {
                    return -x;
                },
                /**
                 * This returns 1 if x evaluates to zero, otherwise 0.
                 * 
                 * @param {Object} x
                 * @returns {0|1}
                 */
                not: function(x) {
                    return x == 0 ? 1 : 0;
                },
                /**
                 * This returns 0 if x === y and 1 otherwise.
                 * 
                 * @param {Object} x
                 * @param {Object} y
                 * @returns {0|1}
                 */
                notequal: function(x, y) {
                    return this.not(this.equal(x, y));
                },
                /**
                 * This returns 1 if x &le; y and 0 otherwise.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {0|1}
                 */
                notgreater: function(x, y) {
                    return this.not(this.greater(x, y));
                },
                /**
                 * This returns 1 if x &ge; y and 0 otherwise.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {0|1}
                 */
                notless: function(x, y) {
                    return this.not(this.less(x, y));
                },
                /**
                 * This returns 1 if either x or y evaluate to non-zero values.
                 * Otherwise 0 is returned.
                 * 
                 * @param {Object} x
                 * @param {Object} y
                 * @returns {0|1}
                 */
                or: function(x, y) {
                    return x != 0 || y != 0 ? 1 : 0;
                },
                /**
                 * Represents the ratio of any circle's circumference to its
                 * diameter in Euclidean geometry.
                 * 
                 * @returns {Number}
                 */
                pi: function() {
                    return Math.PI;
                },
                /**
                 * Raises x to the power y.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                pow: function(x, y) {
                    return Math.pow(x, y);
                },
                /**
                 * Convert x to radians. x is assumed to be in degrees.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                rad: function(x) {
                    return this.multiply(x, this.divide(this.pi(), 180));
                },
                /**
                 * Generates a pseudo-random number between -1 and 1.
                 * 
                 * @returns {Number}
                 */
                rand: function() {
                    return RandomNumberGenerator.random(-1, 1);
                },
                /**
                 * This function takes zero, one or two arguments.
                 * If there are zero arguments, a random number between 0 and 1
                 * is generated. If there is one argument x, a random integer
                 * between 1 and x is generated. Finally, if there are two arguments,
                 * a random integer between x and y is generated.
                 * 
                 * @returns {Number}
                 */
                random: function() {
                    var a = 0, b = 1;
                    if (arguments.length > 0) {
                        a = 1;
                        b = arguments[0];
                    }
                    if (arguments.length > 1) {
                        a = b;
                        b = arguments[1];
                    }
                    return RandomNumberGenerator.random(a, b);
                },
                /**
                 * Ensures x contains a decimal point.
                 * 
                 * @param {Object} x
                 * @returns {Number}
                 */
                real: function(x) {
                    return new Number(x);
                },
                /**
                 * Generates a pseudo-random number between 0 and 1.
                 * 
                 * @returns {Number}
                 */
                rnd: function() {
                    return RandomNumberGenerator.random();
                },
                /**
                 * Rounds x to the nearest integer. It uses "asymmetric half-up"
                 * rounding. So 1.5 is rounded to 2, but -1.5 is rounded to -2 (not 1).
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                round: function(x) {
                    return this.multiply(this.subtract(this.greater(x, 0), this.less(x, 0)), Math.round(this.abs(x)));
                },
                /**
                 * Secant of x. By employing the r operator, x can be in radians.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                sec: function(x) {
                    return this.divide(1, this.cos(x));
                },
                /**
                 * Sine of x. By employing the r operator, x can be in radians.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                sin: function(x) {
                    return Math.sin(this.rad(x));
                },
                /**
                 * The hyperbolic sine of x.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                sinh: function(x) {
                    return this.multiply(0.5, this.subtract(this.exp(x), this.exp(-x)));
                },
                /**
                 * Calculates &radic;x.
                 * @param {Number} x
                 * @returns {Number}
                 */
                sqrt: function(x) {
                    return Math.sqrt(x);
                },
                /**
                 * Subtract x from y.
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                subtract: function(x, y) {
                    return x - y;
                },
                /**
                 * Tangent of x. By employing the r operator, x can be in radians.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                tan: function(x) {
                    return Math.tan(this.rad(x));
                },
                /**
                 * The hyperbolic tangent of x.
                 * 
                 * @param {Number} x
                 * @returns {Number}
                 */
                tanh: function(x) {
                    return this.divide(this.sinh(x), this.cosh(x));
                },
                /**
                 * This evaluates to 1.
                 * 
                 * @returns {1}
                 */
                true: function() {
                    return 1;
                },
                /**
                 * Calculates &radic;(x&sup2; + y&sup2;).
                 * 
                 * @param {Number} x
                 * @param {Number} y
                 * @returns {Number}
                 */
                veclen: function(x, y) {
                    return this.sqrt(this.add(this.pow(x, 2), this.pow(y, 2)));
                }
            };
        }
);
