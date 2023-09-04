package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private String httpVersion;
    private HttpStatus httpStatus;
    private String body;
    private Map<HttpHeader, String> headers = new LinkedHashMap<>();

    public String httpVersion() {
        return httpVersion;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public String body() {
        return body;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpResponse setHeader(HttpHeader header, String value) {
        headers.put(header, value);
        return this;
    }

    public String getResponse() {
        return String.join("\r\n",
                httpVersion + " " + httpStatus.statusCode() + " " + httpStatus.statusText() + " ",
                headers.keySet().stream()
                        .map(header -> header.value() + ": " + headers.get(header))
                        .collect(Collectors.joining("\r\n")),
                "",
                body);
    }
}
