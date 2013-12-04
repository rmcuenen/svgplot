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

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class for testing {@code RandomGenerator.js}.
 *
 * @author R. M. Cuenen
 */
public class RandomGeneratorTest extends AbstractTestClass {

    private static final String MODULE_NAME = "RandomGenerator";
    private static final String FUNCTION_FORMAT = "function(RNG) { var d = %s; for (var i = 0; i < 1e6; i++) { var r = RNG.random(); %s } %s setResult(d); }";
    private static final Object[] RANGE_TEST = {"[Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY]",
        "d[0] = Math.min(d[0], r); d[1] = Math.max(d[1], r);", ""};
    private static final Object[] MEAN_TEST = {"0", "d += r;", "d /= 1e6;"};
    private static final Object[] VARIANCE_TEST = {"0; var m = 0; var s = []", "s.push(r); m += r;",
        "m /= 1e6; for (var i = 0; i < 1e6; i++) { d += (s[i] - m) * (s[i] - m); } d /= (1e6 - 1);"};
    private static final Object[] BUCKET_TEST = {"0; b = Array.apply(null, new Array(1e4)).map(Number.prototype.valueOf, 0);",
        "b[Math.floor(r * 1e4)]++;",
        "d = b.reduce(function(t, o) { return t + ((o - 100) * (o - 100) / 100); }, 0);"};

    @Test
    public void rangeTest() {
        load(MODULE_LOADER, 1);
        String rangeTestCallback = String.format(FUNCTION_FORMAT, RANGE_TEST);
        require(rangeTestCallback, MODULE_NAME);
        String result = getResult();
        assertNotNull(result);
        String[] range = result.split(",");
        assertEquals(Double.parseDouble(range[0]), 0D, 1E-5);
        assertEquals(Double.parseDouble(range[1]), 1D, 1E-5);
    }

    @Test
    public void meanTest() {
        load(MODULE_LOADER, 1);
        String meanTestCallback = String.format(FUNCTION_FORMAT, MEAN_TEST);
        require(meanTestCallback, MODULE_NAME);
        String result = getResult();
        assertNotNull(result);
        assertEquals(Double.parseDouble(result), 0.5, Math.sqrt(3) / 3000D);
    }

    @Test
    public void varianceTest() {
        load(MODULE_LOADER, 1);
        String varianceTestCallback = String.format(FUNCTION_FORMAT, VARIANCE_TEST);
        require(varianceTestCallback, MODULE_NAME);
        String result = getResult();
        assertNotNull(result);
        assertEquals(Double.parseDouble(result), 1D / 12D, Math.sqrt(222222D) / 3999996D);
    }

    @Test
    public void bucketTest() {
        load(MODULE_LOADER, 1);
        String bucketTestCallback = String.format(FUNCTION_FORMAT, BUCKET_TEST);
        require(bucketTestCallback, MODULE_NAME);
        String result = getResult();
        assertNotNull(result);
        assertEquals(Double.parseDouble(result), 1E4 - 1D, Math.sqrt(2E4 - 2D));
    }

    @Test
    public void ksTest() {
        //TODO
    }
}
