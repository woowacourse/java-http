package org.apache.coyote.http11.request;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

public class RequestUri {

    private static final String HTML_URI = ".html";
    private static final String CSS_URI = ".css";
    private static final String JAVASCRIPT_URI = ".js";

    private static final Map<String, String> contentTypeMap = Map.of(
            HTML_URI, "text/html;charset=utf-8",
            CSS_URI, "text/css;charset=utf-8",
                    JAVASCRIPT_URI, "text/javascript;charset=utf-8"
    );

    private final String value;

    public RequestUri(final String value) {
        this.value = value;
    }

    public boolean isEqual(final String target) {
        return value.equals(target);
    }

    public String getContentType() {
        if (value.endsWith(CSS_URI)) {
            return contentTypeMap.get(CSS_URI);
        } else if (value.endsWith(JAVASCRIPT_URI)) {
            return contentTypeMap.get(JAVASCRIPT_URI);
        }
        return contentTypeMap.get(HTML_URI);
    }

    public String getResponseBody() throws IOException {
        if (value.equals("/")) {
            return "Hello world!";
        } else if (value.startsWith("/login")) {
            return readFile("static/login.html");
        }
        return readFile("static" + value);
    }

    private String readFile(final String filePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(filePath);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
