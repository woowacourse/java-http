package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.HttpMessageNotReadableException;

import java.util.Arrays;

public enum RequestMethod {
    POST("POST"),
    GET("GET"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String printedName;

    RequestMethod(final String printedName) {
        this.printedName = printedName;
    }

    public static RequestMethod from(final String printedName) {
        return Arrays.stream(values())
                .filter(requestMethod -> requestMethod.printedName.equals(printedName))
                .findFirst()
                .orElseThrow(HttpMessageNotReadableException::new);
    }
}
