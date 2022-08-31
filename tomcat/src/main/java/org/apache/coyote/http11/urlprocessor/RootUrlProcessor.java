package org.apache.coyote.http11.urlprocessor;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UrlResponse;

public class RootUrlProcessor implements UrlProcessor {

    @Override
    public UrlResponse getResponse(String url) {
        return new UrlResponse("Hello world!", ContentType.HTML.getValue());
    }
}
