package org.apache.coyote.http11.request;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Header;

public record HttpRequest(Http11Method method, String requestUri, List<Http11Query> queries,
                          List<Http11Header> headers, List<Http11Cookie> cookies,
                          LinkedHashMap<String, String> body) {

    private static final Http11RequestParser REQUEST_PARSER = new Http11RequestParser();

    private static final Http11StartLineParser START_LINE_PARSER = new Http11StartLineParser();

    private static final Http11MethodParser METHOD_PARSER = new Http11MethodParser(START_LINE_PARSER);

    private static final Http11RequestUriParser REQUEST_URI_PARSER = new Http11RequestUriParser(START_LINE_PARSER);

    private static final Http11RequestHeaderParser HEADER_PARSER = new Http11RequestHeaderParser(START_LINE_PARSER);

    private static final Http11RequestCookieParser COOKIE_PARSER = new Http11RequestCookieParser(START_LINE_PARSER);

    private static final Http11QueryParser queryStringParser = new Http11QueryParser();

    public static HttpRequest from(InputStream inputStream) {
        String rawRequest = REQUEST_PARSER.readAsString(inputStream);
        Http11Method http11Method = METHOD_PARSER.parseMethod(rawRequest);
        String requestUri = REQUEST_URI_PARSER.parseRequestURI(rawRequest);
        List<Http11Query> http11Queries = queryStringParser.parse(requestUri);

        List<Http11Header> http11Headers = HEADER_PARSER.parseHeaders(rawRequest);
        List<Http11Cookie> cookies = COOKIE_PARSER.parseCookies(rawRequest);
        LinkedHashMap<String, String> body = REQUEST_PARSER.parseBody(rawRequest);
        return new HttpRequest(http11Method, requestUri, http11Queries, http11Headers, cookies, body);
    }

    public boolean hasSessionCookie() {
        return cookies.stream()
                .map(Http11Cookie::key)
                .anyMatch(key -> key.equals("JSESSIONID"));
    }

    public Optional<Http11Cookie> findSessionCookie() {
        return cookies.stream()
                .filter(Http11Cookie::isSessionCookie)
                .findAny();
    }
}
