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
    private static final Rectangle2D BEFORE_RECT = new Rectangle2D.Double(-1.5, 1.5, 1, 1);

    /**
     * Test the scaling of the SVGDocument.
     */
    @Test(dependsOnMethods = "initializeDriver")
    public void documentScalerTest() {
        Wait wait = load(MODULE_LOADER_SCALER, 10);
        wait.until(RESULT_SET);
        WebElement circle = getElementById(PLACEHOLDER_ID);
        String before = circle.getAttribute("before");
        String after = circle.getAttribute("after");
        assertNotEquals(before, after, getMessage());
        assertEquals(before, toJSON(BEFORE_RECT), getMessage());
        String[] size = getResult().split(",");
        Rectangle2D afterRect = createRect(Double.parseDouble(size[0]), Double.parseDouble(size[1]));
        assertEquals(after, toJSON(afterRect), getMessage());
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
        double x = Math.floor(BEFORE_RECT.getX());
        double y = Math.floor(BEFORE_RECT.getY());
        double w = BEFORE_RECT.getWidth() + 2 * (BEFORE_RECT.getX() - x);
        double h = BEFORE_RECT.getHeight() + 2 * (BEFORE_RECT.getY() - y);
        double sx = cw / w;
        double sy = ch / h;
        double s = Math.min(sx, sy);
        double left = BEFORE_RECT.getX() - x;
        double top = BEFORE_RECT.getY() - y;
        return new Rectangle2D.Double(
                sx * Math.ceil(left) - s * (left - Math.floor(left)),
                sy * Math.ceil(top) - s * (top - Math.floor(top)),
                s * BEFORE_RECT.getWidth(),
                s * BEFORE_RECT.getHeight());
    }

    /**
     * Returns the JSON string representation of the rectangle object.
     *
     * @param r The rectangle to stringify.
     * @return The JSON string representation.
     */
    private String toJSON(Rectangle2D r) {
        String left = r.getMinX() % 1 == 0 ? Integer.toString((int) r.getMinX()) : Double.toString(r.getMinX());
        String top = r.getMinY() % 1 == 0 ? Integer.toString((int) r.getMinY()) : Double.toString(r.getMinY());
        String bottom = r.getMaxY() % 1 == 0 ? Integer.toString((int) r.getMaxY()) : Double.toString(r.getMaxY());
        String right = r.getMaxX() % 1 == 0 ? Integer.toString((int) r.getMaxX()) : Double.toString(r.getMaxX());
        String width = r.getWidth() % 1 == 0 ? Integer.toString((int) r.getWidth()) : Double.toString(r.getWidth());
        String height = r.getHeight() % 1 == 0 ? Integer.toString((int) r.getHeight()) : Double.toString(r.getHeight());
        StringBuilder rect = new StringBuilder();
        rect.append('{');
        rect.append('"').append("height").append('"').append(':').append(height).append(',');
        rect.append('"').append("width").append('"').append(':').append(width).append(',');
        rect.append('"').append("left").append('"').append(':').append(left).append(',');
        rect.append('"').append("bottom").append('"').append(':').append(bottom).append(',');
        rect.append('"').append("right").append('"').append(':').append(right).append(',');
        rect.append('"').append("top").append('"').append(':').append(top).append('}');
        return rect.toString();
    }

}
