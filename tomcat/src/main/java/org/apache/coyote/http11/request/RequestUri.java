package org.apache.coyote.http11.request;

public class RequestUri {

    private final String uri;
    private final RequestParams requestParams;

    private RequestUri(String uri, RequestParams requestParams) {
        this.uri = uri;
        this.requestParams = requestParams;
    }

    public static RequestUri of(String uri) {
        if (uri.contains("?")) {
            String[] split = uri.split("\\?");
            return new RequestUri(split[0], RequestParams.parse(split[1]));
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
