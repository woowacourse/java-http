package org.apache.coyote.http11.http11handler.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import javax.swing.text.html.HTML;
import org.apache.coyote.http11.ExtensionContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;

public class HandlerSupporter {

    private static final String DIRECTORY = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String EXTENSION_IDENTIFIER = ".";

    public ResponseComponent extractElements(String uri, StatusCode statusCode) {
        return new ResponseComponent(
                statusCode,
                getContentType(uri),
                getContentLength(uri),
                extractBody(uri)
        );
    }

    public String addHtmlExtension(String uri) {
        return uri + HTML_EXTENSION;
    }

    public boolean noExtension(String uri) {
        return !uri.contains(EXTENSION_IDENTIFIER);
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

    public String extractExtension(String uri) {
        int extensionStartIndex = uri.lastIndexOf(EXTENSION_IDENTIFIER) + 1;
        return uri.substring(extensionStartIndex);
    }

    private String getContentLength(String uri) {
        return Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource(DIRECTORY + uri)).getFile()).length());
    }
}
