package org.apache.coyote.http11;

import java.io.IOException;

public class Http11Response {

    private static final String RESPONSE_FORMAT = """
            %s

            %s
            """;

    private final Http11ResponseHeader header;
    private final Http11ResponseBody body;

    public Http11Response(Http11ResponseHeader header, Http11ResponseBody body) {
        this.header = header;
        this.body = body;
    }

    public static Http11Response of(StatusLine statusLine, String firstValueAccept, String requestUri) throws IOException {
        Http11ResponseBody body = Http11ResponseBody.from(requestUri);
        int contentLength = body.getContentLength();
        Http11ResponseHeader header = new Http11ResponseHeader(statusLine, ContentType.from(firstValueAccept), contentLength);

        return new Http11Response(header, body);
    }

    public String getResponse() {
        return String.format(RESPONSE_FORMAT, header.getHeader(), body.getBody());
    }
}
