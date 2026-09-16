package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;
import javax.annotation.Nonnull;

public class Request {

    private final String method;
    private final String path;
    private final RequestParams requestParams;
    private final ContentType contentType;

    private Request(String method, String path, RequestParams requestParams, ContentType contentType) {
        this.method = method;
        this.path = path;
        this.requestParams = requestParams;
        this.contentType = contentType;
    }

    public static Request from(String requestLine, Map<String, String> headers) {
        StringTokenizer tokenizer = new StringTokenizer(requestLine);
        String method = tokenizer.nextToken();
        ContentType contentType = parseContentType(headers);
        return splitUri(tokenizer, method, contentType);
    }

    @Nonnull
    private static Request splitUri(StringTokenizer tokenizer, String method, ContentType contentType) {
        String uri = tokenizer.nextToken();
        int index = uri.indexOf('?');
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        RequestParams requestParams = RequestParams.of(queryString);
        return new Request(method, path, requestParams, contentType);
    }

    private static ContentType parseContentType(Map<String, String> headers) {
        String acceptLine = headers.get("accept");
        return Arrays.stream(ContentType.values())
                .filter(contentType -> acceptLine.contains(contentType.getType()))
                .findFirst()
                .orElse(ContentType.HTML);
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

    public ContentType getContentType() {
        return contentType;
    }
}
