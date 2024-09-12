package org.apache.coyote.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http.HeaderName;
import org.apache.coyote.coockie.HttpCookie;
import org.apache.coyote.http.ResourceType;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.request.HttpRequest;

public class HttpResponse {

    public static final String STATIC_PATH = "/static";

    private final StatusLine statusLine;
    private final Map<String, String> header;
    private final HttpCookie cookie;
    private String body;

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

    private Map<String, String> mapHeader(HttpRequest httpRequest, ResourceType resourceType) throws IOException {
        Map<String, String> headerEntry = new HashMap<>();
        headerEntry.put(HeaderName.SET_COOKIE.getValue(), httpRequest.getCookieResponse());
        headerEntry.put(HeaderName.CONTENT_TYPE.getValue(), httpRequest.getContentType());
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
        header.put(HeaderName.CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
    }

    public void generateJSESSIONID() {
        cookie.generateJSESSIONID();
        header.put(HeaderName.SET_COOKIE.getValue(), cookie.getResponse());
    }

    public String getJESSIONID() {
        return cookie.getJESSIONID();
    }
}
