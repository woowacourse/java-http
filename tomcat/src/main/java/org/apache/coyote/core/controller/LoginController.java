package org.apache.coyote.core.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.io.ClassPathResource;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public void service(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        Login(request);

        String responseBody = new ClassPathResource().getStaticContent(request.getPath());

        response.setStatus("OK");
        response.setContentType(request.findContentType());
        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);
    }

    private void Login(final HttpRequest request) {
        Map<String, String> queryString = request.getQueryParams();
        String account = queryString.get("account");
        String password = queryString.get("password");

        User user = findUser(account);
        checkUserPassword(user, password);

        printUser(user);
    }

    private void checkUserPassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
    }

    private void printUser(final User user) {
        log.info(user.toString());
    }
}
