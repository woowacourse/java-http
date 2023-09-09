package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;

public enum HandlerMapping {

    MAIN("/", HttpMethod.GET, "Hello world!"),
    INDEX("/index.html", HttpMethod.GET, "index.html"),
    INDEX_STYLE("/css/styles.css", HttpMethod.GET, "css/styles.css"),
    INDEX_JS("/js/scripts.js", HttpMethod.GET, "js/scripts.js"),
    CHART_BAR_JS("/assets/chart-bar.js", HttpMethod.GET, "assets/chart-bar.js"),
    CHART_AREA_JS("/assets/chart-area.js", HttpMethod.GET, "assets/chart-area.js"),
    CHART_PIE_JS("/assets/chart-pie.js", HttpMethod.GET, "assets/chart-pie.js"),
    LOGIN("/login", HttpMethod.GET, "login.html"),
    LOGIN_POST("/login", HttpMethod.POST, "login.html"),
    UNAUTHORIZED("/401.html", HttpMethod.GET, "401.html"),
    REGISTER("/register", HttpMethod.GET, "register.html"),
    REGISTER_POST("/register", HttpMethod.POST, "register.html");

    private final String path;
    private final HttpMethod httpMethod;
    private final String response;

    HandlerMapping(final String path, final HttpMethod httpMethod, final String response) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.response = response;
    }

    public static HandlerMapping find(final String path, final HttpMethod httpMethod) {
        return Arrays.stream(HandlerMapping.values())
            .filter(mapping -> mapping.path.equals(path) && mapping.httpMethod == httpMethod)
            .findAny()
            .orElse(null);
    }

    public String getResponse() {
        return response;
    }
}
