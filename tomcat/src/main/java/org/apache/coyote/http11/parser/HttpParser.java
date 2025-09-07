package org.apache.coyote.http11.parser;

import java.io.IOException;
import java.util.Map;

public interface HttpParser {

    ContentParseResult parseContent(
            String contentPath, Map<String, String> query, String method,
            Map<String, String> requestBody
    ) throws IOException;

    boolean isParseAble(String request);
}
