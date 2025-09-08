package org.apache.coyote.http11.parser;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.service.HttpServices;

import java.io.IOException;
import java.util.Map;

public class Http11RequestServiceProcessor implements HttpParser {

    private final HttpServices httpServices;

    public Http11RequestServiceProcessor(HttpServices httpServices) {
        this.httpServices = httpServices;
    }

    @Override
    public RequestResult parseContent(
            String contentPath,
            Map<String, String> query,
            String method,
            Map<String, String> requestBody,
            HttpCookies cookies,
            Session session
    ) throws IOException {
        return httpServices.processServiceRequest(contentPath, query, method, requestBody, cookies, session);
    }

    @Override
    public boolean isParseAble(String request) {
        return true;
    }
}
