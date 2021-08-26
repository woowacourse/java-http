package nextstep.jwp.http.response;


import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final String body;

    public HttpResponse(int statusCode, ContentType contentType, String body) {
        statusLine = new StatusLine(statusCode);
        headers = new ResponseHeaders();
        this.body = body;
        if (body != null && !body.isBlank()) {
            putHeaders(contentType);
        }
    }

    public HttpResponse(int statusCode, String LocationHeader) {
        this(statusCode, null, null);
        headers.put("Location", LocationHeader);
    }

    private void putHeaders(ContentType contentType) {
        headers.put("Content-Type", contentType.getValue() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public byte[] getBytes() {
        if (body == null) {
            return getResponseAsBytesWithEmptyBody();
        }
        return getResponseAsBytesWithBody();
    }

    private byte[] getResponseAsBytesWithEmptyBody() {
        return String.join(NEW_LINE,
                        statusLine.toString(),
                        headers.toString())
                .getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getResponseAsBytesWithBody() {
        return String.join(NEW_LINE,
                        statusLine.toString(),
                        headers.toString(),
                        "",
                        body)
                .getBytes(StandardCharsets.UTF_8);
    }
}
