package org.apache.coyote.request;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.support.HttpMethod;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;
    private static final String START_LINE_DELIMITER = " ";
    private static final int START_LINE_METHOD_INDEX = 0;
    private static final int START_LINE_URI_INDEX = 1;
    private static final String QUERY_STRING_BEGIN_SIGN = "?";

    private final HttpMethod method;
    private final String uri;
    private final Parameters parameters;

    private HttpRequest(HttpMethod method, String uri, Parameters parameters) {
        this.method = method;
        this.uri = uri;
        this.parameters = parameters;
    }

    private HttpRequest(HttpMethod method, String uri) {
        this(method, uri, new Parameters(new HashMap<>()));
    }

    public static HttpRequest of(List<String> request) {
        final var startLine = request.get(START_LINE_INDEX).split(START_LINE_DELIMITER);
        final var method = HttpMethod.valueOf(startLine[START_LINE_METHOD_INDEX]);
        final var uri = startLine[START_LINE_URI_INDEX];
        if (uri.contains(QUERY_STRING_BEGIN_SIGN)) {
            final var delimiterIndex = uri.indexOf(QUERY_STRING_BEGIN_SIGN);
            final var path = uri.substring(0, delimiterIndex);
            final var queryString = uri.substring(delimiterIndex + 1);
            return new HttpRequest(method, path, Parameters.ofQueryString(queryString));
        }
        return new HttpRequest(method, uri);
    }

    public boolean isGet() {
        return this.method.equals(HttpMethod.GET);
    }

    public Optional<User> checkLoginAccount() {
        if (uri.startsWith("/login") && parameters.contains("account")) {
            return InMemoryUserRepository.findByAccount(parameters.get("account"));
        }
        return Optional.empty();
    }

    public String getUri() {
        return uri;
    }
}
