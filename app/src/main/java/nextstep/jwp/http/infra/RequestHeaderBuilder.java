package nextstep.jwp.http.infra;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.http.common.HttpHeaders;

public class RequestHeaderBuilder {

    private final Map<String, String> headers;

    public RequestHeaderBuilder() {
        this.headers = new LinkedHashMap<>();
    }

    public RequestHeaderBuilder addHeader(String[] splitRequestHeaders) {
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
