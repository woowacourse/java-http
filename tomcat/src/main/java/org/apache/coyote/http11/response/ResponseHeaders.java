package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.HeaderField.CONTENT_LENGTH;
import static org.apache.coyote.http11.HeaderField.CONTENT_TYPE;
import static org.apache.coyote.http11.HeaderField.LOCATION;

public class ResponseHeaders {

    private final String headers;

    private ResponseHeaders(String headers) {
        this.headers = headers;
    }

    public static ResponseHeaders create(String url, String body) {
        return new ResponseHeaders(createHeaders(url, body));
    }

    public static ResponseHeaders createWithLocation(String redirectUrl) {
        return new ResponseHeaders(createHeadersForRedirect(redirectUrl));
    }

    public static ResponseHeaders createWithLocationAndJSessionId(String redirectUrl, String jSessionId) {
        String headers1 = String.join("\r\n",
                LOCATION + ": " + redirectUrl,
                "Set-Cookie: " + "JSESSIONID=" + jSessionId,
                "");
        System.out.println("headers1 = " + headers1);
        return new ResponseHeaders(headers1);
    }

    private static String createHeaders(String url, String body) {
        String contentType = getContentType(url);
        String contentLength = body.getBytes().length + " ";
        return String.join("\r\n",
                CONTENT_TYPE + ": " + contentType,
                CONTENT_LENGTH + ": " + contentLength,
                "");
    }

    private static String createHeadersForRedirect(String redirectUrl) {
        return String.join("\r\n",
                LOCATION + ": " + redirectUrl,
                "");
    }

    private static String getContentType(String requestUri) {
        return ContentType.find(requestUri) + ";charset=utf-8 ";
    }

    public String getHeadersToString() {
        return String.join("\r\n", headers, "");
    }

    public String getHeaders() {
        return headers;
    }
}
