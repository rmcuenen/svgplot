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

import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;

/**
 * Abstract class that loads and stops the Selenium {@code WebDriver}.
 *
 * @author R. M. Cuenen
 */
public abstract class AbstractTestClass {

    public static final ExpectedCondition<Boolean> RESULT_SET = new ExpectedCondition<Boolean>() {

        @Override
        public Boolean apply(WebDriver driver) {
            try {
                WebElement placeholder = driver.findElement(By.id(PLACEHOLDER_ID));
                if (placeholder != null) {
                    return placeholder.getAttribute(RESULT_ATTRIBUTE) != null;
                } else {
                    return false;
                }
            } catch (StaleElementReferenceException ex) {
                return null;
            }
        }
    };
    public static final String MODULE_LOADER = "/ModuleLoader.svg";
    private static final String TITLE_SCRIPT = "document.getElementsByTagName('title')[0].textContent = arguments[0];";
    protected static final String PLACEHOLDER_ID = "placeholder";
    private static final String RESULT_ATTRIBUTE = "result";
    private static final String BASE_URL = "http://localhost:8080";
    private static final String CONTEXT = "/test";

    /**
     * Load the given resource into the browser. This also sets the document's
     * title to the implementing class name.
     *
     * @param driver The WebDriver executing the test.
     * @param resource The resource to be loaded.
     * @param timeout The timeout in seconds of the returned {@code Wait}
     * object.
     * @return The {@code Wait} object obtained from the WebDriver.
     */
    protected Wait load(WebDriver driver, String resource, int timeout) {
        driver.get(BASE_URL + CONTEXT + resource);
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(TITLE_SCRIPT, getClass().getSimpleName());
        }
        return new WebDriverWait(driver, timeout);
    }

    /**
     * Require the given modules.
     *
     * @param driver The WebDriver executing the test.
     * @param callback The callback function to be called after loading.
     * @param modules The modules that will be required.
     */
    protected void require(WebDriver driver, String callback, String... modules) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        StringBuilder script = new StringBuilder();
        script.append("SVGModule.require(");
        script.append(toString(modules)).append(',');
        script.append(callback).append(");");
        js.executeScript(script.toString());
    }

    /**
     * Retrieve the result as stored in the place holders result attribute.
     *
     * @param driver The WebDriver executing the test.
     * @return The stored result.
     */
    protected String getResult(WebDriver driver) {
        WebElement placeholder = getElementById(driver, PLACEHOLDER_ID);
        return placeholder.getAttribute(RESULT_ATTRIBUTE);
    }

    /**
     * Retrieve the text of an alert window. This method also accepts the alert
     * (pressing OK).
     *
     * @param driver The WebDriver executing the test.
     * @throws NoAlertPresentException If the dialog cannot be found
     * @return The message text.
     */
    protected String getAlert(WebDriver driver) throws NoAlertPresentException {
        Alert alert = driver.switchTo().alert();
        String text = alert.getText();
        alert.accept();
        return text;
    }

    /**
     * Retrieve a WebElement by its ID.
     *
     * @param driver The WebDriver executing the test.
     * @param id The element's identifier.
     * @throws NoSuchElementException If no matching elements are found
     * @return The corresponding WebElement.
     */
    protected WebElement getElementById(WebDriver driver, String id) throws NoSuchElementException {
        return driver.findElement(By.id(id));
    }

    /**
     * Retrieve the message to use within assert statements.
     *
     * @param driver The WebDriver executing the test.
     * @return An error message.
     */
    protected String getMessage(WebDriver driver) {
        StringBuilder msg = new StringBuilder("Failed ");
        if (driver instanceof HasCapabilities) {
            msg.append("on ");
            msg.append(((HasCapabilities) driver).getCapabilities().getBrowserName());
        } else {
            msg.append("with ");
            msg.append(driver);
        }
        return msg.toString();
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

    @DataProvider(name = "driver")
    public Object[][] driverProdiver() {
        List<WebDriver> drivers = TestSuiteClass.getWebDrivers();
        Object[][] result = new Object[drivers.size()][1];
        for (int i = 0; i < result.length; i++) {
            result[i][0] = drivers.get(i);
        }
        return result;
    }
}
