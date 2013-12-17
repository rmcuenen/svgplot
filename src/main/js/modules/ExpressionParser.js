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
        function(ME) {
            /**
             * @class This object represents the parse tree. It implements the
             *        visitor pattern where the nodes are stored as 'actions'
             *        in a action list.
             * @name Visitor
             * @property {Object[]} ActionList The action list holding the 'actions' to visit.
             * @property {Object[]} Stack The stack where intermediate ressults can be moved to.
             * @property {String} Variable The string that should be replaced by a given value.
             * @property {Object} Result The (current) result.
             */
            function Visitor() {
                this.ActionList = [];
                this.Stack = [];
            }

            /**
             * @lends Visitor
             */
            Visitor.prototype = {
                /**
                 * Sets the variable for which variable substitution actions should
                 * insert a given value during the visiting of the parse tree.
                 * 
                 * @param {String} variable The variable token (including '#').
                 */
                setVariable: function(variable) {
                    this.Variable = variable.substring(1);
                },
                /**
                 * Visits all the 'actions' of the parse tree.
                 * 
                 * @param {Object} value The variable substitution value.
                 * @returns {Object} The result of this parse tree visit.
                 */
                visit: function(value) {
                    this.Stack = [];
                    this.ActionList.forEach(function(action) {
                        action.visit(value);
                    });
                    return this.Result;
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
             * @name InputStream
             * @param {String} string The input characters for the stream.
             * @property {String} stream The string that represents the character stream.
             * @property {Integer} pos The current position in the charater stream.
             */
            function InputStream(string) {
                this.stream = string;
                this.pos = 0;
            }

            /**
             * @lends InputStream
             */
            InputStream.prototype = {
                /**
                 * Read one character from the stream.
                 * 
                 * @returns {String} The current character of the stream.
                 */
                read: function() {
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
                 */
                flush: function() {
                    if (this.pos <= this.stream.length) {
                        throw "Unrecognized character: " + this.split();
                    }
                },
                /**
                 * Split the total character stream at the current positon.
                 * 
                 * @returns {String} The string representation of the character stream
                 *                   with square brackets around the current position.
                 */
                split: function() {
                    return this.stream.substring(0, this.pos - 1) +
                            '[' + this.stream.charAt(this.pos - 1) + ']' +
                            this.stream.substring(this.pos);
                }
            };

            /**
             * @class The Interpreter provides functions to put parsed and reconized
             *        tokens in the action list of the parse tree.
             * @name Interpreter
             * @param {Visitor} visitor The parse tree.
             * @property {Visitor} ParseTree Reference to the parse tree that
             *                                 implements the visitor pattern.
             */
            function Interpreter(visitor) {
                this.ParseTree = visitor;
            }

            /**
             * @lends Interpreter
             */
            Interpreter.prototype = {
                /**
                 * Puts a literal (numerical) value on the action list.
                 * 
                 * @param {Number} value The literal value.
                 */
                literal: function(value) {
                    var context = this.ParseTree;
                    this.ParseTree.ActionList.push({
                        visit: function() {
                            context.Result = value;
                        }
                    });
                },
                /**
                 * Puts a 'move to stack' action on the action list.
                 */
                move: function() {
                    var context = this.ParseTree;
                    this.ParseTree.ActionList.push({
                        visit: function() {
                            context.Stack.unshift(context.Result);
                        }
                    });
                },
                /**
                 * Puts a variable substitution action on the action list.
                 * 
                 * @param {String} variable The variable name.
                 */
                variable: function(variable) {
                    var context = this.ParseTree;
                    this.ParseTree.ActionList.push({
                        visit: function(value) {
                            if (variable !== context.Variable) {
                                throw "Unknown variable '#" + variable + "'";
                            }
                            context.Result = value;
                        }
                    });
                },
                /**
                 * Puts a function evaluation action on the action list.
                 * 
                 * @param {String} name The name of the function to be evaluated.
                 * @param {Integer} paramCount The number of function parameters.
                 */
                function: function(name, paramCount) {
                    if (typeof ME[name] === 'undefined') {
                        throw "Unknown function '" + name + "'";
                    }
                    var context = this.ParseTree;
                    this.ParseTree.ActionList.push({
                        visit: function() {
                            var count = paramCount | 0;
                            var parameters = count === 0 ? [] : [context.Result];
                            for (var i = 1; i < count; ++i) {
                                parameters.unshift(context.Stack.shift());
                            }
                            context.Result = ME[name].apply(ME, parameters);
                        }
                    });
                }
            };

            /**
             * @class The Parser parses an input stream and converts the reconized
             *        tokens into action nodes for the parse tree interpreter.
             * @name Parser
             * @param {InputStream} input The character stream to be parsed.
             * @param {Interpreter} tree The interpreter that takes the actions.
             * @property {InputStream} Input Reference to the character input stream.
             * @property {Interpreter} Tree Reference to the parse tree interpreter.
             */
            function Parser(input, tree) {
                this.Input = input;
                this.Tree = tree;
            }

            /**
             * @lends Parser
             */
            Parser.prototype = {
                /**
                 * The character we are looking at.
                 * 
                 * @type String
                 */
                Look: '',
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
                RelOp: ['=', '!', '<', '>'],
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
                 * Throws an exception idicating that something was expected.
                 * 
                 * @param {String} s The string describing what was expected.
                 */
                Expected: function(s) {
                    throw s + " Expected at '" + this.Input.split() + "'";
                },
                /**
                 * Retrieve the next character from the input stream.
                 */
                GetChar: function() {
                    this.Look = this.Input.read();
                },
                /**
                 * Keep reading whitespace characters to skip parsing.
                 */
                SkipWhite: function() {
                    while (this.Look === ' ' || this.Look === '\t') {
                        this.GetChar();
                    }
                },
                /**
                 * Parses a varibale token (after the '#' character).
                 */
                Var: function() {
                    var Token = '';
                    if (!/[a-zA-Z]/.test(this.Look)) {
                        this.Expected("Variable");
                    }
                    while (/[a-zA-Z0-9_]/.test(this.Look)) {
                        Token += this.Look;
                        this.GetChar();
                    }
                    this.SkipWhite();
                    this.Tree.variable(Token);
                },
                /**
                 * Parses a function token.
                 */
                Func: function() {
                    var Name = '';
                    if (!/[a-z]/.test(this.Look)) {
                        this.Expected("Function");
                    }
                    while (/[a-z0-9_]/.test(this.Look)) {
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
                            while (this.Look === ',') {
                                this.Tree.move();
                                this.Match(',');
                                this.Special();
                                count++;
                            }
                        }
                        this.Match(')');
                    }
                    this.Tree.function(Name, count);
                },
                /**
                 * Parses a number token.
                 */
                Num: function() {
                    var Value = this.Look;
                    if (!/[0-9\.]/.test(this.Look)) {
                        this.Expected("Number");
                    }
                    switch (this.Look) {
                        case '0':
                            this.GetChar();
                            if (this.Look === '.') {
                                Value += this.Look;
                                Value += this.Frac();
                            }
                            break;
                        case '.':
                            Value += this.Frac();
                            break;
                        default:
                            this.GetChar();
                            Value += this.Int(true);
                            if (this.Look === '.') {
                                Value += this.Look;
                                Value += this.Frac();
                            } else {
                                Value += this.Exp();
                            }
                            break;
                    }
                    this.SkipWhite();
                    this.Tree.literal(new Number(Value));
                },
                Frac: function() {
                    var Value = '';
                    if (this.Look !== '.') {
                        this.Expected("'.'");
                    }
                    this.GetChar();
                    Value += this.Int(false);
                    Value += this.Exp();
                    return Value;
                },
                Int: function(empty) {
                    var Value = '';
                    while (/[0-9]/.test(this.Look)) {
                        Value += this.Look;
                        this.GetChar();
                    }
                    if (Value.length === 0 && !empty) {
                        this.Expected("Integer");
                    }
                    return Value;
                },
                Exp: function() {
                    var Value = '';
                    if (this.Look === 'e' || this.Look === 'E') {
                        Value += this.Look;
                        this.GetChar();
                        if (this.Look === '+' || this.Look === '-') {
                            Value += this.Look;
                            this.GetChar();
                        }
                        if (!/[1-9]/.test(this.Look)) {
                            this.Expected("Non-Zero");
                        }
                        Value += this.Look;
                        this.GetChar();
                        Value += this.Int(true);
                    }
                    return Value;
                },
                Match: function(x) {
                    if (this.Look !== x) {
                        this.Expected("'" + x + "'");
                    } else {
                        this.GetChar();
                        this.SkipWhite();
                    }
                },
                Fragment: function() {
                    if (this.Look === '(') {
                        this.Match('(');
                        this.Special();
                        this.Match(')');
                    } else if (this.Look === '#') {
                        this.Match('#');
                        this.Var();
                    } else if (/[a-z]/.test(this.Look)) {
                        this.Func();
                    } else {
                        this.Num();
                    }
                    if (this.Look === this.FacOp[0]) {
                        this.Match(this.FacOp[0]);
                        this.Tree.function("factorial", 1);
                    }
                },
                SignedFragment: function() {
                    var index = this.NegNotOp.indexOf(this.Look);
                    if (index === -1) {
                        this.Fragment();
                    } else {
                        this.Tree.move();
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
                        this.Tree.function(func, 1);
                    }
                },
                Factor: function() {
                    this.Fragment();
                    while (this.Look === this.PowOp[0]) {
                        this.Tree.move();
                        this.Match(this.PowOp[0]);
                        this.SignedFragment();
                        this.Tree.function("pow", 2);
                    }
                    if (this.Look === this.DegOp[0]) {
                        this.Match(this.DegOp[0]);
                        this.Tree.function("deg", 1);
                    }
                },
                SignedFactor: function() {
                    var index = this.NegNotOp.indexOf(this.Look);
                    if (index === -1) {
                        this.Factor();
                    } else {
                        this.Tree.move();
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
                        this.Tree.function(func, 1);
                    }
                },
                Term: function() {
                    this.SignedFactor();
                    var index = this.MultDevAndOp.indexOf(this.Look);
                    while (index !== -1) {
                        this.Tree.move();
                        this.Match(this.MultDevAndOp[index]);
                        var func;
                        switch (index) {
                            case 0:
                                func = "multuply";
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
                        this.Tree.function(func, 2);
                        index = this.MultDevAndOp.indexOf(this.Look);
                    }
                },
                Expression: function() {
                    this.Term();
                    var index = this.AddSubOrOp.indexOf(this.Look);
                    while (index !== -1) {
                        this.Tree.move();
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
                        this.Tree.function(func, 2);
                        index = this.AddSubOrOp.indexOf(this.Look);
                    }
                },
                Relation: function() {
                    this.Expression();
                    var index = this.RelOp.indexOf(this.Look);
                    while (index !== -1) {
                        this.Tree.move();
                        this.Match(this.RelOp[index]);
                        var func;
                        switch (index) {
                            case 0:
                                this.Match('=');
                                func = "equal";
                                break;
                            case 1:
                                this.Match('=');
                                func = "notequal";
                                break;
                            case 2:
                                func = "less";
                                if (this.Look === '=') {
                                    this.Match('=');
                                    func = "notgreater";
                                }
                                break;
                            case 3:
                                func = "greater";
                                if (this.Look === '=') {
                                    this.Match('=');
                                    func = "notless";
                                }
                        }
                        this.Expression();
                        this.Tree.function(func, 2);
                        index = this.RelOp.indexOf(this.Look);
                    }
                },
                Special: function() {
                    this.Relation();
                    if (this.Look === this.IfOp[0]) {
                        this.Tree.move();
                        this.Match(this.IfOp[0]);
                        this.Relation();
                        this.Tree.move();
                        this.Match(this.ElOp[0]);
                        this.Relation();
                        this.Tree.function("ifthenelse", 3);
                    }
                },
                parse: function() {
                    this.GetChar();
                    this.SkipWhite();
                    this.Special();
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
                 * Parses the given exprerssion into a parse tree.
                 * 
                 * @param {String} expression The expression string.
                 * @returns {Visitor} The resulting parse tree.
                 */
                parse: function(expression) {
                    var input = new InputStream(expression);
                    var visitor = new Visitor();
                    var tree = new Interpreter(visitor);
                    var parser = new Parser(input, tree);
                    parser.parse();
                    input.flush();
                    return visitor;
                }
            };
        });