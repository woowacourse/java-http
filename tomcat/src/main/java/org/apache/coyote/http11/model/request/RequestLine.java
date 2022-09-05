package org.apache.coyote.http11.model.request;

import java.util.Map;

public class RequestLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;

    private final Method method;
    private final Url url;
    private final QueryParams queryParams;

    public RequestLine(final String line) {
        String[] split = line.split(SEPARATOR);

        this.method = Method.findByName(split[METHOD_INDEX]);
        this.url = Url.of(split[URL_INDEX]);
        this.queryParams = url.getQueryParams();
    }

    public Method getMethod() {
        return method;
    }

    public String getUrl() {
        return url.getUrl();
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getQueryParams();
    }
}
