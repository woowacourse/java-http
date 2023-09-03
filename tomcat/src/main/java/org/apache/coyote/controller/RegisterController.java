package org.apache.coyote.controller;

import nextstep.FileResolver;
import org.apache.coyote.Controller;
import org.apache.coyote.domain.HttpRequestHeader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RegisterController extends Controller {

    public String run(final HttpRequestHeader httpRequestHeader) throws IOException {
        final String parsedUri = httpRequestHeader.getUri();
        final Map<String, String> queryStrings = parseQueryStrings(parsedUri);
        if (queryStrings.isEmpty()) {
            final FileResolver file = FileResolver.findFile(parsedUri);
            return file.getResponse();
        }
        return null;
    }

    private Map<String, String> parseQueryStrings(final String parsedUri) {
        if (!parsedUri.contains("?")) {
            return Collections.emptyMap();
        }
        final int index = parsedUri.indexOf("?");
        final String queryStringUri = parsedUri.substring(index + 1);
        final String[] strings = queryStringUri.split("&");
        Map<String, String> queryStrings = new HashMap<>();
        for (final String string : strings) {
            final String[] keyValue = string.split("=");
            queryStrings.put(keyValue[0], keyValue[1]);
        }
        return queryStrings;
    }
}
