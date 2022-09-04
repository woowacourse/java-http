package org.apache.coyote.http11.http11handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.ExtensionContentType;

public class Http11StaticResourceHandler implements Http11Handler {

    private static final String DIRECTORY = "static";

    @Override
    public boolean isProperHandler(String uri) {
        try {
            Objects.requireNonNull(getClass().getClassLoader().getResource(DIRECTORY + uri));
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public Map<String, String> extractElements(String uri) {
        Map<String, String> headerElements = new HashMap<>();
        headerElements.put("Content-Type", getContentType(uri));
        headerElements.put("Content-Length", getContentLength(uri));
        headerElements.put("body", extractBody(uri));
        return headerElements;
    }

    private String extractBody(String uri) {
        try {
            return Files.readString(new File(Objects.requireNonNull(
                    getClass().getClassLoader().getResource(DIRECTORY + uri)).getFile()).toPath());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private String getContentType(String uri) {
        String extension = extractExtension(uri);
        return ExtensionContentType.toContentType(extension);
    }

    private String extractExtension(String uri) {
        int extensionStartIndex = uri.lastIndexOf(".") + 1;
        return uri.substring(extensionStartIndex);
    }

    private String getContentLength(String uri) {
        return Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource(DIRECTORY + uri)).getFile()).length());
    }
}
