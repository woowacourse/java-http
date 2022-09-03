package nextstep.jwp.servlet;

import org.apache.coyote.support.HttpStatus;

public class ViewResource {

    private final HttpStatus status;
    private final String uri;

    public ViewResource(HttpStatus status, String uri) {
        this.status = status;
        this.uri = uri;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getUri() {
        return uri;
    }
}
