package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.request.HttpVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseEntity {

    public static final String RESPONSE_DELIMITER = " ";

    private final HttpVersion httpVersion;
    private final ResponseStatus responseStatus;
    private final Map<String, String> responseHeaders;
    private final String responseBody;
    private final Map<String, String> cookies = new HashMap<>();

    private ResponseEntity(HttpVersion httpVersion, ResponseStatus responseStatus, Map<String, String> responseHeaders, String responseBody) {
        this.httpVersion = httpVersion;
        this.responseStatus = responseStatus;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static ResponseEntity redirect(String redirectionFile) {
        Map<String, String> headers = Map.of("Location", redirectionFile);
        return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.FOUND, headers, "");
    }

    public static ResponseEntity ok(String fileData, ContentType contentType) {
        Map<String, String> headers = Map.of("Content-Type", contentType.getHttpContentType(), "Content-Length", String.valueOf(fileData.getBytes().length));
        return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.OK, headers, fileData);
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    public String generateResponseMessage() {
        return String.join(
                "\r\n",
                generateStatus(),
                generateHeader(),
                "",
                responseBody
        );
    }

    private String generateStatus() {
        return String.join(
                RESPONSE_DELIMITER,
                httpVersion.getVersion(),
                String.valueOf(responseStatus.getStatusCode()),
                responseStatus.name(),
                ""
        );
    }

    private String generateHeader() {
        StringJoiner headers = new StringJoiner("\r\n");
        responseHeaders.entrySet().stream()
                .map(headerEntry -> headerEntry.getKey() + ": " + headerEntry.getValue() + " ")
                .forEach(headers::add);
        if (cookies.containsKey("JSESSIONID")) {
            String jsessionid = cookies.get("JSESSIONID");
            headers.add("Set-Cookie: JSESSIONID=" + jsessionid + " ");
        }
        return headers.toString();
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
