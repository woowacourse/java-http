package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpHeader {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String requestStartLine;
    private final Map<String, String> requestHeaders;

    public HttpHeader(final String requestStartLine, final String requestHeaders) {
        this.requestStartLine = requestStartLine;
        this.requestHeaders = PairConverter.toMap(requestHeaders, "\n", ": ");
    }

    public String getResponseHeader(final StatusCode statusCode, final String response) {
        final String method = requestStartLine.split(" ")[0];
        final String path = requestStartLine.split(" ")[1];

        if (method.equals("POST") && path.startsWith("/login") && statusCode.isEqual(StatusCode.MOVED_TEMPORARILY)) {
            return setCookieIfNotExist(statusCode, response);
        }
        return responseWithOutJSESIONID(statusCode, response);
    }

    private String setCookieIfNotExist(final StatusCode statusCode, final String response) {
        if (requestHeaders.containsKey("Cookie")) {
            final HttpCookie httpCookie = new HttpCookie(this.requestHeaders.get("Cookie"));
            if (httpCookie.containsKey("JSESSIONID")) {
                return responseWithOutJSESIONID(statusCode, response);
            }
        }

        return String.join("\r\n",
                HTTP_VERSION + statusCode.getStatusMessage(),
                "Set-Cookie: JSESSIONID=" + HttpCookie.getJSESSIONID(),
                "Content-Type: " + getResponseContentType().getMIMEType() + ";charset=utf-8 ",
                "Content-Length: " + response.getBytes().length + " ",
                "");
    }

    private String responseWithOutJSESIONID(final StatusCode statusCode, final String response) {
        return String.join("\r\n",
                HTTP_VERSION + statusCode.getStatusMessage(),
                "Content-Type: " + getResponseContentType().getMIMEType() + ";charset=utf-8 ",
                "Content-Length: " + response.getBytes().length + " ",
                "");
    }

    private ContentType getResponseContentType() {
        final String path = requestStartLine.split(" ")[1];
        if (path.contains(EXTENSION_DELIMITER)) {
            final String[] splitExtension = path.split("\\" + EXTENSION_DELIMITER);
            return ContentType.matchMIMEType(splitExtension[splitExtension.length - 1]);
        }
        return ContentType.HTML;
    }

    public String getStartLine() {
        return requestStartLine;
    }
}
