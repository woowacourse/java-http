package org.apache.coyote.http11.request;

public class RequestUri {

    private static final String PARAM_SEPARATOR = "\\?";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final String uri;
    private final RequestParams requestParams;

    private RequestUri(String uri, RequestParams requestParams) {
        this.uri = uri;
        this.requestParams = requestParams;
    }

    public static RequestUri of(String uri) {
        if (uri.contains(PARAM_SEPARATOR)) {
            String[] paramKeyValue = uri.split(PARAM_SEPARATOR);
            return new RequestUri(paramKeyValue[KEY_INDEX], RequestParams.parse(paramKeyValue[VALUE_INDEX]));
        }
        return new RequestUri(uri, RequestParams.EMPTY);
    }

    public boolean existsQueryParam() {
        return !requestParams.equals(RequestParams.EMPTY);
    }

    public String getUri() {
        return uri;
    }

    public String getRequestParam(String key) {
        return requestParams.getParam(key);
    }

    @Override
    public String toString() {
        return "RequestUri{" +
                "uri='" + uri + '\'' +
                ", requestParams=" + requestParams +
                '}';
    }
}
