package org.apache.coyote.http11.data;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode;
    private final Map<String, String> headers;
    private String body;
    private Cookies cookies;
    private String viewName;

    private HttpResponse(
            int statusCode,
            Map<String, String> headers,
            String body,
            Cookies cookies,
            String viewName
    ) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        this.cookies = cookies;
        this.viewName = viewName;
    }

    public static HttpResponse create() {
        return new HttpResponse(200, new HashMap<>(), "", Cookies.empty(), "");
    }

    private static final Map<Integer, String> httpStatusMessage = new HashMap<>() {
        {
            // TODO: Add more status codes and messages as needed
            put(200, "OK");
            put(204, "No Content");
            put(302, "Found");
            put(400, "Bad Request");
            put(404, "Not Found");
            put(500, "Internal Server Error");
        }
    };
    private static final String CRLF = " \r\n";

    public void notFound() {
        this.statusCode = 404;
        this.body = "Not Found";
    }

    public void badRequest() {
        this.statusCode = 400;
        this.body = "Bad Request";
    }

    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }

    public void setCookies(final Cookies cookies) {
        this.cookies = cookies;
    }

    /*
     * response.addCookie(cookie)
     * → Response의 구성 요소로 Cookie를 본다.
     * → "Response에 쿠키를 추가한다."
     *
     * response.getCookies().addCookie(cookie)
     * → Cookies를 독립적인 일급 컬렉션으로 본다.
     * → "Response의 Cookies에 쿠키를 추가한다."
     *
     * 둘중 고민을 많이 했으나.
     * Cookies 자체가 일급 컬렉션이고, 쿠키 변경 규칙을 Cookies가 책임지는 설계가 좋다고 생각했다.
     */
    public Cookies getCookies() {
        return cookies;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(httpStatusMessage.get(statusCode))
                .append(CRLF);

        for (var entry : headers.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }

        for (Cookie cookie : cookies.values()) {
            sb.append("Set-Cookie: ")
                    .append(cookie)
                    .append(CRLF);
        }

        sb.append("Content-Length: ")
                .append(body.getBytes().length)
                .append(CRLF);

        return sb.append("\r\n")
                .append(body)
                .toString();
    }
}
