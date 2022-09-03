package servlet.mapping;

import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;

public class ResponseEntity {

    private final HttpMethod method;
    private final String uri;
    private final HttpStatus status;

    public ResponseEntity(HttpMethod method, String uri, HttpStatus status) {
        this.method = method;
        this.uri = uri;
        this.status = status;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
