package org.apache.coyote.http11.http11handler.impl;

import org.apache.coyote.http11.ExtensionContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11request.Http11Request;

public class DefaultPageHandler implements Http11Handler {

    private static final String TARGET_URI = "/";
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return http11Request.getUri().equals(TARGET_URI);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        return new ResponseComponent(
                StatusCode.OK,
                ExtensionContentType.HTML.getContentType(),
                Integer.toString(DEFAULT_MESSAGE.length()),
                DEFAULT_MESSAGE);
    }
}

