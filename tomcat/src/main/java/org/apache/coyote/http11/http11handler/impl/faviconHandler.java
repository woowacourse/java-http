package org.apache.coyote.http11.http11handler.impl;

import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;

public class faviconHandler implements Http11Handler {

    private static final String SUPPORT_EXTENSION = "ico";

    private final HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        String extension = handlerSupporter.extractExtension(http11Request.getUri());
        return extension.equals(SUPPORT_EXTENSION);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        return new ResponseComponent(StatusCode.OK, "ico", "0", "");
    }
}
