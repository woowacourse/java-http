package org.apache.coyote.request;

import java.util.List;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.HttpException;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.support.HttpVersion;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;
    private final Parameters parameters;

    private HttpRequest(StartLine startLine) {
        this.method = startLine.getMethod();
        this.uri = startLine.getUri();
        this.version = startLine.getVersion();
        this.parameters = startLine.getParameters();
    }

    public static HttpRequest of(List<String> request) {
        if (request == null || request.size() == 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        return new HttpRequest(StartLine.of(request.get(START_LINE_INDEX)));
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

    public Parameters getParameters() {
        return parameters;
    }
}
