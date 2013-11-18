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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
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

    private static final String CONTEXT = "/test";
    private static final String LOADER_RESOURCE = "/TestModuleLoader.svg";
    private String baseResourceUrl;
    private Server server;
    private WebDriver driver;

    @BeforeClass
    public void startServer() throws Exception {
        server = new Server(0);
        HandlerCollection handlers = new ContextHandlerCollection();
        WebAppContext context = new WebAppContext("src/test/resources", CONTEXT);
        handlers.addHandler(context);
        context = new WebAppContext("src/main/js", "/");
        handlers.addHandler(context);
        server.setHandler(handlers);
        server.start();
        int actualPort = server.getConnectors()[0].getLocalPort();
        baseResourceUrl = "http://localhost:" + actualPort + CONTEXT;
        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities dc = DesiredCapabilities.chrome();
        dc.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new RemoteWebDriver(dc);
    }

    @Test
    public void testSVGPlotModuleLoader() {
        driver.get(baseResourceUrl + LOADER_RESOURCE);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loaded-text")));
    }

    @AfterClass(alwaysRun = true)
    public void stopServer() throws Exception {
        driver.quit();
        server.stop();
    }
}
