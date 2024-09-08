package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpHeaders;

public class HttpHeaderBuilder {
    private Optional<String> contentType = Optional.of("text/html");
    private Optional<Integer> contentLength = Optional.empty();
    private Optional<String> location = Optional.empty();
    private Optional<String> setCookie = Optional.empty();

    public HttpHeaderBuilder() {
    }

    public HttpHeaderBuilder contentType(String contentType) {
        this.contentType = Optional.ofNullable(contentType);
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

    public HttpHeaderBuilder setCookie(Cookie cookie) {
        this.setCookie = Optional.of(cookie.serialize());
        return this;
    }

    public HttpHeaders build() {
        Map<String, String> payLoads = new LinkedHashMap<>();

        contentType.ifPresent(type -> payLoads.put("Content-Type", type));
        contentLength.ifPresent(length -> payLoads.put("Content-Length", Integer.toString(length)));
        location.ifPresent(location -> payLoads.put("Location", location));
        setCookie.ifPresent(setCookie -> payLoads.put("Set-Cookie", setCookie));

        return new HttpHeaders(payLoads);
    }
}
