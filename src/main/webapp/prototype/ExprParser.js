/**
 * Grammar:
 * <relation> ::= <expression> [<RelOp> <expression>]*
 * <expression> ::= <term> [<AddSubOrOp> <term>]*
 * <term> ::= <signed-factor> [<MultDevAndOp> <signed-factor>]*
 * <signed-factor> ::= <NegNotOp>? <factor>
 * <factor> ::= <fragment> [<PowOp> <signed-fragment>]* <DegOp>?
 * <signed-fragment> ::= <NegNotOp>? <fragment>
 * <fragment> ::= [( <relation> ) | <variable> | <function> | <number>] <FacOp>?
 *
 * <variable> ::= # [a-zA-Z] [a-zA-Z0-9_]*
 * <function> ::= [a-z] [a-z0-9_]* [( <relation>? | <relation> [, <relation>]* )]?
 * <number> ::= 
 * 
 * <RelOp> ::= == | < | > | <= | >= | !=
 * <AddSubOrOp> ::= + | - | ||
 * <MultDevAndOp> ::= * | / | &&
 * <NegNotOp> ::= - || !
 * <PowOp> ::= ^
 * <DegOp> ::= r
 * <FacOp> ::= !
 */
(function() {
    function gcd(a, b) {
        if (b === 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }
    var Ar = 16807;
    var pr = 2147483647;
    var ql = 127773;
    var rr = 2836;
    var conv = 1.0 / (pr - 1);
    var seed = new Date().getTime() % pr;

    while (gcd(seed, pr) > 1) {
        seed = (seed + 1) % pr;
    }

    var RNG = {
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

    var MathEngine = {
        abs: function(x) {
            return Math.abs(x);
        },
        acos: function(x) {
            return this.deg(Math.acos(x));
        },
        add: function(x, y) {
            return x + y;
        },
        and: function(x, y) {
            return x != 0 && y != 0 ? 1 : 0;
        },
        asin: function(x) {
            return this.deg(Math.asin(x));
        },
        atan: function(x) {
            return this.deg(Math.atan(x));
        },
        atan2: function(x, y) {
            return this.ifthenelse(this.not(y), this.ifthenelse(this.less(x, 0), 180, 0), this.multiply(2, this.atan(this.divide(y, this.add(this.veclen(x, y), x)))));
        },
        ceil: function(x) {
            return Math.ceil(x);
        },
        cos: function(x) {
            return Math.cos(this.rad(x));
        },
        cosec: function(x) {
            return this.divide(1, this.sin(x));
        },
        cosh: function(x) {
            return this.multiply(0.5, this.add(this.exp(x), this.exp(-x)));
        },
        cot: function(x) {
            return this.divide(1, this.tan(x));
        },
        deg: function(x) {
            return this.multiply(x, this.divide(180, this.pi()));
        },
        div: function(x, y) {
            return this.int(this.round(this.divide(x, y)));
        },
        divide: function(x, y) {
            return x / y;
        },
        e: function() {
            return Math.E;
        },
        equal: function(x, y) {
            return x === y ? 1 : 0;
        },
        exp: function(x) {
            return Math.exp(x);
        },
        factorial: function(x) {
            return this.int(x) === 0 ? 1 : this.multiply(this.int(x), this.factorial(this.subtract(x, 1)));
        },
        false: function() {
            return 0;
        },
        floor: function(x) {
            return Math.floor(x);
        },
        frac: function(x) {
            return x % 1;
        },
        greater: function(x, y) {
            return x > y ? 1 : 0;
        },
        int: function(x) {
            return 0 | x;
        },
        ifthenelse: function(x, y, z) {
            return x != 0 ? y : z;
        },
        less: function(x, y) {
            return x < y ? 1 : 0;
        },
        ln: function(x) {
            return Math.log(x);
        },
        log10: function(x) {
            return this.divide(this.ln(x), this.ln(10));
        },
        log2: function(x) {
            return this.divide(this.ln(x), this.ln(2));
        },
        max: function() {
            var m = Number.NEGATIVE_INFINITY;
            for (var i = 0; i < arguments.length; i++)
                m = Math.max(m, arguments[i]);
            return m;
        },
        min: function() {
            var m = Number.POSITIVE_INFINITY;
            for (var i = 0; i < arguments.length; i++)
                m = Math.min(m, arguments[i]);
            return m;
        },
        mod: function(x, y) {
            return this.subtract(x, this.multiply(y, this.int(this.divide(x, y))));
        },
        Mod: function(x, y) {
            return this.subtract(x, this.multiply(y, this.floor(this.divide(x, y))));
        },
        multiply: function(x, y) {
            return x * y;
        },
        neg: function(x) {
            return -x;
        },
        not: function(x) {
            return x == 0 ? 1 : 0;
        },
        notequal: function(x, y) {
            return this.not(this.equal(x, y));
        },
        notgreater: function(x, y) {
            return this.not(this.greater(x, y));
        },
        notless: function(x, y) {
            return this.not(this.less(x, y));
        },
        or: function(x, y) {
            return x != 0 || y != 0 ? 1 : 0;
        },
        pi: function() {
            return Math.PI;
        },
        pow: function(x, y) {
            return Math.pow(x, y);
        },
        rad: function(x) {
            return this.multiply(x, this.divide(this.pi(), 180));
        },
        rand: function() {
            return RNG.random(-1, 1);
        },
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
            return RNG.random(a, b);
        },
        real: function(x) {
            return new Number(x);
        },
        rnd: function() {
            return RNG.random();
        },
        round: function(x) {
            return this.multiply(this.subtract(this.greater(x, 0), this.less(x, 0)), Math.round(this.abs(x)));
        },
        sec: function(x) {
            return this.divide(1, this.cos(x));
        },
        sin: function(x) {
            return Math.sin(this.rad(x));
        },
        sinh: function(x) {
            return this.multiply(0.5, this.subtract(this.exp(x), this.exp(-x)));
        },
        sqrt: function(x) {
            return Math.sqrt(x);
        },
        subtract: function(x, y) {
            return x - y;
        },
        tan: function(x) {
            return Math.tan(this.rad(x));
        },
        tanh: function(x) {
            return this.divide(this.sinh(x), this.cosh(x));
        },
        true: function() {
            return 1;
        },
        veclen: function(x, y) {
            return this.sqrt(this.add(this.pow(x, 2), this.pow(y, 2)));
        }
    };

    var Input = {
        read: function() {
            if (this.pos >= this.chars.length) {
                this.pos = this.chars.length + 1;
                return '';
            }
            return this.chars.charAt(this.pos++);
        },
        flush: function() {
            if (this.pos <= this.chars.length) {
                throw "Unrecognized character: " + this.chars.charAt(this.pos - 1);
            }
        },
        split: function() {
            return this.chars.substring(0, this.pos - 1) +
                    '[' + this.chars[this.pos - 1] + ']' +
                    this.chars.substring(this.pos);
        }
    };
    var Parser = {
        Look: '',
        RelOp: ['=', '!', '<', '>'],
        Expected: function(s) {
            throw s + " Expected at '" + Input.split() + "'";
        },
        GetChar: function() {
            this.Look = Input.read();
        },
        SkipWhite: function() {
            while (this.Look === ' ' || this.Look === '\t') {
                this.GetChar();
            }
        },
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
            this._variable(Token);
        },
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
                    this.Relation();
                    count++;
                    while (this.Look === ',') {
                        this._move();
                        this.Match(',');
                        this.Relation();
                        count++;
                    }
                }
                this.Match(')');
            }
            this._function(Name, count);
        },
        Num: function() {
            var Decimal = false;
            var Value = '';
            if (!/[0-9\.]/.test(this.Look)) {
                this.Expected("Number");
            }
            while (/[0-9\.]/.test(this.Look)) {
                if (this.Look === '.') {
                    if (Decimal) {
                        this.Expected("Number");
                    }
                    Decimal = true;
                }
                Value += this.Look;
                this.GetChar();
            }
            this.SkipWhite();
            this._literal(new Number(Value));
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
                this.Relation();
                this.Match(')');
            } else if (this.Look === '#') {
                this.Match('#');
                this.Var();
            } else if (/[a-z]/.test(this.Look)) {
                this.Func();
            } else {
                this.Num();
            }
            if (this.Look === '!') {
                this.Match('!');
                this._function("factorial", 1);
            }
        },
        SignedFragment: function() {
            if (this.Look === '-') {
                this._move();
                this.Match('-');
                this.Fragment();
                this._function("neg", 1);
            } else if (this.Look === '!') {
                this._move();
                this.Match('!');
                this.Fragment();
                this._function("not", 1);
            } else {
                this.Fragment();
            }
        },
        Factor: function() {
            this.Fragment();
            while (this.Look === '^') {
                this._move();
                this.Match('^');
                this.SignedFragment();
                this._function("pow", 2);
            }
            if (this.Look === 'r') {
                this.Match('r');
                this._function("deg", 1);
            }
        },
        SignedFactor: function() {
            if (this.Look === '-') {
                this._move();
                this.Match('-');
                this.Factor();
                this._function("neg", 1);
            } else if (this.Look === '!') {
                this._move();
                this.Match('!');
                this.Factor();
                this._function("not", 1);
            } else {
                this.Factor();
            }
        },
        Term: function() {
            this.SignedFactor();
            while (this.Look === '*' || this.Look === '/'
                    || this.Look === '&') {
                this._move();
                if (this.Look === '*') {
                    this.Match('*');
                    this.SignedFactor();
                    this._function("multiply", 2);
                } else if (this.Look === '/') {
                    this.Match('/');
                    this.SignedFactor();
                    this._function("divide", 2);
                } else {
                    this.Match('&');
                    this.Match('&');
                    this.SignedFactor();
                    this._function("and", 2);
                }
            }
        },
        Expression: function() {
            this.Term();
            while (this.Look === '+' || this.Look === '-'
                    || this.Look === '|') {
                this._move();
                if (this.Look === '+') {
                    this.Match('+');
                    this.Term();
                    this._function("add", 2);
                } else if (this.Look === '-') {
                    this.Match('-');
                    this.Term();
                    this._function("subtract", 2);
                } else {
                    this.Match('|');
                    this.Match('|');
                    this.Term();
                    this._function("or", 2);
                }
            }
        },
        Relation: function() {
            this.Expression();
            var index = this.RelOp.indexOf(this.Look);
            while (index !== -1) {
                this._move();
                var func;
                switch (index) {
                    case 0:
                        this.Match('=');
                        this.Match('=');
                        func = "equal";
                        break;
                    case 1:
                        this.Match('!');
                        this.Match('=');
                        func = "notequal";
                        break;
                    case 2:
                        this.Match('<');
                        func = "less";
                        if (this.Look === '=') {
                            this.Match('=');
                            func = "notgreater";
                        }
                        break;
                    case 3:
                        this.Match('>');
                        func = "greater";
                        if (this.Look === '=') {
                            this.Match('=');
                            func = "notless";
                        }
                }
                this.Expression();
                this._function(func, 2);
                index = this.RelOp.indexOf(this.Look);
            }
        },
        _literal: function(value) {
            var context = Tree;
            Tree.ActionList.push({
                visit: function() {
                    context.Result = value;
                }
            });
        },
        _move: function() {
            var context = Tree;
            Tree.ActionList.push({
                visit: function() {
                    context.Stack.unshift(context.Result);
                }
            });
        },
        _variable: function(variable) {
            var context = Tree;
            Tree.ActionList.push({
                visit: function(value) {
                    if (variable !== context.Variable) {
                        throw "Unknown variable '#" + variable + "'";
                    }
                    context.Result = value;
                }
            });
        },
        _function: function(name, paramCount) {
            if (typeof MathEngine[name] === 'undefined') {
                throw "Unknown function '" + name + "'";
            }
            var context = Tree;
            Tree.ActionList.push({
                visit: function() {
                    var count = paramCount | 0;
                    var parameters = count === 0 ? [] : [context.Result];
                    for (var i = 1; i < count; ++i) {
                        parameters.unshift(context.Stack.shift());
                    }
                    context.Result = MathEngine[name].apply(MathEngine, parameters);
                }
            });
        }
    };

    var Tree;

    var ExpressionParser = {
        parse: function(expression) {
            Input.chars = expression;
            Input.pos = 0;
            Tree = {
                ActionList: [],
                setVariable: function(variable) {
                    this.Variable = variable.substring(1);
                },
                visit: function(value) {
                    this.Stack = [];
                    this.ActionList.forEach(function(action) {
                        action.visit(value);
                    });
                    return this.Result;
                }
            };
            Parser.GetChar();
            Parser.SkipWhite();
            Parser.Relation();
            Input.flush();
            return Tree;
        }
    };

    function Init() {
        var value = prompt('Input (#x)');
        if (value !== null) {
            try {
                Write(value, ' = ');
                var result = ExpressionParser.parse(value);
                result.setVariable('#x')
                WriteLn(result.visit(prompt('#x')));
            } catch (e) {
                WriteLn();
                WriteLn('\u2407', 'Error: ', e, '.');
                throw '';
            }
        }
    }

    Init();

    function WriteLn() {
        var s = Array.prototype.slice.call(arguments);
        s.push('</br>');
        Write.apply(this, s);
    }

    function Write() {
        var s = Array.prototype.slice.call(arguments);
        document.write(s.join(''));
    }
})();