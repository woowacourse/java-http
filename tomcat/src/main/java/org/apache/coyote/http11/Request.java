package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {

    private static final Logger log = LoggerFactory.getLogger(Request.class);
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

    public static Request changePath(Request request, String path) {
        return new Request(request.method, path, request.requestParams, request.contentType);
    }

    @Nonnull
    private static Request splitUri(StringTokenizer tokenizer, String method, ContentType contentType) {
        String uri = tokenizer.nextToken();
        int index = uri.indexOf('?');
        if (index != -1) {
            String path = uri.substring(0, index);
            String queryString = uri.substring(index + 1);
            RequestParams requestParams = RequestParams.of(queryString);
            return new Request(method, path, requestParams, contentType);
        }
        return new Request(method, uri, null, contentType);
    }

    private static ContentType parseContentType(Map<String, String> headers) {
        log.info(headers.entrySet().toString());
        String acceptLine = headers.getOrDefault("accept", "");
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
