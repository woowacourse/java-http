package org.apache.coyote.http11;

public class HttpResponse {

    private final StatusLine statusLine;
    private final String filePath;
    private final HttpHeaders headers;
    private String body;

    private HttpResponse(final StatusLine statusLine, final String filePath) {
        this.statusLine = statusLine;
        this.filePath = filePath;
        this.headers = HttpHeaders.empty();
    }

    public static HttpResponse ok(final String filePath) {
        return new HttpResponse(StatusLine.http11(HttpStatus.OK), filePath);
    }

    public static HttpResponse found(final String filePath, final String redirect) {
        final HttpResponse response = new HttpResponse(StatusLine.http11(HttpStatus.FOUND), filePath);
        response.addHeader("Location", redirect);

        return response;
    }

    public static HttpResponse seeOther(final String filePath, final String redirect) {
        final HttpResponse response = new HttpResponse(StatusLine.http11(HttpStatus.SEE_OTHER), filePath);
        response.addHeader("Location", redirect);

        return response;
    }

    public static HttpResponse unauthorized() {
        final HttpResponse response = new HttpResponse(StatusLine.http11(HttpStatus.UNAUTHORIZED), "/401.html");
        response.addHeader("Location", "/401.html");

        return response;
    }

    public static HttpResponse notFound() {
        final HttpResponse response = new HttpResponse(StatusLine.http11(HttpStatus.NOT_FOUND), "/404.html");
        response.addHeader("Location", "/404.html");

        return response;
    }

    public void addBody(final String body) {
        this.body = body;
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public void addEntityHeaders() {
        headers.put("Content-Type", getContentType() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(getContentLength()));
    }

    private String getContentType() {
        final String defaultContentType = "text/html";
        if (filePath.equals("/")) {
            return defaultContentType;
        }
        final String prefix = "text/";
        final int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == 0) {
            throw new IllegalArgumentException("유효한 타겟 uri가 아닙니다.");
        }
        return prefix + filePath.substring(lastDotIndex + 1);
    }

    private int getContentLength() {
        return body.getBytes().length;
    }

    public String getMessage() {
        return String.join("\r\n",
            statusLine.toString(),
            headers.toString(),
            "",
            body);
    }

    public String filePath() {
        return filePath;
    }
}
