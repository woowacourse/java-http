package org.apache.coyote.http11.parser;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;

import java.io.IOException;
import java.util.Map;

public interface HttpParser {

    RequestResult parseContent(
            String contentPath, Map<String, String> query, String method,
            Map<String, String> requestBody,
            HttpCookies cookies, Session session
    ) throws IOException;

    boolean isParseAble(String request);
}
