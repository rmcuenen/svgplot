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

import java.io.IOException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;
import static cuenen.raymond.svgplot.PathValidator.*;
import org.openqa.selenium.WebDriver;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code SVGPlotter.js}.
 *
 * @author R. M. Cuenen
 */
public class SVGPlotterTest extends AbstractTestClass {

    private static final String MODULE_NAME = "SVGPlotter";
    private static final String CREATE_ELEMENT = "var el=document.createElementNS(SVGModule.SVG_NS, 'plot');";
    private static final String CALLBACK = "function(p){%s setResult(p.handle(el));}";

    /**
     * Test the handling of plot elements.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", expectedExceptions = NoSuchElementException.class)
    public void handlePlotElementTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER, 10);
        StringBuilder plot = new StringBuilder(CREATE_ELEMENT);
        addAttribute(plot, "stroke", "black");
        addAttribute(plot, "domain", "-1:1");
        addAttribute(plot, "samples", "10");
        addAttribute(plot, "function", "#t^2,atan(#t)");
        addAttribute(plot, "variable", "t");
        addAttribute(plot, "id", "plot-element");
        String callback = String.format(CALLBACK, plot.toString());
        require(driver, callback, MODULE_NAME);
        wait.until(RESULT_SET);
        assertEquals(getResult(driver), "[object SVGPathElement]", getMessage(driver));
        getElementById(driver, "plot-element");
    }

    /**
     * Test the replacement of plot elements in the SVGDocument.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void handleAndReplaceTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER, 10);
        StringBuilder plot = new StringBuilder(CREATE_ELEMENT);
        addAttribute(plot, "stroke", "blue");
        addAttribute(plot, "stroke-width", "0.05");
        addAttribute(plot, "fill", "none");
        addAttribute(plot, "domain", "0:2");
        addAttribute(plot, "samples", "10");
        addAttribute(plot, "function", "0.5*#x^2-1");
        addAttribute(plot, "id", "plot-element");
        addAttribute(plot, "transform", "translate(100, 250) scale(100)");
        plot.append("document.documentElement.appendChild(el);");
        String callback = String.format(CALLBACK, plot.toString());
        require(driver, callback, MODULE_NAME);
        wait.until(RESULT_SET);
        WebElement path = getElementById(driver, "plot-element");
        validatePath(path.getAttribute("d"), X_HALFSQUAREDMINUS1, 0, 2, 10);
    }

    /**
     * Test for an error when there is no 'function' attribute specified.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver")
    public void handleNoFunctionTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER, 10);
        StringBuilder elem = new StringBuilder(CREATE_ELEMENT);
        addAttribute(elem, "samples", "100");
        String callback = String.format(CALLBACK, elem.toString());
        require(driver, callback, MODULE_NAME);
        wait.until(ExpectedConditions.alertIsPresent());
        String alert = getAlert(driver);
        String msg = getMessage(driver);
        assertTrue(alert.startsWith("NotFoundError: Function not set: <plot samples=\"100\" />"),
                msg + " --> " + alert);
    }

    private void addAttribute(Appendable sb, String name, String value) {
        try {
            sb.append("el.setAttribute(\"").append(name);
            sb.append("\",\"").append(value).append("\");");
        } catch (IOException ex) {
        }
    }
}
