package org.apache.coyote.http11.request;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.MimeType;

public class RootRequestProcessor implements HttpRequestProcessor{
    @Override
    public HttpResponse process(HttpRequest request) {
        String responseBody = "Hello world!";
        return new HttpResponse(
                HttpStatus.OK,
                MimeType.of("html"),
                responseBody);
    }
}
