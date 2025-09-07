package org.apache.coyote.http11.parser;

import org.apache.coyote.http11.ParseHttpRequest;
import org.apache.coyote.http11.service.HttpServices;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11GetProcessor {

    private final List<HttpParser> parsers;

    public Http11GetProcessor() {
        parsers = List.of(new HtmlParser(), new CssParser(), new Http11RequestServiceProcessor(new HttpServices()));
    }

    public ContentParseResult parse(ParseHttpRequest httpRequest) throws IOException {
        Map<String, String> queryFinder = parseQueries(httpRequest.httpRequest());
        String contentPath = parseContentPath(httpRequest.httpRequest());
        return getContentParseResult(contentPath, queryFinder, httpRequest.method(), httpRequest.requestBody());
    }

    private String parseContentPath(String httpRequest) {
        return httpRequest.split("\\?")[0];
    }

    private Map<String, String> parseQueries(String httpReqeust) {
        String[] requestSplit = httpReqeust.split("\\?");

        if (!isQueryProcessable(requestSplit)) {
            return new HashMap<>();
        }

        return parseQuery(requestSplit);
    }

    private Map<String, String> parseQuery(String[] requestSplit) {
        Map<String, String> queryFinder = new HashMap<>();
        String queryPart = requestSplit[1];
        String[] querysArray = queryPart.split("&");

        for (String query : querysArray) {
            String[] keyValues = query.split("=");
            queryFinder.put(keyValues[0], keyValues[1]);
        }

        return queryFinder;
    }

    private boolean isQueryProcessable(String[] requestSplit) {
        return requestSplit.length >= 2;
    }

    private ContentParseResult getContentParseResult(
            String request,
            final Map<String, String> query,
            String method,
            Map<String, String> requestBody
    ) throws IOException {
        if (request.isBlank()) {
            throw new IllegalArgumentException("처리할 수 없는 요청입니다");
        }

        for (HttpParser contentParser : parsers) {
            if (!contentParser.isParseAble(request)) {
                continue;
            }

            return contentParser.parseContent(request, query, method, requestBody);
        }

        throw new IllegalArgumentException("처리할 수 없는 요청입니다");
    }
}
