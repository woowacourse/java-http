package org.apache.coyote.http11.dto.request;

import java.util.Map;

public record RequestPath(
        String uri,
        Map<String, String> queryParams
) {

    public RequestPath redirectUri(final String uri) {
        return new RequestPath(uri, this.queryParams);
    }
}
