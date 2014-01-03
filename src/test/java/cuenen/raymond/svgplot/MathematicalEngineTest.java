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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code MathematicalEngine.js}.
 *
 * @author R. M. Cuenen
 */
public class MathematicalEngineTest extends AbstractTestClass {

    private static final String MODULE_NAME = "MathematicalEngine";
    private static final String FUNCTION_FORMAT = "function(ME) { var d = %s; setResult(d); }";
    private static final String[] BASIC_ARITHMETIC_FUNCTIONS = {"add(75,6)", "subtract(75,6)",
        "neg(50)", "multiply(75,6)", "divide(75,6)", "div(75,9)", "factorial(5)",
        "sqrt(10)", "sqrt(8765.432)", "pow(2,7)", "exp(1)", "exp(2.34)", "ln(10)",
        "ln(ME.exp(5))", "log10(100)", "log2(128)", "abs(-5)", "abs(4*-3)*-1",
        "mod(20,6)", "mod(-100,30)", "Mod(-100,30)"};
    private static final String[] ROUNDING_FUNCTIONS = {"round(32.5/17)", "round(398/12)",
        "floor(32.5/17)", "floor(398/12)", "ceil(32.5/17)", "ceil(398/12)", "int(32.5/17)",
        "frac(32.5/17)", "real(4)"};
    private static final String[] TRIGONOMETRIC_FUNCTIONS = {"pi()", "deg(ME.pi())",
        "rad(90)", "deg(3*ME.pi()/2)", "sin(60)", "sin(ME.deg(ME.pi()/3))", "cos(60)",
        "cos(ME.deg(ME.pi()/3))", "tan(45)", "tan(ME.deg(2*ME.pi()/8))", "sec(45)",
        "cosec(30)", "cot(15)", "asin(0.7071)", "acos(0.5)", "atan(1)", "atan2(-4,3)"};
    private static final String[] COMPARISON_AND_LOGICAL_FUNCTIONS = {"equal(20,20)",
        "greater(20,25)", "less(20,25)", "notequal(20,25)", "notgreater(20,25)",
        "notless(20,25)", "and(5>4,6>7)", "or(5>4,6>7)", "not(true)",
        "ifthenelse(5==4,\"yes\",\"no\")", "true() ? \"yes\" : \"no\"",
        "false() ? \"yes\" : \"no\""};
    private static final String[] MISCELLANEOUS_FUNCTIONS = {"min(3,4,-2,250,-8,100)",
        "max(3,4,-2,250,-8,100)", "veclen(12,5)", "sinh(0.5)", "cosh(0.5)", "tanh(0.5)"};
    private static final Object[] BASIC_ARITHMETIC_RESULTS = {Double.valueOf(81),
        Double.valueOf(69), Double.valueOf(-50), Double.valueOf(450), Double.valueOf(12.5),
        Integer.valueOf(8), Double.valueOf(120), Double.valueOf(3.1622776601683795),
        Double.valueOf(93.62388584116769), Double.valueOf(128), Double.valueOf(2.718281828459045),
        Double.valueOf(10.381236562731845), Double.valueOf(2.302585092994046),
        Double.valueOf(5), Double.valueOf(2), Double.valueOf(7), Double.valueOf(5),
        Double.valueOf(-12), Double.valueOf(2), Double.valueOf(-10), Double.valueOf(20)};
    private static final Object[] ROUNDING_RESULTS = {Double.valueOf(2), Double.valueOf(33),
        Double.valueOf(1), Double.valueOf(33), Double.valueOf(2), Double.valueOf(34),
        Integer.valueOf(1), Double.valueOf(0.911764705882353), Double.valueOf(4.0)};
    private static final Object[] TRIGONOMETRIC_RESULTS = {Double.valueOf(3.141592653589793),
        Double.valueOf(180), Double.valueOf(1.5707963267948966), Double.valueOf(270),
        Double.valueOf(0.8660254037844386), Double.valueOf(0.8660254037844386),
        Double.valueOf(0.5), Double.valueOf(0.5), Double.valueOf(1.0), Double.valueOf(1.0),
        Double.valueOf(1.414213562373095), Double.valueOf(2.0), Double.valueOf(3.7320508075688776),
        Double.valueOf(44.99945053347443), Double.valueOf(60.0), Double.valueOf(45),
        Double.valueOf(143.13010235415598)};
    private static final Object[] COMPARISON_AND_LOGICAL_RESULTS = {Integer.valueOf(1),
        Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1),
        Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0),
        "no", "yes", "no"};
    private static final Object[] MISCELLANEOUS_RESULTS = {Double.valueOf(-8),
        Double.valueOf(250), Double.valueOf(13), Double.valueOf(0.5210953054937474),
        Double.valueOf(1.1276259652063807), Double.valueOf(0.4621171572600098)};

    /**
     * Test the Basic Arithmetic Functions.
     * <ul>
     * <li>add
     * <li>subtract
     * <li>neg
     * <li>multiply
     * <li>divide
     * <li>div
     * <li>factorial
     * <Li>sqrt
     * <li>pow
     * <li>e
     * <li>exp
     * <li>ln
     * <li>log10
     * <li>log2
     * <li>abs
     * <li>mod
     * <li>Mod
     * </ul>
     */
    @Test(dependsOnMethods = "initializeDriver")
    public void basicArithmeticFunctionsTest() {
        executeTest(BASIC_ARITHMETIC_RESULTS, BASIC_ARITHMETIC_FUNCTIONS);
    }

    /**
     * Test the Rounding Functions.
     * <ul>
     * <li>round
     * <li>floor
     * <li>ceil
     * <li>int
     * <li>frac
     * <li>real
     * </ul>
     */
    @Test(dependsOnMethods = "initializeDriver")
    public void roundingFunctionsTest() {
        executeTest(ROUNDING_RESULTS, ROUNDING_FUNCTIONS);
    }

    /**
     * Test the Trigonometric Functions.
     * <ul>
     * <li>pi
     * <li>sin
     * <li>cos
     * <li>tan
     * <li>sec
     * <li>cosec
     * <li>cot
     * <li>asin
     * <li>acos
     * <li>atan
     * <li>atan2
     * </ul>
     */
    @Test(dependsOnMethods = "initializeDriver")
    public void trigonometricFunctionsTest() {
        executeTest(TRIGONOMETRIC_RESULTS, TRIGONOMETRIC_FUNCTIONS);
    }

    /**
     * Test the Comparison and Logical Functions.
     * <ul>
     * <li>equal
     * <li>greater
     * <li>less
     * <li>notequal
     * <li>notgreater
     * <li>notless
     * <li>and
     * <li>or
     * <li>not
     * <li>ifthenelse
     * <li>true
     * <li>false
     * </ul>
     */
    @Test(dependsOnMethods = "initializeDriver")
    public void comparisonAndLogicalFunctionsTest() {
        executeTest(COMPARISON_AND_LOGICAL_RESULTS, COMPARISON_AND_LOGICAL_FUNCTIONS);
    }

    /**
     * Test the range of the Pseudo-random Functions.
     * <ul>
     * <li>rnd
     * <li>rand
     * <li>random
     * </ul>
     * These functions use the {@code RandomNumberGenerator} module.
     */
    @Test(dependsOnMethods = "initializeDriver")
    public void pseudoRandomFunctionsTest() {
        performRangeTest("rnd()", 0D, 1D);
        performRangeTest("rand()", -1D, 1D);
        performRangeTest("random()", 0D, 1D);
        performRangeTest("random(100)", 1D, 100D);
        performRangeTest("random(232,762)", 232D, 762D);
    }

    /**
     * Test the Miscellaneous Functions.
     * <ul>
     * <li>min
     * <li>max
     * <li>veclen
     * <li>sinh
     * <li>cosh
     * <li>tanh
     * </ul>
     */
    @Test(dependsOnMethods = "initializeDriver")
    public void miscellaneousFunctionsTest() {
        executeTest(MISCELLANEOUS_RESULTS, MISCELLANEOUS_FUNCTIONS);
    }

    /**
     * Convenience function for performing the range tests.
     *
     * @param function The pseudo-random function to test.
     * @param range The expected range of the result.
     */
    private void performRangeTest(String function, double... range) {
        Wait wait = load(MODULE_LOADER, 1);
        String functionCall = createRangeFunctionCall(function);
        String callback = String.format(FUNCTION_FORMAT, functionCall);
        require(callback, MODULE_NAME);
        wait.until(RESULT_SET);
        String result = getResult();
        assertNotNull(result, getMessage());
        checkRange(result, range, function);
    }

    /**
     * Convenience function for executing the tests.
     *
     * @param results The array of expected result values.
     * @param functions The array of functions to test.
     */
    private void executeTest(Object[] results, String... functions) {
        Wait wait = load(MODULE_LOADER, 1);
        String functionCall = createFunctionCall(functions);
        String callback = String.format(FUNCTION_FORMAT, functionCall);
        require(callback, MODULE_NAME);
        wait.until(RESULT_SET);
        String result = getResult();
        assertNotNull(result, getMessage());
        checkResult(result, results, functions);
    }

    /**
     * Creates the JavaScript code executing the range test for the given
     * pseudo-random function.
     *
     * @param rnd The pseudo-random function to call.
     * @return The JavaScript code for testing.
     */
    private String createRangeFunctionCall(String rnd) {
        StringBuilder sb = new StringBuilder();
        sb.append("0; var b = [Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY];");
        sb.append("for (var i = 0; i < 1e6; i++) { var r = ME.").append(rnd).append(';');
        sb.append("b[0] = Math.min(b[0], r); b[1] = Math.max(b[1], r);").append('}');
        sb.append("d = b");
        return sb.toString();
    }

    /**
     * Creates the JavaScript code executing the given functions.
     *
     * @param functions The functions to call.
     * @return The JavaScript code for testing.
     */
    private String createFunctionCall(String... functions) {
        int iMax = functions.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0;; i++) {
            sb.append("ME.").append(functions[i]);
            if (i == iMax) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    /**
     * Validate the range of the given pseudo-random function.
     *
     * @param result The result obtained from the test.
     * @param expected The expected range.
     * @param function The pseudo-random function being tested.
     */
    private void checkRange(String result, double[] expected, String function) {
        String[] range = result.split(",");
        double delta = Math.max(expected[0], expected[1]) * 1E-5;
        assertEquals(Double.parseDouble(range[0]), expected[0], Math.abs(delta),
                getMessage() + ": " + function);
        assertEquals(Double.parseDouble(range[1]), expected[1], Math.abs(delta),
                getMessage() + ": " + function);
    }

    /**
     * Validate the results of the given functions.
     *
     * @param result The result obtained from the test.
     * @param expected The expected result values.
     * @param functions The functions being tested.
     */
    private void checkResult(String result, Object[] expected, String[] functions) {
        String[] results = result.split(",");
        assertEquals(results.length, expected.length, getMessage());
        for (int i = 0; i < expected.length; i++) {
            Object actual = convertValue(results[i], expected[i].getClass());
            if (actual instanceof Double) {
                assertEquals(((Double) actual).doubleValue(), ((Double) expected[i]).doubleValue(),
                        1E-6, getMessage() + ": " + functions[i]);
            } else {
                assertEquals(actual, expected[i], getMessage() + ": " + functions[i]);
            }
        }
    }

    /**
     * Converts a {@code String} value to the target type.
     *
     * @param value The {@code String} value.
     * @param target The target type.
     * @return The converted value.
     */
    private Object convertValue(String value, Class<?> target) {
        if (String.class.isAssignableFrom(target)) {
            return value;
        }
        Object result = null;
        try {
            Method parseMethod = target.getDeclaredMethod("valueOf", String.class);
            result = parseMethod.invoke(null, value);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            // Ignore
        }
        return result;
    }
}
