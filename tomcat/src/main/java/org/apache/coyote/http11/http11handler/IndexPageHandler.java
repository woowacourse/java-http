package org.apache.coyote.http11.http11handler;

import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.slf4j.Logger;

public class IndexPageHandler implements Http11Handler {

    private static final String URI = "/index";
    private static final String URI_WITH_EXTENSION = "/index.html";

    private QueryStringProcessor queryStringProcessor = new QueryStringProcessor();
    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(String uri) {
        uri = queryStringProcessor.removeQueryString(uri);
        return uri.equals(URI) || uri.equals(URI_WITH_EXTENSION);
    }

    @Override
    public ResponseComponent handle(Logger log, String uri) {
        if (handlerSupporter.noExtension(uri)) {
            uri = handlerSupporter.addHtmlExtension(uri);
        }
        return handlerSupporter.extractElements(uri, StatusCode.OK);
    }
}
