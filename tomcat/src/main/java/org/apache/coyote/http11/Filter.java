package org.apache.coyote.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Filter {

    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    private Filter() {
    }

    public static void logUserInfoIfExists(Request request) {
        RequestURI requestURI = request.getRequestURI();

        if (requestURI.hasQueryParameters()) {
            String[] queryParameters = requestURI.queryParameters();
            log.info(queryParameters[0]);
            log.info(queryParameters[1]);
        }
    }
}
