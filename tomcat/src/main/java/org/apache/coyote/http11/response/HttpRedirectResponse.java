package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;

public class HttpRedirectResponse extends HttpResponse {

    protected final String redirectUri;

    public HttpRedirectResponse(
            final OutputStream outputStream,
            final String redirectUri
    ) {
        super(outputStream, "302", null, null, null);
        this.redirectUri = redirectUri;
    }

    @Override
    public void flush() throws IOException {
        outputStream.write(HttpRedirectResponseParser.parseToBytes(this));
        outputStream.flush();
    }
}
