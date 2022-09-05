package org.apache.coyote.http11.http11handler;

import java.util.List;
import java.util.Locale;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.slf4j.Logger;

public class ResourceHandler implements Http11Handler {

    private static final List<String> SUPPORT_EXTENSION = List.of("css", "js");

    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(String uri) {
        String extension = handlerSupporter.extractExtension(uri).toLowerCase(Locale.ROOT);
        return SUPPORT_EXTENSION.contains(extension);
    }

    @Override
    public ResponseComponent handle(Logger log, String uri) {
        return handlerSupporter.extractElements(uri, StatusCode.OK);
    }
}
