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
        "ExpressionParser",
        ["MathematicalEngine"],
        function (MathematicalEngine) {
            /**
             * @class This object represents the parse tree. It implements the
             *        visitor pattern where the nodes are stored as 'actions'
             *        in an action list.
             * @name Visitor
             * @param {Object[]} actionList The action list holding the 'actions' to visit.
             * @property {Object[]} ActionList The action list holding the 'actions' to visit.
             */
            function Visitor(actionList) {
                this.ActionList = actionList;
            }

            /**
             * @lends Visitor
             */
            Visitor.prototype = {
                /**
                 * Visits all the 'actions' of the parse tree.
                 * 
                 * @param {Object} variables The variables and their substitution values.
                 * @returns {Object} The result of this parse tree visit.
                 */
                visit: function (variables) {
                    var Stack = [];
                    var Result = 0;
                    for (var i = 0; i < this.ActionList.length; i++) {
                        Result = this.ActionList[i].apply(Result, Stack, variables);
                    }
                    return Result;
                }
            };

            /**
             * This object represents the character stream that is
             * being parsed.
             * 
             * @type Object
             */

            /**
             * @class This object represents the character stream that is being parsed.
             * @name CharacterStream
             * @param {String} string The input characters for the stream.
             * @property {String} stream The string that represents the character stream.
             * @property {Integer} pos The current position in the character stream.
             */
            function CharacterStream(string) {
                this.stream = string;
                this.pos = 0;
            }

            /**
             * @lends CharacterStream
             */
            CharacterStream.prototype = {
                /**
                 * Read one character from the stream.
                 * 
                 * @returns {String} The current character of the stream.
                 */
                read: function () {
                    if (this.pos >= this.stream.length) {
                        this.pos = this.stream.length + 1;
                        return '';
                    }
                    return this.stream.charAt(this.pos++);
                },
                /**
                 * Flush the stream.
                 * If there are still characters in the stream
                 * an exception is thrown.
                 * 
                 * @throws {Exception} When the current position is not at the
                 *                     end of the stream.
                 */
                flush: function () {
                    if (this.pos <= this.stream.length) {
                        var error = new Error("Unrecognized character: " + this.split());
                        error.name = "ParseError";
                        throw error;
                    }
                },
                /**
                 * Split the total character stream at the current position.
                 * 
                 * @returns {String} The string representation of the character stream
                 *                   with square brackets around the current position.
                 */
                split: function () {
                    var char = this.stream.charAt(this.pos - 1);
                    if (typeof char === 'undefined') {
                        char = '';
                    }
                    return this.stream.substring(0, this.pos - 1) +
                            '[' + char + ']' +
                            this.stream.substring(this.pos);
                }
            };

            /**
             * @class The Parser parses an input stream and converts the recognized
             *        tokens into action nodes for the parse tree.
             * @name Parser
             * @param {CharacterStream} input The character stream to be parsed.
             * @property {CharacterStream} Input Reference to the character input stream.
             * @property {Object[]} ActionList The action list holding the 'actions' to visit.
             * @property {String} Look The character we are looking at.
             */
            function Parser(input) {
                this.Input = input;
                this.ActionList = [];
                this.Look = '';
            }

            /**
             * @lends Parser
             */
            Parser.prototype = {
                /**
                 * The if-operator.
                 * 
                 * @type String[]
                 */
                IfOp: ['?'],
                /**
                 * The else-operator.
                 * 
                 * @type String[]
                 */
                ElOp: [':'],
                /**
                 * The relational operators (equals, less, etc.).
                 * 
                 * @type String[]
                 */
                RelOp: ['=', '<', '>'],
                /**
                 * The add- subtract- and or-operators.
                 * 
                 * @type String[]
                 */
                AddSubOrOp: ['+', '-', '|'],
                /**
                 * The multiply- divide- and and-operators.
                 * 
                 * @type String[]
                 */
                MultDevAndOp: ['*', '/', '&'],
                /**
                 * The negate- and not-operators.
                 * 
                 * @type String[]
                 */
                NegNotOp: ['-', '!'],
                /**
                 * The power-operator.
                 * 
                 * @type String[]
                 */
                PowOp: ['^'],
                /**
                 * The degree-operator.
                 * 
                 * @type String[]
                 */
                DegOp: ['r'],
                /**
                 * The factorial-operator.
                 * 
                 * @type String[]
                 */
                FacOp: ['!'],
                /**
                 * Puts a literal (numerical) value on the action list.
                 * 
                 * @param {Number} value The literal value.
                 */
                Literal: function (value) {
                    this.ActionList.push({
                        apply: function (result, stack, variables) {
                            return value;
                        }
                    });
                },
                /**
                 * Puts a 'move to stack' action on the action list.
                 */
                Move: function () {
                    this.ActionList.push({
                        apply: function (result, stack, variables) {
                            stack.unshift(result);
                            return result;
                        }
                    });
                },
                /**
                 * Puts a variable substitution action on the action list.
                 * 
                 * @param {String} variable The variable name.
                 */
                Variable: function (variable) {
                    this.ActionList.push({
                        apply: function (result, stack, variables) {
                            var Variables = variables || {};
                            if (!Variables.hasOwnProperty(variable)) {
                                var error = new Error("Unknown variable '#" + variable + "'");
                                error.name = "NotFoundError";
                                throw error;
                            }
                            return Variables[variable];
                        }
                    });
                },
                /**
                 * Puts a function evaluation action on the action list.
                 * 
                 * @param {String} name The name of the function to be evaluated.
                 * @param {Integer} paramCount The number of function parameters.
                 * @throws {NotFoundError} When the MathematicalEngine does not
                 *                         have a function with the given name.
                 */
                Function: function (name, paramCount) {
                    if (typeof MathematicalEngine[name] === 'undefined') {
                        var error = new Error("Unknown function '" + name + "'");
                        error.name = "NotFoundError";
                        throw error;
                    }
                    this.ActionList.push({
                        apply: function (result, stack, variables) {
                            var count = paramCount | 0;
                            var parameters = count === 0 ? [] : [result];
                            for (var i = 1; i < count; ++i) {
                                parameters.unshift(stack.shift());
                            }
                            return MathematicalEngine[name].apply(MathematicalEngine, parameters);
                        }
                    });
                },
                /**
                 * Throws an exception indicating that something was expected.
                 * 
                 * @param {String} s The string describing what was expected.
                 * @throws {ParseError}
                 */
                Expected: function (s) {
                    var error = new Error(s + " Expected at '" + this.Input.split() + "'");
                    error.name = "ParseError";
                    throw error;
                },
                /**
                 * Retrieve the next character from the input stream.
                 */
                GetChar: function () {
                    this.Look = this.Input.read();
                },
                /**
                 * Keep reading white-space characters to skip parsing.
                 */
                SkipWhite: function () {
                    while (this.Look === ' ' || this.Look === '\t') {
                        this.GetChar();
                    }
                },
                /**
                 * Parses a variable token (after the '#' character).
                 * 
                 * Grammar:
                 * variable := '#' [a-zA-Z] [a-zA-Z0-9_]*
                 */
                Var: function () {
                    var Token = '';
                    if (!/[a-zA-Z]/.test(this.Look)) { // [a-zA-Z]
                        this.Expected("Variable");
                    }
                    while (/[a-zA-Z0-9_]/.test(this.Look)) { // [a-zA-z0-9_]*
                        Token += this.Look;
                        this.GetChar();
                    }
                    this.SkipWhite();
                    this.Variable(Token);
                },
                /**
                 * Parses a function token.
                 * 
                 * Grammar:
                 * function := [a-z] [a-z0-9_]* ('(' special? | special (',' special)* ')')?
                 */
                Func: function () {
                    var Name = '';
                    if (!/[a-z]/.test(this.Look)) { // [a-z]
                        this.Expected("Function");
                    }
                    while (/[a-z0-9_]/.test(this.Look)) { // [a-z0-9_]*
                        Name += this.Look;
                        this.GetChar();
                    }
                    this.SkipWhite();
                    var count = 0;
                    if (this.Look === '(') {
                        this.Match('(');
                        if (this.Look !== ')') {
                            this.Special();
                            count++;
                            while (this.Look === ',') { // (',' special)*
                                this.Move();
                                this.Match(',');
                                this.Special();
                                count++;
                            }
                        }
                        this.Match(')');
                    }
                    this.Function(Name, count);
                },
                /**
                 * Parses a number token.
                 * 
                 * Grammar:
                 * number := zero ('.' fraction)? | '.' fraction | non-zero integer? ('.' fraction | exponent)?
                 * zero := '0'
                 * non-zero := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
                 */
                Num: function () {
                    var Value = this.Look;
                    if (!/[0-9\.]/.test(this.Look)) { // zero | non-zero | '.'
                        this.Expected("Number");
                    }
                    switch (this.Look) {
                        case '0': // zero
                            this.GetChar();
                            if (this.Look === '.') { // ('.' fraction)?
                                Value += this.Look;
                                Value += this.Frac();
                            }
                            break;
                        case '.': // '.' fraction
                            Value += this.Frac();
                            break;
                        default: // non-zero
                            this.GetChar();
                            Value += this.Int(false); // integer?
                            if (this.Look === '.') { // ('.' fraction)?
                                Value += this.Look;
                                Value += this.Frac();
                            } else { // exponent?
                                Value += this.Exp();
                            }
                            break;
                    }
                    this.SkipWhite();
                    this.Literal(new Number(Value));
                },
                /**
                 * Parses and returns the fraction fragment of a number token.
                 * The current character is assumed to be the '.' character but
                 * is not included into the result.
                 * 
                 * Grammar:
                 * fraction := integer exponent?
                 * 
                 * @returns {String} The fraction part of a number or an empty
                 *                   string when no fraction fragment is present.
                 */
                Frac: function () {
                    var Value = '';
                    if (this.Look !== '.') {
                        this.Expected("'.'");
                    }
                    this.GetChar();
                    Value += this.Int(true); // integer
                    Value += this.Exp(); // exponent?
                    return Value;
                },
                /**
                 * Parses and returns the integer fragment of a number token
                 * (required = false) or the integer part of an exponent part
                 * or a fraction fragment (required = true).
                 * 
                 * Grammar:
                 * integer := (zero | non-zero) (zero | non-zero)*
                 * zero := '0'
                 * non-zero := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
                 * 
                 * @param {Boolean} required Indicates whether or not the integer
                 *                           part is required.
                 * @returns {String} The parsed integer part, or an empty string
                 *                   when no integer part is present.
                 */
                Int: function (required) {
                    var Value = '';
                    while (/[0-9]/.test(this.Look)) { // (zero | non-zero)*
                        Value += this.Look;
                        this.GetChar();
                    }
                    if (Value.length === 0 && required) {
                        this.Expected("Integer");
                    }
                    return Value;
                },
                /**
                 * Parses and returns the exponent fragment of a number token
                 * or the exponent part of a fraction fragment.
                 * 
                 * Grammar:
                 * exponent := ('e' | 'E') ('+' | '-')? non-zero integer?
                 * non-zero := '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
                 * 
                 * @returns {String} The parsed exponent part, or an empty string
                 *                   when no exponent part is present.
                 */
                Exp: function () {
                    var Value = '';
                    if (this.Look === 'e' || this.Look === 'E') { // ('e' | 'E')
                        Value += this.Look;
                        this.GetChar();
                        if (this.Look === '+' || this.Look === '-') { // ('+' | '-')?
                            Value += this.Look;
                            this.GetChar();
                        }
                        if (!/[1-9]/.test(this.Look)) { // non-zero
                            this.Expected("Non-Zero");
                        }
                        Value += this.Look;
                        this.GetChar();
                        Value += this.Int(false); // integer?
                    }
                    return Value;
                },
                /**
                 * Match the current character in the input stream to the given
                 * character and skip to the next (non white space) character.
                 * 
                 * @param {String} x The character to match to.
                 * @throws {Exception} When the characters do not match.
                 */
                Match: function (x) {
                    if (this.Look !== x) {
                        this.Expected("'" + x + "'");
                    } else {
                        this.GetChar();
                        this.SkipWhite();
                    }
                },
                /**
                 * Parses a fragment token.
                 * 
                 * Grammar:
                 * fragment := ('(' special ')' | variable | function | number) FacOp?
                 */
                Fragment: function () {
                    if (this.Look === '(') { // '(' special ')'
                        this.Match('(');
                        this.Special();
                        this.Match(')');
                    } else if (this.Look === '#') { // variable
                        this.Match('#');
                        this.Var();
                    } else if (/[a-z]/.test(this.Look)) { // function
                        this.Func();
                    } else { // number
                        this.Num();
                    }
                    if (this.Look === this.FacOp[0]) { // FacOp?
                        this.Match(this.FacOp[0]);
                        this.Function("factorial", 1);
                    }
                },
                /**
                 * Parses a signed-fragment token.
                 * 
                 * Grammar:
                 * signed-fragment := NegNotOp? fragment
                 */
                SignedFragment: function () {
                    var index = this.NegNotOp.indexOf(this.Look);
                    if (index === -1) { // fragment
                        this.Fragment();
                    } else { // NegNotOp fragment
                        this.Move();
                        this.Match(this.NegNotOp[index]);
                        var func;
                        switch (index) {
                            case 0:
                                func = "neg";
                                break;
                            case 1:
                                func = "not";
                                break;
                        }
                        this.Fragment();
                        this.Function(func, 1);
                    }
                },
                /**
                 * Parses a factor token.
                 * 
                 * Grammar:
                 * factor := fragment (PowOp signed-fragment)* DegOp?
                 */
                Factor: function () {
                    this.Fragment(); // fragment
                    while (this.Look === this.PowOp[0]) { // (PowOp signed-fragment)*
                        this.Move();
                        this.Match(this.PowOp[0]);
                        this.SignedFragment();
                        this.Function("pow", 2);
                    }
                    if (this.Look === this.DegOp[0]) { // DegOp?
                        this.Match(this.DegOp[0]);
                        this.Function("deg", 1);
                    }
                },
                /**
                 * Parses a signed-factor token.
                 * 
                 * Grammar:
                 * signed-factor := NegNotOp? factor
                 */
                SignedFactor: function () {
                    var index = this.NegNotOp.indexOf(this.Look);
                    if (index === -1) { // factor
                        this.Factor();
                    } else { // NegNotOp factor
                        this.Move();
                        this.Match(this.NegNotOp[index]);
                        var func;
                        switch (index) {
                            case 0:
                                func = "neg";
                                break;
                            case 1:
                                func = "not";
                                break;
                        }
                        this.Factor();
                        this.Function(func, 1);
                    }
                },
                /**
                 * Parses a term token.
                 * 
                 * Grammar:
                 * term := signed-factor (MultDevAndOp signed-factor)*
                 */
                Term: function () {
                    this.SignedFactor(); // signed-factor
                    var index = this.MultDevAndOp.indexOf(this.Look);
                    while (index !== -1) { // (MultDevAndOp signed-factor)*
                        this.Move();
                        this.Match(this.MultDevAndOp[index]);
                        var func;
                        switch (index) {
                            case 0:
                                func = "multiply";
                                break;
                            case 1:
                                func = "divide";
                                break;
                            case 2:
                                this.Match('&');
                                func = "and";
                                break;
                        }
                        this.SignedFactor();
                        this.Function(func, 2);
                        index = this.MultDevAndOp.indexOf(this.Look);
                    }
                },
                /**
                 * Parses an expression token.
                 * 
                 * Grammar:
                 * expression := term (AddSubOrOp term)*
                 */
                Expression: function () {
                    this.Term(); // term
                    var index = this.AddSubOrOp.indexOf(this.Look);
                    while (index !== -1) { // (AddSubOrOp term)*
                        this.Move();
                        this.Match(this.AddSubOrOp[index]);
                        var func;
                        switch (index) {
                            case 0:
                                func = "add";
                                break;
                            case 1:
                                func = "subtract";
                                break;
                            case 2:
                                this.Match('|');
                                func = "or";
                                break;
                        }
                        this.Term();
                        this.Function(func, 2);
                        index = this.AddSubOrOp.indexOf(this.Look);
                    }
                },
                /**
                 * Parses a relation token.
                 * 
                 * Grammar:
                 * relation := expression (RelOp expression)*
                 */
                Relation: function () {
                    this.Expression(); // expression
                    var index = this.RelOp.indexOf(this.Look);
                    while (index !== -1) { // (RelOp expression)*
                        this.Move();
                        this.Match(this.RelOp[index]);
                        var func;
                        switch (index) {
                            case 0:
                                this.Match('=');
                                func = "equal";
                                break;
                            case 1:
                                func = "less";
                                if (this.Look === '=') {
                                    this.Match('=');
                                    func = "notgreater";
                                } else if (this.Look === '>') {
                                    this.Match('>');
                                    func = "notequal";
                                }
                                break;
                            case 2:
                                func = "greater";
                                if (this.Look === '=') {
                                    this.Match('=');
                                    func = "notless";
                                }
                        }
                        this.Expression();
                        this.Function(func, 2);
                        index = this.RelOp.indexOf(this.Look);
                    }
                },
                /**
                 * Parses a special token.
                 * 
                 * Grammar:
                 * special := relation (IfOp relation ElOp relation)?
                 */
                Special: function () {
                    this.Relation(); // relation
                    if (this.Look === this.IfOp[0]) { // (IfOp relation ElOp relation)?
                        this.Move();
                        this.Match(this.IfOp[0]);
                        this.Relation();
                        this.Move();
                        this.Match(this.ElOp[0]);
                        this.Relation();
                        this.Function("ifthenelse", 3);
                    }
                },
                /**
                 * Parse the character input stream into the parse tree.
                 * 
                 * @return {Visitor} The resulting parse tree.
                 */
                parse: function () {
                    this.GetChar();
                    this.SkipWhite();
                    this.Special();
                    this.Input.flush();
                    return new Visitor(this.ActionList);
                }
            };

            /**
             * An expression parser that parses a string into a parse tree.
             * The resulting parse tree implements the visitor pattern with which
             * the parse tree can be evaluated.
             * For the grammar refer to the project documentation.
             * 
             * @namespace ExpressionParser
             */
            return {
                /**
                 * Parses the given expression into a parse tree.
                 * 
                 * @param {String} expression The expression string.
                 * @returns {Visitor} The resulting parse tree.
                 */
                parse: function (expression) {
                    var input = new CharacterStream(expression);
                    var parser = new Parser(input);
                    return parser.parse();
                }
            };
        });
