package org.apache.coyote.http11;

import java.io.BufferedReader;

public enum RequestMethod {

    GET,
    POST;

    public static RequestManager selectManager(String method, RequestParser requestParser) {
        if (method.equals(GET.name())) {
            return new GetRequestMangerImpl(requestParser);
        }
        return new PostRequestMangerImpl(requestParser);
    }
}
