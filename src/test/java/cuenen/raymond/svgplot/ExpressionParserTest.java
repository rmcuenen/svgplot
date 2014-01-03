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

import org.openqa.selenium.WebDriver;
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
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void numberTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "0", 0, false), 0, msg);
        assertStartsWith(evaluateExpression(driver, "0.", 0, true), "ParseError: Integer Expected at '0.[]'", msg);
        assertEquals(evaluateExpression(driver, "0.01", 0, false), 0.01, msg);
        assertStartsWith(evaluateExpression(driver, ".", 0, true), "ParseError: Integer Expected at '.[]'", msg);
        assertEquals(evaluateExpression(driver, ".00", 0, false), 0, msg);
        assertStartsWith(evaluateExpression(driver, "1e0", 0, true), "ParseError: Non-Zero Expected at '1e[0]'", msg);
        assertEquals(evaluateExpression(driver, "1e2", 0, false), 100, msg);
        assertEquals(evaluateExpression(driver, "2e-3", 0, false), 0.002, msg);
        assertStartsWith(evaluateExpression(driver, "1e2.5", 0, true), "ParseError: Unrecognized character: 1e2[.]5", msg);
        assertEquals(evaluateExpression(driver, "0.01e+4", 0, false), 100, msg);
        assertStartsWith(evaluateExpression(driver, "0e2", 0, true), "ParseError: Unrecognized character: 0[e]2", msg);
        assertEquals(evaluateExpression(driver, "124", 0, false), 124, msg);
    }

    /**
     * Test the parsing of {@code variable}s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void variableTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertStartsWith(evaluateExpression(driver, "#x", Math.PI, true), "NotFoundError: Unknown variable '#x'", msg);
        assertEquals(evaluateExpression(driver, "#x\"); tree.setVariable(\"#x", Math.PI, false), Math.PI, msg);
        assertStartsWith(evaluateExpression(driver, "#0", 0, true), "ParseError: Variable Expected at '#[0]'", msg);
        assertStartsWith(evaluateExpression(driver, "#_Wrong", 0, true), "ParseError: Variable Expected at '#[_]Wrong'", msg);
        assertEquals(evaluateExpression(driver, "#This_is_Valid_2\"); tree.setVariable(\"#This_is_Valid_2", 2.5, false), 2.5, msg);
    }

    /**
     * Test the parsing of {@code function}s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void functionTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "e", 0, false), Math.E, msg);
        assertBetween(evaluateExpression(driver, "sin(180)", 0, false), 0, 1E-15, msg);
        assertEquals(evaluateExpression(driver, "max(1,2.5,50,-3)", 0, false), 50, msg);
        assertStartsWith(evaluateExpression(driver, "ajax", 0, true), "NotFoundError: Unknown function 'ajax'", msg);
        assertBetween(evaluateExpression(driver, "random()", 0, false), 0, 1, msg);
    }

    /**
     * Test the parsing of {@code (signed-)fragment}s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void fragmentTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "#x!\"); tree.setVariable(\"#x", 5, false), 120, msg);
        assertEquals(evaluateExpression(driver, "max(-1,0,5)!", 0, false), 120, msg);
        assertEquals(evaluateExpression(driver, "-5!", 0, false), -120, msg);
        assertEquals(evaluateExpression(driver, "!false", 0, false), 1, msg);
    }

    /**
     * Test the parsing of {@code (signed-)factor}s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void factorTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "#x^2\"); tree.setVariable(\"#x", 12, false), 144, msg);
        assertEquals(evaluateExpression(driver, "32400^(0.5)", 0, false), 180, msg);
        assertEquals(evaluateExpression(driver, "cos(pi r)", 0, false), -1, msg);
        assertEquals(evaluateExpression(driver, "(-1)^4", 0, false), 1, msg);
    }

    /**
     * Test the parsing of {@code term)s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void termTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "2*2*2*2", 0, false), 16, msg);
        assertEquals(evaluateExpression(driver, "-1/2*1/2", 0, false), -0.25, msg);
        assertEquals(evaluateExpression(driver, "#x/#x^2\"); tree.setVariable(\"#x", 2, false), 0.5, msg);
        assertStartsWith(evaluateExpression(driver, "2**2", 0, true), "ParseError: Number Expected at '2*[*]2'", msg);
        assertEquals(evaluateExpression(driver, "#x&&(#x-1)\"); tree.setVariable(\"#x", 1, false), 0, msg);
    }

    /**
     * Test the parsing of {@code expression}s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void expressionTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "-1+2-3+4", 0, false), 2, msg);
        assertEquals(evaluateExpression(driver, "#x||(#x-1)\"); tree.setVariable(\"#x", 2, false), 1, msg);
        assertEquals(evaluateExpression(driver, "veclen(3,4)+25", 0, false), 30, msg);
    }

    /**
     * Test the parsing of {@code relation}s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void relationTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "-5<0", 0, false), 1, msg);
        assertEquals(evaluateExpression(driver, "5>0", 0, false), 1, msg);
        assertEquals(evaluateExpression(driver, "#x<>12\"); tree.setVariable(\"#x", 2, false), 1, msg);
        assertEquals(evaluateExpression(driver, "1==true", 2, false), 1, msg);
        assertEquals(evaluateExpression(driver, "4>=5", 0, false), 0, msg);
        assertEquals(evaluateExpression(driver, "#x<=6\"); tree.setVariable(\"#x", 12, false), 0, msg);
    }

    /**
     * Test the parsing of {@code special}s.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void specialTest(WebDriver driver) {
        String msg = getMessage(driver);
        assertEquals(evaluateExpression(driver, "true() ? #x : pi\"); tree.setVariable(\"#x", Math.E, false), Math.E, msg);
        assertStartsWith(evaluateExpression(driver, "1 ? true", 0, true), "ParseError: ':' Expected at '1 ? true[]'", msg);
        assertEquals(evaluateExpression(driver, "cos(pi r)+1?acos(1):1.2", 0, false), 1.2, msg);
        assertStartsWith(evaluateExpression(driver, "0?:true", 0, true), "ParseError: Number Expected at '0?[:]true'", msg);
    }

    /**
     * Convenience method to evaluate an expression.
     *
     * @param driver The WebDriver executing the test.
     * @param expression The expression to be parsed.
     * @param value The value to use with variable substitution.
     * @param alert {@code true} when an alert is expected, {@code false}
     * otherwise.
     * @return The parsed result.
     */
    private String evaluateExpression(WebDriver driver, String expression, double value, boolean alert) {
        Wait wait = load(driver, MODULE_LOADER, 10);
        String callback = String.format(FUNCTION_FORMAT, expression, value);
        require(driver, callback, MODULE_NAME);
        if (alert) {
            wait.until(ExpectedConditions.alertIsPresent());
            return getAlert(driver);
        } else {
            wait.until(RESULT_SET);
            return getResult(driver);
        }
    }

    /**
     * Assert that a {@code String} starts with another {@code String}.
     *
     * @param actual The actual string to compare.
     * @param expected The expected beginning.
     * @param message The error message to use.
     */
    private void assertStartsWith(String actual, String expected, String message) {
        StringBuilder msg = new StringBuilder(message);
        msg.append(": ").append(actual).append(" does not start with ").append(expected);
        Assert.assertTrue(actual.startsWith(expected), msg.toString());
    }

    /**
     * Assert that a {@code String} can be converted to {@code Double} and is
     * equal to the expected value.
     *
     * @param actual The actual string to compare.
     * @param expected The expected number.
     * @param message The error message to use.
     */
    private void assertEquals(String actual, double expected, String message) {
        Assert.assertEquals(Double.valueOf(actual), Double.valueOf(expected), message);
    }

    /**
     * Assert that a {@code String} can be converted to a {@code Double} and has
     * a value between the given range.
     *
     * @param actual The actual string to compare.
     * @param min The minimal value.
     * @param max The maximal value.
     * @param message The error message to use.
     */
    private void assertBetween(String actual, double min, double max, String message) {
        double mid = (min + max) / 2.0;
        double dist = (max - min) / 2.0;
        StringBuilder msg = new StringBuilder(message);
        msg.append(": ").append(actual).append(" not between ").append(min).append(" and ").append(max);
        Assert.assertEquals(Double.parseDouble(actual), mid, dist, msg.toString());
    }
}
