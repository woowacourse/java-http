package nextstep.jwp.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;

public class ResponseEntity {

    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private final String body;
    private final boolean isRestResponse;

    public ResponseEntity(HttpStatusCode statusCode, HttpHeaders headers, String body, boolean isRestResponse) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        this.isRestResponse = isRestResponse;
    }

    public static ResponseEntity forward(HttpStatusCode statusCode, String path) {
        final var headers = HttpHeaders.defaultHeaders();
        return new ResponseEntity(statusCode, headers, path, false);
    }

    public static ResponseEntity ok(String body) {
        return new ResponseEntity(HttpStatusCode.OK, HttpHeaders.defaultHeaders(), body, true);
    }

    public static ResponseEntity found(String location) {
        final var headers = HttpHeaders.defaultHeaders();
        headers.put(HttpHeaders.LOCATION, location);
        return new ResponseEntity(HttpStatusCode.FOUND, headers, "", true);
    }

    public String getBody() {
        return body;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public boolean isRestResponse() {
        return isRestResponse;
    }
}
