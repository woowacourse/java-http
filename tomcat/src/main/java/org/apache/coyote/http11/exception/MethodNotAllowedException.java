package org.apache.coyote.http11.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.http11.request.HttpMethod;

public class MethodNotAllowedException extends RuntimeException {
    private static final String DELIMITER = ", ";

    private final List<HttpMethod> allowedMethods;

    public MethodNotAllowedException(final List<HttpMethod> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedMethods() {
        return allowedMethods.stream()
                .map(HttpMethod::name)
                .collect(Collectors.joining(DELIMITER));
    }
}
