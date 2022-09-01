package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;

    private final String method;
    private final String url;
    private final Map<String, String> queryParams;

    public RequestLine(final String line) {
        String[] split = line.split(SEPARATOR);

        this.method = split[METHOD_INDEX];
        String originUrl = split[URL_INDEX];
        this.queryParams = new HashMap<>();
        String[] splitUrl = originUrl.split("\\?");
        if (originUrl.contains("?")) {
            String rawQueryParams = splitUrl[1];
            String[] splitQueryParams = rawQueryParams.split("&");
            for (String queryParam : splitQueryParams) {
                String[] splitQueryParam = queryParam.split("=");
                queryParams.put(splitQueryParam[0], splitQueryParam[1]);
            }
        }
        this.url = splitUrl[0];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(final String key) {
        return queryParams.get(key);
    }
}
