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

    @Test
    public void numberTest() {
        assertEquals(evaluateExpression("0", 0, false), 0);
        assertStartsWith(evaluateExpression("0.", 0, true), "Integer Expected at '0.[]'");
        assertEquals(evaluateExpression("0.01", 0, false), 0.01);
        assertStartsWith(evaluateExpression(".", 0, true), "Integer Expected at '.[]'");
        assertEquals(evaluateExpression(".00", 0, false), 0);
        assertStartsWith(evaluateExpression("1e0", 0, true), "Non-Zero Expected at '1e[0]'");
        assertEquals(evaluateExpression("1e2", 0, false), 100);
        assertEquals(evaluateExpression("2e-3", 0, false), 0.002);
        assertStartsWith(evaluateExpression("1e2.5", 0, true), "Unrecognized character: 1e2[.]5");
        assertEquals(evaluateExpression("0.01e+4", 0, false), 100);
        assertStartsWith(evaluateExpression("0e2", 0, true), "Unrecognized character: 0[e]2");
        assertEquals(evaluateExpression("124", 0, false), 124);
    }

    @Test
    public void variableTest() {
        assertStartsWith(evaluateExpression("#x", Math.PI, true), "Unknown variable '#x'");
        assertEquals(evaluateExpression("#x\"); tree.setVariable(\"#x", Math.PI, false), Math.PI);
    }

    @Test
    public void functionTest() {
        assertEquals(evaluateExpression("e", 0, false), Math.E);
        assertBetween(evaluateExpression("sin(180)", 0, false), 0, 1E-15);
        assertEquals(evaluateExpression("max(1,2.5,50,-3)", 0, false), 50);
        assertStartsWith(evaluateExpression("ajax", 0, true), "Unknown function 'ajax'");
        assertBetween(evaluateExpression("random()", 0, false), 0, 1);
    }

    private String evaluateExpression(String expression, double value, boolean alert) {
        Wait wait = load(MODULE_LOADER, 10);
        String callback = String.format(FUNCTION_FORMAT, expression, value);
        require(callback, MODULE_NAME);
        if (alert) {
            wait.until(ExpectedConditions.alertIsPresent());
            return getAlert();
        } else {
            return getResult();
        }
    }

    private static void assertStartsWith(String actual, String expected) {
        StringBuilder msg = new StringBuilder();
        msg.append(actual).append(" does not start with ").append(expected);
        Assert.assertTrue(actual.startsWith(expected), msg.toString());
    }

    private static void assertEquals(String actual, double expected) {
        Assert.assertEquals(Double.valueOf(actual), Double.valueOf(expected));
    }

    private static void assertBetween(String actual, double min, double max) {
        double mid = (min + max) / 2.0;
        double dist = (max - min) / 2.0;
        StringBuilder msg = new StringBuilder();
        msg.append(actual).append(" not between ").append(min).append(" and ").append(max);
        Assert.assertEquals(Double.parseDouble(actual), mid, dist, msg.toString());
    }
}
