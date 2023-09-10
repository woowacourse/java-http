package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.apache.coyote.http11.request.HttpVersion.HTTP_1_1;

public class HttpResponse {

    protected static final String RESPONSE_DELIMITER = " ";
    protected static final String JSESSIONID = "JSESSIONID";

    private ResponseLine responseLine;
    private Map<String, String> responseHeaders;
    private String responseBody;
    private Cookie cookie;

//    private HttpResponse(Map<String, String> responseHeaders, String responseBody, Cookie cookie) {
//        this.responseHeaders = responseHeaders;
//        this.responseBody = responseBody;
//        this.cookie = cookie;
//    }

    public HttpResponse() {
        this.responseLine = new ResponseLine();
        this.responseHeaders = new HashMap<>();
        this.responseBody = "";
        this.cookie = Cookie.from("");
    }

    public void redirect(String redirectionFile) {
        this.responseLine.redirect(HTTP_1_1);
        this.responseHeaders.put("Location", redirectionFile);
        this.responseBody = "";
    }

    public void ok(String fileData, ContentType contentType) {
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
        return responseLine.generateMessage();
    }

    private String generateHeader() {
        StringJoiner headers = new StringJoiner("\r\n");
        responseHeaders.entrySet().stream()
                .map(headerEntry -> headerEntry.getKey() + ": " + headerEntry.getValue() + " ")
                .forEach(headers::add);

        if (cookie.containsKey(JSESSIONID)) {
            String jsessionid = cookie.findByKey(JSESSIONID);
            headers.add(String.format("Set-Cookie: %s=%s ", JSESSIONID, jsessionid));
        }
        return headers.toString();
    }

    public Cookie getCookie() {
        return cookie;
    }
}
