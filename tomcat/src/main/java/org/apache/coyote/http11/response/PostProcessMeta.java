package org.apache.coyote.http11.response;

import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.headers.RequestHeader;

public class PostProcessMeta {

    private final HttpRequest request;
    private final ResponseBody body;

    public PostProcessMeta(HttpRequest request, ResponseBody body) {
        this.request = request;
        this.body = body;
    }

    public long bodyLength() {
        return body.getAsString().length();
    }

    public Optional<RequestHeader> findHeaderByField(String field) {
        try {
            return Optional.of(request.findHeader(field));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
