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
    private final Map<HeaderName, String> header;
    private final HttpCookie cookie;
    private String body; // TODO: final 적용

    public HttpResponse(HttpRequest httpRequest) throws IOException {
        this.statusLine = new StatusLine();
        this.header = mapHeader(httpRequest);
        this.cookie = httpRequest.getHttpCookie();
    }

    public HttpResponse() {
        this.statusLine = new StatusLine();
        this.header = initHtmlHeader();
        this.cookie = new HttpCookie("");
        this.body = "";
    }

    private Map<HeaderName, String> initHtmlHeader() {
        Map<HeaderName, String> rawHeader = new HashMap<>();
        rawHeader.put(HeaderName.CONTENT_TYPE, ContentType.HTML.getResponse());

        return rawHeader;
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

        for (Entry<HeaderName, String> headerEntry : header.entrySet()) {
            response.append(headerEntry.getKey().getValue())
                    .append(": ")
                    .append(headerEntry.getValue())
                    .append("\r\n");
        }
        return String.valueOf(response);
    }

    private Map<HeaderName, String> mapHeader(HttpRequest httpRequest) {
        Map<HeaderName, String> headerEntry = new HashMap<>();
        headerEntry.put(HeaderName.CONTENT_TYPE, httpRequest.get(HeaderName.CONTENT_TYPE));
        headerEntry.put(HeaderName.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        headerEntry.put(HeaderName.SET_COOKIE.getValue(), httpRequest.getCookieResponse());
        return headerEntry;
    }

//    private String mapBody(HttpRequest httpRequest) throws IOException {
    private String mapBody(String resource) throws IOException {
        StringBuilder rawBody = new StringBuilder();
        Path path = Path.of(getClass().getResource(STATIC_PATH + resource).getPath());

        Files.readAllLines(path)
                .stream()
                .forEach(line -> rawBody.append(line).append("\n"));
        return String.valueOf(rawBody);
    }

    public void setStatusCode(StatusCode statusCode) {
        statusLine.setStatusCode(statusCode);
    }

    public void setHeader(HeaderName headerName, String value) {
        header.put(headerName, value);
    }

    public void setBody(String resource) throws IOException {
        this.body = mapBody(resource);
    }
}

//        if (uri.endsWith(".html")) {
//            contentType = "text/html; charset=utf-8 ";
//            statusCode = "200 OK";
//            path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
//        }
//        if (uri.endsWith(".css")) {
//            contentType = "text/css; charset=utf-8 ";
//            statusCode = "200 OK";
//            path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
//        }
//        if (uri.endsWith(".js")) {
//            contentType = "application/javascript ";
//            statusCode = "200 OK";
//            path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
//        }
