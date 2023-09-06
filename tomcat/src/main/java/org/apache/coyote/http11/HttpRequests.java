package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

public enum HttpRequests {
    INDEX("GET", "/index.html", "static/index.html", "200", "OK", "text/html"),
    CSS("GET", "/css/styles.css", "static/css/styles.css", "200", "OK", "text/css"),
    LOGIN("GET", "/login", "static/login.html", "200", "OK", "text/html"),
    LOGIN_POST("POST", "/login", "/index.html", "302", "Found", "text/html"),
    SCRIPT_JS("GET", "/js/scripts.js", "static/js/scripts.js", "200", "OK", "Application/javascript"),
    CHART_AREA_JS("GET", "/assets/chart-area.js", "static/assets/chart-area.js", "200", "OK", "Application/javascript"),
    CHART_BAR_JS("GET", "/assets/chart-bar.js", "static/assets/chart-bar.js", "200", "OK", "Application/javascript"),
    CHART_PIE_JS("GET", "/assets/chart-pie.js", "static/assets/chart-pie.js", "200", "OK", "Application/javascript"),
    UNAUTHORIZED("GET", "/401.html", "static/401.html", "401", "Unauthorized", "text.html"),
    FOUND("GET", "/redirect", "/index.html", "302", "Found", "text.html"),
    REGISTER("GET", "/register", "static/register.html", "200", "OK", "text.html"),
    REGISTER_MEMBER("POST", "/register", "/index.html", "201", "Created", "text.html"),
    HELLO("GET", "/", "NONE", "200", "OK", "text/html");

    private final String method;
    private final String url;
    private final String fileName;
    private final String statusCode;
    private final String statusText;
    private final String contentType;

    HttpRequests(String method, String url, String fileName, String statusCode, String statusText, String contentType) {
        this.method = method;
        this.url = url;
        this.fileName = fileName;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.contentType = contentType;
    }

    public static HttpRequests ofResourceNameAndMethod(String resourceName, String method) throws IOException, URISyntaxException {
        return Arrays.stream(values())
                .filter(request -> request.method.equals(method) && resourceName.startsWith(request.url))
                .findAny()
                .orElse(HELLO);
    }

    public Path readPath() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource(this.fileName);

        return Path.of(url.toURI());
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getContentType() {
        return contentType;
    }
}
