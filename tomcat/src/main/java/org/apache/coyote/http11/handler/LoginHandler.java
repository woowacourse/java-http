package org.apache.coyote.http11.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.RequestTarget;
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public String handle(final HttpRequest httpRequest) {
        inquireUser(httpRequest.getRequestTarget());
        String uri = httpRequest.getRequestTarget().getUri();
        String responseBody = FileReader.read(uri + ".html");
        return createResponseMessage(ContentType.from(uri), responseBody);
    }

    private void inquireUser(final RequestTarget requestTarget) {
        Map<String, String> queryParameters = requestTarget.getQueryParameters();
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("User not found."));
        if (user.checkPassword(password)) {
            log.info("User : {}", user);
        }
    }
}
