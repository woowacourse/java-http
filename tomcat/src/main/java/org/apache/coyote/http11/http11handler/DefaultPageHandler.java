package org.apache.coyote.http11.http11handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.ExtensionContentType;
import org.slf4j.Logger;

public class DefaultPageHandler implements Http11Handler {

    private static final String TARGET_URI = "/";
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public boolean isProperHandler(String uri) {
        return uri.equals(TARGET_URI);
    }

    @Override
    public Map<String, String> handle(Logger log, String uri) {
        Map<String, String> headerElements = new HashMap<>();
        headerElements.put("Content-Type", ExtensionContentType.HTML.getContentType());
        headerElements.put("Content-Length", Integer.toString(DEFAULT_MESSAGE.length()));
        headerElements.put("body", DEFAULT_MESSAGE);
        return headerElements;
    }
}

