package org.apache.coyote.http11;

import java.util.Map;
import java.util.StringTokenizer;
import javax.annotation.Nonnull;

public class Request {

    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    private Request(RequestHeader requestHeader, RequestBody requestBody) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static Request from(String requestLine, Map<String, String> headers) {
        StringTokenizer tokenizer = new StringTokenizer(requestLine);
        String method = tokenizer.nextToken();
        ContentType contentType = parseContentType(headers);
        return splitUri(tokenizer, method, contentType);
    }

    public static Request from(RequestHeader requestHeader, RequestBody requestBody) {
        return new Request(requestHeader, requestBody);
    }

    @Nonnull
    private static Request splitUri(StringTokenizer tokenizer, String method, ContentType contentType) {
        String uri = tokenizer.nextToken();
        int separatorIndex = uri.indexOf('?');
        if (separatorIndex != -1) {
            String path = uri.substring(0, separatorIndex);
            String queryString = uri.substring(separatorIndex + 1);
            RequestParams requestParams = RequestParams.of(queryString);
            return new Request(method, path, requestParams, contentType);
        }
        return new Request(method, uri, null, contentType);
    }

    public String getPath() {
        return path;
    }

    public String getRequestParam(String key) {
        if (requestParams == null) {
            return "";
        }
        return requestParams.getParams(key);
    }

    public String getContentType() {
        return contentType.getType();
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", requestParams=" + requestParams +
                ", contentType=" + contentType +
                '}';
    }
}
