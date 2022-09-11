package nextstep.jwp.controller;

import static org.apache.coyote.http11.handler.StaticResourceHandler.readFile;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        final User user = InMemoryUserRepository.findByAccount(request.getRequestBodyValue("account"))
                .orElseThrow(UserNotFoundException::new);
        if (user.checkPassword(request.getRequestBodyValue("password"))) {
            log.info(String.format("user : %s", user));
            return new HttpResponse(request.getHttpVersion(), HttpStatus.FOUND, new Location("/index.html"),
                    request.getContentType(), readFile(request.getHttpPath()));
        }
        return new HttpResponse(request.getHttpVersion(), HttpStatus.FOUND, new Location("/401.html"),
                request.getContentType(), readFile(request.getHttpPath()));
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return new HttpResponse(request.getHttpVersion(), HttpStatus.OK, Location.empty(), request.getContentType(),
                readFile(request.getHttpPath()));
    }
}


