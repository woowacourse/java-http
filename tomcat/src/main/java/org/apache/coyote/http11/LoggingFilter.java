package org.apache.coyote.http11;

import org.apache.coyote.http11.message.Cookie;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    private LoggingFilter() {
    }

    public static void logUserInfoIfExists(Request request) {
        Headers requestHeaders = request.getHeaders();
        Cookie cookie = requestHeaders.getCookie();

        if (cookie.hasKey("JSESSIONID")) {
            String sessionId = cookie.getValue("JSESSIONID");
            log.info("session {} is logged in.", sessionId);
        }
    }
}
