package nextstep.jwp.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;

public class ResponseEntity {

    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private final String body;

    public ResponseEntity(HttpStatusCode statusCode, HttpHeaders headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static ResponseEntity ok(String body) {
        return new ResponseEntity(HttpStatusCode.OK, HttpHeaders.defaultHeaders(), body);
    }

    public static ResponseEntity found(String location) {
        final var headers = HttpHeaders.defaultHeaders();
        headers.put(HttpHeaders.LOCATION, location);
        return new ResponseEntity(HttpStatusCode.FOUND, headers, "");
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
}
