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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test class for testing {@code SVGPlotModule.js}.
 *
 * @author R. M. Cuenen
 */
public class SVGPlotModuleTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String CONTEXT = "/test";
    private static final String TEST_MODULE_ID = "loaded-text";
    private static final String TEST_MODULE_LOADER_WITH_BASE = "/TestModuleLoaderWithBase.svg";
    private static final String TEST_MODULE_LOADER_RELATIVE = "/TestModuleLoaderRelative.svg";
    private WebDriver driver;

    @BeforeClass
    public void startSelenium() throws Exception {
        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities dc = DesiredCapabilities.chrome();
        dc.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(dc);
    }

    @Test
    public void testSVGPlotModuleLoaderWithBase() {
        driver.get(BASE_URL + CONTEXT + TEST_MODULE_LOADER_WITH_BASE);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(TEST_MODULE_ID)));
    }

    @Test
    public void testSVGPlotModuleRelative() {
        driver.get(BASE_URL + CONTEXT + TEST_MODULE_LOADER_RELATIVE);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(TEST_MODULE_ID)));
    }

    @AfterClass(alwaysRun = true)
    public void stopSelenium() throws Exception {
        driver.quit();
    }
}
