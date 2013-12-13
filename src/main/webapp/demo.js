function postData() {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        if (request.readyState === 4) {
            loadData(request.responseText);
        }
    };
    request.open("POST", "/plotcreator", true);
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.send(getPlotData());
}

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

function loadData(data) {
    var target = document.getElementById("plotwindow");
    target = (target.contentWindow) ? target.contentWindow :
            (target.contentDocument.document) ? target.contentDocument.document : target.contentDocument;
    target.document.open();
    target.document.write(data);
    target.document.close();
    target.document.body.style.cssText = "margin: 0px;";
}

function getPlotData() {
    var minValue = document.getElementById("min").value;
    var maxValue = document.getElementById("max").value;
    document.getElementById("domain").value = minValue + ':' + maxValue;
    var form = document.getElementById("plotinput");
    var data = "";
    for (var i = 0; i < form.elements.length; i++) {
        var element = form.elements[i];
        var elementType = element.type.toUpperCase();
        if (element.name) {
            if (elementType === "TEXT"
                    || elementType === "NUMBER"
                    || elementType === "HIDDEN") {
                data += (data.length > 0 ? '&' : '')
                        + encodeURIComponent(element.name) + '='
                        + encodeURIComponent(element.value);
            } else if (elementType === "SELECT") {
                for (var j = 0; j < element.options.length; j++) {
                    var option = element.options[j];
                    if (option.selected) {
                        data += (data.length > 0 ? '&' : '')
                                + encodeURIComponent(element.name) + '='
                                + encodeURIComponent(option.value ? option.value : option.text);
                    }
                }
            }
        }
    }
    return data;
}