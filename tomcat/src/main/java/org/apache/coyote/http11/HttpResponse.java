package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final ResponseBody body;

    public HttpResponse(StatusLine statusLine, ResponseHeaders headers, ResponseBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public byte[] getBytes() {
        List<String> lines = new ArrayList<>();
        lines.add(statusLine.toMessage());
        lines.addAll(headers.toLines());
        lines.add("");
        lines.add(body.getContent());

        return String.join("\r\n", lines).getBytes(StandardCharsets.UTF_8);
    }
}
