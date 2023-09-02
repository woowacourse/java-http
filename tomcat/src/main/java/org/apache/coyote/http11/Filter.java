package org.apache.coyote.http11;

import org.apache.coyote.http11.message.Cookie;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Filter {

    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    private Filter() {
    }

    public static void logUserInfoIfExists(Request request) {
        Headers requestHeaders = request.getHeaders();
        Cookie cookie = requestHeaders.getCookie();

        if (cookie.hasKey("JSESSIONID")) {
            log.info("session {} is logged in.", cookie.getValue("JSESSIONID"));
        }
    }
}
