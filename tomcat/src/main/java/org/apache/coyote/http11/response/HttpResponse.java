package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.request.HttpVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.apache.coyote.http11.request.HttpVersion.HTTP_1_1;
import static org.apache.coyote.http11.response.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.ResponseStatus.OK;

public class HttpResponse {

    public static final String RESPONSE_DELIMITER = " ";
    public static final String JSESSIONID = "JSESSIONID";

    private HttpVersion httpVersion;
    private ResponseStatus responseStatus;
    private Map<String, String> responseHeaders = new HashMap<>();
    private String responseBody;
    private Cookie cookie = Cookie.from("");

    public static HttpResponse create() {
        return new HttpResponse();
    }

    public void redirect(String redirectionFile) {
        this.httpVersion = HTTP_1_1;
        this.responseStatus = FOUND;
        this.responseHeaders.put("Location", redirectionFile);
        this.responseBody = "";
    }

    public void ok(String fileData, ContentType contentType) {
        this.httpVersion = HTTP_1_1;
        this.responseStatus = OK;
        this.responseHeaders.put("Content-Type", contentType.getHttpContentType());
        this.responseHeaders.put("Content-Length", String.valueOf(fileData.getBytes().length));
        this.responseBody = fileData;
    }

    public void addCookie(String key, String value) {
        if (cookie == null) {
            this.cookie = Cookie.from("");
        }
        cookie.addCookie(key, value);
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

        if (cookie.containsKey(JSESSIONID)) {
            String jsessionid = cookie.findByKey(JSESSIONID);
            headers.add("Set-Cookie: JSESSIONID=" + jsessionid + " ");
        }
        return headers.toString();
    }

    public Cookie getCookie() {
        return cookie;
    }
}
