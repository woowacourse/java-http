package com.techcourse;

import org.apache.coyote.http11.StaticFileHandler;
import org.apache.coyote.http11.response.MimeType;

import java.io.IOException;

public class ResourceLoader {

    private static final StaticFileHandler staticFileHandler = new StaticFileHandler();

    public static String readWithFallback(String path) throws IOException {
        String[] candidates = {path, path + ".html", "/404.html"};
        
        for (String candidate : candidates) {
            if (staticFileHandler.exists(candidate)) {
                return staticFileHandler.readFile(candidate);
            }
        }
        
        throw new IOException("Resource not found: " + path);
    }

    public static MimeType getMimeType(String path) {
        return staticFileHandler.getContentType(path);
    }

    private ResourceLoader() {
    }
}
