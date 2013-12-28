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

    @Test(expectedExceptions = NoSuchElementException.class)
    public void handlePlotElementTest() {
        Wait wait = load(MODULE_LOADER, 10);
        StringBuilder plot = new StringBuilder(CREATE_ELEMENT);
        addAttribute(plot, "stroke", "black");
        addAttribute(plot, "domain", "-1:1");
        addAttribute(plot, "samples", "10");
        addAttribute(plot, "function", "#t^2,atan(#t)");
        addAttribute(plot, "variable", "t");
        addAttribute(plot, "id", "plot-element");
        String callback = String.format(CALLBACK, plot.toString());
        require(callback, MODULE_NAME);
        wait.until(RESULT_SET);
        assertEquals(getResult(), "[object SVGPathElement]");
        getElementById("plot-element");
    }

    @Test
    public void handleAndReplaceTest() {
        Wait wait = load(MODULE_LOADER, 10);
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
        require(callback, MODULE_NAME);
        wait.until(RESULT_SET);
        WebElement path = getElementById("plot-element");
        validatePath(path.getAttribute("d"), X_HALFSQUAREDMINUS1, 0, 2, 0.2);
    }

    @Test
    public void handleNoFunctionTest() {
        Wait wait = load(MODULE_LOADER, 10);
        StringBuilder elem = new StringBuilder(CREATE_ELEMENT);
        addAttribute(elem, "samples", "100");
        String callback = String.format(CALLBACK, elem.toString());
        require(callback, MODULE_NAME);
        wait.until(ExpectedConditions.alertIsPresent());
        String alert = getAlert();
        assertTrue(alert.startsWith("NotFoundError: Function not set: <plot samples=\"100\" />"), alert);
    }

    private void addAttribute(Appendable sb, String name, String value) {
        try {
            sb.append("el.setAttribute(\"").append(name);
            sb.append("\",\"").append(value).append("\");");
        } catch (IOException ex) {
        }
    }
}
