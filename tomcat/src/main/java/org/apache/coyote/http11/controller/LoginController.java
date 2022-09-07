package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.Headers;
import org.apache.coyote.http11.http.domain.HttpCookie;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        try {
            Map<String, String> parameters = httpRequest.getMessageBody()
                    .getParameters();
            String account = parameters.get("account");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new NotFoundException("User not found."));
            String password = parameters.get("password");
            return login(user, password);
        } catch (NotFoundException e) {
            return HttpResponse.found(
                    Headers.builder()
                            .location("/401.html"),
                    new MessageBody(""));
        }
    }

    private HttpResponse login(final User user, final String password) {
        if (user.checkPassword(password)) {
            log.info("User : {}", user);
            UUID uuid = UUID.randomUUID();
            return HttpResponse.found(
                    Headers.builder()
                            .setCookie(uuid)
                            .location("/index.html"),
                    new MessageBody(""));
        }
        return HttpResponse.found(
                Headers.builder()
                        .location("/401.html"),
                new MessageBody(""));
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        HttpCookie cookie = httpRequest.getHeaders().getCookie();
        if (cookie.containsJSESSIONID()) {
            return HttpResponse.found(
                    Headers.builder()
                            .location("/index.html"),
                    new MessageBody(""));
        }
        String uri = httpRequest.getRequestLine()
                .getRequestTarget()
                .getUri();
        String responseBody = FileReader.read(uri + ".html");
        return HttpResponse.ok(ContentType.from(uri), new MessageBody(responseBody));
    }
}
