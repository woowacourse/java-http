package org.apache.coyote;

import java.util.List;

public class Constants {

    public static final String ROOT = "static";

    public static final List<String> HTML = List.of("/index.html", "/login.html", "/register.html");
    public static final List<String> JS = List.of("/js/scripts.js",
            "/assets/chart-area.js", "/assets/chart-bar.js", "/assets/chart-pie.js");
    public static final List<String> CSS = List.of("/css/styles.css");
    public static final List<String> IMG = List.of("/assets/img/error-404-monochrome.svg");
}
