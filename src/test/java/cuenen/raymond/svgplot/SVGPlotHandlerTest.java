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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;
import static cuenen.raymond.svgplot.PathValidator.*;
import org.openqa.selenium.WebDriver;

/**
 * Test class for testing {@code SVGPlotHandler.js}.
 *
 * @author R. M. Cuenen
 */
public class SVGPlotHandlerTest extends AbstractTestClass {

    private static final String MODULE_NAME = "SVGPlotHandler";
    private static final String MODULE_LOADER_PLOT = "/ModuleLoaderPlot.svg";
    private static final String CALLBACK_FORMAT = "function() { %s }";
    private static final String DONE_SCRIPT = createDoneScript();
    private static final String DONE_ID = "done";
    private static final String APPEND_SCRIPT = "setTimeout(appendPlot, 500);";
    private static final String APPEND_ID = "added-plot";

    /**
     * Creates the JavaScript adding an element to the document. This is used to
     * determine when the test is done.
     *
     * @return The JavaScript string.
     */
    private static String createDoneScript() {
        StringBuilder sb = new StringBuilder();
        sb.append("var d = document.createElementNS(SVGModule.SVG_NS, \"text\");");
        sb.append("d.setAttribute(\"id\",\"done\");");
        sb.append("document.documentElement.appendChild(d);");
        return sb.toString();
    }

    /**
     * Test the SVGPlothandler. Note that this module has no interface.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = "all")
    public void handlerTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER_PLOT, 1);
        String callback = String.format(CALLBACK_FORMAT, DONE_SCRIPT);
        require(driver, callback, MODULE_NAME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DONE_ID)));
        validateResult(driver);
    }

    /**
     * Test that the SVGPlotHandler also handles plot elements when added after
     * loading.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = "all")
    public void changeTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER_PLOT, 1);
        String callback = String.format(CALLBACK_FORMAT, APPEND_SCRIPT);
        require(driver, callback, MODULE_NAME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(APPEND_ID)));
        validateResult(driver);
        WebElement path = getElementById(driver, APPEND_ID);
        validatePath(path.getAttribute("d"), X_SIN, -Math.PI, Math.PI, 25);
    }

    /**
     * Validate the two generated paths.
     *
     * @param driver The WebDriver executing the test.
     */
    private void validateResult(WebDriver driver) {
        WebElement path = getElementById(driver, "svg-plot-1");
        validatePath(path.getAttribute("d"), SQUARED_ATAN, -1, 1, 10);
        path = getElementById(driver, "svg-plot-2");
        validatePath(path.getAttribute("d"), X_HALFSQUAREDMINUS1, 0, 2, 10);
    }
}
