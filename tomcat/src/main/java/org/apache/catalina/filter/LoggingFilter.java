package org.apache.catalina.filter;

import org.apache.coyote.http11.message.Cookie;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    private LoggingFilter() {
    }

    public static void doFilter(HttpRequest httpRequest, HttpResponse httpResponse) {
        Headers requestHeaders = httpRequest.getHeaders();
        Cookie cookie = requestHeaders.getCookie();

        if (cookie.hasKey("JSESSIONID")) {
            String sessionId = cookie.getValue("JSESSIONID");
            log.info("session {} is logged in.", sessionId);
        }
    }
}
