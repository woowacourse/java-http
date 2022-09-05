package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.model.HttpParam;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.utils.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.apache.coyote.model.ContentType.HTML;


public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final HttpRequest httpRequest;

    public LoginHandler(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        logParams(httpRequest);
        String body = RequestUtil.getResponseBody("/login.html", getClass());
        return HttpResponse.of(HTML.getExtension(), body)
                .getOkResponse();
    }

    private void logParams(final HttpRequest httpRequest) {
        HttpParam httpParam = httpRequest.getParams();
        if (!httpParam.isEmpty() && checkUser(httpParam)) {
            log.info("request uri : {}. query param : {}", httpRequest.getPath(), httpParam);
        }
    }

    private boolean checkUser(final HttpParam params) {
        final String account = params.getByKey("account");
        final String password = params.getByKey("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        return optionalUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
