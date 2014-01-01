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

/**
 * The template text used for generating the plot svg.
 *  
 * @type String
 */
var template;

/**
 * Posts the plot-data to the template and loads it.
 */
function postData() {
    if (typeof template === 'undefined') {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function() {
            if (request.readyState === 4) {
                template = request.responseText;
                loadPlotData();
            }
        };
        request.open("GET", "demo.tpl", true);
        request.send(null);
    } else {
        loadPlotData();
    }
}

/**
 * Request the demo.svg file from the server.
 * This is the initially displayed image.
 */
function loadDemo() {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        if (request.readyState === 4) {
            loadData(request.responseText);
        }
    };
    request.open("GET", "demo.svg", true);
    request.send(null);
}

/**
 * Writes the given data to the document of the target iframe.
 * 
 * @param {String} data The data to write.
 */
function loadData(data) {
    var target = document.getElementById("plotwindow");
    target = (target.contentWindow) ? target.contentWindow :
            (target.contentDocument.document) ? target.contentDocument.document : target.contentDocument;
    target.document.open();
    target.document.write(data);
    target.document.close();
    target.document.body.style.cssText = "margin: 0px;";
}

/**
 * Reads and serializes the form data into the template.
 */
function loadPlotData() {
    var params = {};
    var minValue = document.getElementById("min").value;
    var maxValue = document.getElementById("max").value;
    document.getElementById("domain").value = minValue + ':' + maxValue;
    var form = document.getElementById("plotinput");
    for (var i = 0; i < form.elements.length; i++) {
        var element = form.elements[i];
        var elementType = element.type.toUpperCase();
        if (element.name) {
            if (elementType === "TEXT"
                    || elementType === "NUMBER"
                    || elementType === "HIDDEN") {
                params[element.name] = element.value;
            } else if (elementType.indexOf("SELECT") === 0) {
                for (var j = 0; j < element.options.length; j++) {
                    var option = element.options[j];
                    if (option.selected) {
                        params[element.name] = option.value ? option.value : option.text;
                    }
                }
            }
        }
    }
    var data = template;
    for (var name in params) {
        if (params.hasOwnProperty(name)) {
            var re = new RegExp('{\\$' + name + '}', 'g');
            data = data.replace(re, params[name]);
        }
    }
    loadData(data);
}