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

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract class that loads and stops the Selenium {@code WebDriver}.
 *
 * @author R. M. Cuenen
 */
public abstract class AbstractTestClass {

    public static final String MODULE_LOADER = "/ModuleLoader.svg";
    private static final String TITLE_SCRIPT = "document.getElementsByTagName('title')[0].textContent = arguments[0];";
    private static final String PLACEHOLDER_ID = "placeholder";
    private static final String RESULT_ATTRIBUTE = "result";
    private static final String BASE_URL = "http://localhost:8080";
    private static final String CONTEXT = "/test";

    private WebDriver driver;
    private JavascriptExecutor js;

    @BeforeClass
    public void startSelenium() throws Exception {
        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities dc = DesiredCapabilities.chrome();
        dc.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(dc);
        js = (JavascriptExecutor) driver;
    }

    @AfterClass(alwaysRun = true)
    public void stopSelenium() throws Exception {
        driver.quit();
    }

    /**
     * Load the given resource into the browser. This also sets the document's
     * title to the implementing class name.
     *
     * @param resource The resource to be loaded.
     * @param timeout The timeout in seconds of the returned {@code Wait}
     * object.
     * @return The {@code Wait} object obtained from the WebDriver.
     */
    protected Wait load(String resource, int timeout) {
        driver.get(BASE_URL + CONTEXT + resource);
        js.executeScript(TITLE_SCRIPT, getClass().getSimpleName());
        return new WebDriverWait(driver, timeout);
    }

    /**
     * Require the given modules.
     *
     * @param callback The callback function to be called after loading.
     * @param modules The modules that will be required.
     */
    protected void require(String callback, String... modules) {
        StringBuilder script = new StringBuilder();
        script.append("SVGModule.require(");
        script.append(toString(modules)).append(',');
        script.append(callback).append(");");
        js.executeScript(script.toString());
    }

    /**
     * Retrieve the result as stored in the place holders result attribute.
     *
     * @return The stored result.
     */
    protected String getResult() {
        WebElement placeholder = getElementById(PLACEHOLDER_ID);
        return placeholder.getAttribute(RESULT_ATTRIBUTE);
    }

    /**
     * Retrieve the text of an alert window. This method also accepts the alert
     * (pressing OK).
     *
     * @throws NoAlertPresentException If the dialog cannot be found
     * @return The message text.
     */
    protected String getAlert() throws NoAlertPresentException {
        Alert alert = driver.switchTo().alert();
        String text = alert.getText();
        alert.accept();
        return text;
    }

    /**
     * Retrieve a WebElement by its ID.
     *
     * @param id The element's identifier.
     * @throws NoSuchElementException If no matching elements are found
     * @return The corresponding WebElement.
     */
    protected WebElement getElementById(String id) throws NoSuchElementException {
        return driver.findElement(By.id(id));
    }

    /**
     * Converts an array of module identifiers to the request string.
     *
     * @param a The array of module identifier.
     * @return The string representation of the request array.
     */
    private static String toString(String[] a) {
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0;; i++) {
            b.append('"').append(a[i]).append('"');
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }
}
