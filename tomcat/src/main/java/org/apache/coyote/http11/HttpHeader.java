package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpHeader {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String NEW_LINE = "\r\n";

    private final String requestStartLine;
    private final Map<String, String> requestHeaders;

    public HttpHeader(final String requestStartLine, final String requestHeaders) {
        this.requestStartLine = requestStartLine;
        this.requestHeaders = PairConverter.toMap(requestHeaders, "\n", ": ");
    }

    public String getResponseHeader(final StatusCode statusCode,
                                    final String path,
                                    final int contentLength, final String cookie) {
        if (isValidLoginRequest(path) && statusCode.isEqual(StatusCode.MOVED_TEMPORARILY)) {
            return responseWithJSESIONID(statusCode, path, contentLength, cookie);
        }
        return responseWithOutJSESIONID(statusCode, path, contentLength);
    }

    private boolean isValidLoginRequest(final String path) {
        return getMethod().equals("POST")
                && getUrl().startsWith("/login")
                && path.startsWith("/index");
    }

    private String responseWithJSESIONID(final StatusCode statusCode,
                                         final String path,
                                         final int contentLength, String cookie) {
        if (hasJSESSIONID() && getJSESSIONID().equals(cookie)) {
            return responseWithOutJSESIONID(statusCode, path, contentLength);
        }
        if (cookie == null) {
            throw new RuntimeException("cookie가 존재하지 않습니다.");
        }

        return String.join(NEW_LINE,
                HTTP_VERSION + statusCode.getStatusMessage(),
                "Set-Cookie: JSESSIONID=" + cookie,
                "Content-Type: " + getResponseContentType().getMIMEType() + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                locationHeader(statusCode, path)
        );
    }

    private String locationHeader(final StatusCode statusCode, final String path) {
        if (statusCode.isEqual(StatusCode.MOVED_TEMPORARILY)) {
            return "Location: " + path + " " + NEW_LINE;
        }
        return "";
    }

    public boolean hasJSESSIONID() {
        if (hasCookie()) {
            final HttpCookie httpCookie = new HttpCookie(this.requestHeaders.get("Cookie"));
            return httpCookie.containsKey("JSESSIONID");
        }
        return false;
    }

    public boolean hasCookie() {
        return requestHeaders.containsKey("Cookie");
    }

    public String getJSESSIONID() {
        if (hasJSESSIONID()) {
            final HttpCookie httpCookie = new HttpCookie(this.requestHeaders.get("Cookie"));
            return httpCookie.getJSESSIONID();
        }
        throw new RuntimeException("JSESSIONID가 없습니다.");
    }

    private String responseWithOutJSESIONID(final StatusCode statusCode, final String path, final int contentLength) {
        return String.join(NEW_LINE,
                HTTP_VERSION + statusCode.getStatusMessage(),
                "Content-Type: " + getResponseContentType().getMIMEType() + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                locationHeader(statusCode, path));
    }

    private ContentType getResponseContentType() {
        if (getUrl().contains(EXTENSION_DELIMITER)) {
            final String[] splitExtension = getUrl().split("\\" + EXTENSION_DELIMITER);
            return ContentType.matchMIMEType(splitExtension[splitExtension.length - 1]);
        }
        return ContentType.HTML;
    }

    public String getStartLine() {
        return requestStartLine;
    }

    public String getMethod() {
        return getStartLine().split(" ")[0];
    }

    public String getUrl() {
        return getStartLine().split(" ")[1];
    }
}
