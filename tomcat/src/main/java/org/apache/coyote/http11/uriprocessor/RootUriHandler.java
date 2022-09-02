package org.apache.coyote.http11.uriprocessor;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;

public class RootUriHandler implements UriHandler {

    @Override
    public UriResponse getResponse(String uri) {
        return new UriResponse("Hello world!", ContentType.HTML.getValue());
    }
}
