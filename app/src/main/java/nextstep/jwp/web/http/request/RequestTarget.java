package nextstep.jwp.web.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.exception.NoMatchingElement;

public class RequestTarget {

    private final Map<String, Optional<String>> requestParams = new HashMap<>();
    private final String url;

    public RequestTarget(String url) {
        String[] parseUrl = url.split("\\?");
        this.url = parseUrl[0];

        if (hasQueryString(parseUrl)) {
            parseQueryString(parseUrl[1]);
        }
    }

    private boolean hasQueryString(String[] parseUrl) {
        return parseUrl.length > 1;
    }

    private void parseQueryString(String queryString) {
        String[] values = queryString.split("&");
        for (String value : values) {
            String[] keyAndValue = value.split("=");
            requestParams.put(keyAndValue[0], Optional.of(keyAndValue[1]));
        }
    }

    public String getUrl() {
        return url;
    }

    public String getRequestParams(String key) {
        return requestParams.get(key)
            .orElseThrow(NoMatchingElement::new);
    }
}
