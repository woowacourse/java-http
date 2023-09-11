package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Http11Response {

    private static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CRLF = "\r\n";

    private final Map<String, String> cookieValues = new HashMap<>();
    private URL resource;
    private int httpStatusCode;
    private String statusMessage;

    public Http11Response(URL resource, int httpStatusCode, String statusMessage) {
        this.resource = resource;
        this.httpStatusCode = httpStatusCode;
        this.statusMessage = statusMessage;
    }

    public Http11Response() {
    }

    public void addCookie(final String key, final String value) {
        cookieValues.put(key, value);
    }

    private String buildResponse() throws IOException {
        final File requestedResource = new File(getResource().getFile());
        final String responseBody = buildResponseBody(requestedResource);

        final StringBuilder sb = new StringBuilder();
        sb.append(HTTP_VERSION + " " + httpStatusCode + " " + statusMessage + " ").append(CRLF)
                .append("Content-Type: " + contentType(resource.getPath()) + ";charset=utf-8 ").append(CRLF)
                .append("Content-Length: " + responseBody.getBytes().length + " ").append(CRLF)
                .append(cookieResponse(cookieValues)).append("\r\n")
                .append(responseBody);

        return sb.toString();
    }

    private String buildResponseBody(File location) throws IOException {
        String responseBody = "";

        if (location.isDirectory()) {
            responseBody = "Hello world!";
        }

        if (location.isFile()) {
            responseBody = new String(Files.readAllBytes(location.toPath()));
        }
        return responseBody;
    }

    private String cookieResponse(Map<String, String> cookieValues) {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> cookie : cookieValues.entrySet()) {
            sb.append("Set-Cookie: ").append(cookie.getKey()).append("=").append(cookie.getValue()).append("\r\n");
        }
        return sb.toString();
    }

    private String contentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    public String getVersion() {
        return HTTP_VERSION;
    }

    public URL getResource() {
        return resource;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, String> getCookieValues() {
        return Collections.unmodifiableMap(cookieValues);
    }

    public String getResponse() throws IOException {
        return buildResponse();
    }

    public void setResource(URL resource) {
        this.resource = resource;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
