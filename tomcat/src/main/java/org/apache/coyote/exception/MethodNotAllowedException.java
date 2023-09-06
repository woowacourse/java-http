package org.apache.coyote.exception;

import java.util.List;
import org.apache.coyote.common.HttpMethod;

public class MethodNotAllowedException extends RuntimeException{

    private final List<HttpMethod> allowedMethod;

    public MethodNotAllowedException(List<HttpMethod> allowedMethod) {
        super();
        this.allowedMethod = allowedMethod;
    }

    public List<HttpMethod> getAllowedMethods() {
        return allowedMethod;
    }
}
