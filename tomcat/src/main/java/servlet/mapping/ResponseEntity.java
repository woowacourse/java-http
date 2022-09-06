package servlet.mapping;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.HttpCookie;
import org.apache.coyote.http11.response.element.HttpStatus;

public class ResponseEntity {

    private final String uri;
    private final HttpStatus status;
    private final Map<String, String> headers;

    public ResponseEntity(String uri, HttpStatus status) {
        this.uri = uri;
        this.status = status;
        this.headers = new HashMap<>();
    }

    public ResponseEntity(String uri, HttpStatus status, Map<String, String> headers) {
        this.uri = uri;
        this.status = status;
        this.headers = headers;
    }

    public static ResponseEntity ok(String uri) {
        return new ResponseEntity(uri, HttpStatus.OK);
    }

     public static ResponseEntity found(String location) {
        return new ResponseEntity(location, HttpStatus.FOUND);
    }

    public ResponseEntity addCookie(HttpCookie cookie) {
        this.headers.put("Set-Cookie",  cookie.getResponse());
        return this;
    }

    public String getUri() {
        return uri;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
