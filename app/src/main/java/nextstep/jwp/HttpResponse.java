package nextstep.jwp;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final StatusLine statusLine;
    private final String contentType;
    private final String contentLength;
    private final byte[] body;

    public HttpResponse(byte[] body) {
        this.statusLine = new StatusLine();
        this.contentType = "Content-Type: text/html;charset=utf-8 ";
        this.contentLength = "Content-Length: " + body.length + " ";
        this.body = body;
    }

    public byte[] toBytes() {
        final String response = String.join("\r\n",
                statusLine.asString(),
                contentType,
                contentLength,
                "",
                "");
        return response.getBytes(StandardCharsets.UTF_8);
    }
}

