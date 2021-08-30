package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpError;
import nextstep.jwp.http.HttpRequest;

public abstract class AbstractController {

    private final HttpRequest httpRequest;

    public AbstractController(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    abstract byte[] get(HttpRequest httpRequest) throws IOException;

    abstract byte[] post(HttpRequest httpRequest) throws IOException;

    abstract byte[] error(HttpError httpError) throws IOException;

    public byte[] proceed() throws IOException {
        if ("GET".equals(httpRequest.method())) {
            return get(httpRequest);
        }
        if ("POST".equals(httpRequest.method())) {
            return post(httpRequest);
        }
        return error(HttpError.FORBIDDEN);
    }
}
