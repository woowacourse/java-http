package org.apache.coyote.http.request;

import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpVersion;

public class HttpRequest {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String ROOT_PATH = "/";
    private static final String DEFAULT_EXTENSION = ".html";

    private static final int NOT_FOUND_INDEX = -1;

    private final RequestLine requestLine;
    private final ContentType contentType;
    private final RequestHeader requestHeader;
    private final HttpCookie httpCookie;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine,
                       final ContentType contentType,
                       final RequestHeader requestHeader,
                       final HttpCookie httpCookie,
                       final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.contentType = contentType;
        this.requestHeader = requestHeader;
        this.httpCookie = httpCookie;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final RequestLine requestLine,
                                 final RequestHeader requestHeader,
                                 final RequestBody requestBody) {

        ContentType contentType = extractContentType(requestLine.getPath());
        HttpCookie httpCookie = HttpCookie.from(requestHeader.getCookie());

        return new HttpRequest(requestLine, contentType, requestHeader, httpCookie, requestBody);
    }

    private static ContentType extractContentType(final String path) {
        Objects.requireNonNull(path);
        int extensionDelimiterIndex = path.lastIndexOf(EXTENSION_DELIMITER);

        if (hasExtension(extensionDelimiterIndex)) {
            String extension = path.substring(extensionDelimiterIndex + 1);
            return ContentType.from(extension);
        }
        return ContentType.TEXT_HTML;
    }

    private static boolean hasExtension(int delimiterIndex) {
        return delimiterIndex != NOT_FOUND_INDEX;
    }

    public String getFilePath() {
        return requestLine.getFilePath();
    }

    public boolean isRootPath() {
        return requestLine.isRootPath();
    }

    public boolean hasEmptySessionId() {
        return httpCookie.hasEmptySessionId();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public Map<String, String> getRequestParams() {
        return requestLine.getRequestParams();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getJSessionId() {
        return httpCookie.getJSessionId();
    }

    public Map<String, String> getRequestBody() {
        return requestBody.values();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}
