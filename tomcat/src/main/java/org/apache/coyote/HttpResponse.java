package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String LOCATION = "Location";
    private static final String STATIC = "static";
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    private static final String CONTENT_TYPE_TEXT_CSS = "text/css";
    private static final String CONTENT_TYPE_TEXT_JAVASCRIPT = "text/javascript";
    private static final String PATH_404_HTML = "static/404.html";
    private static final String HTTP_STATUS_OK = "200 OK";
    private static final String HTTP_STATUS_NOT_FOUND = "404 Not Found";
    private static final String HTTP_STATUS_FOUND = "302 Found";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID=";

    private String status;
    private String version;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";


    public void send(final String status, final String contentType, final String body) {
        this.status = status;
        version = HTTP_VERSION_1_1;
        headers.put(CONTENT_TYPE, contentType + CHARSET_UTF_8);
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void sendStaticHtml(final String target) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(STATIC + target);
        if (resource == null) {
            URL notFound = getClass().getClassLoader().getResource(PATH_404_HTML);
            send(HTTP_STATUS_NOT_FOUND, CONTENT_TYPE_TEXT_HTML, Files.readString(Path.of(notFound.toURI())));
            return;
        }
        send(HTTP_STATUS_OK, contentTypeOf(target), Files.readString(Path.of(resource.toURI())));
    }


    public void sendRedirect(final String locationUrl) {
        this.status = HTTP_STATUS_FOUND;
        version = HTTP_VERSION_1_1;
        headers.put(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML + CHARSET_UTF_8);
        headers.put(CONTENT_LENGTH, String.valueOf(0));
        headers.put(LOCATION, locationUrl);
        this.body = "";
    }

    private String contentTypeOf(final String target) {
        if (target.endsWith(".css")) {
            return CONTENT_TYPE_TEXT_CSS;
        }
        if (target.endsWith(".js")) {
            return CONTENT_TYPE_TEXT_JAVASCRIPT;
        }
        return CONTENT_TYPE_TEXT_HTML;
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }

    public void setCookie(String id) {
        headers.put(SET_COOKIE, JSESSIONID + id);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
