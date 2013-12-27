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
package cuenen.raymond.svgplot;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for testing {@code ExpressionParser.js}.
 *
 * @author R. M. Cuenen
 */
public class ExpressionParserTest extends AbstractTestClass {

    private static final String MODULE_NAME = "ExpressionParser";
    private static final String FUNCTION_FORMAT = "function(Parser) { var tree = Parser.parse(\"%s\"); setResult(tree.visit(%s)); }";

    /**
     * Test the parsing of {@code number}s.
     */
    @Test
    public void numberTest() {
        assertEquals(evaluateExpression("0", 0, false), 0);
        assertStartsWith(evaluateExpression("0.", 0, true), "ParseError: Integer Expected at '0.[]'");
        assertEquals(evaluateExpression("0.01", 0, false), 0.01);
        assertStartsWith(evaluateExpression(".", 0, true), "ParseError: Integer Expected at '.[]'");
        assertEquals(evaluateExpression(".00", 0, false), 0);
        assertStartsWith(evaluateExpression("1e0", 0, true), "ParseError: Non-Zero Expected at '1e[0]'");
        assertEquals(evaluateExpression("1e2", 0, false), 100);
        assertEquals(evaluateExpression("2e-3", 0, false), 0.002);
        assertStartsWith(evaluateExpression("1e2.5", 0, true), "ParseError: Unrecognized character: 1e2[.]5");
        assertEquals(evaluateExpression("0.01e+4", 0, false), 100);
        assertStartsWith(evaluateExpression("0e2", 0, true), "ParseError: Unrecognized character: 0[e]2");
        assertEquals(evaluateExpression("124", 0, false), 124);
    }

    /**
     * Test the parsing of {@code variable}s.
     */
    @Test
    public void variableTest() {
        assertStartsWith(evaluateExpression("#x", Math.PI, true), "NotFoundError: Unknown variable '#x'");
        assertEquals(evaluateExpression("#x\"); tree.setVariable(\"#x", Math.PI, false), Math.PI);
        assertStartsWith(evaluateExpression("#0", 0, true), "ParseError: Variable Expected at '#[0]'");
        assertStartsWith(evaluateExpression("#_Wrong", 0, true), "ParseError: Variable Expected at '#[_]Wrong'");
        assertEquals(evaluateExpression("#This_is_Valid_2\"); tree.setVariable(\"#This_is_Valid_2", 2.5, false), 2.5);
    }

    /**
     * Test the parsing of {@code function}s.
     */
    @Test
    public void functionTest() {
        assertEquals(evaluateExpression("e", 0, false), Math.E);
        assertBetween(evaluateExpression("sin(180)", 0, false), 0, 1E-15);
        assertEquals(evaluateExpression("max(1,2.5,50,-3)", 0, false), 50);
        assertStartsWith(evaluateExpression("ajax", 0, true), "NotFoundError: Unknown function 'ajax'");
        assertBetween(evaluateExpression("random()", 0, false), 0, 1);
    }

    /**
     * Test the parsing of {@code (signed-)fragment}s.
     */
    @Test
    public void fragmentTest() {
        assertEquals(evaluateExpression("#x!\"); tree.setVariable(\"#x", 5, false), 120);
        assertEquals(evaluateExpression("max(-1,0,5)!", 0, false), 120);
        assertEquals(evaluateExpression("-5!", 0, false), -120);
        assertEquals(evaluateExpression("!false", 0, false), 1);
    }

    /**
     * Test the parsing of {@code (signed-)factor}s.
     */
    @Test
    public void factorTest() {
        assertEquals(evaluateExpression("#x^2\"); tree.setVariable(\"#x", 12, false), 144);
        assertEquals(evaluateExpression("32400^(0.5)", 0, false), 180);
        assertEquals(evaluateExpression("cos(pi r)", 0, false), -1);
        assertEquals(evaluateExpression("(-1)^4", 0, false), 1);
    }

    /**
     * Test the parsing of {@code term)s.
     */
    @Test
    public void termTest() {
        assertEquals(evaluateExpression("2*2*2*2", 0, false), 16);
        assertEquals(evaluateExpression("-1/2*1/2", 0, false), -0.25);
        assertEquals(evaluateExpression("#x/#x^2\"); tree.setVariable(\"#x", 2, false), 0.5);
        assertStartsWith(evaluateExpression("2**2", 0, true), "ParseError: Number Expected at '2*[*]2'");
        assertEquals(evaluateExpression("#x&&(#x-1)\"); tree.setVariable(\"#x", 1, false), 0);
    }

    /**
     * Test the parsing of {@code expression}s.
     */
    @Test
    public void expressionTest() {
        assertEquals(evaluateExpression("-1+2-3+4", 0, false), 2);
        assertEquals(evaluateExpression("#x||(#x-1)\"); tree.setVariable(\"#x", 2, false), 1);
        assertEquals(evaluateExpression("veclen(3,4)+25", 0, false), 30);
    }

    /**
     * Test the parsing of {@code relation}s.
     */
    @Test
    public void relationTest() {
        assertEquals(evaluateExpression("-5<0", 0, false), 1);
        assertEquals(evaluateExpression("5>0", 0, false), 1);
        assertEquals(evaluateExpression("#x<>12\"); tree.setVariable(\"#x", 2, false), 1);
        assertEquals(evaluateExpression("1==true", 2, false), 1);
        assertEquals(evaluateExpression("4>=5", 0, false), 0);
        assertEquals(evaluateExpression("#x<=6\"); tree.setVariable(\"#x", 12, false), 0);
    }

    /**
     * Test the parsing of {@code special}s.
     */
    @Test
    public void specialTest() {
        assertEquals(evaluateExpression("true() ? #x : pi\"); tree.setVariable(\"#x", Math.E, false), Math.E);
        assertStartsWith(evaluateExpression("1 ? true", 0, true), "ParseError: ':' Expected at '1 ? true[]'");
        assertEquals(evaluateExpression("cos(pi r)+1?acos(1):1.2", 0, false), 1.2);
        assertStartsWith(evaluateExpression("0?:true", 0, true), "ParseError: Number Expected at '0?[:]true'");
    }

    /**
     * Convenience method to evaluate an expression.
     *
     * @param expression The expression to be parsed.
     * @param value The value to use with variable substitution.
     * @param alert {@code true} when an alert is expected, {@code false}
     * otherwise.
     * @return The parsed result.
     */
    private String evaluateExpression(String expression, double value, boolean alert) {
        Wait wait = load(MODULE_LOADER, 10);
        String callback = String.format(FUNCTION_FORMAT, expression, value);
        require(callback, MODULE_NAME);
        if (alert) {
            wait.until(ExpectedConditions.alertIsPresent());
            return getAlert();
        } else {
            wait.until(RESULT_SET);
            return getResult();
        }
    }

    /**
     * Assert that a {@code String} starts with another {@code String}.
     *
     * @param actual The actual string to compare.
     * @param expected The expected beginning.
     */
    private static void assertStartsWith(String actual, String expected) {
        StringBuilder msg = new StringBuilder();
        msg.append(actual).append(" does not start with ").append(expected);
        Assert.assertTrue(actual.startsWith(expected), msg.toString());
    }

    /**
     * Assert that a {@code String} can be converted to {@code Double} and is
     * equal to the expected value.
     *
     * @param actual The actual string to compare.
     * @param expected The expected number.
     */
    private static void assertEquals(String actual, double expected) {
        Assert.assertEquals(Double.valueOf(actual), Double.valueOf(expected));
    }

    /**
     * Assert that a {@code String} can be converted to a {@code Double} and has
     * a value between the given range.
     *
     * @param actual The actual string to compare.
     * @param min The minimal value.
     * @param max The maximal value.
     */
    private static void assertBetween(String actual, double min, double max) {
        double mid = (min + max) / 2.0;
        double dist = (max - min) / 2.0;
        StringBuilder msg = new StringBuilder();
        msg.append(actual).append(" not between ").append(min).append(" and ").append(max);
        Assert.assertEquals(Double.parseDouble(actual), mid, dist, msg.toString());
    }
}
