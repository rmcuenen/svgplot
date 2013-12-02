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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;

/**
 * Test class for testing {@code SVGPlotModule.js}.
 *
 * @author R. M. Cuenen
 */
public class SVGPlotModuleTest extends AbstractTestClass {

    private static final String TEST_MODULE_NAME = "TestModule";
    private static final String TEST_MODULE_CALLBACK = "function(element) { document.documentElement.appendChild(element); }";
    private static final String TEST_MODULE_ID = "loaded-text";

    @Test
    public void testSVGPlotModuleLoaderWithBase() {
        Wait wait = load(MODULE_LOADER_WITH_BASE, 10);
        require(TEST_MODULE_CALLBACK, TEST_MODULE_NAME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(TEST_MODULE_ID)));
    }

    @Test
    public void testSVGPlotModuleRelative() {
        Wait wait = load(MODULE_LOADER_RELATIVE, 10);
        require(TEST_MODULE_CALLBACK, TEST_MODULE_NAME);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(TEST_MODULE_ID)));
    }
}
