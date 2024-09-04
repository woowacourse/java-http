package org.apache.coyote.http11.request;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Http11Header;

public record Http11Request(Http11Method method, String requestUri, List<Http11Query> queries,
                            List<Http11Header> headers, List<Cookie> cookies, LinkedHashMap<String, String> body) {

    public static Http11Request from(InputStream inputStream) {
        Http11RequestParser requestParser = new Http11RequestParser();
        String rawRequest = requestParser.readAsString(inputStream);
        Http11Method http11Method = requestParser.parseMethod(rawRequest);
        String requestUri = requestParser.parseRequestURI(rawRequest);
        Http11QueryStringParser queryStringParser = new Http11QueryStringParser();
        LinkedHashMap<String, String> rawQueries = queryStringParser.parse(requestUri);
        List<Http11Query> http11Queries = rawQueries.keySet().stream()
                .map(key -> new Http11Query(key, rawQueries.get(key)))
                .toList();
        List<Http11Header> http11Headers = requestParser.parseHeaders(rawRequest);
        List<Cookie> cookies = requestParser.parseCookies(rawRequest);
        LinkedHashMap<String, String> body = requestParser.parseBody(rawRequest);

        return new Http11Request(http11Method, requestUri, http11Queries, http11Headers, cookies, body);
    }

    public boolean hasSessionCookie() {
        return cookies.stream()
                .map(Cookie::key)
                .anyMatch(key -> key.equals("JSESSIONID"));
    }
}
