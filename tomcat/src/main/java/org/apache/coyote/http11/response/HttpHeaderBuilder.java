package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpHeaders;

public class HttpHeaderBuilder {
    private Optional<String> contentType = Optional.of("text/html");
    private Optional<Integer> contentLength;
    private Optional<String> location;
    private Optional<String> setCookie;

    public HttpHeaderBuilder() {
    }

    public HttpHeaderBuilder contentType(String contentType) {
        this.contentType = Optional.of(contentType);
        return this;
    }

    public HttpHeaderBuilder contentLength(Integer contentLength) {
        this.contentLength = Optional.of(contentLength);
        return this;
    }

    public HttpHeaderBuilder location(String location) {
        this.location = Optional.of(location);
        return this;
    }

    public HttpHeaderBuilder setCookie(String setCookie) {
        this.setCookie = Optional.of(setCookie);
        return this;
    }

    public HttpHeaders build() {
        Map<String, String> payLoads = new HashMap<>();

        contentType.ifPresent(type -> payLoads.put("Content-Type", type));
        contentLength.ifPresent(length -> payLoads.put("Content-Length", Integer.toString(length)));
        location.ifPresent(location -> payLoads.put("Location", location));
        setCookie.ifPresent(setCookie -> payLoads.put("Set-Cookie", setCookie));

        return new HttpHeaders(payLoads);
    }
}
