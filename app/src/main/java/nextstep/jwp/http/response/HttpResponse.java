package nextstep.jwp.http.response;


import nextstep.jwp.staticresource.StaticResource;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final ResponseHeaders headers;
    private StatusLine statusLine;
    private String body;

    public HttpResponse() {
        headers = new ResponseHeaders();
    }

    public void assignStatusCode(int statusCode) {
        statusLine = new StatusLine(statusCode);
    }

    public void addStaticResource(StaticResource staticResource) {
        body = staticResource.getContent();
        putHeaders(staticResource.getContentType());
    }

    private void putHeaders(ContentType contentType) {
        headers.put("Content-Type", contentType.getValue() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void assignLocationHeader(String locationHeader) {
        headers.put("Location", locationHeader);
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
