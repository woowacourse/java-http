package org.apache.coyote.http11.request;

import java.util.List;
import java.util.function.Consumer;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class HttpRequestURI {

    private static final String REGISTER_PAGE = "/register.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String QUERY_STRING_BEGIN_CHAR = "?";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final String oldUri;
    private final String uri;
    private final HttpRequestBody httpRequestBody;
    private final String httpMethod;
    private final String httpVersion;

    private HttpRequestURI(
            final String uri,
            final HttpRequestBody httpRequestBody,
            final String httpMethod,
            final String httpVersion
    ) {
        this(uri, uri, httpRequestBody, httpMethod, httpVersion);
    }

    private HttpRequestURI(
            final String oldUri,
            final String uri,
            final HttpRequestBody httpRequestBody,
            final String httpMethod,
            final String httpVersion
    ) {
        this.oldUri = oldUri;
        this.uri = uri;
        this.httpRequestBody = httpRequestBody;
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestURI get(final List<String> requestURIElements, final Consumer<User> logProcessor) {
        final var uri = requestURIElements.get(URI_INDEX);

        if (uri.startsWith("/login")) {
            return parseLoginRequestURI(uri, requestURIElements, logProcessor);
        }

        if (uri.startsWith("/register")) {
            return parseRequestURI(REGISTER_PAGE, HttpRequestBody.empty(), requestURIElements);
        }

        return parseRequestURI(uri, HttpRequestBody.empty(), requestURIElements);
    }

    private static HttpRequestURI parseLoginRequestURI(
            final String uri,
            final List<String> requestURIElements,
            final Consumer<User> logProcessor
    ) {
        final var index = uri.indexOf(QUERY_STRING_BEGIN_CHAR);

        if (isExistQueryString(index)) {
            final var queryString = HttpRequestBody.from(uri.substring(index + 1));
            return parseLoginRequestURI(queryString, requestURIElements, logProcessor);
        }

        return parseRequestURI(LOGIN_PAGE, HttpRequestBody.empty(), requestURIElements);
    }

    private static HttpRequestURI parseLoginRequestURI(
            final HttpRequestBody httpRequestBody,
            final List<String> requestURIElements,
            final Consumer<User> logProcessor
    ) {
        final var account = httpRequestBody.getValue("account");
        final var password = httpRequestBody.getValue("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    logProcessor.accept(user);
                    return parseRequestURI(LOGIN_PAGE, INDEX_PAGE, httpRequestBody, requestURIElements);
                })
                .orElseGet(() -> parseRequestURI(LOGIN_PAGE, LOGIN_FAIL_PAGE, HttpRequestBody.empty(), requestURIElements));
    }

    private static boolean isExistQueryString(final int index) {
        return index != -1;
    }

    private static HttpRequestURI parseRequestURI(
            final String oldPage,
            final String page,
            final HttpRequestBody httpRequestBody,
            final List<String> requestURIElements
    ) {
        return new HttpRequestURI(
                oldPage,
                page,
                httpRequestBody,
                requestURIElements.get(HTTP_METHOD_INDEX),
                requestURIElements.get(HTTP_VERSION_INDEX)
        );
    }

    private static HttpRequestURI parseRequestURI(
            final String page,
            final HttpRequestBody httpRequestBody,
            final List<String> requestURIElements
    ) {
        return new HttpRequestURI(
                page,
                httpRequestBody,
                requestURIElements.get(HTTP_METHOD_INDEX),
                requestURIElements.get(HTTP_VERSION_INDEX)
        );
    }

    public static HttpRequestURI post(
            final List<String> requestURIElements,
            final String requestBody,
            final Consumer<User> logProcessor
    ) {
        final var uri = requestURIElements.get(URI_INDEX);
        final HttpRequestBody parsedHttpRequestBody = HttpRequestBody.from(requestBody);
        if (uri.startsWith("/register")) {
            final var account = parsedHttpRequestBody.getValue("account");
            final var password = parsedHttpRequestBody.getValue("password");
            final var email = parsedHttpRequestBody.getValue("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return parseRequestURI(REGISTER_PAGE, INDEX_PAGE, parsedHttpRequestBody, requestURIElements);
        }

        return parseLoginRequestURI(parsedHttpRequestBody, requestURIElements, logProcessor);
    }

    public boolean isLoginSuccess() {
        return this.oldUri.equals(LOGIN_PAGE) && this.uri.equals(INDEX_PAGE);
    }

    public String getOldUri() {
        return oldUri;
    }

    public String getUri() {
        return uri;
    }

    public HttpRequestBody getRequestBody() {
        return httpRequestBody;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
