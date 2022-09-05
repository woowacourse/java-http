package org.apache.coyote.http11.http11handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.ExtensionContentType;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.slf4j.Logger;

public class IndexPageHandler implements Http11Handler {

    private static final String URI = "/index";
    private static final String URI_WITH_EXTENSION = "/index.html";
    private static final String DIRECTORY = "static";

    private QueryStringProcessor queryStringProcessor = new QueryStringProcessor();
    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(String uri) {
        uri = queryStringProcessor.removeQueryString(uri);
        return uri.equals(URI) || uri.equals(URI_WITH_EXTENSION);
    }

    @Override
    public Map<String, String> handle(Logger log, String uri) {
        if (handlerSupporter.noExtension(uri)) {
            uri = handlerSupporter.addHtmlExtension(uri);
        }
        return handlerSupporter.extractElements(uri);
    }
}
