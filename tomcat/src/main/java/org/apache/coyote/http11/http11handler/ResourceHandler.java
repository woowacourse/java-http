package org.apache.coyote.http11.http11handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.ExtensionContentType;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.slf4j.Logger;

public class ResourceHandler implements Http11Handler {

    private static final String DIRECTORY = "static";
    private static final List<String> SUPPORT_EXTENSION = List.of("css", "js");

    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(String uri) {
        String extension = extractExtension(uri).toLowerCase(Locale.ROOT);
        return SUPPORT_EXTENSION.contains(extension);
    }

    private String extractExtension(String uri) {
        int extensionStartIndex = uri.lastIndexOf(".") + 1;
        return uri.substring(extensionStartIndex);
    }

    @Override
    public Map<String, String> handle(Logger log, String uri) {
        return handlerSupporter.extractElements(uri);
    }
}
