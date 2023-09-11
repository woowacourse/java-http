package org.apache.coyote.exception;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import org.apache.coyote.common.HttpMethod;

public class MethodNotAllowedException extends RuntimeException{

    private final Collection<HttpMethod> allowedMethod;

    public MethodNotAllowedException(Collection<HttpMethod> allowedMethod) {
        super();
        this.allowedMethod = allowedMethod;
    }

    public List<String> getAllowedMethods() {
        return allowedMethod.stream()
            .map(HttpMethod::name)
            .collect(toList());
    }
}
