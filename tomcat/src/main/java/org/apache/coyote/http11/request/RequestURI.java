package org.apache.coyote.http11.request;

import java.util.List;
import java.util.function.Consumer;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RequestURI {

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
    private final RequestBody requestBody;
    private final String httpMethod;
    private final String httpVersion;

    private RequestURI(
            final String uri,
            final RequestBody requestBody,
            final String httpMethod,
            final String httpVersion
    ) {
        this(uri, uri, requestBody, httpMethod, httpVersion);
    }

    private RequestURI(
            final String oldUri,
            final String uri,
            final RequestBody requestBody,
            final String httpMethod,
            final String httpVersion
    ) {
        this.oldUri = oldUri;
        this.uri = uri;
        this.requestBody = requestBody;
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
    }

    public static RequestURI get(final List<String> requestURIElements, final Consumer<User> logProcessor) {
        final var uri = requestURIElements.get(URI_INDEX);

        if (uri.startsWith("/login")) {
            return parseLoginRequestURI(uri, requestURIElements, logProcessor);
        }

        if (uri.startsWith("/register")) {
            return parseRequestURI(REGISTER_PAGE, RequestBody.empty(), requestURIElements);
        }

        return parseRequestURI(uri, RequestBody.empty(), requestURIElements);
    }

    private static RequestURI parseLoginRequestURI(
            final String uri,
            final List<String> requestURIElements,
            final Consumer<User> logProcessor
    ) {
        final var index = uri.indexOf(QUERY_STRING_BEGIN_CHAR);

        if (isExistQueryString(index)) {
            final var queryString = RequestBody.from(uri.substring(index + 1));
            return parseLoginRequestURI(queryString, requestURIElements, logProcessor);
        }

        return parseRequestURI(LOGIN_PAGE, RequestBody.empty(), requestURIElements);
    }

    private static RequestURI parseLoginRequestURI(
            final RequestBody requestBody,
            final List<String> requestURIElements,
            final Consumer<User> logProcessor
    ) {
        final var account = requestBody.getValue("account");
        final var password = requestBody.getValue("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    logProcessor.accept(user);
                    return parseRequestURI(LOGIN_PAGE, INDEX_PAGE, requestBody, requestURIElements);
                })
                .orElseGet(() -> parseRequestURI(LOGIN_PAGE, LOGIN_FAIL_PAGE, RequestBody.empty(), requestURIElements));
    }

    private static boolean isExistQueryString(final int index) {
        return index != -1;
    }

    private static RequestURI parseRequestURI(
            final String oldPage,
            final String page,
            final RequestBody requestBody,
            final List<String> requestURIElements
    ) {
        return new RequestURI(
                oldPage,
                page,
                requestBody,
                requestURIElements.get(HTTP_METHOD_INDEX),
                requestURIElements.get(HTTP_VERSION_INDEX)
        );
    }

    private static RequestURI parseRequestURI(
            final String page,
            final RequestBody requestBody,
            final List<String> requestURIElements
    ) {
        return new RequestURI(
                page,
                requestBody,
                requestURIElements.get(HTTP_METHOD_INDEX),
                requestURIElements.get(HTTP_VERSION_INDEX)
        );
    }

    public static RequestURI post(
            final List<String> requestURIElements,
            final String requestBody,
            final Consumer<User> logProcessor
    ) {
        final var uri = requestURIElements.get(URI_INDEX);
        final RequestBody parsedRequestBody = RequestBody.from(requestBody);
        if (uri.startsWith("/register")) {
            final var account = parsedRequestBody.getValue("account");
            final var password = parsedRequestBody.getValue("password");
            final var email = parsedRequestBody.getValue("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return parseRequestURI(REGISTER_PAGE, INDEX_PAGE, parsedRequestBody, requestURIElements);
        }

        return parseLoginRequestURI(parsedRequestBody, requestURIElements, logProcessor);
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

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
