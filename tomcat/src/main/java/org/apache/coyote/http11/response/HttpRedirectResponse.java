package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.resource.Cookies;
import org.apache.coyote.http11.resource.ResponseStatus;

public class HttpRedirectResponse extends HttpResponse {

    protected final String redirectUri;

    public HttpRedirectResponse(final OutputStream outputStream,
                                final Cookies cookies,
                                final String redirectUri) {
        super(outputStream, ResponseStatus.REDIRECT, null, null, 0, cookies, null);
        this.redirectUri = redirectUri;
    }

    @Override
    public void flush() throws IOException {
        outputStream.write(HttpRedirectResponseParser.parseToBytes(this));
        outputStream.flush();
    }
}
