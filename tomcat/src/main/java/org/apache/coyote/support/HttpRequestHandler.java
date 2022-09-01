package org.apache.coyote.support;

import java.util.Optional;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    public String handle(HttpRequest request) {
        if (request.isGet()) {
            logAccount(request);
            return new ResourceResponse(request.getUri()).toHttpResponseMessage();
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    private void logAccount(HttpRequest request) {
        Optional<User> account = request.checkLoginAccount();
        if (account.isEmpty()) {
            return;
        }
        log.info(account.get().toString());
    }
}
