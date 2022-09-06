package org.apache.coyote.http11.http11handler.impl;

import java.util.List;
import java.util.Locale;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11response.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;

public class ResourceHandler implements Http11Handler {

    private static final List<String> SUPPORT_EXTENSION = List.of("css", "js");

    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        String extension = handlerSupporter.extractExtension(http11Request.getUri()).toLowerCase(Locale.ROOT);
        return SUPPORT_EXTENSION.contains(extension);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        return handlerSupporter.resourceResponseComponent(http11Request.getUri(), StatusCode.OK);
    }
}
