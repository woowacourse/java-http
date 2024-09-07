package org.apache.catalina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    public static final String STATIC_PATH = "/static";

    private final StatusLine statusLine;
    private final Map<String, String> header;
    private final HttpCookie cookie;
    private String body; // TODO: final 적용

    public HttpResponse(HttpRequest httpRequest, ResourceType resourceType) throws IOException {
        this.statusLine = new StatusLine();
        if (resourceType == ResourceType.NON_STATIC) {
            this.body = "";
        }
        if (resourceType == ResourceType.STATIC) {
            this.body = mapBody(httpRequest.getPath());
        }
        this.header = mapHeader(httpRequest, resourceType);
        this.cookie = httpRequest.getHttpCookie();
    }

    public HttpResponse() {
        this.statusLine = new StatusLine();
        this.header = new HashMap<>();
        this.cookie = new HttpCookie("");
        this.body = "";
    }

    public String getReponse() {
        StringBuilder response = new StringBuilder();

        response.append(statusLine.getStatusLineResponse())
                .append("\r\n")
                .append(getHeaderResponse())
                .append("\r\n")
                .append(body);
        return String.valueOf(response);
    }

    private String getHeaderResponse() {
        StringBuilder response = new StringBuilder();

        for (Entry<String, String> headerEntry : header.entrySet()) {
            response.append(headerEntry.getKey())
                    .append(": ")
                    .append(headerEntry.getValue())
                    .append("\r\n");
        }
        return String.valueOf(response);
    }

    private Map<String, String> mapHeader(HttpRequest httpRequest, ResourceType resourceType) {
        Map<String, String> headerEntry = new HashMap<>();
        headerEntry.put(HeaderName.SET_COOKIE.getValue(), httpRequest.getCookieResponse());
        headerEntry.put(HeaderName.CONTENT_TYPE.getValue(), httpRequest.getContentType().getResponse());
        if (resourceType == ResourceType.STATIC) {
            headerEntry.put(HeaderName.CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
        }
        return headerEntry;
    }

    private String mapBody(String resource) throws IOException {
        StringBuilder rawBody = new StringBuilder();
        Path path = Path.of(getClass().getResource(STATIC_PATH + resource).getPath());

        Files.readAllLines(path)
                .forEach(line -> rawBody.append(line).append("\r\n"));
        return String.valueOf(rawBody);
    }

    public void setStatusCode(StatusCode statusCode) {
        statusLine.setStatusCode(statusCode);
    }

    public void setHeader(HeaderName headerName, String value) {
        header.put(headerName.getValue(), value);
    }

    public void setBody(String resource) throws IOException {
        this.body = mapBody(resource);
    }

    public void setJSESSIONID() {
        cookie.setJSESSIONID();
    }

    public String getJESSIONID() {
        return cookie.getJESSIONID();
    }
}
