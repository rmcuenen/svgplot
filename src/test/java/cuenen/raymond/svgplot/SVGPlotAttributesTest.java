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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code SVGPlotAttributes.js}.
 *
 * @author R. M. Cuenen
 */
public class SVGPlotAttributesTest extends AbstractTestClass {

    private static final String MODULE_NAME = "SVGPlotAttributes";
    private static final String ALREADY_SET = "StateError: The attribute '%s' is already defined";
    private static final String ATTR_FUNCTION_FORMAT = "function(Attr){var a=Attr.create(\"%s\");for(var i=0;i<%d;i++){a.parse(\"%s\");}setResult(a.value);}";
    private static final String SET_FUNCTION_FORMAT = "function(Attr){Attr.setAttribute(\"%1$s\", %2$s, %3$s);var a=Attr.create(\"%1$s\");a.parse(\"%4$s\");setResult(a.value);}";
    private static final String NAMES_FUNCTION = "function(Attr){setResult(Attr.names());}";
    private static final String[][] VALID_ATTRIBUTES = {
        {"domain", "0:2*pi", "-5,5", "0,6.283185307179586"},
        {"samples", "101", "25", "101"},
        {"variable", "test_Var", "#x", "#test_Var"},
        {"connected", "smooth", "sharp", "smooth"},
        {"function", "#x^2", "null", ",[object Object]"}
    };
    private static final String[][] INVALID_ATTRIBUTES = {
        {"domain", "0:-1", "ParseError: Invalid domain: 0 > -1"},
        {"domain", "-5,5", "ParseError: Unknown domain format: -5,5"},
        {"samples", "pi", "ParseError: Invalid samples: pi"},
        {"samples", "0", "ParseError: Invalid samples: 0"},
        {"variable", "_t", "ParseError: Invalid variable: _t"},
        {"connected", "normal", "ParseError: Invalid connection type: normal"},
        {"function", "#x,#^2,0", "ParseError: Invalid function: #x,#^2,0"}
    };
    private static final String[] UNKNOWN_ATTRIBUTE = {"Bogus", "six", "NotFoundError: Unknown attribute: Bogus"};
    private static final String[] NEW_ATTRIUTE = {UNKNOWN_ATTRIBUTE[0], "0", "function(a){var v=0;for(var i=0;i<a.length;i++){v+=a.charCodeAt(i);}return v;}"};

    /**
     * Test the parsing of the plot-element attributes.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = "all")
    public void validAttributesTest(WebDriver driver) {
        String msg = getMessage(driver);
        for (String[] attribute : VALID_ATTRIBUTES) {
            run(driver, attribute, msg);
        }
    }

    /**
     * Verify the error when parsing the attribute twice.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = {"all", "alert"})
    public void multipleAttributesTest(WebDriver driver) {
        String msg = getMessage(driver);
        for (String[] attribute : VALID_ATTRIBUTES) {
            Wait wait = parseAttribute(driver, attribute[0], attribute[1], 2);
            wait.until(ExpectedConditions.alertIsPresent());
            String alert = getAlert(driver);
            assertTrue(alert.startsWith(String.format(ALREADY_SET, attribute[0])),
                    msg + ": " + attribute[0] + " --> " + alert);
        }
    }

    /**
     * Verify the exceptions when an invalid attribute value is supplied.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = {"all", "alert"})
    public void invalidAttributesTest(WebDriver driver) {
        String msg = getMessage(driver);
        for (String[] attribute : INVALID_ATTRIBUTES) {
            Wait wait = parseAttribute(driver, attribute[0], attribute[1], 1);
            wait.until(ExpectedConditions.alertIsPresent());
            String alert = getAlert(driver);
            assertTrue(alert.startsWith(attribute[2]),
                    msg + ": " + attribute[0] + " --> " + alert);
        }
    }

    /**
     * Verify the exception of creating an unknown attribute.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = {"all", "alert"})
    public void unknownAttributeTest(WebDriver driver) {
        Wait wait = parseAttribute(driver, UNKNOWN_ATTRIBUTE[0], UNKNOWN_ATTRIBUTE[1], 0);
        wait.until(ExpectedConditions.alertIsPresent());
        String alert = getAlert(driver);
        String msg = getMessage(driver);
        assertTrue(alert.startsWith(UNKNOWN_ATTRIBUTE[2]),
                msg + ": " + UNKNOWN_ATTRIBUTE[0] + " --> " + alert);
    }

    /**
     * Create and test a newly defined attribute.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = "all")
    public void newAttributeTest(WebDriver driver) {
        int v = 0;
        for (int i = 0; i < UNKNOWN_ATTRIBUTE[1].length(); i++) {
            v += UNKNOWN_ATTRIBUTE[1].codePointAt(i);
        }
        Wait wait = load(driver, MODULE_LOADER, 1);
        String callback = String.format(SET_FUNCTION_FORMAT, NEW_ATTRIUTE[0],
                NEW_ATTRIUTE[1], NEW_ATTRIUTE[2], UNKNOWN_ATTRIBUTE[1]);
        require(driver, callback, MODULE_NAME);
        wait.until(RESULT_SET);
        String result = getResult(driver);
        String msg = getMessage(driver);
        assertEquals(result, String.valueOf(v), msg);
    }

    /**
     * Test for failure of a newly defined attribute.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = {"all", "alert"})
    public void invalidNewAttributeTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER, 1);
        String callback = String.format(SET_FUNCTION_FORMAT, NEW_ATTRIUTE[0],
                NEW_ATTRIUTE[1], "{parse: function(a){return a;}}", UNKNOWN_ATTRIBUTE[1]);
        require(driver, callback, MODULE_NAME);
        wait.until(ExpectedConditions.alertIsPresent());
        String msg = getMessage(driver);
        String alert = getAlert(driver);
        assertTrue(alert.startsWith("TypeError: Invalid parse function"),
                msg + " --> " + alert);
    }

    /**
     * Test the result of the 'names' interface function.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = "all")
    public void namesTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER, 1);
        require(driver, NAMES_FUNCTION, MODULE_NAME);
        wait.until(RESULT_SET);
        String msg = getMessage(driver);
        String result = getResult(driver);
        assertNotNull(result, msg);
        List<String> names = new ArrayList<>(Arrays.asList(result.split(",")));
        for (String[] attr : VALID_ATTRIBUTES) {
            assertTrue(names.contains(attr[0]), msg + ": " + attr[0]);
            names.remove(attr[0]);
        }
        assertTrue(names.isEmpty(), msg);
    }

    /**
     * Run the attribute test. This test will create the attribute three times:
     * <ol>
     * <li>Retrieve the default value.
     * <li>Parse and retrieve the value.
     * <li>Parse twice and verify the exception.
     * </ol>
     *
     * @param driver The WebDriver executing the test.
     * @param attribute The attribute description [name, string, default,
     * parsed].
     * @param msg The error message to use.
     */
    private void run(WebDriver driver, String[] attribute, String msg) {
        Wait wait = parseAttribute(driver, attribute[0], attribute[1], 0);
        wait.until(RESULT_SET);
        assertEquals(getResult(driver), attribute[2], msg + ": " + attribute[0]);
        wait = parseAttribute(driver, attribute[0], attribute[1], 1);
        wait.until(RESULT_SET);
        assertEquals(getResult(driver), attribute[3], msg + ": " + attribute[0]);
    }

    /**
     * Convenience method to load the attribute creator.
     *
     * @param driver The WebDriver executing the test.
     * @param name The attribute name.
     * @param value The string to be parsed.
     * @param count The number of parse calls to be made.
     * @return The {@code Wait} object obtained from the WebDriver.
     */
    private Wait parseAttribute(WebDriver driver, String name, String value, int count) {
        Wait wait = load(driver, MODULE_LOADER, 1);
        String callback = String.format(ATTR_FUNCTION_FORMAT, name, count, value);
        require(driver, callback, MODULE_NAME);
        return wait;
    }
}
