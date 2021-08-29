package nextstep.jwp.infra;

import nextstep.jwp.domain.HttpHeaders;

import java.util.HashMap;

public class HttpHeaderBuilder {
    private final HashMap<String, String> headers;

    public HttpHeaderBuilder() {
        this.headers = new HashMap<>();
    }

    public HttpHeaderBuilder addHeader(String[] splitRequestHeaders) {
        for (int i = 1; i < splitRequestHeaders.length; i++) {
            String[] separateHeaderByColon = splitRequestHeaders[i].split(":");
            headers.put(separateHeaderByColon[0], separateHeaderByColon[1].trim());
        }
        return this;
    }

    public HttpHeaders build() {
        return HttpHeaders.of(headers);
    }
}
