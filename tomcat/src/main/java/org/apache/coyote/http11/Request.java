package org.apache.coyote.http11;

import java.util.StringTokenizer;
import javax.annotation.Nonnull;

public class Request {

    private final String method;
    private final String path;
    private final RequestParams requestParams;
    private final ContextType contentType;

    private Request(String method, String path, RequestParams requestParams, ContextType contentType) {
        this.method = method;
        this.path = path;
        this.requestParams = requestParams;
        this.contentType = contentType;
    }

    public static Request from(String requestLine, ContextType contentType) {
        StringTokenizer tokenizer = new StringTokenizer(requestLine);
        String method = tokenizer.nextToken();
        return splitUri(tokenizer, method, contentType);
    }

    @Nonnull
    private static Request splitUri(StringTokenizer tokenizer, String method, ContextType contentType) {
        String uri = tokenizer.nextToken();
        int index = uri.indexOf('?');
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        RequestParams requestParams = RequestParams.of(queryString);
        return new Request(method, path, requestParams, contentType);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }

    public ContextType getContentType() {
        return contentType;
    }
}
