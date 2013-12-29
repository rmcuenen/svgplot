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

import java.awt.geom.Point2D;
import static org.testng.Assert.assertEquals;

/**
 * Utility class that validates the path commands of a function.
 *
 * @author R. M. Cuenen
 */
public final class PathValidator {

    public static final Function X_HALFSQUAREDMINUS1 = new Function() {

        @Override
        public Point2D eval(double x) {
            return new Point2D.Double(x, 0.5 * x * x - 1.0);
        }
    };

    public static final Function SQUARED_ATAN = new Function() {

        @Override
        public Point2D eval(double x) {
            return new Point2D.Double(x * x, Math.atan(x));
        }
    };

    public static final Function X_SIN = new Function() {

        @Override
        public Point2D eval(double x) {
            return new Point2D.Double(x, Math.sin(x));
        }
    };

    public static interface Function {

        public Point2D eval(double x);
    }

    private PathValidator() {
        // Utility class.
    }

    public static void validatePath(String path, Function func, double start, double end, int count) {
        assertEquals(path.charAt(0), 'M');
        int index = 1;
        double x = start;
        double step = (end - start) / count;
        for (int i = 0; i <= count; i++) {
            Point2D p = func.eval(x);
            int next = path.indexOf('L', index);
            if (next == -1) {
                next = path.length();
            }
            String coords[] = path.substring(index, next).split(",");
            assertEquals(Double.parseDouble(coords[0]), p.getX(), 1E-6);
            assertEquals(Double.parseDouble(coords[1]), p.getY() != 0 ? -p.getY() : p.getY(), 1E-6);
            x += step;
            index = next + 1;
        }
    }
}
