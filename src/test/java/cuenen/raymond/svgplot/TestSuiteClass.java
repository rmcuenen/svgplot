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
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * This class is used to set-up Selenium for testing.
 *
 * @author R. M. Cuenen
 */
public final class TestSuiteClass {

    private static final List<WebDriver> DRIVERS = new ArrayList<>();

    @BeforeSuite(alwaysRun = true)
    @Parameters(value = "drivers")
    public static void startSelenium(String drivers) throws Exception {
        String[] classes = drivers.split(",");
        for (String className : classes) {
            Class<?> driverClass = Class.forName(className.trim());
            DRIVERS.add((WebDriver) driverClass.newInstance());
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void stopSelenium() {
        for (WebDriver driver : DRIVERS) {
            if (driver instanceof HasCapabilities) {
                Capabilities caps = ((HasCapabilities) driver).getCapabilities();
                System.out.println(String.format("Tested with %s %s on %s", caps.getBrowserName(),
                        caps.getVersion(), caps.getPlatform()));
            } else {
                System.out.println("Tested with " + driver);
            }
            driver.quit();
        }
    }

    /**
     * Return the registered WebDrivers.
     *
     * @return The configured WebDriver objects.
     */
    public static List<WebDriver> getWebDrivers() {
        return Collections.unmodifiableList(DRIVERS);
    }
}
