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
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code SVGPlotAttribute.js}.
 *
 * @author R. M. Cuenen
 */
public class SVGPlotAttributeTest extends AbstractTestClass {

    private static final String MODULE_NAME = "SVGPlotAttribute";
    private static final String ALREADY_SET = "The attribute '%s' is already defined";
    private static final String ATTR_FUNCTION_FORMAT = "function(Attr){var a=Attr.create(\"%s\");for(var i=0;i<%d;i++){a.parse(\"%s\");}setResult(a.value);}";
    private static final String SET_FUNCTION_FORMAT = "function(Attr){Attr.setAttribute(\"%1$s\", %2$s, %3$s);var a=Attr.create(\"%1$s\");a.parse(\"%4$s\");setResult(a.value);}";
    private static final String[][] VALID_ATTRIBUTES = {
        {"domain", "0:2*pi", "-5,5", "0,6.283185307179586"},
        {"samples", "101", "25", "101"},
        {"variable", "test_Var", "#x", "#test_Var"},
        {"connected", "smooth", "sharp", "smooth"},
        {"function", "#x^2", "null", ",[object Object]"}
    };
    private static final String[][] INVALID_ATTRIBUTES = {
        {"domain", "0:-1", "Invalid domain: 0 > -1"},
        {"domain", "-5,5", "Unknown domain format: -5,5"},
        {"samples", "pi", "Invalid samples: pi"},
        {"samples", "0", "Invalid samples: 0"},
        {"variable", "_t", "Invalid variable: _t"},
        {"connected", "normal", "Invalid connection type: normal"},
        {"function", "#x,#^2,0", "Invalid function: #x,#^2,0"}
    };
    private static final String[] UNKNOWN_ATTRIBUTE = {"Bogus", "six", "Unknown attribute: Bogus"};
    private static final String[] NEW_ATTRIUTE = {UNKNOWN_ATTRIBUTE[0], "0", "function(a){var v=0;for(var i=0;i<a.length;i++){v+=a.charCodeAt(i);}return v;}"};

    /**
     * Test the parsing of the plot-element attributes.
     */
    @Test
    public void validAttributesTest() {
        for (String[] attribute : VALID_ATTRIBUTES) {
            run(attribute);
        }
    }

    /**
     * Verify the exceptions when an invalid attribute value is supplied.
     */
    @Test
    public void invalidAttributesTest() {
        for (String[] attribute : INVALID_ATTRIBUTES) {
            Wait wait = parseAttribute(attribute[0], attribute[1], 1);
            wait.until(ExpectedConditions.alertIsPresent());
            assertTrue(getAlert().startsWith(attribute[2]), attribute[0]);
        }
    }

    /**
     * Verify the exception of creating an unknown attribute.
     */
    @Test
    public void unknownAttributeTest() {
        Wait wait = parseAttribute(UNKNOWN_ATTRIBUTE[0], UNKNOWN_ATTRIBUTE[1], 0);
        wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(getAlert().startsWith(UNKNOWN_ATTRIBUTE[2]), UNKNOWN_ATTRIBUTE[0]);
    }

    /**
     * Create and test a newly defined attribute.
     */
    @Test
    public void newAttributeTest() {
        Wait wait = load(MODULE_LOADER, 10);
        String callback = String.format(SET_FUNCTION_FORMAT, NEW_ATTRIUTE[0],
                NEW_ATTRIUTE[1], "{parse: function(a){return a;}}", UNKNOWN_ATTRIBUTE[1]);
        require(callback, MODULE_NAME);
        wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(getAlert().startsWith("Invalid parse function"));
        int v = 0;
        for (int i = 0; i < UNKNOWN_ATTRIBUTE[1].length(); i++) {
            v += UNKNOWN_ATTRIBUTE[1].codePointAt(i);
        }
        load(MODULE_LOADER, 10);
        callback = String.format(SET_FUNCTION_FORMAT, NEW_ATTRIUTE[0],
                NEW_ATTRIUTE[1], NEW_ATTRIUTE[2], UNKNOWN_ATTRIBUTE[1]);
        require(callback, MODULE_NAME);
        assertEquals(getResult(), String.valueOf(v));
    }

    /**
     * Run the attribute test. This test will create the attribute three times:
     * <ol>
     * <li>Retrieve the default value.
     * <li>Parse and retrieve the value.
     * <li>Parse twice and verify the exception.
     * </ol>
     *
     * @param attribute The attribute description [name, string, default,
     * parsed].
     */
    private void run(String[] attribute) {
        for (int i = 0; i < 3; i++) {
            Wait wait = parseAttribute(attribute[0], attribute[1], i);
            switch (i) {
                case 0:
                    assertEquals(getResult(), attribute[2], attribute[0]);
                    break;
                case 1:
                    assertEquals(getResult(), attribute[3], attribute[0]);
                    break;
                case 2:
                    wait.until(ExpectedConditions.alertIsPresent());
                    assertTrue(getAlert().startsWith(String.format(ALREADY_SET, attribute[0])));
                    break;
            }
        }
    }

    /**
     * Convenience method to load the attribute creator.
     *
     * @param name The attribute name.
     * @param value The string to be parsed.
     * @param count The number of parse calls to be made.
     * @return The {@code Wait} object obtained from the WebDriver.
     */
    private Wait parseAttribute(String name, String value, int count) {
        Wait wait = load(MODULE_LOADER, 10);
        String callback = String.format(ATTR_FUNCTION_FORMAT, name, count, value);
        require(callback, MODULE_NAME);
        return wait;
    }
}
