package nextstep.jwp.domain.response;

import nextstep.jwp.domain.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String NEW_LINE = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private HttpStatus httpStatus;
    private Map<String, String> headerMap;
    private String body = "";

    public HttpResponse() {
        httpStatus = null;
        headerMap = new LinkedHashMap<>();
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headerMap, String body) {
        this.httpStatus = httpStatus;
        this.headerMap = headerMap;
        this.body = body;
    }

    public byte[] getBytes() {
        return String.join(NEW_LINE,
                getHttpLine(),
                toHeaderLine(headerMap),
                "",
                body
        ).getBytes();
    }

    private String toHeaderLine(Map<String, String> headerMap) {
        return headerMap.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining(NEW_LINE));
    }

    private String getHttpLine() {
        return HTTP_VERSION + " " + httpStatus.value() + " " + httpStatus.responsePhrase() + " ";
    }

    public HttpResponse respond(String uri) throws IOException {
        return respond(uri, HttpStatus.OK);
    }

    public HttpResponse respond(String uri, HttpStatus httpStatus) throws IOException {
        Resource resourceFile = new Resource(uri);
        String contentType = resourceFile.getContentType();
        String body = resourceFile.getContent();
        setProperty(httpStatus, contentType, body);
        return this;
    }

    private void setProperty(HttpStatus httpStatus, String contentType, String body) {
        this.httpStatus = httpStatus;
        this.headerMap.put(CONTENT_TYPE, contentType);
        this.headerMap.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public HttpResponse redirect(String uri, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        headerMap.put("Location", uri);
        return this;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }
}

