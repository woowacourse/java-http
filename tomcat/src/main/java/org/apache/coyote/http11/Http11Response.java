package org.apache.coyote.http11;

import java.io.IOException;
import java.util.List;

public class Http11Response {

    private static final String RESPONSE_FORMAT = String.join("\r\n",
            "%s",
            "%s");

    private final Http11ResponseHeader header;
    private final Http11ResponseBody body;

    public Http11Response(Http11ResponseHeader header, Http11ResponseBody body) {
        this.header = header;
        this.body = body;
    }

    public static Http11Response of(StatusLine statusLine, List<String> acceptTypes, RequestUri requestUri)
            throws IOException {
        Http11ResponseBody body = Http11ResponseBody.from(requestUri);
        int contentLength = body.getContentLength();
        ContentType from = ContentType.from(acceptTypes);
        Http11ResponseHeader header = new Http11ResponseHeader(statusLine,
                from,
                contentLength);

        return new Http11Response(header, body);
    }

    public String getResponse() {
        return String.format(RESPONSE_FORMAT, header, body);
    }
}
