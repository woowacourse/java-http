package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.NoSuchElementException;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    public String handle(final RequestHeader request) {
        final String uri = request.getUri().getPath();

        if (uri.equals("/")) {
            return text("Hello world!");
        } else if (uri.startsWith("/login")) {
            return login(request);
        }
        return resource(request);
    }

    private String login(final RequestHeader request) {
        final Uri uri = Uri.parse(request.getUri());

        final String account = uri.findParam("account");
        final String password = uri.findParam("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(password)) {
            LOG.info(user.toString());
        }

        return resource("login.html");
    }


    private String resource(final RequestHeader request) {
        final URI uri = request.getUri();
        final String filePath = uri.getPath();

        return resource(filePath);
    }

    private String resource(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(filePath))
                .asFormat();
    }

    private String text(final String body) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(body)
                .asFormat();
    }
}
