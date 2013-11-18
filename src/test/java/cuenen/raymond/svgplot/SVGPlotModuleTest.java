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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
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
public class SVGPlotModuleTest extends HttpServlet {

    private static final String CONTEXT = "/test";
    private String baseResourceUrl;
    private Server server;
    private WebDriver driver;

    @BeforeClass
    public void startServer() throws Exception {
        server = new Server(0);
        WebAppContext context = new WebAppContext("src/test/resources", "/test-modules");
        server.addHandler(context);
        context = new WebAppContext("src/main/js", "/");
        context.addServlet(new ServletHolder(this), CONTEXT);
        server.addHandler(context);
        server.start();
        int actualPort = server.getConnectors()[0].getLocalPort();
        baseResourceUrl = "http://localhost:" + actualPort + CONTEXT;
        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities dc = DesiredCapabilities.chrome();
        dc.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new RemoteWebDriver(dc);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String page = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                + "  <title>SVG Plot test</title>\n"
                + "  <script id=\"svgplot-loader\" xlink:href=\"/SVGPlotModule.js\" base=\"test-modules\" />\n"
                + "  <script>\n"
                + "    <![CDATA[\n"
                + "      SVGModule.require([\"TestModule\"]);\n"
                + "    ]]>\n"
                + "  </script>\n"
                + "</svg>";
        resp.getWriter().write(page);
    }

    @Test
    public void testSVGPlotModule() {
        driver.get(baseResourceUrl);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loaded-text")));
    }

    @AfterClass(alwaysRun = true)
    public void stopServer() throws Exception {
        driver.quit();
        server.stop();
    }
}
