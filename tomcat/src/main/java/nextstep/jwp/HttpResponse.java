package nextstep.jwp;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private Map<String, String> headers = new LinkedHashMap<>();
    private final HttpStatus httpStatus;
    private final String body;
    private final HttpCookie httpCookie = new HttpCookie();

    public HttpResponse(Map<String, String> headers,
                        HttpStatus httpStatus, String body) {
        this.headers = headers;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public static HttpResponse ok(String responseBody, ContentType contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType.value + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        return new HttpResponse(headers, HttpStatus.OK, responseBody);
    }

    public static HttpResponse found(String location) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Length", "0");
        headers.put("Location", location);
        return new HttpResponse(headers, HttpStatus.FOUND, "");
    }

    public void addCookie(String key, String value) {
        httpCookie.add(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }

    public String getCookieValue(String key) {
        return httpCookie.get(key);
    }
}
