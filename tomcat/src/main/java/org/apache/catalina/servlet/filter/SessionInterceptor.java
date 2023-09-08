package org.apache.catalina.servlet.filter;

import nextstep.jwp.db.SessionRepository;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.NoSuchElementException;

public class SessionInterceptor implements Interceptor {

    @Override
    public boolean preHandle(HttpRequest request) {
        if (request.getHttpHeadersLine().hasCookie(("JSESSIONID"))) {
            final String session = request.getCookie()
                    .orElseThrow(NoSuchElementException::new);
            if (SessionRepository.islogin(session)) {
                return true;
            }
        }
        return false;
    }
}