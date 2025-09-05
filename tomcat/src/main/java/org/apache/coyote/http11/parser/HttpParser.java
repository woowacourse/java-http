package org.apache.coyote.http11.parser;

import java.io.IOException;
import java.util.Map;

public interface HttpParser {

    ContentParseResult parseContent(String contentPath, Map<String, String> query) throws IOException;

    boolean isParseAble(String request);
}
