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
 * Test class for testing {@code MathematicalEngine.js}.
 *
 * @author R. M. Cuenen
 */
public class MathematicalEngineTest extends AbstractTestClass {
    
    private static final String MODULE_NAME = "MathematicalEngine";
    private static final String FUNCTION_FORMAT = "function(ME) { var d = %s; setResult(d); }";
    
    @Test
    public void dummyTest() {
        load(MODULE_LOADER, 1);
        String callback = String.format(FUNCTION_FORMAT, "ME.pi()");
        require(callback, MODULE_NAME);
        String result = getResult();
        assertNotNull(result);
    }
}
