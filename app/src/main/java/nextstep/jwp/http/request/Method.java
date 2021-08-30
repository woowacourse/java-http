package nextstep.jwp.http.request;

import nextstep.jwp.exception.NotImplementedException;

public enum Method {
    GET,
    POST;
    /*
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE;
     */

    public static Method matchOf(String requestMethod) {
        try {
            return valueOf(requestMethod);
        } catch (IllegalArgumentException e) {
            throw new NotImplementedException(requestMethod);
        }
    }
}
