package nextstep.jwp.handler;

import static nextstep.jwp.http.common.ContentType.TEXT_HTML;
import static nextstep.jwp.http.common.HttpMethod.GET;

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
            return HttpResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, httpRequest.getRequestHeaders(),
                StaticResourceUtil.findByPath("/500.html"));
        }
    }

    private static HttpResponse findByUri(final HttpRequest httpRequest) throws IOException {
        if ("/".equals(httpRequest.getRequestUri())) {
            if (GET.name().equals(httpRequest.getRequestMethod())) {
                return HttpResponse.create(HttpStatus.OK, httpRequest.getRequestHeaders(), new StaticResource(new Content("Hello world!"), TEXT_HTML));
            }
        }

        if ("/login".equals(httpRequest.getRequestUri())) {
            if (GET.name().equals(httpRequest.getRequestMethod())) {
                User user;
                try {
                    user = InMemoryUserRepository.findByAccount(httpRequest.getQueryParameterValue("account"))
                        .orElseThrow(NotFoundUserException::new);
                    validateCheckPassword(user, httpRequest.getQueryParameterValue("password"));
                    log.info("user : {}", user);
                    return HttpResponse.create(HttpStatus.OK, httpRequest.getRequestHeaders(), StaticResourceUtil.findByPath("/login.html"));
                } catch (NotFoundUserException e) {
                    return HttpResponse.create(HttpStatus.UNAUTHORIZED, httpRequest.getRequestHeaders(),
                        StaticResourceUtil.findByPath("/401.html"));
                }
            }
        }

        StaticResource staticResource = StaticResourceUtil.findByPath(httpRequest.getRequestUri());
        return HttpResponse.create(HttpStatus.OK, httpRequest.getRequestHeaders(), staticResource);
    }

    private static void validateCheckPassword(final User user, final String password) {
        if (user.checkPassword(password)) {
            return;
        }
        throw new NotFoundUserException();
    }
}
