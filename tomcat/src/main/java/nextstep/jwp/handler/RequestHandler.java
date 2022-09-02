package nextstep.jwp.handler;

import static nextstep.jwp.http.common.ContentType.TEXT_HTML;
import static nextstep.jwp.http.request.RequestMethod.GET;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.Content;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.model.User;
import nextstep.jwp.util.StaticResourceUtil;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static HttpResponse execute(final HttpRequest httpRequest) throws IOException {
        try {
            return findByUri(httpRequest);
        } catch (RuntimeException e) {
            return HttpResponse.createBody(HttpStatus.INTERNAL_SERVER_ERROR,
                StaticResourceUtil.findByPathWithExtension("/", "500.html"));
        }
    }

    private static HttpResponse findByUri(final HttpRequest httpRequest) throws IOException {
        if ("/".equals(httpRequest.getUri())) {
            if (GET.equals(httpRequest.getMethod())) {
                return HttpResponse.createBody(HttpStatus.OK, new StaticResource(new Content("Hello world!"), TEXT_HTML));
            }
        }

        if ("/login".equals(httpRequest.getUri())) {
            if (GET.equals(httpRequest.getMethod())) {
                User user;
                try {
                    user = InMemoryUserRepository.findByAccount(httpRequest.getUriParameter("account"))
                        .orElseThrow(NotFoundUserException::new);
                    validateCheckPassword(user, httpRequest.getUriParameter("password"));
                    log.info("user : {}", user);
                    return HttpResponse.createBody(HttpStatus.OK, StaticResourceUtil.findByPathWithExtension("/login", ".html"));
                } catch (NotFoundUserException e) {
                    return HttpResponse.createBody(HttpStatus.UNAUTHORIZED,
                        StaticResourceUtil.findByPathWithExtension("/", "401.html"));
                }
            }
        }

        StaticResource staticResource = StaticResourceUtil.findByPath(httpRequest.getUri());
        return HttpResponse.createBody(HttpStatus.OK, staticResource);
    }

    private static void validateCheckPassword(final User user, final String password) {
        if (user.checkPassword(password)) {
            return;
        }
        throw new NotFoundUserException();
    }
}
