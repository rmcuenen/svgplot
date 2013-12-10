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
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author R. M. Cuenen
 */
public class OnlineDemoServlet extends HttpServlet {

    private static final String[] PARAMETERS = {"base", "domain", "samples", "variable", "connected", "function"};
    private static final String CONTENT_TYPE = "image/svg+xml";
    private static final String TEMPLATE_FILE = "template.svg";
    private static final String CHARSET = "UTF-8";

    private final MessageFormat template = new MessageFormat("");

    @Override
    public void init() throws ServletException {
        InputStream input = getClass().getResourceAsStream(TEMPLATE_FILE);
        String pattern = new Scanner(input, CHARSET).useDelimiter("\\A").next();
        template.applyPattern(pattern);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> params = new ArrayList<>();
        for (String name : PARAMETERS) {
            params.add(req.getParameter(name));
        }
        resp.setContentType(CONTENT_TYPE);
        PrintWriter writer = resp.getWriter();
        writer.println(template.format(params.toArray()));
    }
}
