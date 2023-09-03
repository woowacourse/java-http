package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.Constants.SPACE;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RequestURI {

    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String QUERY_STRING_BEGIN_CHAR = "?";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final String uri;
    private final QueryString queryString;
    private final String httpMethod;
    private final String httpVersion;

    private RequestURI(
            final String uri,
            final QueryString queryString,
            final String httpMethod,
            final String httpVersion
    ) {
        this.uri = uri;
        this.queryString = queryString;
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
    }

    public static RequestURI from(final String requestURILine, final Consumer<User> logProcessor) {
        final var requestURIElements = parseRequestURIElements(requestURILine);
        final var uri = requestURIElements.get(URI_INDEX);

        if (uri.startsWith("/login")) {
            return parseLoginRequestURI(uri, requestURIElements, logProcessor);
        }

        return parseRequestURI(uri, QueryString.empty(), requestURIElements);
    }

    private static List<String> parseRequestURIElements(final String requestURILine) {
        return Arrays.stream(requestURILine.split(SPACE))
                .collect(Collectors.toUnmodifiableList());
    }

    private static RequestURI parseLoginRequestURI(
            final String uri,
            final List<String> requestURIElements,
            final Consumer<User> logProcessor
    ) {
        final var index = uri.indexOf(QUERY_STRING_BEGIN_CHAR);

        if (isExistQueryString(index)) {
            final var queryString = QueryString.from(uri.substring(index + 1));
            return parseLoginRequestURI(queryString, requestURIElements, logProcessor);
        }

        return parseRequestURI(LOGIN_PAGE, QueryString.empty(), requestURIElements);
    }

    private static RequestURI parseLoginRequestURI(
            final QueryString queryString,
            final List<String> requestURIElements,
            final Consumer<User> logProcessor
    ) {
        final var account = queryString.getValue("account");
        final var password = queryString.getValue("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    logProcessor.accept(user);
                    return parseRequestURI(INDEX_PAGE, queryString, requestURIElements);
                })
                .orElseGet(() -> parseRequestURI(LOGIN_FAIL_PAGE, QueryString.empty(), requestURIElements));
    }

    private static boolean isExistQueryString(final int index) {
        return index != -1;
    }

    private static RequestURI parseRequestURI(
            final String page,
            final QueryString queryString,
            final List<String> requestURIElements
    ) {
        return new RequestURI(
                page,
                queryString,
                requestURIElements.get(HTTP_METHOD_INDEX),
                requestURIElements.get(HTTP_VERSION_INDEX)
        );
    }

    public String getUri() {
        return uri;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

}
