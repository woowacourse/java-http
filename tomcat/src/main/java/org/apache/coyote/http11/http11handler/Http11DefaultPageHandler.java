package org.apache.coyote.http11.http11handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.ExtensionContentType;

public class Http11DefaultPageHandler implements Http11Handler {

    private static final String TARGET_URI = "/";
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public boolean isProperHandler(String uri) {
        return uri.equals(TARGET_URI);
    }

    @Override
    public Map<String, String> extractElements(String uri) {
        Map<String, String> headerElements = new HashMap<>();
        headerElements.put("spec", "HTTP/1.1");
        headerElements.put("Content-Type", getContentType(uri));
        headerElements.put("Content-Length", getContentLength(uri));
        headerElements.put("body", extractBody(uri));
        return headerElements;
    }

    private String extractBody(String uri) {
        return DEFAULT_MESSAGE;
    }

    private String getContentType(String uri) {
        return ExtensionContentType.HTML.getContentType();
    }

    private String getContentLength(String uri) {
        return Integer.toString(DEFAULT_MESSAGE.length());
    }
}
