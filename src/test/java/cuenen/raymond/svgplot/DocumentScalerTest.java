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

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code DocumentScaler.js}.
 *
 * @author R. M. Cuenen
 */
public class DocumentScalerTest extends AbstractTestClass {

    private static final String MODULE_LOADER_SCALER = "/ModuleLoaderScaler.svg";
    private static final Rectangle2D BBOX = new Rectangle2D.Double(-1.5, 1.5, 1, 1);

    /**
     * Test the scaling of the SVGDocument.
     *
     * @param driver The WebDriver executing the test.
     */
    @Test(dataProvider = "driver", groups = "all")
    public void documentScalerTest(WebDriver driver) {
        Wait wait = load(driver, MODULE_LOADER_SCALER, 1);
        wait.until(RESULT_SET);
        WebElement circle = getElementById(driver, PLACEHOLDER_ID);
        String before = circle.getAttribute("before");
        String after = circle.getAttribute("after");
        String msg = getMessage(driver);
        assertNotEquals(before, after, msg);
        Capabilities caps = ((HasCapabilities) driver).getCapabilities();
        Rectangle2D beforeRect = getBeforeRect(caps);
        assertEquals(toRect(before), beforeRect, msg);
        String[] size = getResult(driver).split(",");
        Rectangle2D afterRect = createRect(Double.parseDouble(size[0]), Double.parseDouble(size[1]));
        if ("opera".equals(caps.getBrowserName())) {
            afterRect.setRect(
                    Math.floor(afterRect.getX()),
                    Math.floor(afterRect.getY()),
                    Math.floor(afterRect.getWidth()),
                    Math.floor(afterRect.getHeight()));
        }
        Rectangle2D expected = toRect(after);
        if ("firefox".equals(caps.getBrowserName())) {
            /* Don't know how firefox comes op with its position coordinates. */
            assertTrue(Double.compare(expected.getWidth(), 2 * afterRect.getWidth()) == 0);
            assertTrue(Double.compare(expected.getHeight(), 2 * afterRect.getHeight()) == 0);
        } else {
            assertEquals(expected, afterRect, msg);
        }
    }

    private Rectangle2D getBeforeRect(Capabilities caps) {
        switch (caps.getBrowserName()) {
            case "opera":
                return new Rectangle2D.Double(
                        Math.ceil(BBOX.getX()),
                        Math.floor(BBOX.getY()),
                        BBOX.getWidth(),
                        BBOX.getHeight()
                );
            case "firefox":
                return new Rectangle2D.Double(
                        Math.floor(BBOX.getX()),
                        Math.floor(BBOX.getY()),
                        BBOX.getWidth() + 2 * (BBOX.getX() - Math.floor(BBOX.getX())),
                        BBOX.getHeight() + 2 * (BBOX.getY() - Math.floor(BBOX.getY())));
            case "chrome":
            default:
                return new Rectangle2D.Double(
                        BBOX.getX(),
                        BBOX.getY(),
                        BBOX.getWidth(),
                        BBOX.getHeight());
        }
    }

    /**
     * Converts an JSON string to a Rectangle.
     *
     * @param json The JSON string to convert.
     * @return The representing Rectangle.
     */
    private Rectangle2D toRect(String json) {
        Map<String, Double> obj = new HashMap<>();
        String key = "";
        int index = 0;
        while (index < json.length()) {
            switch (json.charAt(index)) {
                case '{':
                case '"':
                case '}':
                    break;
                case ':':
                    int i = json.indexOf(',', index);
                    if (i == -1) {
                        i = json.length() - 1;
                    }
                    obj.put(key, Double.parseDouble(json.substring(index + 1, i)));
                    key = "";
                    index = i;
                    break;
                default:
                    key += json.charAt(index);
            }
            index++;
        }
        return new Rectangle2D.Double(obj.get("left"), obj.get("top"),
                obj.get("width"), obj.get("height"));
    }

    /**
     * Create the expected scaled rectangle. This is based upon the client width
     * and height of the browser.
     *
     * @param cw The client width.
     * @param ch The client height.
     * @return The scaled rectangle.
     */
    private Rectangle2D createRect(double cw, double ch) {
        double x = Math.floor(BBOX.getX());
        double y = Math.floor(BBOX.getY());
        double w = BBOX.getWidth() + 2 * (BBOX.getX() - x);
        double h = BBOX.getHeight() + 2 * (BBOX.getY() - y);
        double sx = cw / w;
        double sy = ch / h;
        double s = Math.min(sx, sy);
        double left = BBOX.getX() - x;
        double top = BBOX.getY() - y;
        return new Rectangle2D.Double(
                sx * Math.ceil(left) - s * (left - Math.floor(left)),
                sy * Math.ceil(top) - s * (top - Math.floor(top)),
                s * BBOX.getWidth(),
                s * BBOX.getHeight());
    }
}
