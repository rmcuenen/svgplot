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
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code RandomGenerator.js}.
 *
 * @author R. M. Cuenen
 */
public class RandomGeneratorTest extends AbstractTestClass {

    private static final String MODULE_NAME = "RandomGenerator";
    private static final String FUNCTION_FORMAT = "function(RNG) { var d = %s; for (var i = 0; i < 1e6; i++) { var r = RNG.random(); %s } alert(d); return d; }";
    private static final Object[] RANGE_TEST = {"[Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY]",
        "d[0] = Math.min(d[0], r); d[1] = Math.max(d[1], r);"};

    @Test
    public void rangeTest() {
        load(MODULE_LOADER, 1);
        String rangeTestCallback = String.format(FUNCTION_FORMAT, RANGE_TEST);
        List<Object> range = (List<Object>) require(rangeTestCallback, MODULE_NAME);
        assertEquals((Double) range.get(0), 0D, 1E-6);
        assertEquals((Double) range.get(1), 1D, 1E-6);
    }
}
